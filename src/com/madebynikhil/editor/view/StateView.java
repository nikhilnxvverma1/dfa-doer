package com.madebynikhil.editor.view;

import com.madebynikhil.editor.DesignerController;
import com.madebynikhil.model.State;
import com.madebynikhil.model.Transition;
import com.madebynikhil.observer.Observable;
import com.madebynikhil.observer.Observer;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import org.omg.CORBA.SystemException;

import java.util.LinkedList;
import java.util.List;


/**
 * View corresponding to one state that this is observing.
 * Created by NikhilVerma on 02/11/16.
 */
public class StateView extends DesignerElementView implements Observer{

    public static final double STATE_RADIUS=25;
    private static final double STATE_INNER_RADIUS=20;
    private DesignerController designerController;
    private State state;

    private Circle outerCircle;
    private Circle innerCircle;
    private Label label;
    private TransitionView currentlyEditedTransition;
    private boolean exitedThisStateAtLeastOnceWhileEditing;
    private List<TransitionView> transitionViewList=new LinkedList<>();

    public StateView(DesignerController designerController, State state) {
        this.designerController = designerController;
        this.designerController.subscribe(this);
        this.state = state;
        this.state.subscribe(this);
        this.initView();
        this.setupEvents();
    }

    private void initView(){
        this.outerCircle=new Circle(designerController.lengthInCurrentZoom(STATE_RADIUS),Color.WHITE);
        this.outerCircle.setStroke(Color.BLACK);
        this.innerCircle=new Circle(designerController.lengthInCurrentZoom(STATE_INNER_RADIUS),Color.WHITE);
        this.innerCircle.setVisible(state.isFinalState());
        this.innerCircle.setStroke(Color.BLACK);
        this.label=new Label(this.state.getLabel());
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
            this.state.setFinalState(!this.state.isFinalState());
        }
        designerController.selectElement(this,!mouseEvent.isShiftDown());
        mouseEvent.consume();
    }

    private void statePressed(MouseEvent event){
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
            Point2D modelPosition=designerController.toModelSpace(positionInsideDesigner);
            state.setPosition(modelPosition.getX(),modelPosition.getY());
        }
        event.consume();
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
                this.transitionViewList.add(currentlyEditedTransition);
            }
            currentlyEditedTransition=null;
        }
    }

    private TransitionView containsTransition(StateView finalStateView){
        for (TransitionView transitionView : transitionViewList){
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
}
