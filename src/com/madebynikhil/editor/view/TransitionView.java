package com.madebynikhil.editor.view;

import com.madebynikhil.editor.DesignerController;
import com.madebynikhil.model.State;
import com.madebynikhil.model.Transition;
import com.madebynikhil.observer.Observable;
import com.madebynikhil.observer.Observer;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * This view shows a pointing arrow between 2 states.
 * Transition views can be shared between 2 or more transitions.
 * This class can also be used as the starting arrow to indicate
 * the starting state.
 * Created by NikhilVerma on 02/11/16.
 */
public class TransitionView extends Group implements Observer{

    public static final double START_ARROW=70;
    private static final double ARROW_WIDTH=10;
    private static final double ARROW_HEIGHT=10;
    private DesignerController designerController;
    private StateView initialStateView;
    private StateView finalStateView;
    private List<Transition> transitionList=new LinkedList<>();

    private Line line;
    private Polygon arrowHead;
    private Label label;
    private TextField textField;
    private Point2D startingPosition;
    private Point2D endingPosition;

    public TransitionView(DesignerController designerController){
        this.designerController = designerController;
        this.initView();
    }

    public TransitionView(DesignerController designerController,StateView initialStateView) {
        this.designerController = designerController;
        this.initialStateView = initialStateView;
        this.initialStateView.getState().subscribe(this);
        this.transitionList.add(createBlankTransition(initialStateView));
        this.initView();
        setStartingPosition(new Point2D(initialStateView.getLayoutX(),initialStateView.getLayoutY()));
    }

    private Transition createBlankTransition(StateView initialState){
        Transition transition=new Transition(initialState.getState(),null);
        return transition;
    }

    private void initView() {
        this.line=new Line();
        this.line.setStartX(0);
        this.line.setStartY(0);
        this.arrowHead=new Polygon();
        this.setArrowHeadPoints();
//        this.label=new Label("?");
//        this.label.setCursor(Cursor.HAND);
//        this.label.setTranslateY(-20);
        this.textField=new TextField("?");
        this.textField.setStyle("" +
                "-fx-background-color: -fx-control-inner-background;" +
                "    -fx-background-insets: 0;" +
                "    -fx-padding: 1 3 1 3;");
        this.textField.setTranslateY(-25);
        this.textField.setTranslateX(-25);
        this.textField.setMinWidth(50);
        this.textField.setPrefWidth(50);
        this.textField.setMaxWidth(400);
        this.textField.setAlignment(Pos.CENTER);
        this.textField.setFocusTraversable(false);
        this.textField.setOnMouseClicked(this::editTransitions);
        this.textField.setOnAction(this::commitEditingTransitions);
        this.getChildren().addAll(this.line,this.arrowHead,this.textField);
    }

    private void editTransitions(MouseEvent event) {
        this.textField.requestFocus();
        this.textField.setStyle("" +
                "-fx-background-color: #EEE;" +
                "    -fx-background-insets: 0;" +
                "    -fx-padding: 1 3 1 3;");
    }

    private void commitEditingTransitions(ActionEvent event) {
        String text=textField.getText();
        String [] symbols=text.split(",");
        System.out.println("Revising transitions");

        for (String symbol : symbols){
            System.out.print(symbol);
        }

        designerController.getDesigner().requestFocus();
        this.textField.setStyle("" +
                "-fx-background-color: -fx-control-inner-background;" +
                "    -fx-background-insets: 0;" +
                "    -fx-padding: 1 3 1 3;");
    }

    private void setArrowHeadPoints(){
        double width=designerController.lengthInCurrentZoom(TransitionView.ARROW_WIDTH);
        double height=designerController.lengthInCurrentZoom(TransitionView.ARROW_HEIGHT);
        this.arrowHead.getPoints().setAll(
                width/2,0.0,
                -width/2,-height/2,
                -width/2,height/2);

    }

    @Override
    public void observableModified(Observable observable) {
        if(initialStateView != null && observable == initialStateView.getState()){
            recomputeEndpointsBasedOnStatePositions();
        } else if(finalStateView != null && observable == finalStateView.getState()){
            recomputeEndpointsBasedOnStatePositions();
        }
    }

    public Point2D getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(Point2D startingPosition) {
        this.startingPosition = startingPosition;
        if(this.endingPosition==null){
            this.endingPosition=this.startingPosition;
        }
        layoutAsPerEndpoints();
    }

    public Point2D getEndingPosition() {
        return endingPosition;
    }

    public void setEndingPosition(Point2D endingPosition) {
        this.endingPosition = endingPosition;
        if(this.startingPosition==null){
            this.startingPosition=this.endingPosition;
        }else if(initialStateView!=null){

            //make the starting position fall on the circumference on initial state
            double radius=designerController.lengthInCurrentZoom(StateView.STATE_RADIUS);

            Point2D from=new Point2D(initialStateView.getLayoutX(),initialStateView.getLayoutY());

            double angle=angleOfSegment(from,endingPosition);
            this.startingPosition=pointAtLength(from,angle,radius);
        }
        layoutAsPerEndpoints();
    }

