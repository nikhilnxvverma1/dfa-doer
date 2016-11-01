package com.madebynikhil.editor.command;

/**
 * A command is used to store a unit of action that is performed in the
 * editor. These actions are supposedly undoable so implementing classes will
 * be responsible for both execution and unexecution.
 * Created by NikhilVerma on 01/11/16.
 */
public interface Command {

    void execute();
    void unexecute();
}
