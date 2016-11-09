package com.madebynikhil.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To be used on methods for tracking absolute performance.
 * This annotation acts as a pointcut for an around advice
 * on a method to give the execution time in milliseconds.
 * Created by NikhilVerma on 09/11/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TrackPerformance {
}
