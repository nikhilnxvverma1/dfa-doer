package com.madebynikhil.editor.view;

import com.madebynikhil.editor.DesignerController;
import com.madebynikhil.model.State;
import com.madebynikhil.observer.Observable;
import com.madebynikhil.observer.Observer;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 * Arrow that points to the starting state
 * Created by NikhilVerma on 02/11/16.
 */
public class StartArrowView extends Group implements Observer{

    private DesignerController designerController;
    private State startingState;

    private Line line;
    private Polygon arrowHead;

    @Override
    public void observableModified(Observable observable) {

    }
}
