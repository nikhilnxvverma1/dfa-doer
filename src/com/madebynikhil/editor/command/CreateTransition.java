package com.madebynikhil.editor.command;

import com.madebynikhil.editor.controller.DesignerController;
import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.editor.view.TransitionView;

/**
 * Action performed when two states are joined via a transition.
 * This command takes care of both the model and the view
 * Created by NikhilVerma on 09/11/16.
 */
public class CreateTransition extends Command{
    private TransitionView transitionView;

    public CreateTransition(TransitionView transitionView) {
        this.transitionView = transitionView;
    }

    @Override
    void execute() {
        //add the view to the designer
        DesignerController designerController=transitionView.getDesignerController();
        designerController.getDesigner().getChildren().add(transitionView);
        transitionView.recomputeEndpointsBasedOnStatePositions();

        //configure the model with the states
        StateView initialStateView = transitionView.getInitialStateView();
        StateView finalStateView = transitionView.getFinalStateView();

        //also put it in the map of the model
        for (String s : transitionView.getSymbolList()) {
            initialStateView.getState().getOutgoingTransitionMap().put(
                    s,transitionView.getFinalStateView().getState().getName());

            //put it in this views' map
            initialStateView.getTransitionViewMap().put(s,transitionView);
        }
    }

    @Override
    void unExecute() {
        //remove the view from the designer
        DesignerController designerController=transitionView.getDesignerController();
        designerController.getDesigner().getChildren().remove(transitionView);

        //configure the model to remove the tranistion between the states
        StateView initialStateView = transitionView.getInitialStateView();
        StateView finalStateView = transitionView.getFinalStateView();


        //remove this transition view from the initial state's map too
        for (String s : transitionView.getSymbolList()) {
            initialStateView.getState().getOutgoingTransitionMap().remove(s);

            //remove it from the initial views' map
            initialStateView.getTransitionViewMap().remove(s);
        }
    }

    @Override
    public String getName() {
        return "Create Transition";
    }
}
