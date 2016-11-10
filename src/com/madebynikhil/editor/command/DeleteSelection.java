package com.madebynikhil.editor.command;

import com.madebynikhil.editor.controller.DesignerController;
import com.madebynikhil.editor.view.DesignerElementView;
import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.editor.view.TransitionView;

import java.util.*;

/**
 * Deletes all the currently selected items together.
 * Created by NikhilVerma on 09/11/16.
 */
public class DeleteSelection extends CompositeCommand{

    private DesignerController designerController;
    private List<DesignerElementView> itemsToDelete;
    private List<StateView> apparentStateViewList;
    private TransitionView apparentStartArrow;

    public DeleteSelection(DesignerController designerController) {
        this.designerController = designerController;

        //since the selection keeps changing, we create another list that retains all items at this point in time
        this.itemsToDelete=new LinkedList<>(designerController.getSelectedElements());
        this.apparentStateViewList=new LinkedList<>(designerController.getStateViewList());
        this.apparentStartArrow=designerController.getStartArrowView();
        accountAllElementsAndDelete();
    }

    private void accountAllElementsAndDelete(){

        Set<StateView> statesToDelete=new HashSet<>(itemsToDelete.size());
        Set<TransitionView> transitionsToDelete=new HashSet<>(itemsToDelete.size());

        for (DesignerElementView elementView : itemsToDelete) {
            if(elementView instanceof StateView){
                StateView stateView = (StateView) elementView;
                statesToDelete.add(stateView);

                //if this is the starting state delete the arrow too
                if(apparentStartArrow!=null && apparentStartArrow.getFinalStateView()==stateView){
                    transitionsToDelete.add(apparentStartArrow);
                }

                //remove all outgoing edge views
                Collection<TransitionView> outgoingEdges = stateView.getTransitionViewMap().values();
                for (TransitionView outgoing : outgoingEdges) {
                    transitionsToDelete.add(outgoing);
                }

                //remove all undecided outgoing edge views too
                Collection<TransitionView> undecidedOutgoingEdges = stateView.getUndecidedTransitionViewMap().values();
                for (TransitionView outgoing : undecidedOutgoingEdges) {
                    transitionsToDelete.add(outgoing);
                }

                //now remove all incoming edge views from other states
                for (StateView dependent : apparentStateViewList) {

                    //look for states that have an outgoing edge of this 'possible dependent' to this state view
                    if(dependent!=stateView){

                        //check all outgoing edges of the possible dependent
                        Set<Map.Entry<String, TransitionView>> entries = dependent.getTransitionViewMap().entrySet();
                        for (Map.Entry<String, TransitionView> entry : entries) {

                            //check if the edge is incoming onto the current state view(the one in loop)
                            TransitionView incoming=entry.getValue();
                            if(incoming.getFinalStateView()==stateView){
                                transitionsToDelete.add(incoming);
                            }

                        }

                        //if there is an undecided edge leading to this state view, add that too
                        TransitionView undecided=dependent.getUndecidedTransitionViewMap().get(stateView.getState().getName());
                        if(undecided!=null){
                            transitionsToDelete.add(undecided);
                        }
                    }
                }
            }else if(elementView instanceof TransitionView){
                transitionsToDelete.add((TransitionView)elementView);
            }
        }
        makeDeleteCommands(statesToDelete,transitionsToDelete);
    }

    private void makeDeleteCommands(Set<StateView> statesToDelete,Set<TransitionView> transitionToDelete){
        for (StateView stateView : statesToDelete) {
            this.commandList.add(new DeleteDesignElement(stateView));
        }

        for (TransitionView transitionView : transitionToDelete) {
            if(transitionToDelete==apparentStartArrow){
                this.commandList.add(new ChangeStartState(apparentStartArrow,null));
            }else{
                this.commandList.add(new DeleteDesignElement(transitionView));
            }
        }

    }

    private void selectItemsToDelete(){
        designerController.clearSelection();
        for (DesignerElementView elementView : itemsToDelete) {
            designerController.selectElement(elementView,false);
        }
    }

    @Override
    void unExecute() {
        super.unExecute();
        selectItemsToDelete();
    }

    @Override
    public String getName() {
        if(commandList.size()==1){
            return commandList.get(0).getName();
        }else{
            return "Delete Items";
        }
    }
}
