package com.madebynikhil.editor.command;

import com.madebynikhil.editor.view.DesignerElementView;

import java.util.LinkedList;
import java.util.List;

/**
 * By holding a list of commands that get executed together,
 * a composite command helps in using several commands to create one command
 * Created by NikhilVerma on 09/11/16.
 */
public abstract class CompositeCommand extends Command{

    protected List<Command> commandList=new LinkedList<>();

    @Override
    void execute() {
        for (Command command : commandList) {
            command.execute();
        }
    }

    @Override
    void unExecute() {
        for (Command command : commandList) {
            command.unExecute();
        }
    }

}
