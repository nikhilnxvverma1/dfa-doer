package com.madebynikhil.editor.command;

import com.madebynikhil.editor.view.DesignerElementView;

/**
 * Deletes the design element contained.
 * This command is the exact opposite of CreateDesignElement.
 * Created by NikhilVerma on 09/11/16.
 */
public class DeleteDesignElement extends Command{
    private DesignerElementView designerElementView;

    public DeleteDesignElement(DesignerElementView designerElementView) {
        this.designerElementView = designerElementView;
    }

    @Override
    void execute() {
        designerElementView.removeFromModelAndView();
    }

    @Override
    void unExecute() {
        designerElementView.integrateInModelAndView();
        designerElementView.getDesignerController().selectElement(designerElementView);
    }

    @Override
    public String getName() {
        return "Delete Design Element";
    }
}
