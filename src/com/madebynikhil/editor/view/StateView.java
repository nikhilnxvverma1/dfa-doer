package com.madebynikhil.editor.view;

import com.madebynikhil.editor.DesignerController;
import com.madebynikhil.model.State;
import com.madebynikhil.observer.Observable;
import com.madebynikhil.observer.Observer;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;


/**
 * View corresponding to one state that this is observing.
 * Created by NikhilVerma on 02/11/16.
 */
public class StateView extends Group implements Observer{

    public static final double STATE_RADIUS=10;
    private static final double STATE_INNER_RADIUS=8;
    private DesignerController designerController;
    private State state;

    private Circle outerCircle;
    private Circle innerCircle;
    private Label label;

    public StateView(DesignerController designerController, State state) {
        this.designerController = designerController;
        this.state = state;
        this.state.subscribe(this);

    }

    private void initView(){
        this.outerCircle=new Circle(STATE_RADIUS);
        this.innerCircle=new Circle(STATE_INNER_RADIUS);
        this.getChildren().add(this.outerCircle);
        this.getChildren().add(this.innerCircle);
    }

    @Override
    public void observableModified(Observable observable) {
        if(observable==state){
            this.setLayoutX(state.getX());
            this.setLayoutY(state.getY());
        }
    }
}
