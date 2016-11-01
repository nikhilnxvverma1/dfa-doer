package com.madebynikhil.model;

import java.util.LinkedList;
import java.util.List;

/**
 * For a given set of input, transition tells which state to go to next.
 * Created by NikhilVerma on 01/11/16.
 */
public class Transition {
    private State from;
    private State to;
    private List<Integer> symbolIndices=new LinkedList<>();

    public Transition(State from, State to) {
        this.from = from;
        this.to = to;
    }

    public State getFrom() {
        return from;
    }

    public void setFrom(State from) {
        this.from = from;
    }

    public State getTo() {
        return to;
    }

    public void setTo(State to) {
        this.to = to;
    }

    public List<Integer> getSymbolIndices() {
        return symbolIndices;
    }

    public void setSymbolIndices(List<Integer> symbolIndices) {
        this.symbolIndices = symbolIndices;
    }
}
