package com.madebynikhil.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Root model class containing all the states along with description and symbols.
 * Created by NikhilVerma on 01/11/16.
 */
public class StateMachine {

    private String description;
    private List<String> symbolList=new ArrayList<>();
    private State startingState;
    private ArrayList<State> stateList=new ArrayList<>();

    public StateMachine() {
        symbolList.add("a");
        symbolList.add("b");
        description="Set of all languages that contain a or b";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSymbolList() {
        return symbolList;
    }

    public void setSymbolList(List<String> symbolList) {
        this.symbolList = symbolList;
    }

    public State getStartingState() {
        return startingState;
    }

    public void setStartingState(State startingState) {
        this.startingState = startingState;
    }

    public ArrayList<State> getStateList() {
        return stateList;
    }

    public void setStateList(ArrayList<State> stateList) {
        this.stateList = stateList;
    }
}
