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

    public static final Color DEFAULT_COLOR=Color.BLACK;
    public static final Color HOVER_COLOR=Color.LIGHTBLUE;
    public static final Color SELECTED_COLOR=Color.BLUE;
    public static final Color TEST_HIGHLIGHT_COLOR=Color.YELLOW;//new Color(167.0/255,97.0/255,9.0/255,1);
    public static final Color TEST_FAIL_COLOR=Color.RED;
    public static final Color TEST_PASS_COLOR=Color.GREEN;


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
