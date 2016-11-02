package com.madebynikhil.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;

/**
 * This is the front most controller that is attached to the user interface.
 * This handles all the events and delegates them to several classes
 * Created by NikhilVerma on 01/11/16.
 */
public class MainWindowController {
    @FXML private Button testInput;
    @FXML private Button finishAnimation;
    @FXML private AnchorPane animationControls;
    @FXML private Hyperlink symbolsLink;
    @FXML private TextField editSymbols;
    @FXML private Button symbolsOk;
    @FXML private Button symbolsCancel;

    @FXML private Hyperlink descriptionLink;
    @FXML private TextField editDescription;
    @FXML private Button descriptionOk;
    @FXML private Button descriptionCancel;

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

    @FXML
    public void mouseClicked(MouseEvent mouseEvent){
        if(mouseEvent.getClickCount()==2){
            System.out.println("mouse double clicked");
        }
    }

    @FXML
    public void mousePressed(MouseEvent mouseEvent){
        System.out.println("mouse pressed at "+mouseEvent.getScreenX()+" "+mouseEvent.getScreenY());
    }

    @FXML
    public void mouseDragged(MouseEvent mouseEvent){
        System.out.println("mouse dragged at "+mouseEvent.getScreenX()+" "+mouseEvent.getScreenY());
    }
    @FXML
    public void mouseReleased(MouseEvent mouseEvent){
        System.out.println("mouse released at "+mouseEvent.getScreenX()+" "+mouseEvent.getScreenY());

    }

    @FXML
    private void delete(ActionEvent actionEvent){
        System.out.println("Delete pressed");
    }

    @FXML
    private void editSymbols(ActionEvent actionEvent){
        this.allowSymbolEditing(true);
    }

    @FXML
    private void commitEditingSymbols(ActionEvent actionEvent){
        this.allowSymbolEditing(false);
    }

    @FXML
    private void cancelEditingSymbols(ActionEvent actionEvent){
        this.allowSymbolEditing(false);
    }

    private void allowSymbolEditing(boolean allow){
        this.setEditingControls(allow,symbolsLink,editSymbols,symbolsOk,symbolsCancel);
    }

    @FXML
    private void editDescription(ActionEvent actionEvent){
        this.allowDescriptionEditing(true);
    }

    @FXML
    private void commitEditingDescription(ActionEvent actionEvent){
        this.allowDescriptionEditing(false);
    }

    @FXML
    private void cancelEditingDescription(ActionEvent actionEvent){
        this.allowDescriptionEditing(false);
    }

    private void allowDescriptionEditing(boolean allow){
        this.setEditingControls(allow,descriptionLink,editDescription,descriptionOk,descriptionCancel);
    }
    
    private void setEditingControls(boolean visible,Hyperlink link,TextField editor,Button ok,Button cancel){
        link.setVisible(!visible);
        link.setManaged(!visible);

        editor.setVisible(visible);
        editor.setManaged(visible);

        ok.setVisible(visible);
        ok.setManaged(visible);

        cancel.setVisible(visible);
        cancel.setManaged(visible);
    }
}
