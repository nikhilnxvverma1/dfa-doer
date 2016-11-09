package com.madebynikhil.editor.command;

/**
 * A command is used to store a unit of action that is performed in the
 * editor. These actions are supposedly undoable so implementing classes will
 * be responsible for both execution and unexecution.
 * Created by NikhilVerma on 01/11/16.
 */
public abstract class Command {

    /**
     * By committing a command, the system recognizes an event that becomes available in the history for undo redo.
     * @param executionPending if true, executes this command ,otherwise doesn't
     */
    public void commit(boolean executionPending){
        if(executionPending){
            execute();
        }
    }

    /** Executes the command and takes care of the model and the view*/
    abstract void execute();
    /** Un-Executes the command and reverts back to the previous state of he model and the view*/
    abstract void unExecute();

    /** Defines a user friendly name that identifies this command */
    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
