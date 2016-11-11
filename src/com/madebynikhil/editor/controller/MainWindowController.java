package com.madebynikhil.editor.controller;

import com.madebynikhil.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.swing.*;
import java.io.File;

/**
 * This is the front most controller that is attached to the user interface.
 * This handles all the events and delegates them to several classes.
 * Created by NikhilVerma on 01/11/16.
 */
public class MainWindowController {

    public static final String OPEN_FILE = "Open file";
    public static final String SAVE_FILE_AS = "Save File As...";

    @FXML private Pane designer;

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

    private Workspace workspace;

    public void initialize() {

        this.workspace = new Workspace(this);

        //hide the animation pane
        closeAnimationPane();
    }

    @FXML
    private void openAnimationPane(){
        System.out.println("opening animation pane");
        workspace.getRunController().setOpen(true);
        testInput.setDisable(true);
    }

    @FXML
    private void closeAnimationPane(){
        System.out.println("closing animation pane");
        workspace.getRunController().setOpen(false);
        testInput.setDisable(false);
    }

    @FXML
    public void mouseClicked(MouseEvent mouseEvent){
        this.workspace.getDesignerController().handleMouseClicked(mouseEvent);
    }

    @FXML
    public void mousePressed(MouseEvent mouseEvent){
//        System.out.println("mouse pressed at "+mouseEvent.getScreenX()+" "+mouseEvent.getScreenY());
        this.workspace.getDesignerController().handleMousePress(mouseEvent);
    }

    @FXML
    public void mouseDragged(MouseEvent mouseEvent){
//        System.out.println("mouse dragged at "+mouseEvent.getScreenX()+" "+mouseEvent.getScreenY());
        this.workspace.getDesignerController().handleMouseDrag(mouseEvent);
    }
    @FXML
    public void mouseReleased(MouseEvent mouseEvent){
//        System.out.println("mouse released at "+mouseEvent.getScreenX()+" "+mouseEvent.getScreenY());
        this.workspace.getDesignerController().handleMouseRelease(mouseEvent);

    }

    @FXML
    private void delete(ActionEvent actionEvent){
        System.out.println("Delete pressed");
        workspace.getDesignerController().deleteSelectedElements();
    }

    @FXML
    private void editSymbols(ActionEvent actionEvent){
        this.allowSymbolEditing(true);
    }

    @FXML
    private void commitEditingSymbols(ActionEvent actionEvent){
        String invalidSymbol=this.workspace.setNewSymbolsFrom(editSymbols.getText());
        if(invalidSymbol==null){
            this.symbolsLink.setText("{"+Workspace.getSymbolsAsCSV(workspace.getStateMachine().getSymbolList())+"}");
        }
        this.allowSymbolEditing(false);
    }

    @FXML
    private void cancelEditingSymbols(ActionEvent actionEvent){
        this.allowSymbolEditing(false);
    }

    private void allowSymbolEditing(boolean allow){
        this.setEditingControls(allow,symbolsLink,editSymbols,symbolsOk,symbolsCancel);
        editSymbols.setText(Workspace.getSymbolsAsCSV(workspace.getStateMachine().getSymbolList()));
    }

    @FXML
    private void editDescription(ActionEvent actionEvent){
        this.allowDescriptionEditing(true);
    }

    @FXML
    private void commitEditingDescription(ActionEvent actionEvent){
        this.workspace.setNewDescription(editDescription.getText());
        this.descriptionLink.setText(editDescription.getText());
        this.allowDescriptionEditing(false);
    }

    @FXML
    private void cancelEditingDescription(ActionEvent actionEvent){
        this.allowDescriptionEditing(false);
    }

    private void allowDescriptionEditing(boolean allow){
        this.setEditingControls(allow,descriptionLink,editDescription,descriptionOk,descriptionCancel);
        editDescription.setText(workspace.getStateMachine().getDescription());
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

    public Pane getDesigner() {
        return designer;
    }

    @FXML
    void newDocument(ActionEvent event) {
        System.out.println("New file");

        //open new instance of the application
        Platform.runLater(() -> {
            try {
                new Main().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void openDocument(ActionEvent event) {
        System.out.println("Opening document");
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle(OPEN_FILE);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("State Machine JSON","*.json"));
        File file = fileChooser.showOpenDialog(designer.getScene().getWindow());
        System.out.println("File to open is "+file);

        if(workspace.isEmptyDocument()){
            //open in the same instance of the application
            workspace.loadFromJsonFile(file);
        }else if(file!=null){
            //open new instance of the application running in a parallel thread
            Platform.runLater(new Runnable() {
                public void run() {
                    try {
                        new Main(file).start(new Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    void saveDocument(ActionEvent event) {
        if(workspace.getFile()==null){
            saveAsDocument(event);
        }else{
            System.out.println("Saving the document in the current file");
            workspace.save();
        }
    }

    @FXML
    void saveAsDocument(ActionEvent event) {
        System.out.println("Saving the document as a new file");
        this.saveAsNewFile();
    }

    private boolean saveAsNewFile(){
        //open the file chooser pop up
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle(SAVE_FILE_AS);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("State Machine's JSON","*.json"));
        Window window = designer.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);
        if(file!=null){
            System.out.println("File chosen is "+file);
            workspace.saveAs(file);
            return true;
        }else{
            System.out.println("No file chosen ");
            return false;
        }
    }

    @FXML
    void export(ActionEvent event) {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Unsupported");
        alert.setHeaderText(null);
        alert.setContentText("Export feature is currently not supported");
        alert.showAndWait();
    }

    @FXML
    void quit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void undo(ActionEvent event){
        //this event essentially defines a pointcut for the undo redo aspect
    }

    @FXML
    private void redo(ActionEvent event){
        //this event essentially defines a pointcut for the undo redo aspect
    }

    public AnchorPane getAnimationControls() {
        return animationControls;
    }
}
