package com.madebynikhil.editor;

import com.madebynikhil.editor.view.StartArrowView;
import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.editor.view.TransitionView;
import com.madebynikhil.model.State;
import com.madebynikhil.model.Transition;
import com.madebynikhil.observer.Observable;
import com.madebynikhil.util.Point;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.List;

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
    private boolean currentlyEditingStartArrow=false;

    public DesignerController(Workspace workspace, Pane designer) {
        this.workspace = workspace;
        this.designer = designer;
    }

    public void handleMouseClicked(MouseEvent mouseEvent){
        if(mouseEvent.getClickCount()==2){
            System.out.println("mouse double clicked");
            State newState=new State(mouseEvent.getX(),mouseEvent.getY());
            StateView stateView=new StateView(this,newState);
            this.designer.getChildren().add(stateView);
            this.stateViewList.add(stateView);

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
            System.out.println("Comming here");
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
        }
        currentlyEditingStartArrow=false;
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
}
