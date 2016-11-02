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

    private DesignerController designerController;
    private State state;

    private Circle outerCircle;
    private Circle innerCircle;
    private Label label;

    public StateView(DesignerController designerController, State state) {
        this.designerController = designerController;
        this.state = state;
    }

    @Override
    public void observableModified(Observable observable) {

    }
}
