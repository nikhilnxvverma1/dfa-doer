package com.madebynikhil.editor.controller;

import com.madebynikhil.editor.command.ChangeStartState;
import com.madebynikhil.editor.command.CreateDesignElement;
import com.madebynikhil.editor.command.DeleteSelection;
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

    private static final double INITIAL_ZOOM=0;
    private Workspace workspace;
    private Pane designer;
    private double zoom=INITIAL_ZOOM;
    private List<StateView> stateViewList=new LinkedList<>();
    private TransitionView startArrowView;
    private TransitionView currentlyEditedStartArrow;
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
            new CreateDesignElement(new StateView(this,newState)).commit(true);
        }else{
            clearSelection();
        }
    }

    public void handleMousePress(MouseEvent event){
        if(event.isShiftDown()){
            if (currentlyEditedStartArrow==null) {
                currentlyEditedStartArrow=new TransitionView(this);
                designer.getChildren().add(currentlyEditedStartArrow);
            }
            currentlyEditedStartArrow.setStartingPosition(new Point2D(event.getX(),event.getY()));

        }
    }

    public void handleMouseDrag(MouseEvent event){

        if(event.isShiftDown() && currentlyEditedStartArrow!=null){

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
                currentlyEditedStartArrow.setFinalStateView(pointedState);
            }else{
                currentlyEditedStartArrow.setFinalStateView(null);
                currentlyEditedStartArrow.setEndingPosition(position);
            }
        }
    }

    public void handleMouseRelease(MouseEvent event){
        if(currentlyEditedStartArrow!=null){
            if(currentlyEditedStartArrow.getFinalStateView()==null){
                //remove this transition
                designer.getChildren().remove(currentlyEditedStartArrow);
                currentlyEditedStartArrow=null;
            }else{
                new ChangeStartState(startArrowView,currentlyEditedStartArrow).commit(true);
            }
        }
        currentlyEditedStartArrow=null;
    }

    public void deleteSelectedElements(){
        new DeleteSelection(this).commit(true);
    }

    public void setStartArrowPositionAndLength() {
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

    public TransitionView getCurrentlyEditedStartArrow() {
        return currentlyEditedStartArrow;
    }

    public TransitionView getStartArrowView() {
        return startArrowView;
    }

    public void mouseEntered(DesignerElementView elementView){
        if (!selectedElements.contains(elementView) &&
                !workspace.getRunController().isOpen()) {
            elementView.setColor(DesignerElementView.HOVER_COLOR);
        }
    }

    public void mouseExited(DesignerElementView elementView){
        if (!selectedElements.contains(elementView)) {
            elementView.setColor(DesignerElementView.DEFAULT_COLOR);
        }
    }

    public void selectElement(DesignerElementView elementView,boolean clearExisting){
        if(!selectedElements.contains(elementView)){
            if(clearExisting){
                clearSelection();
            }
            this.selectedElements.add(elementView);
            elementView.setColor(DesignerElementView.SELECTED_COLOR);
        }
    }

    public void selectElement(DesignerElementView elementView){
        this.selectElement(elementView,true);
    }

    public void unSelectElement(DesignerElementView elementView){
        if(selectedElements.contains(elementView)){
            this.selectedElements.remove(elementView);
            elementView.setColor(DesignerElementView.DEFAULT_COLOR);
        }
    }

    public void clearSelection(){

        for(DesignerElementView elementView: this.selectedElements){
            elementView.setColor(DesignerElementView.DEFAULT_COLOR);
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

        //create transitions from the above generated state view map
        for(State state: stateMachine.getStateList()){

            //create a reverse map i.e. where keys are states and values are symbols
            Set<Map.Entry<String, String>> symbolToState = state.getOutgoingTransitionMap().entrySet();
            Map <String, List<String>> stateToSymbol = new HashMap<String,List<String>>();
            for(Map.Entry<String, String> entry : symbolToState){
                String outgoingSymbol= entry.getKey();
                String outgoingStateName = entry.getValue();

                List<String> correspondingSymbolList=stateToSymbol.get(outgoingStateName);
                if(correspondingSymbolList==null){
                    correspondingSymbolList=new LinkedList<>();
                    stateToSymbol.put(outgoingStateName,correspondingSymbolList);
                }
                correspondingSymbolList.add(outgoingSymbol);
            }

            //for each state that this state outgoes to, make a transition view
            for (Map.Entry<String, List<String>> stateFromSymbols : stateToSymbol.entrySet()) {
                StateView finalStateView=stateViewMap.get(stateFromSymbols.getKey());
                List<String> symbolList = stateFromSymbols.getValue();

                TransitionView transitionView=new TransitionView(this,
                        stateViewMap.get(state.getName()),
                        finalStateView);
                transitionView.setSymbolList(symbolList);
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

    public void setStartArrowView(TransitionView startArrowView) {
        this.startArrowView = startArrowView;
    }
}
