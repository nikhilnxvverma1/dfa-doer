package com.madebynikhil.model;

import com.madebynikhil.observer.Observable;

import java.util.LinkedList;
import java.util.List;

/**
 * State contains information about label, position and weather the state is final or not.
 * Created by NikhilVerma on 01/11/16.
 */
public class State extends Observable{

    private double x;
    private double y;
    private String label;
    private boolean finalState;
    private List<Transition> outgoingTransitionList=new LinkedList<>();

    public State(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setPosition(double x,double y){
        this.x=x;
        this.y=y;
        notifyAllObservers();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isFinalState() {
        return finalState;
    }

    public void setFinalState(boolean finalState) {
        this.finalState = finalState;
        this.notifyAllObservers();
    }

    public List<Transition> getOutgoingTransitionList() {
        return outgoingTransitionList;
    }

    public void setOutgoingTransitionList(List<Transition> outgoingTransitionList) {
        this.outgoingTransitionList = outgoingTransitionList;
    }

}
