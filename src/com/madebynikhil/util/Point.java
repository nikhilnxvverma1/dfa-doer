package com.madebynikhil.util;

import javafx.geometry.Point2D;

/**
 * Simple mutable 2d point class containing double x double y.
 * @author Created by NikhilVerma on 02/11/16.
 * @deprecated use JavaFX's Point2D class instead.
 */
public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point2D point2D) {
        this.x=point2D.getX();
        this.y=point2D.getY();
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
}
