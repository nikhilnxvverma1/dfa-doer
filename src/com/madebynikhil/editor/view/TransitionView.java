package com.madebynikhil.editor.view;

import com.madebynikhil.editor.DesignerController;
import com.madebynikhil.model.Transition;
import com.madebynikhil.observer.Observable;
import com.madebynikhil.observer.Observer;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.LinkedList;
import java.util.List;

/**
 * This view shows a pointing arrow between 2 states.
 * Transition views can be shared between 2 or more transitions.
 * Created by NikhilVerma on 02/11/16.
 */
public class TransitionView extends Group implements Observer{

    private DesignerController designerController;
    private List<Transition> transitionList=new LinkedList<>();

    private Line line;
    private Polygon arrowHead;
    private Label label;

    public TransitionView(DesignerController designerController) {
        this.designerController = designerController;
    }

    @Override
    public void observableModified(Observable observable) {

    }
}
