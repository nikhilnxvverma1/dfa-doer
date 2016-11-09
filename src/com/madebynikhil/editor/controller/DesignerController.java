package com.madebynikhil.editor.controller;

import com.madebynikhil.editor.view.DesignerElementView;
import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.editor.view.TransitionView;
import com.madebynikhil.model.State;
import com.madebynikhil.model.StateMachine;
import com.madebynikhil.model.Transition;
import com.madebynikhil.observer.Observable;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Responsible for all GUI based designing of the state diagram.
 * Created by NikhilVerma on 02/11/16.
 */
public class DesignerController extends Observable{

    public static final Color DEFAULT_COLOR=Color.BLACK;
    public static final Color HOVER_COLOR=Color.LIGHTBLUE;
    public static final Color SELECTED_COLOR=Color.BLUE;

    private static final double INITIAL_ZOOM=0;
    private Workspace workspace;
    private Pane designer;
    private double zoom=INITIAL_ZOOM;
    private List<StateView> stateViewList=new LinkedList<>();
    private TransitionView startArrowView;
    private boolean currentlyEditingStartArrow=false;
    private LinkedList<DesignerElementView> selectedElements=new LinkedList<>();

    public DesignerController(Workspace workspace, Pane designer) {
        this.workspace = workspace;
        this.designer = designer;
    }

    public void handleMouseClicked(MouseEvent mouseEvent){
        if(mouseEvent.getClickCount()==2){
            System.out.println("mouse double clicked");
            State newState=new State(mouseEvent.getX(),mouseEvent.getY());
            newState.setName("S"+(stateViewList.size()+1));
            StateView stateView=new StateView(this,newState);
            this.designer.getChildren().add(stateView);
            this.stateViewList.add(stateView);
            this.workspace.getStateMachine().getStateList().add(stateView.getState());
            selectElement(stateView);
        }else{
            clearSelection();
        }
    }

    public void handleMousePress(MouseEvent event){
        if(event.isShiftDown()){
            if (startArrowView==null) {
                startArrowView=new TransitionView(this);
                designer.getChildren().add(startArrowView);
            }
            startArrowView.setStartingPosition(new Point2D(event.getX(),event.getY()));
            currentlyEditingStartArrow=true;
        }
    }

    public void handleMouseDrag(MouseEvent event){

        if(event.isShiftDown() && startArrowView!=null){

            Point2D position = new Point2D(event.getX(), event.getY());

            //check if any state contains the point being dragged
            StateView pointedState=null;
            for(StateView stateView : stateViewList){
                Point2D localPoint= stateView.parentToLocal(position);
                if(stateView.contains(localPoint)){
                    pointedState=stateView;
                    break;
                }
            }

            if(pointedState!=null){
                startArrowView.setFinalStateView(pointedState);
            }else{
                startArrowView.setFinalStateView(null);
                startArrowView.setEndingPosition(position);
            }

        }
    }

    public void handleMouseRelease(MouseEvent event){
        if(startArrowView!=null){
            if(startArrowView.getFinalStateView()==null){
                //remove this transition
                designer.getChildren().remove(startArrowView);
                startArrowView=null;
            }else{

                //new starting state
                this.workspace.getStateMachine().setStartingState(startArrowView.getFinalStateView().getState());
                setStartArrowPositionAndLength();
            }
        }
        currentlyEditingStartArrow=false;
    }

    private void setStartArrowPositionAndLength() {
        double length=lengthInCurrentZoom(TransitionView.START_ARROW);
        double radius=lengthInCurrentZoom(StateView.STATE_RADIUS);
        Point2D endingPosition = startArrowView.getEndingPosition();
        Point2D startingPosition=new Point2D(
                endingPosition.getX()-length,
                startArrowView.getFinalStateView().getLayoutY()
                );
        Point2D newEndingPosition=new Point2D(
                startArrowView.getFinalStateView().getLayoutX()-radius,
                startArrowView.getFinalStateView().getLayoutY()
        );
        startArrowView.setEndpoints(startingPosition,newEndingPosition);
    }

