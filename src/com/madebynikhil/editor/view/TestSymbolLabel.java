package com.madebynikhil.editor.view;

import com.madebynikhil.editor.controller.RunController;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * The clickable label in the testing area while running a string.
 * Created by NikhilVerma on 11/11/16.
 */
public class TestSymbolLabel extends Label {

    public static final Color DEFAULT_COLOR=Color.BLACK;
    private static final Color HOVER_COLOR=Color.BLUE;
    private static final Color COMPLETED_COLOR=Color.GREEN;
    private static final Color SELECTED_COLOR=Color.ORANGE;
    private static final Color FAIL_COLOR=Color.RED;

    private RunController runController;
    private int index;
    private String testString;

    public TestSymbolLabel(RunController runController,String testString, int index) {
        this.runController=runController;
        this.index = index;
        this.testString = testString;
        this.setText(testString.charAt(index)+"");
        initView();
        setupEvents();
    }

    private void initView() {
        this.setColorAccordingToIndexProgress();
        this.setFont(Font.font("Cambria",28));
    }

    private void setupEvents() {
        //setup hover and click links
        this.addEventHandler(MouseEvent.MOUSE_CLICKED,this::symbolClicked);
        this.addEventHandler(MouseEvent.MOUSE_ENTERED,this::symbolEntered);
        this.addEventHandler(MouseEvent.MOUSE_EXITED,this::symbolExited);
    }

    private void symbolClicked(MouseEvent event){
        System.out.println("Symbol clicked"+testString.charAt(index));
        runController.setCurrentIndex(index);
        runController.setLabelColorsAccordingToProgress();
    }

    private void symbolEntered(MouseEvent event){
        setTextFill(HOVER_COLOR);
    }

    private void symbolExited(MouseEvent event){
        setColorAccordingToIndexProgress();
    }

    public void setColorAccordingToIndexProgress() {
        if (runController.getCurrentIndex()==index) {
            setTextFill(SELECTED_COLOR);
        }else if(index < runController.getCurrentIndex()){
            setTextFill(COMPLETED_COLOR);
        }else{
            setTextFill(DEFAULT_COLOR);
        }
    }

    public int getIndex() {
        return index;
    }
}
