package com.madebynikhil.editor.view;

import com.madebynikhil.editor.command.MoveStates;
import com.madebynikhil.editor.command.ToggleState;
import com.madebynikhil.editor.controller.DesignerController;
import com.madebynikhil.model.State;
import com.madebynikhil.observer.Observable;
import com.madebynikhil.observer.Observer;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.*;


/**
 * View corresponding to one state that this is observing.
 * Created by NikhilVerma on 02/11/16.
 */
public class StateView extends DesignerElementView implements Observer{

    public static final double STATE_RADIUS=25;
    private static final double STATE_INNER_RADIUS=20;
    private static double pressedX;
    private static double pressedY;
    private static double lastModelDx;
    private static double lastModelDy;

    private DesignerController designerController;
    private State state;

    private Circle outerCircle;
    private Circle innerCircle;
    private Label label;
    private TransitionView currentlyEditedTransition;
    private boolean exitedThisStateAtLeastOnceWhileEditing;
    private Map<String,TransitionView> transitionViewMap=new HashMap<>();

    public StateView(DesignerController designerController, State state) {
        this.designerController = designerController;
        this.designerController.subscribe(this);
        this.state = state;
        this.initView();
        this.setupEvents();
        this.state.subscribe(this);//this should be done at the end because the handler depends on the views being setup
    }

    private void initView(){
        this.outerCircle=new Circle(designerController.lengthInCurrentZoom(STATE_RADIUS),Color.WHITE);
        this.outerCircle.setStroke(Color.BLACK);
        this.innerCircle=new Circle(designerController.lengthInCurrentZoom(STATE_INNER_RADIUS),Color.WHITE);
        this.innerCircle.setVisible(state.isFinalState());
        this.innerCircle.setStroke(Color.BLACK);
        this.label=new Label(this.state.getName());
        this.label.setTranslateX(-7);
        this.label.setTranslateY(-7);
        this.getChildren().add(this.outerCircle);
        this.getChildren().add(this.innerCircle);
        this.getChildren().add(this.label);
        Point2D position = this.designerController.toViewSpace(state.getX(), state.getY());
        this.setLayoutX(position.getX());
        this.setLayoutY(position.getY());
    }

    private void setupEvents(){
        this.outerCircle.addEventHandler(MouseEvent.MOUSE_CLICKED,this::stateClicked);
        this.innerCircle.addEventHandler(MouseEvent.MOUSE_CLICKED,this::stateClicked);
        this.label.addEventHandler(MouseEvent.MOUSE_CLICKED,this::stateClicked);

        this.outerCircle.addEventHandler(MouseEvent.MOUSE_PRESSED,this::statePressed);
        this.innerCircle.addEventHandler(MouseEvent.MOUSE_PRESSED,this::statePressed);
        this.label.addEventHandler(MouseEvent.MOUSE_PRESSED,this::statePressed);

        this.outerCircle.addEventHandler(MouseEvent.MOUSE_DRAGGED,this::stateDragged);
        this.label.addEventHandler(MouseEvent.MOUSE_DRAGGED,this::stateDragged);

        this.outerCircle.addEventHandler(MouseEvent.MOUSE_RELEASED,this::stateReleased);
        this.label.addEventHandler(MouseEvent.MOUSE_RELEASED,this::stateReleased);

        this.outerCircle.addEventHandler(MouseEvent.MOUSE_ENTERED,(e)->designerController.mouseEntered(this));
        this.innerCircle.addEventHandler(MouseEvent.MOUSE_ENTERED,(e)->designerController.mouseEntered(this));
        this.label.addEventHandler(MouseEvent.MOUSE_ENTERED,(e)->designerController.mouseEntered(this));

        this.outerCircle.addEventHandler(MouseEvent.MOUSE_EXITED,(e)->designerController.mouseExited(this));
        this.innerCircle.addEventHandler(MouseEvent.MOUSE_EXITED,(e)->designerController.mouseExited(this));
        this.label.addEventHandler(MouseEvent.MOUSE_EXITED,(e)->designerController.mouseExited(this));
    }

    @Override
    public void observableModified(Observable observable) {
        if(observable==state){
            Point2D position = this.designerController.toViewSpace(state.getX(), state.getY());
            this.setLayoutX(position.getX());
            this.setLayoutY(position.getY());
            this.innerCircle.setVisible(state.isFinalState());
        }
    }

    private void stateClicked(MouseEvent mouseEvent){
        if(mouseEvent.getClickCount()==2){
            new ToggleState(this).commit(true);
        }
        designerController.selectElement(this,!mouseEvent.isShiftDown());
        mouseEvent.consume();
    }

