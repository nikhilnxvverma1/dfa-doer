package com.madebynikhil.editor.view;

import com.madebynikhil.editor.controller.DesignerController;
import com.madebynikhil.editor.controller.Workspace;
import com.madebynikhil.model.Transition;
import com.madebynikhil.observer.Observable;
import com.madebynikhil.observer.Observer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.Arrays;
import java.util.List;

/**
 * This view shows a pointing arrow between 2 states.
 * Transition views can be shared between 2 or more transitions.
 * This class can also be used as the starting arrow to indicate
 * the starting state.
 * Created by NikhilVerma on 02/11/16.
 */
public class TransitionView extends DesignerElementView implements Observer{

    public static final double START_ARROW=70;
    private static final double ARROW_WIDTH=10;
    private static final double ARROW_HEIGHT=10;
    private DesignerController designerController;
    private StateView initialStateView;
    private StateView finalStateView;
    private Transition transition;

    private Line line;
    private Polygon arrowHead;
    private Label label;
    private TextField textField;
    private Point2D startingPosition;
    private Point2D endingPosition;

    public TransitionView(DesignerController designerController){
        this.designerController = designerController;
        this.initView();
        this.setupEvents();
    }

    public TransitionView(DesignerController designerController,StateView initialStateView) {
        this.designerController = designerController;
        this.initialStateView = initialStateView;
        this.initialStateView.getState().subscribe(this);
        this.transition=createBlankTransition(initialStateView);
        this.initView();
        this.setupEvents();
        setStartingPosition(new Point2D(initialStateView.getLayoutX(),initialStateView.getLayoutY()));
    }

    public TransitionView(DesignerController designerController,StateView initialStateView,StateView finalStateView) {
        this.designerController = designerController;
        this.initialStateView = initialStateView;
        this.finalStateView = finalStateView;
        this.initialStateView.getState().subscribe(this);
        this.finalStateView.getState().subscribe(this);
        this.transition=initialStateView.getState().getOutgoingTransitionMap().get(finalStateView.getState().getName());
        this.initView();
        this.setupEvents();
        recomputeEndpointsBasedOnStatePositions();
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

        this.textField=new TextField(getLabelBasedOnSymbolList());
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
        this.getChildren().addAll(this.line,this.arrowHead,this.textField);
    }

    private String getLabelBasedOnSymbolList(){
        if(transition==null){//case for start arrow
            return null;
        }else{
            if(transition.getSymbolList().size()>0){
                return Workspace.getSymbolsAsCSV(transition.getSymbolList());
            }else{
                return "?";
            }
        }
    }

    private void setupEvents(){

        this.textField.setOnMouseClicked(this::editTransitions);
        this.textField.setOnAction(this::commitEditingTransitions);

        this.line.addEventHandler(MouseEvent.MOUSE_ENTERED,(e)->designerController.mouseEntered(this));
        this.textField.addEventHandler(MouseEvent.MOUSE_ENTERED,(e)->designerController.mouseEntered(this));
        this.arrowHead.addEventHandler(MouseEvent.MOUSE_ENTERED,(e)->designerController.mouseEntered(this));

        this.line.addEventHandler(MouseEvent.MOUSE_EXITED,(e)->designerController.mouseExited(this));
        this.textField.addEventHandler(MouseEvent.MOUSE_EXITED,(e)->designerController.mouseExited(this));
        this.arrowHead.addEventHandler(MouseEvent.MOUSE_EXITED,(e)->designerController.mouseExited(this));

        EventHandler<MouseEvent> selectThis=(e)->{
            designerController.selectElement(this,!e.isShiftDown());
            e.consume();
        };

        this.line.addEventHandler(MouseEvent.MOUSE_CLICKED,selectThis);
        this.arrowHead.addEventHandler(MouseEvent.MOUSE_CLICKED,selectThis);

    }

    private void editTransitions(MouseEvent event) {
//        if(event.getClickCount()<2){
//            return;
//        }
        this.textField.requestFocus();
        this.textField.setStyle("" +
                "-fx-background-color: #EEE;" +
                "    -fx-background-insets: 0;" +
                "    -fx-padding: 1 3 1 3;");
    }

    private void commitEditingTransitions(ActionEvent event) {
        String text=textField.getText();
        String [] symbols=text.split(",");
        System.out.println("Revising transitions");//TODO validate first
        List<String> symbolList = Arrays.asList(symbols);
        this.transition.setSymbolList(symbolList);
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
            finalStateView.getState().subscribe(this);
            if (transition!=null) {
                transition.setTo(finalStateView.getState());
            }
        }else if(transition!=null){
            transition.setTo(null);
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

    @Override
    public void setColor(Color color) {
        this.line.setStroke(color);
        this.arrowHead.setFill(color);
    }

    public Transition getTransition() {
        return transition;
    }

    public DesignerController getDesignerController() {
        return designerController;
    }


}
