package com.madebynikhil.editor.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;

/**
 * Abstract class for views in the in the designer. Most Notable subviews include
 * StateView and TransitionView.
 * Created by NikhilVerma on 07/11/16.
 */
public abstract class DesignerElementView extends Group{
    public abstract void setColor(Color color);
}
