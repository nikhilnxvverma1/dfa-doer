package com.madebynikhil.model;

import com.madebynikhil.observer.Observable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * State contains information about name, position and weather the state is final or not.
 * Created by NikhilVerma on 01/11/16.
 */
public class State extends Observable{

    private double x;
    private double y;
    private String name;
    private boolean finalState;
    private Map<String,Transition> outgoingTransitionMap=new HashMap<>();

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
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFinalState() {
        return finalState;
    }

    public void setFinalState(boolean finalState) {
        this.finalState = finalState;
    }

    public Map<String, Transition> getOutgoingTransitionMap() {
        return outgoingTransitionMap;
    }

    public void setOutgoingTransitionMap(Map<String, Transition> outgoingTransitionMap) {
        this.outgoingTransitionMap = outgoingTransitionMap;
    }
}
