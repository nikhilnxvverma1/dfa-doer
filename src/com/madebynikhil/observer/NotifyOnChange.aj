package com.madebynikhil.observer;

import com.madebynikhil.observer.Observable;

/**
 * After every change via a setter, this aspect triggers the notification event.
 * Created by NikhilVerma on 09/11/16.
 */
public aspect NotifyOnChange {

    pointcut changed(Observable observable):(execution(public void Observable+.set*(..)) && target(observable));

    after(Observable observable):changed(observable){
        observable.notifyAllObservers();
    }
}
