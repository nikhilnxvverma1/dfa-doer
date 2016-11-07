package com.madebynikhil.editor;

import com.madebynikhil.editor.command.Command;
import com.madebynikhil.model.StateMachine;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Top level delegate class that manages most of the editor operations and holds the
 * root data model and corresponding views(generated from data model).
 * Created by NikhilVerma on 01/11/16.
 */
public class Workspace {

    private MainWindowController mainWindowController;

    private StateMachine stateMachine;
    private Stack<Command> history=new Stack<>();
    private Stack<Command> future=new Stack<>();
    private String filename;
    private DesignerController designerController;


    public Workspace(MainWindowController mainWindowController) {
        this(mainWindowController,null);
    }

    public Workspace(MainWindowController mainWindowController,String filename) {
        this.mainWindowController=mainWindowController;
        this.filename = filename;
        this.designerController=new DesignerController(this,mainWindowController.getDesigner());
        if(filename==null){
            this.stateMachine=new StateMachine();

        }else{
            //TODO load from json file
            //TODO initialize the views
        }
    }

    public MainWindowController getMainWindowController() {
        return mainWindowController;
    }

    public DesignerController getDesignerController() {
        return designerController;
    }

    public StateMachine getStateMachine() {
        return stateMachine;
    }

    /**
     * Sets the new symbol from a comma separated string
     * @param text comma seperated string received usualy from a textfield
     * @return null if everything is perfect, otherwise it spits out the invalid symbol
     */
    public String setNewSymbolsFrom(String text){

        String [] symbols=text.split(",");
        for (String symbol : symbols){
            if(symbol.length()!=1){
                return symbol;
            }
        }
        this.stateMachine.setSymbolList(Arrays.asList(symbols));
        return null;
    }

    public void setNewDescription(String text){
        this.stateMachine.setDescription(text);
    }
}
