package com.madebynikhil.observer;

import java.util.LinkedList;
import java.util.List;

/**
 * Observable class that holds a list of observers
 * Created by NikhilVerma on 01/11/16.
 */
public abstract class Observable {

    private transient List<Observer> observerList=new LinkedList<>();

    public void setObserverList(List<Observer> observerList) {
        this.observerList = observerList;
    }

    public void notifyAllObservers(){
        for (Observer observer : observerList){
            observer.observableModified(this);
        }
    }

    public boolean subscribe(Observer observer){
        return observerList.add(observer);
    }

    public boolean unsubscribe(Observer observer){
        return observerList.remove(observer);
    }

}
