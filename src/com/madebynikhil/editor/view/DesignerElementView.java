package com.madebynikhil.editor.view;

import com.madebynikhil.editor.controller.DesignerController;
import javafx.scene.Group;
import javafx.scene.paint.Color;

/**
 * Abstract class for views in the in the designer. Most Notable subviews include
 * StateView and TransitionView.
 * Created by NikhilVerma on 07/11/16.
 */
public abstract class DesignerElementView extends Group{
    protected DesignerController designerController;

    public DesignerElementView(DesignerController designerController) {
        this.designerController = designerController;
    }

    public DesignerController getDesignerController() {
        return designerController;
    }

    public abstract void setColor(Color color);
    public abstract void removeFromModelAndView();
    public abstract void integrateInModelAndView();
}
