package com.madebynikhil.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 * This is the front most controller that is attached to the user interface.
 * This handles all the events and delegates them to several classes
 * Created by NikhilVerma on 01/11/16.
 */
public class MainWindowController {
    @FXML private Button testInput;
    @FXML private Button finishAnimation;
    @FXML private AnchorPane animationControls;

    @FXML
    private void openAnimationPane(){
        System.out.println("opening animation pane");
        animationControls.setVisible(true);
        animationControls.setManaged(true);
        testInput.setDisable(true);
    }

    @FXML
    private void closeAnimationPane(){
        System.out.println("closing animation pane");
        animationControls.setVisible(false);
        animationControls.setManaged(false);
        testInput.setDisable(false);
    }
}
