package com.madebynikhil.editor.command;

import com.madebynikhil.editor.controller.DesignerController;
import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.model.StateMachine;

/**
 * State create and displayed in the designer.
 * Created by NikhilVerma on 09/11/16.
 */
public class CreateState extends Command{

    private StateView stateView;

    public CreateState(StateView stateView) {
        this.stateView = stateView;
    }

    @Override
    void execute() {
        //remove this state from view
        DesignerController designerController=stateView.getDesignerController();
        designerController.getDesigner().getChildren().remove(stateView);

        //TODO remove all outgoing transitions from view and from model

        StateMachine statemachine=designerController.getWorkspace().getStateMachine();
    }

    @Override
    void unExecute() {

    }

    @Override
    public String getName() {
        return null;
    }
}
