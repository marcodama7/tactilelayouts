package com.github.marcodama7.tactilelayouts.utils;

import android.view.MotionEvent;
import android.view.View;

public class PointPosition {

    public float x;
    public float y;
    public long timestamp;

    public PointPosition(){

    }

    public PointPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    public PointPosition(float x, float y, long timestamp){
        this.x = x;
        this.y = y;
        this.timestamp = timestamp;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double getDiagonal() {
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static PointPosition getRawPoint(PointPosition p, View view, MotionEvent event, int pointerId) {
        if (p == null) p = new PointPosition();
        int pointerIndex = event.findPointerIndex(pointerId);
        if (pointerId < 0 || pointerIndex <0 || pointerIndex>=event.getPointerCount()) {
            return null;
        }
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        final int location[] = {0, 0};
        view.getLocationOnScreen(location);
        p.setX(x+location[0]);
        p.setY(y+location[1]);
        //p.setTimestamp(new Date().getTime());
        return p;
    }

}