    public Point2D toModelSpace(Point2D point){
        return toModelSpace(point.getX(),point.getY());
    }

    public Point2D toModelSpace(double x, double y){
        return new Point2D(x,y);
    }

    public Point2D toViewSpace(Point2D point){
        return toViewSpace(point.getX(),point.getY());
    }

    public Point2D toViewSpace(double x,double y){
        return new Point2D(x,y);
    }

    public double lengthInModalSpace(double length){
        return length;
    }

    public double lengthInViewSpace(double length){
        return length;
    }

    public double lengthInCurrentZoom(double initialZoomLength){
        return initialZoomLength;
    }


    public Workspace getWorkspace() {
        return workspace;
    }

    public Pane getDesigner() {
        return designer;
    }

    public List<StateView> getStateViewList() {
        return stateViewList;
    }

    public boolean isCurrentlyEditingStartArrow() {
        return currentlyEditingStartArrow;
    }

    public TransitionView getStartArrowView() {
        return startArrowView;
    }

    public void mouseEntered(DesignerElementView elementView){
        if (!selectedElements.contains(elementView)) {
            elementView.setColor(HOVER_COLOR);
        }
    }

    public void mouseExited(DesignerElementView elementView){
        if (!selectedElements.contains(elementView)) {
            elementView.setColor(DEFAULT_COLOR);
        }
    }

    public void selectElement(DesignerElementView elementView,boolean clearExisting){
        if(!selectedElements.contains(elementView)){
            if(clearExisting){
                clearSelection();
            }
            this.selectedElements.add(elementView);
            elementView.setColor(SELECTED_COLOR);
        }
    }

    public void selectElement(DesignerElementView elementView){
        this.selectElement(elementView,true);
    }

    public void unSelectElement(DesignerElementView elementView){
        if(selectedElements.contains(elementView)){
            this.selectedElements.remove(elementView);
            elementView.setColor(DEFAULT_COLOR);
        }
    }

    private void clearSelection(){

        for(DesignerElementView elementView: this.selectedElements){
            elementView.setColor(DEFAULT_COLOR);
        }
        selectedElements.clear();
    }

    public void initView() {
        StateMachine stateMachine=workspace.getStateMachine();

        //build a map of the state views
        Map<String,StateView> stateViewMap=new HashMap<>();

        //for each state in the state machine make a corresponding view
        for(State state : stateMachine.getStateList()){
            StateView stateView=new StateView(this,state);
            stateViewMap.put(state.getName(),stateView);
            stateViewList.add(stateView);
            designer.getChildren().add(stateView);
        }

        //create transitions fromt the above generated state view map
        for(State state: stateMachine.getStateList()){

            //for each outgoing transition of this state, create a transition view
            Set<Map.Entry<String, Transition>> entries = state.getOutgoingTransitionMap().entrySet();
            for(Map.Entry<String, Transition> entry : entries){
                String outgoingStateName = entry.getKey();

                TransitionView transitionView=new TransitionView(this,
                        stateViewMap.get(state.getName()),
                        stateViewMap.get(outgoingStateName));
                designer.getChildren().add(transitionView);
            }
        }

        //create starting transition if it exists
        if(stateMachine.getStartingState()!=null){
            startArrowView=new TransitionView(this);
            startArrowView.setFinalStateView(stateViewMap.get(stateMachine.getStartingState().getName()));
            setStartArrowPositionAndLength();
            designer.getChildren().add(startArrowView);
        }
    }

    public LinkedList<DesignerElementView> getSelectedElements() {
        return selectedElements;
    }

    public List<StateView> getOnlySelectedStates(){
        LinkedList<StateView> selectedStates=new LinkedList<>();
        for (DesignerElementView selectedElement : selectedElements) {
            if(selectedElement instanceof StateView){
                selectedStates.add((StateView)selectedElement);
            }
        }
        return selectedStates;
    }
}