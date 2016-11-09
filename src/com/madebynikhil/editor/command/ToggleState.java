package com.madebynikhil.editor.command;

import com.madebynikhil.editor.view.StateView;

/**
 * Action of toggling state on and off
 * Created by NikhilVerma on 09/11/16.
 */
public class ToggleState extends Command{
    private StateView stateView;
    private boolean before;

    public ToggleState(StateView stateView) {
        this.stateView = stateView;
        this.before=stateView.getState().isFinalState();
    }

    @Override
    void execute() {
        this.stateView.getState().setFinalState(!before);
    }

    @Override
    void unExecute() {
        this.stateView.getState().setFinalState(before);
    }

    @Override
    public String getName() {
        return "Toggle State";
    }
}
