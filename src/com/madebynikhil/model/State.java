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
    private Map<String,String> outgoingTransitionMap=new HashMap<>();

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

    public Map<String, String> getOutgoingTransitionMap() {
        return outgoingTransitionMap;
    }

    public void setOutgoingTransitionMap(Map<String, String> outgoingTransitionMap) {
        this.outgoingTransitionMap = outgoingTransitionMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (Double.compare(state.x, x) != 0) return false;
        if (Double.compare(state.y, y) != 0) return false;
        if (finalState != state.finalState) return false;
        if (!name.equals(state.name)) return false;
        return outgoingTransitionMap != null ? outgoingTransitionMap.equals(state.outgoingTransitionMap) : state.outgoingTransitionMap == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + (finalState ? 1 : 0);
        result = 31 * result + (outgoingTransitionMap != null ? outgoingTransitionMap.hashCode() : 0);
        return result;
    }
}
