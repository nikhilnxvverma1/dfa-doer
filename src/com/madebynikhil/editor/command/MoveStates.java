package com.madebynikhil.editor.command;

import com.madebynikhil.editor.controller.DesignerController;
import com.madebynikhil.editor.view.StateView;
import javafx.geometry.Point2D;

import java.util.LinkedList;
import java.util.List;

/**
 * Action of moving state(s) in the Designer
 * Created by NikhilVerma on 09/11/16.
 */
public class MoveStates extends Command{

    private List<StateView> stateViewList;
    private double dx;
    private double dy;

    public MoveStates(StateView stateView, double dx, double dy){
        this(new LinkedList<StateView>(),dx,dy);
        this.stateViewList.add(stateView);

    }

    public MoveStates(List<StateView> stateViewList, double dx, double dy) {
        this.stateViewList = stateViewList;
        this.dx=dx;
        this.dy=dy;
    }

    @Override
    void execute() {
        shiftAllStatesBy(dx,dy);
    }

    @Override
    void unExecute() {
        shiftAllStatesBy(-dx,-dy);
    }

    private void shiftAllStatesBy(double dx,double dy){
        if(stateViewList.size()>0){
            DesignerController designerController=stateViewList.get(0).getDesignerController();
            double xLength=designerController.lengthInModalSpace(dx);
            double yLength=designerController.lengthInModalSpace(dy);
            for (StateView stateView : stateViewList){

                //change the position of the modal and the observers to respond automatically
                Point2D modal = designerController.toModelSpace(stateView.getLayoutX(), stateView.getLayoutY());
                stateView.getState().setPosition(modal.getX()+xLength,modal.getY()+yLength);
            }
        }
    }

    @Override
    public String getName() {
        return "Move States";
    }
}
