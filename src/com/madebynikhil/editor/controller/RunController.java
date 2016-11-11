package com.madebynikhil.editor.controller;

import com.madebynikhil.editor.view.DesignerElementView;
import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.editor.view.TransitionView;
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
        if(open){
            setDesignerElementViewsToDefaultColor();
        }
        workspace.getDesignerController().clearSelection();
    }

    private void setDesignerElementViewsToDefaultColor(){

        TransitionView startArrowView = workspace.getDesignerController().getStartArrowView();
        if(startArrowView!=null){
            startArrowView.setColor(DesignerElementView.DEFAULT_COLOR);
        }

        List<StateView> stateViewList = workspace.getDesignerController().getStateViewList();

        for (StateView stateView : stateViewList) {
            stateView.setColor(DesignerElementView.DEFAULT_COLOR);

            //also set that color to all outgoing edges
            Collection<TransitionView> decidedOutoingEdges = stateView.getTransitionViewMap().values();
            for (TransitionView transitionView : decidedOutoingEdges) {
                transitionView.setColor(DesignerElementView.DEFAULT_COLOR);
            }

            Collection<TransitionView> unDecidedOutoingEdges = stateView.getUndecidedTransitionViewMap().values();
            for (TransitionView transitionView : unDecidedOutoingEdges) {
                transitionView.setColor(DesignerElementView.DEFAULT_COLOR);
            }
        }
    }

    public void changePlaybackSpeed(double sliderValue){
        delay=MIN_DELAY+ sliderValue*(MAX_DELAY-MIN_DELAY);
        System.out.println("delay is "+delay);
    }
}
