package com.github.marcodama7.tactilelayouts.listener;


import com.github.marcodama7.tactilelayouts.utils.PointPosition;

public interface OnMovingListener {
    void onStart(PointPosition position);
    void onMove(PointPosition position, PointPosition offset);
    void onEnd(PointPosition position, PointPosition offset);
}
