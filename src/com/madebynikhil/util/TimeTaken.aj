package com.madebynikhil.util;

/**
 * For all methods annotated with TrackPerformance, this aspect logs out
 * the total time taken by instrumenting an around advice.
 * Created by NikhilVerma on 09/11/16.
 */
public aspect TimeTaken {

    pointcut annotatedMethods():(@annotation(TrackPerformance));

    Object around():annotatedMethods(){
        double startTime=System.currentTimeMillis();
        Object valueReturned = proceed();
        double endTime=System.currentTimeMillis();
        System.out.println("Total time taken by "+thisJoinPoint.getSignature().getName()+": " + (endTime - startTime));
        return valueReturned;
    }
}