    public void setEndpoints(Point2D startingPosition,Point2D endingPosition){
        this.startingPosition=startingPosition;
        this.endingPosition=endingPosition;
        layoutAsPerEndpoints();
    }

    private void layoutAsPerEndpoints() {
        this.setLayoutX(startingPosition.getX());
        this.setLayoutY(startingPosition.getY());
        double diffX = endingPosition.getX() - startingPosition.getX();
        double diffY = endingPosition.getY() - startingPosition.getY();
        this.line.setEndX(diffX);
        this.line.setEndY(diffY);
        this.arrowHead.setLayoutX(diffX);
        this.arrowHead.setLayoutY(diffY);
        double angle = angleOfSegment(startingPosition, endingPosition);
        this.arrowHead.setRotate(angle);
//        this.label.setRotate(angle);
//        this.label.setLayoutX(diffX/2);
//        this.label.setLayoutY(diffY/2);
        this.textField.setRotate(angle);
        this.textField.setLayoutX(diffX/2);
        this.textField.setLayoutY(diffY/2);
        this.textField.setVisible(this.initialStateView!=null && finalStateView!=null);
    }

    public void setFinalStateView(StateView finalStateView){
        if(this.finalStateView!=null){
           this.finalStateView.getState().unsubscribe(this);
        }
        this.finalStateView=finalStateView;
        if (finalStateView!=null) {
            this.setFinalStateToAllTransitions(finalStateView.getState());
            finalStateView.getState().subscribe(this);
        }else{
            this.setFinalStateToAllTransitions(null);
        }

        recomputeEndpointsBasedOnStatePositions();
    }

    public void recomputeEndpointsBasedOnStatePositions(){
        if(finalStateView != null) {
            if(initialStateView != null){
                double radius=designerController.lengthInCurrentZoom(StateView.STATE_RADIUS);

                Point2D from=new Point2D(initialStateView.getLayoutX(),initialStateView.getLayoutY());
                Point2D to=new Point2D(finalStateView.getLayoutX(),finalStateView.getLayoutY());

                double angle=angleOfSegment(from,to);
                Point2D initialCircumferencePoint=pointAtLength(from,angle,radius);

                double reverseAngle=angleOfSegment(to,from);
                Point2D finalCircumferencePoint=pointAtLength(to,reverseAngle,radius);

                setEndpoints(initialCircumferencePoint,finalCircumferencePoint);

            }else if (!(designerController.isCurrentlyEditingStartArrow()&&
                    this==designerController.getStartArrowView())){
                double length=designerController.lengthInCurrentZoom(TransitionView.START_ARROW);
                double radius=designerController.lengthInCurrentZoom(StateView.STATE_RADIUS);
                Point2D endingPosition = getEndingPosition();
                Point2D newEndingPosition=new Point2D(
                        finalStateView.getLayoutX()-radius,
                        finalStateView.getLayoutY()
                );
                Point2D startingPosition=new Point2D(
                        newEndingPosition.getX()-length,
                        finalStateView.getLayoutY()
                );
                setEndpoints(startingPosition,newEndingPosition);
            }
        }
    }

    private void setFinalStateToAllTransitions(State finalState){
        for (Transition transition : transitionList){
            transition.setTo(finalState);
        }
    }

    private double angleOfSegment(Point2D from,Point2D to){
        double inDegrees=0;
        if (to.getX() - from.getX() == 0) {
            inDegrees = 90;
            if(to.getY()<from.getY()){
                inDegrees+=180;
            }
        }else{
            double slope=(double)(to.getY()-from.getY())/(to.getX()-from.getX());
            inDegrees=toDegrees(Math.atan(slope));
            //angle is between +90 and -90
            if(to.getY()>from.getY()){
                if(to.getX()>from.getX()){//first quadrant
                    //do nothing
                }else{//second quadrant
                    inDegrees+=180;
                }
            }else{
                if(to.getX()<from.getX()){//third quadrant
                    inDegrees+=180;
                }else{//fourth quadrant
                    inDegrees+=360;
                }
            }
        }
        return inDegrees;
    }

    private Point2D pointAtLength(Point2D start, double angleInDegrees, double length){
        return new Point2D(
            start.getX()+length*Math.cos(toRadians(angleInDegrees)),
            start.getY()+length*Math.sin(toRadians(angleInDegrees)));

    }

    private double toDegrees(double radians){
        return radians*(180/3.14);
    }

    private double toRadians(double degree){
        return degree*(3.14/180);
    }

    public StateView getInitialStateView() {
        return initialStateView;
    }

    public StateView getFinalStateView() {
        return finalStateView;
    }
}
