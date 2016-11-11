package com.madebynikhil.editor.controller;

import com.madebynikhil.editor.view.DesignerElementView;
import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.editor.view.TransitionView;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.Collection;
import java.util.List;

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
        workspace.getMainWindowController().getRunString().setDisable(open);
        workspace.getDesignerController().runAreaOpened(open);
        openTestInputEditing(true);
    }

    public void changePlaybackSpeed(double sliderValue){
        delay=MAX_DELAY - sliderValue*(MAX_DELAY-MIN_DELAY);
    }

    public void setNewTest(String test) {
        this.test = workspace.getMainWindowController().getTestInput().getText();
        System.out.println("new Test string is "+test);
        openTestInputEditing(false);
    }

    public void cancelledNewTest() {
        //TODO check with existing string
        openTestInputEditing(false);
    }

    public void openTestInputEditing(boolean openForEditing){

        workspace.getMainWindowController().getEditTestInput().setDisable(openForEditing);
        displayNone(workspace.getMainWindowController().getTestInput(),openForEditing);
        displayNone(workspace.getMainWindowController().getTestInputOk(),openForEditing);
        displayNone(workspace.getMainWindowController().getTestInputCancel(),openForEditing);

        if(openForEditing){
            workspace.getMainWindowController().getTestInput().requestFocus();
        }
    }

    private void displayNone(Node node,boolean display){
        node.setManaged(display);
        node.setVisible(display);
    }
}
