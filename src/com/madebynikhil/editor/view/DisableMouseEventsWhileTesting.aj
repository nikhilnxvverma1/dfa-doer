package com.madebynikhil.editor.view;

import javafx.scene.input.MouseEvent;

/**
 * When the "Run String" animation controls are opened, this aspect
 * prevents mouse events from taking place on the design element views.
 * Created by NikhilVerma on 11/11/16.
 */
public aspect DisableMouseEventsWhileTesting {

    pointcut mouseEvents(DesignerElementView elementView):(
            (execution(private void StateView.stateClicked(..))&& target(elementView)) ||
            (execution(private void StateView.statePressed(..))&& target(elementView)) ||
            (execution(private void StateView.stateDragged(..))&& target(elementView)) ||
            (execution(private void StateView.stateReleased(..))&& target(elementView))||
            (execution(private void TransitionView.transitionClickedOrPressed(..))&& target(elementView))
    );

    void around(DesignerElementView elementView):mouseEvents(elementView){

        boolean runAreaOpen=elementView.getDesignerController().getWorkspace().getRunController().isOpen();

        if(!runAreaOpen){
            proceed(elementView);
        }
    }
}
