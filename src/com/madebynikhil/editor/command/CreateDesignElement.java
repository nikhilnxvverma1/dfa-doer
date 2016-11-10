package com.madebynikhil.editor.command;

import com.madebynikhil.editor.view.DesignerElementView;
import com.madebynikhil.editor.view.TransitionView;

/**
 * Design elements include states and transitions.
 * This command takes care of both the model and the view
 * Created by NikhilVerma on 09/11/16.
 */
public class CreateDesignElement extends Command{
    private DesignerElementView designerElementView;

    public CreateDesignElement(DesignerElementView designerElementView) {
        this.designerElementView = designerElementView;
    }

    @Override
    void execute() {
        designerElementView.integrateInModelAndView();
        designerElementView.getDesignerController().selectElement(designerElementView);
    }

    @Override
    void unExecute() {
        designerElementView.removeFromModelAndView();
    }

    @Override
    public String getName() {
        return "Create Transition";
    }
}
