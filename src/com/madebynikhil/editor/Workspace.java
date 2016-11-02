package com.madebynikhil.editor;

import com.madebynikhil.editor.command.Command;
import com.madebynikhil.model.StateMachine;

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


    public Workspace(MainWindowController mainWindowController) {
        this.mainWindowController=mainWindowController;
    }

    public Workspace(MainWindowController mainWindowController,String filename) {
        this.mainWindowController=mainWindowController;
        this.filename = filename;
    }
}
