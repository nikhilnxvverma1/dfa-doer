package com.madebynikhil.editor;

import com.madebynikhil.editor.view.StartArrowView;
import com.madebynikhil.editor.view.StateView;
import com.madebynikhil.editor.view.TransitionView;
import com.madebynikhil.model.State;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.List;

/**
 * Responsible for all GUI based designing of the state diagram.
 * Created by NikhilVerma on 02/11/16.
 */
public class DesignerController {
    private static final double INITIAL_ZOOM=0;
    private Workspace workspace;
    private Pane designer;
    private double zoom=INITIAL_ZOOM;
    private List<StateView> stateViewList=new LinkedList<>();
    private List<TransitionView> transitionViewList=new LinkedList<>();
    private StartArrowView startArrowView;

    public DesignerController(Workspace workspace, Pane designer) {
        this.workspace = workspace;
        this.designer = designer;
    }

    public void handleMouseClicked(MouseEvent mouseEvent){
        if(mouseEvent.getClickCount()==2){
            System.out.println("mouse double clicked");
            State newState=new State(mouseEvent.getX(),mouseEvent.getY());
            StateView stateView=new StateView(this,newState);
            this.designer.getChildren().add(stateView);

        }
    }

    public void handleMousePress(MouseEvent mouseEvent){

    }

    public void handleMouseDrag(MouseEvent mouseEvent){

    }

    public void handleMouseRelease(MouseEvent mouseEvent){

    }
}
