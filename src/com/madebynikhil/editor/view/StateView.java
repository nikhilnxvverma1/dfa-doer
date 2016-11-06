package com.madebynikhil.editor.view;

import com.madebynikhil.editor.DesignerController;
import com.madebynikhil.model.State;
import com.madebynikhil.observer.Observable;
import com.madebynikhil.observer.Observer;
import com.madebynikhil.util.Point;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


/**
 * View corresponding to one state that this is observing.
 * Created by NikhilVerma on 02/11/16.
 */
public class StateView extends Group implements Observer{

    public static final double STATE_RADIUS=25;
    private static final double STATE_INNER_RADIUS=20;
    private DesignerController designerController;
    private State state;

    private Circle outerCircle;
    private Circle innerCircle;
    private Label label;

    public StateView(DesignerController designerController, State state) {
        this.designerController = designerController;
        this.designerController.subscribe(this);
        this.state = state;
        this.state.subscribe(this);
        this.initView();
        this.setupEvents();
    }

    private void initView(){
        this.outerCircle=new Circle(STATE_RADIUS,Color.WHITE);
        this.outerCircle.setStroke(Color.BLACK);
        this.innerCircle=new Circle(STATE_INNER_RADIUS,Color.WHITE);
        this.innerCircle.setVisible(state.isFinalState());
        this.innerCircle.setStroke(Color.BLACK);
        this.getChildren().add(this.outerCircle);
        this.getChildren().add(this.innerCircle);
        Point2D position = this.designerController.toViewSpace(state.getX(), state.getY());
        this.setLayoutX(position.getX());
        this.setLayoutY(position.getY());
    }

    private void setupEvents(){
        this.outerCircle.addEventHandler(MouseEvent.MOUSE_CLICKED,this::stateClicked);
        this.innerCircle.addEventHandler(MouseEvent.MOUSE_CLICKED,this::stateClicked);

        this.outerCircle.addEventHandler(MouseEvent.MOUSE_PRESSED,this::statePressed);
        this.innerCircle.addEventHandler(MouseEvent.MOUSE_PRESSED,this::statePressed);

        this.outerCircle.addEventHandler(MouseEvent.MOUSE_DRAGGED,this::stateDragged);
        this.innerCircle.addEventHandler(MouseEvent.MOUSE_DRAGGED,this::stateDragged);

        this.outerCircle.addEventHandler(MouseEvent.MOUSE_RELEASED,this::stateReleased);
        this.innerCircle.addEventHandler(MouseEvent.MOUSE_RELEASED,this::stateReleased);
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
            mouseEvent.consume();
        }
    }

    private void statePressed(MouseEvent event){

    }

    private void stateDragged(MouseEvent event){
        System.out.println("Handling state drag");
        double sceneX=event.getSceneX();
        double sceneY=event.getSceneY();
        Point2D positionInsideDesigner=designerController.getDesigner().sceneToLocal(sceneX,sceneY);
        Point2D modelPosition=designerController.toModelSpace(positionInsideDesigner);
        state.setPosition(modelPosition.getX(),modelPosition.getY());
        event.consume();
    }

    private void stateReleased(MouseEvent event){

    }
}