    private void statePressed(MouseEvent event){
        pressedX=getLayoutX();
        pressedY=getLayoutY();
        lastModelDx=0;
        lastModelDy=0;
        if(event.isShiftDown()){
            currentlyEditedTransition=new TransitionView(designerController,this);
            this.exitedThisStateAtLeastOnceWhileEditing =false;
            designerController.getDesigner().getChildren().add(currentlyEditedTransition);
            System.out.println("Made transition");
            event.consume();
        }else{
            designerController.selectElement(this);
        }
    }

    private void stateDragged(MouseEvent event){
        double sceneX=event.getSceneX();
        double sceneY=event.getSceneY();
        Point2D positionInsideDesigner=designerController.getDesigner().sceneToLocal(sceneX,sceneY);

        if(event.isShiftDown() && currentlyEditedTransition!=null){
            //check if any state contains the point being dragged
            List<StateView> stateViewList = designerController.getStateViewList();
            StateView pointedState=null;
            for(StateView stateView : stateViewList){
                Point2D localPoint= stateView.parentToLocal(positionInsideDesigner);
                if(stateView.contains(localPoint)){
                    pointedState=stateView;
                    break;
                }
            }

            if((pointedState!=null)&&((pointedState!=this)||(exitedThisStateAtLeastOnceWhileEditing))){
                currentlyEditedTransition.setFinalStateView(pointedState);
            }else{
                currentlyEditedTransition.setFinalStateView(null);
                currentlyEditedTransition.setEndingPosition(positionInsideDesigner);
                exitedThisStateAtLeastOnceWhileEditing =true;
            }

        }else{
            changePositionAndShiftOtherSelectedStates(positionInsideDesigner);
        }
        event.consume();
    }

    private void changePositionAndShiftOtherSelectedStates(Point2D positionInsideDesigner) {

        //first change the position of the this state so we have the latest layout x,y
        Point2D modelPosition=designerController.toModelSpace(positionInsideDesigner);
        state.setPosition(modelPosition.getX(),modelPosition.getY());

        //after setting the position of this model, compute the dx and dy
        double modelDx=designerController.lengthInModalSpace(getLayoutX()-pressedX);
        double modelDy=designerController.lengthInModalSpace(getLayoutY()-pressedY);

        //also do it for all the existing selected states
        for (DesignerElementView elementView : designerController.getSelectedElements()) {
            if(elementView instanceof StateView && elementView!=this){

                //ddd the dx and dy and the observers will respond accordingly
                StateView stateView=(StateView)elementView;
                double x = stateView.getState().getX();
                double y = stateView.getState().getY();
                stateView.getState().setPosition(x+(modelDx-lastModelDx),y+(modelDy-lastModelDy));
            }
        }
        lastModelDx=modelDx;
        lastModelDy=modelDy;
    }


    private void stateReleased(MouseEvent event){
        if(currentlyEditedTransition!=null){
            if((currentlyEditedTransition.getFinalStateView()==null)||
                (this.containsTransition(currentlyEditedTransition.getFinalStateView())!=null)){
                //remove this transition
                designerController.getDesigner().getChildren().remove(currentlyEditedTransition);
                System.out.println("Dismissing adding a new transition");
            }else{
                System.out.println("adding new transition to transition list");
                //put it in this views' map
                this.transitionViewMap.put(
                        currentlyEditedTransition.getFinalStateView().getState().getName(),
                        currentlyEditedTransition);

                //also put it in the map of the model
                state.getOutgoingTransitionMap().put(
                        currentlyEditedTransition.getFinalStateView().getState().getName(),
                        currentlyEditedTransition.getTransition());
            }
            currentlyEditedTransition=null;
        }else{

            if (!(getLayoutX()-pressedX==0 && getLayoutY()-pressedY==0)) {
                new MoveStates(
                        designerController.getOnlySelectedStates(),
                        getLayoutX()-pressedX,
                        getLayoutY()-pressedY).commit(false);
            }
        }
    }

    private TransitionView containsTransition(StateView finalStateView){
        Collection<TransitionView> allOutgoingTransitions = transitionViewMap.values();
        for (TransitionView transitionView : allOutgoingTransitions){
            if(transitionView.getInitialStateView() == this){
                if(transitionView.getFinalStateView() == finalStateView) {
                    return transitionView;
                }
            }else{
                throw new RuntimeException("Transition is not pointing to this state as starting state");
            }
        }
        return null;
    }

    public State getState() {
        return state;
    }

    @Override
    public void setColor(Color color) {
        this.innerCircle.setStroke(color);
        this.outerCircle.setStroke(color);
        //intentionally doesn't do it to the label
    }

    public DesignerController getDesignerController() {
        return designerController;
    }
}
