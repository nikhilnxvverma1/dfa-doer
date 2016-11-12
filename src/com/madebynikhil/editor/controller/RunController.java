package com.madebynikhil.editor.controller;

import com.madebynikhil.editor.view.DesignerElementView;
import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.editor.view.TestSymbolLabel;
import com.madebynikhil.editor.view.TransitionView;
import com.madebynikhil.model.Transition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * All animation and string testing is done using this controller.
 * Created by NikhilVerma on 11/11/16.
 */
public class RunController {

    private static final double MIN_DELAY=10;
    private static final double MAX_DELAY=1000;
    private static final double SYMBOL_SPACING=10;

    private Workspace workspace;
    private boolean open;

    private AnchorPane animationControls;

    private TransitionView currentTransition;
    private StateView currentState;
    private double delay;

    private String test;
    private int currentIndex;

    private HBox symbolContainer;

    public RunController(Workspace workspace) {
        this.workspace=workspace;
        this.animationControls=workspace.getMainWindowController().getAnimationControls();
        symbolContainer = new HBox(SYMBOL_SPACING);
        symbolContainer.setAlignment(Pos.CENTER_LEFT);
        workspace.getMainWindowController().getTestStringContainer().getChildren().add(symbolContainer);
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
        makeSymbolLabelsFor(this.test);
    }

    private void makeSymbolLabelsFor(String string){
        //remove all existing labels from the hbox
        symbolContainer.getChildren().clear();

        //create new symbol labels and add them to the list
        int length = string.length();
        for(int i=0;i<length;i++){
            symbolContainer.getChildren().add(new TestSymbolLabel(this, string, i));
        }
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
            displayNone(symbolContainer,false);
        }else{
            displayNone(symbolContainer,true);
        }
    }

    private void displayNone(Node node,boolean display){
        node.setManaged(display);
        node.setVisible(display);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        if(currentIndex>test.length()||currentIndex==this.currentIndex){
            return;
        }
        this.currentIndex = currentIndex;
        //change the colors of the state diagram accordingly
        currentTransition=runTillIndex(currentIndex);
        if (currentTransition!=null) {
            currentState=currentTransition.getFinalStateView();
            currentState.setColor(DesignerElementView.TEST_HIGHLIGHT_COLOR);
        }
    }

    public TransitionView runTillIndex(int index){
        if(index>=test.length()){
            return null;
        }
        TransitionView startArrowView=workspace.getDesignerController().getStartArrowView();
        StateView stateView=startArrowView.getFinalStateView();

        TransitionView pointingToCurrentIndex=null;
        for (int i = 0; i <= index; i++) {
            pointingToCurrentIndex=stateView.getTransitionViewMap().get(test.charAt(i)+"");
            if(pointingToCurrentIndex==null){
                return null;
            }
        }

        return pointingToCurrentIndex;
    }

    public void setLabelColorsAccordingToProgress(){
        for (Node node : symbolContainer.getChildren()) {
            ((TestSymbolLabel)node).setColorAccordingToIndexProgress();
        }
    }
}
