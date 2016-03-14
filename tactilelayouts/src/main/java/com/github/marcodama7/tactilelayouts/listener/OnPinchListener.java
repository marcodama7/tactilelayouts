package com.github.marcodama7.tactilelayouts.listener;


import com.github.marcodama7.tactilelayouts.utils.PointPosition;

public interface OnPinchListener {

    void onStartPinch(PointPosition pointer1, PointPosition pointer2);
    void onMovePinch(PointPosition pointer1, PointPosition pointer2, double ratio);
    void onEndPinch(double ratio);

}
