package com.madebynikhil.model;

import java.util.LinkedList;
import java.util.List;

/**
 * For a given set of input, transition tells which state to go to next.
 * @deprecated use string based key value map in states
 * Created by NikhilVerma on 01/11/16.
 */
public class Transition {

    private transient State from;
    private transient State to;
    private List<String> symbolList=new LinkedList<>();

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

    public List<String> getSymbolList() {
        return symbolList;
    }

    public void setSymbolList(List<String> symbolList) {
        this.symbolList = symbolList;
    }
}
