package com.madebynikhil.editor.controller;

import com.madebynikhil.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

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

    @FXML private Button runString;
    @FXML private Button finishAnimation;
    @FXML private AnchorPane animationControls;
    @FXML private Slider playbackSpeed;
    @FXML private Hyperlink symbolsLink;
    @FXML private TextField editSymbols;
    @FXML private Button symbolsOk;
    @FXML private Button symbolsCancel;
    @FXML private Button stepNext;
    @FXML private Button stepBack;

    @FXML private Hyperlink descriptionLink;
    @FXML private TextField editDescription;
    @FXML private Button descriptionOk;
    @FXML private Button descriptionCancel;
    @FXML private HBox testStringContainer;
    @FXML private TextField testInput;
    @FXML private Button testInputOk;
    @FXML private Button testInputCancel;
    @FXML private Button editTestInput;

    private Workspace workspace;

    public void initialize() {

        this.workspace = new Workspace(this);

        //hide the animation pane
        closeAnimationPane();
        workspace.getRunController().changePlaybackSpeed(playbackSpeed.getValue());
    }

    @FXML
    public void openAnimationPane(){
        System.out.println("opening animation pane");
        workspace.getRunController().setOpen(true);
    }

    @FXML
    public void closeAnimationPane(){
        System.out.println("closing animation pane");
        workspace.getRunController().setOpen(false);
    }

    @FXML
    public void mouseClicked(MouseEvent mouseEvent){
        if (workspace.getRunController().isOpen()) {
            if(mouseEvent.getClickCount()>=2){
                closeAnimationPane();
            }
        }else{
            this.workspace.getDesignerController().handleMouseClicked(mouseEvent);
        }
    }

    @FXML
    public void mousePressed(MouseEvent mouseEvent){
        if (!workspace.getRunController().isOpen()) {
            this.workspace.getDesignerController().handleMousePress(mouseEvent);
        }
    }

    @FXML
    public void mouseDragged(MouseEvent mouseEvent){
        if (!workspace.getRunController().isOpen()) {
            this.workspace.getDesignerController().handleMouseDrag(mouseEvent);
        }
    }
    @FXML
    public void mouseReleased(MouseEvent mouseEvent){
        if (!workspace.getRunController().isOpen()) {
            this.workspace.getDesignerController().handleMouseRelease(mouseEvent);
        }

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

    @FXML
    private void changePlaybackSpeed(MouseEvent mouseEvent){
        workspace.getRunController().changePlaybackSpeed(playbackSpeed.getValue());
    }

    @FXML
    private void testNewInput(ActionEvent actionEvent){
        workspace.getRunController().setNewTest(testInput.getText());
    }

    @FXML
    private void cancelledSettingNewInput(ActionEvent actionEvent){
        workspace.getRunController().cancelledNewTest();
    }

    @FXML
    private void editTestInput(){
        workspace.getRunController().openTestInputEditing(true);
    }

    @FXML
    private void stepNext(ActionEvent actionEvent){
        workspace.getRunController().setCurrentIndex(workspace.getRunController().getCurrentIndex()+1);
    }

    @FXML
    private void stepBack(ActionEvent actionEvent){
        workspace.getRunController().setCurrentIndex(workspace.getRunController().getCurrentIndex()-1);
    }

    public TextField getTestInput() {
        return testInput;
    }

    public Button getRunString() {
        return runString;
    }

    public HBox getTestStringContainer() {
        return testStringContainer;
    }

    public Button getTestInputOk() {
        return testInputOk;
    }

    public Button getTestInputCancel() {
        return testInputCancel;
    }

    public Button getEditTestInput() {
        return editTestInput;
    }

    public Button getStepNext() {
        return stepNext;
    }

    public Button getStepBack() {
        return stepBack;
    }
}
