package com.madebynikhil.editor.controller;

import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.editor.view.TransitionView;
import javafx.scene.layout.AnchorPane;

/**
 * All animation and string testing is done using this controller.
 * Created by NikhilVerma on 11/11/16.
 */
public class RunController {

    private static final double MIN_DELAY=10;
    private static final double MAX_DELAY=1000;

    private Workspace workspace;
    private boolean open;

    private AnchorPane animationControls;

    private TransitionView currentTransition;
    private StateView currentState;
    private double delay;

    private String test;
    private int currentIndex;

    public RunController(Workspace workspace) {
        this.workspace=workspace;
        this.animationControls=workspace.getMainWindowController().getAnimationControls();
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        animationControls.setVisible(open);
        animationControls.setManaged(open);
    }
}
