package com.madebynikhil.editor.command;

import com.madebynikhil.editor.view.TransitionView;

import java.util.List;

/**
 * Act of changing the symbols on a transition view
 * Created by NikhilVerma on 09/11/16.
 */
public class ChangeSymbols extends Command{
    private TransitionView transitionView;
    private List<String> oldSymbolList;
    private List<String> newSymbolList;

    public ChangeSymbols(TransitionView transitionView, List<String> oldSymbolList, List<String> newSymbolList) {
        this.transitionView = transitionView;
        this.oldSymbolList = oldSymbolList;
        this.newSymbolList = newSymbolList;
    }

    @Override
    void execute() {
        transitionView.setSymbolList(newSymbolList);
    }

    @Override
    void unExecute() {
        transitionView.setSymbolList(oldSymbolList);
    }

    @Override
    public String getName() {
        return "Change Transition";
    }
}
