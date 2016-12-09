package com.madebynikhil.editor.command;

import com.madebynikhil.editor.command.Command;
import com.madebynikhil.editor.controller.MainWindowController;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Since operational commands such as move, delete, toggle state etc. form
 * a cross cutting concern, this aspect intercepts on all commands and puts
 * them in the history stack.
 * Created by NikhilVerma on 09/11/16.
 */
public aspect UndoRedo {

    private Stack<Command> history=new Stack<>();
    private Stack<Command> future=new Stack<>();

    pointcut operationCommitted(Command command):(call(public void Command+.commit(..))&&target(command));

    after(Command command):operationCommitted(command){
        System.out.println("Action: "+command.getName()+" committed");

        //push the command onto the history stack and reset future from here
        history.push(command);
        future.clear();
    }

    //defines a pointcut for the undo event callback from GUI
    pointcut undo(MainWindowController controller):(execution(private void com.madebynikhil.editor.controller.MainWindowController.undo(..))&& target(controller));

    after(MainWindowController controller):undo(controller){
        System.out.println("Performing undo event");
        try {
            Command lastCommand = history.pop();
            lastCommand.unExecute();
            future.push(lastCommand);
        } catch (EmptyStackException e) {
            System.out.println("History is empty");
        }
    }

    //defines a pointcut for the redo event callback from GUI
    pointcut redo(MainWindowController controller):(execution(private void com.madebynikhil.editor.controller.MainWindowController.redo(..))&& target(controller));

    after(MainWindowController controller):redo(controller){
        System.out.println("Performing redo event");
        try {
            Command revertedCommand = future.pop();
            revertedCommand.execute();
            history.push(revertedCommand);
        } catch (EmptyStackException e) {
            System.out.println("Future is empty");
        }
    }
}
