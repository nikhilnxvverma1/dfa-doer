package com.madebynikhil.editor.command;

import com.madebynikhil.editor.controller.DesignerController;
import com.madebynikhil.editor.view.TransitionView;

/**
 * Changing from an old start state to a new one.
 * Either of Old or new start state can be null.
 * Created by NikhilVerma on 09/11/16.
 */
public class ChangeStartState extends Command{
    private TransitionView oldStartArrow;
    private TransitionView newStartArrow;

    public ChangeStartState(TransitionView oldStartArrow, TransitionView newStartArrow) {
        this.oldStartArrow = oldStartArrow;
        this.newStartArrow = newStartArrow;
    }

    @Override
    void execute() {
        change(oldStartArrow,newStartArrow);
    }

    @Override
    void unExecute() {
        change(newStartArrow,oldStartArrow);
    }

    private void change(TransitionView remove,TransitionView add){
        //either of starting states can be null

        if(remove!=null){
            DesignerController designerController = remove.getDesignerController();
            if(designerController.getDesigner().getChildren().contains(remove)){
                designerController.getDesigner().getChildren().remove(remove);
            }
            designerController.getWorkspace().getStateMachine().setStartingState(null);
            designerController.setStartArrowView(add);
        }
        if(add!=null){
            DesignerController designerController = add.getDesignerController();
            if (!designerController.getDesigner().getChildren().contains(add)) {
                designerController.getDesigner().getChildren().add(add);
            }
            add.recomputeEndpointsBasedOnStatePositions();
            designerController.getWorkspace().getStateMachine().setStartingState(add.getFinalStateView().getState());
            designerController.setStartArrowView(add);
            designerController.setStartArrowPositionAndLength();
        }
    }

    @Override
    public String getName() {
        return "Change Start";
    }
}
