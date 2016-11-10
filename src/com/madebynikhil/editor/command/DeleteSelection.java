package com.madebynikhil.editor.command;

import com.madebynikhil.editor.controller.DesignerController;
import com.madebynikhil.editor.view.DesignerElementView;

import java.util.LinkedList;
import java.util.List;

/**
 * Deletes all the currently selected items together.
 * Created by NikhilVerma on 09/11/16.
 */
public class DeleteSelection extends CompositeCommand{

    private DesignerController designerController;
    private List<DesignerElementView> itemsToDelete;

    public DeleteSelection(DesignerController designerController) {
        this.designerController = designerController;

        //since the selection keeps changing, we create another list that retains all items at this point in time
        this.itemsToDelete=new LinkedList<>(designerController.getSelectedElements());
    }

    @Override
    public String getName() {
        return "Delete Items";
    }
}
