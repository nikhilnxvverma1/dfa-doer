package com.madebynikhil.editor.controller;

import com.madebynikhil.editor.view.DesignerElementView;
import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.editor.view.TestSymbolLabel;
import com.madebynikhil.editor.view.TransitionView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

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
    private boolean playing;
    private Timeline timeline;

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
        if(!open){
            test=null;
            workspace.getDesignerController().resetDesignerElementViewsToDefaultColor();
        }
    }

    public void changePlaybackSpeed(double sliderValue){
        delay=MAX_DELAY - sliderValue*(MAX_DELAY-MIN_DELAY);
        if (timeline != null) {
            timeline.setDelay(new Duration(delay));
        }
    }

    public void setNewTest(String test) {
        this.test = workspace.getMainWindowController().getTestInput().getText();
        System.out.println("new Test string is "+test);
        openTestInputEditing(false);
        makeSymbolLabelsFor(this.test);
        setCurrentIndex(test.length()-1);
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
        openTestInputEditing(false);
        //check with existing string if doesn't exist, close the test area
        if(test==null){
            setOpen(false);
        }
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
        if(test==null||
                currentIndex>test.length()||
                currentIndex==this.currentIndex||
                currentIndex<0||
                currentIndex>=test.length()){
            return;
        }
        this.currentIndex = currentIndex;
        workspace.getDesignerController().resetDesignerElementViewsToDefaultColor();
        //change the colors of the state diagram accordingly
        currentTransition=runTillIndex(currentIndex);
        if (currentTransition!=null) {
            currentState=currentTransition.getFinalStateView();
            currentState.setColor(DesignerElementView.TEST_HIGHLIGHT_COLOR);

            if(currentIndex==test.length()-1){
                if(currentState.getState().isFinalState()){
                    currentState.setColor(DesignerElementView.TEST_PASS_COLOR);
                }else{
                    currentState.setColor(DesignerElementView.TEST_FAIL_COLOR);
                }

            }
        }
        setLabelColorsAccordingToProgress();

        //set the disability of the stepping buttons
        workspace.getMainWindowController().getStepNext().setDisable(currentIndex>=test.length()-1);
        workspace.getMainWindowController().getStepBack().setDisable(currentIndex<=0);

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
            }else{
                stateView=pointingToCurrentIndex.getFinalStateView();
            }
        }

        return pointingToCurrentIndex;
    }

    public void setLabelColorsAccordingToProgress(){

        for (Node node : symbolContainer.getChildren()) {
            TestSymbolLabel symbolLabel = (TestSymbolLabel) node;
            symbolLabel.setColorAccordingToIndexProgress();

            // check if this symbol is the selected symbol and we are at the last state
            if(symbolLabel.getIndex()==currentIndex && currentIndex==test.length()-1){
                if(currentState.getState().isFinalState()){
                    symbolLabel.setTextFill(TestSymbolLabel.PASS_COLOR);
                }else{
                    symbolLabel.setTextFill(TestSymbolLabel.FAIL_COLOR);
                }
            }
        }
    }

    public StateView getCurrentState() {
        return currentState;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        if(playing){
            workspace.getMainWindowController().getPlayPause().setStyle("-fx-background-image: url('/images/pause.png')");

            timeline = new Timeline(new KeyFrame(Duration.millis(delay), actionEvent -> {
                if(currentIndex<test.length()-1){
                    setCurrentIndex(currentIndex+1);
                }else{
                    timeline.stop();
                    this.setPlaying(false);//recursive but it will return back just fine
                }
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

        }else{
            workspace.getMainWindowController().getPlayPause().setStyle("-fx-background-image: url('/images/play.png')");
        }
    }
}
