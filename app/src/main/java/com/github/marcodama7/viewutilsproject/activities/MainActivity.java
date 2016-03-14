package com.github.marcodama7.viewutilsproject.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.marcodama7.tactilelayouts.listener.OnMovingListener;
import com.github.marcodama7.tactilelayouts.listener.OnPinchListener;
import com.github.marcodama7.tactilelayouts.utils.PointPosition;
import com.github.marcodama7.tactilelayouts.views.MovableLayout;
import com.github.marcodama7.tactilelayouts.views.PinchableLayout;
import com.github.marcodama7.tactilelayouts.views.SwappableLayout;
import com.github.marcodama7.viewutilsproject.R;

public class MainActivity extends AppCompatActivity {

    TextView log;

    MovableLayout movableLayout;
    SwappableLayout swappableLayout;
    PinchableLayout pinchableLayout;

    public static final String TAG = "view";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movableLayout = (MovableLayout) findViewById(R.id.frame0);
        swappableLayout = (SwappableLayout) findViewById(R.id.frame1);
        pinchableLayout = (PinchableLayout) findViewById(R.id.frame2);
        log = (TextView) findViewById(R.id.log);
        swappableLayout.setSwipeOrientation(SwappableLayout.SWIPE_ORIENTATION.HORIZONTAL);

        swappableLayout.setOnSwipeHorizontalListener(new SwappableLayout.OnSwipeHorizontalListener() {
            @Override
            public void onSwipeLeft() {
                log.setText("Swipe to LEFT");
            }

            @Override
            public void onSwipeRight() {
                log.setText("Swipe to RIGHT");
            }
        });

        swappableLayout.setOnSwipeVerticalListener(new SwappableLayout.OnSwipeVerticalListener() {
            @Override
            public void onSwipeTop() {
                log.setText("Swipe to TOP");
            }

            @Override
            public void onSwipeBottom() {
                log.setText("Swipe to BOTTOM");
            }
        });

        movableLayout.setOnMovingListener(new OnMovingListener() {
            @Override
            public void onStart(PointPosition position) {
                log.setText("Start Moving");
            }

            @Override
            public void onMove(PointPosition position, PointPosition offset) {
                String value = (position != null && offset != null) ? " moving_coords=("+position.getX()+";"+position.getY()+"); \n offset =("+offset.getX()+";"+offset.getY()+")" : "";
                Log.d(TAG, value);
                log.setText("OnMove");
            }

            @Override
            public void onEnd(PointPosition position, PointPosition offset) {
                log.setText("End Moving");
            }
        });

        pinchableLayout.setOnPinchListener(new OnPinchListener() {
            @Override
            public void onStartPinch(PointPosition pointer1, PointPosition pointer2) {
                log.setText("Start Pinch");
            }

            @Override
            public void onMovePinch(PointPosition pointer1, PointPosition pointer2, double ratio) {
                Log.d(TAG,"move pinch, ratio = "+new Double(ratio).toString());
                log.setText("Move pinch");
            }

            @Override
            public void onEndPinch(double ratio) {
                log.setText("End Pinch");
            }
        });
    }
}
