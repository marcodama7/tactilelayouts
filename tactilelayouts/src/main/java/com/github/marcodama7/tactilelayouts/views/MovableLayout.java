package com.github.marcodama7.tactilelayouts.views;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.github.marcodama7.tactilelayouts.listener.OnMovingListener;
import com.github.marcodama7.tactilelayouts.utils.PointPosition;

import java.util.Date;

public class MovableLayout extends FrameLayout {
    private static final String TAG = "movableiw";


    float startPositionX, startPositionY;
    private static final Integer MINSWAP_DP_X = 60;
    private static final Integer MINSWAP_DP_Y = 60;

    float minSwapDirectionX, minSwapDirectionY = 100;

    int parentWidth, parentHeight = 0;
    boolean deceleration = true;

    private boolean returnToStartPosition = false;

    public boolean isReturnToStartPosition() {
        return returnToStartPosition;
    }

    public void setReturnToStartPosition(boolean returnToStartPosition) {
        this.returnToStartPosition = returnToStartPosition;
    }

    public MovableLayout(Context context) {
        super(context);
        init(context,null,0);
    }

    public MovableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public MovableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        /*
        if (attrs != null) {
            final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MovableImageView, defStyle, 0);
            isHorizontal = typedArray.getBoolean(R.styleable.SwappableLayout_swappableVG_horizontal, true);
            typedArray.recycle();
        }
        if (context != null) {
            int deviceWidth = getContext().getResources().getDisplayMetrics().widthPixels;
            int deviceHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            minSwapDirectionX = deviceWidth * ratioSwapX;
            minSwapDirectionY = deviceHeight * ratioSwapY;
        }
        */

    }

    /****************************************************************************************************************
     * CREATION
     ****************************************************************************************************************/

    /**
     * Called after a view and all of its children has been inflated from XML.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /****************************************************************************************************************
     * LAYOUT
     ****************************************************************************************************************/

    /**
     * Called to determine the size requirements for this view and all of its children.
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * Called when this view should assign a size and position to all of its children.
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        startPositionX = getX();
        startPositionY = getY();
        if (getParent() != null && getParent() instanceof ViewGroup) {
            parentWidth = ((ViewGroup)getParent()).getMeasuredWidth();
            parentHeight = ((ViewGroup)getParent()).getMeasuredHeight();
        }
    }

    /**
     * Called when the size of this view has changed.
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /****************************************************************************************************************
     * DRAWING
     ****************************************************************************************************************/

    /**
     * Called when the view should render its content.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /****************************************************************************************************************
     * EVENT PROCESSING
     ****************************************************************************************************************/


    float lastX = -1;
    float lastY = -1;
    float offsetX = 0;
    float offsetY = 0;
    int numberOfPointers = 0;

    float firstX = 0;
    float firstY = 0;
    float firstPositionX = 0;
    float firstPositionY = 0;

    PointPosition[] pointPositions = new PointPosition[3];
    int pointPositionOffset = 0;
    PointPosition p = new PointPosition();
    int pointerId = -1;
    /**
     * Called when a touch screen motion event occurs.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                numberOfPointers = event.getPointerCount();
                pointerId = event.getPointerId(0);
                p = PointPosition.getRawPoint(p, this, event, pointerId);
                if (p != null) {
                    lastX = p.getX();
                    lastY = p.getY();
                    firstX = lastX;
                    firstY = lastY;
                    offsetX = 0;
                    offsetY = 0;
                    firstPositionX = getX();
                    firstPositionY = getY();
                    pointPositionOffset = 0;
                    pushValue(new PointPosition(firstX, firstY, new Date().getTime()));
                    if (onMovingListener != null) onMovingListener.onStart(new PointPosition(firstX,firstY));
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                numberOfPointers = event.getPointerCount();
                break;
            case MotionEvent.ACTION_UP:
                numberOfPointers = event.getPointerCount();
                p = PointPosition.getRawPoint(p, this, event, pointerId);
                if (p != null) {
                    lastX = p.getX();
                    lastY = p.getY();
                    pointPositions = pushValue(new PointPosition(p.getX(), p.getY(), new Date().getTime()));
                    pointPositionOffset++;
                    if (onMovingListener != null) onMovingListener.onMove(new PointPosition(lastX, lastY), new PointPosition(lastX - firstX, lastY - firstY));
                    if (isReturnToStartPosition()) {
                        returnToStartPosition();
                    }
                    else {
                        decelerate();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                numberOfPointers = event.getPointerCount();
                break;

            case MotionEvent.ACTION_MOVE:
                p = PointPosition.getRawPoint(p, this, event, pointerId);
                if (p != null) {
                    if (lastX <0) {
                        lastX = p.getX();
                        lastY = p.getY();
                    }
                    if (onMovingListener != null) onMovingListener.onMove(
                            new PointPosition(p.getX(), p.getY()),
                            new PointPosition(p.getX() - firstX, p.getY() - firstY)
                    );
                    numberOfPointers = event.getPointerCount();
                    if (numberOfPointers < 2) {
                        move(event);
                    }
                    pointPositions = pushValue(new PointPosition(p.getX(), p.getY(), new Date().getTime()));
                    pointPositionOffset++;
                }
                break;
        }
        return true;
    }


    private void move(float x, float y) {
        int parentWidth = ((ViewGroup) getParent()).getMeasuredWidth();
        int parentHeight = ((ViewGroup) getParent()).getMeasuredHeight();
        if (getMeasuredWidth() < parentWidth) {
            if (x<0){
                x = 0;
            }
            else if ( x > (((ViewGroup)getParent()).getWidth() - getMeasuredWidth())) {
                x = (((ViewGroup)getParent()).getWidth() - getMeasuredWidth());
            }
        }
        else {
            if (x < (parentWidth - getMeasuredWidth())) {
                x = parentWidth - getMeasuredWidth();
            }
            else if (x > 0) {
                x = 0;
            }
        }
        if (getMeasuredHeight() < parentHeight) {
            if (y<0){
                y = 0;
            }
            else if ( y > (((ViewGroup)getParent()).getMeasuredHeight() - getMeasuredHeight())) {
                y = (((ViewGroup)getParent()).getMeasuredHeight() - getMeasuredHeight());
            }
        }
        else {
            if (y < (parentHeight - getMeasuredHeight())) {
                y = parentHeight - getMeasuredHeight();
            }
            else if (y > 0) {
                y = 0;
            }
        }
        setX(x);
        setY(y);
    }

    private void move(MotionEvent event) {
        p = PointPosition.getRawPoint(p, this, event, pointerId);
        if (p != null) {
            offsetX = lastX - p.getX();
            offsetY = lastY - p.getY();
            lastX = p.getX();
            lastY = p.getY();
            float newX = getX() - offsetX;
            float newY = getY() - offsetY;
            move(newX, newY);
        }
    }


    public void returnToStartPosition() {
        moveAnimatedTo(firstPositionX, firstPositionY, 200);
    }

    public void moveAnimatedTo(float x, float y, long duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(pointPositionEvaluator, new PointPosition(getX(),getY()),  new PointPosition(x,y));
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointPosition p = (PointPosition) animation.getAnimatedValue();
                move(p.getX(), p.getY());
            }
        });
        valueAnimator.start();
    }

    private static PointPositionEvaluator pointPositionEvaluator = new PointPositionEvaluator();

    private static class PointPositionEvaluator implements TypeEvaluator<PointPosition> {

        @Override
        public PointPosition evaluate(float fraction, PointPosition startValue, PointPosition endValue) {
            PointPosition p = new PointPosition();
            float x = endValue.x - startValue.x;
            x = x * fraction;
            x = startValue.x + x;
            float y = endValue.y - startValue.y;
            y = y * fraction;
            y = startValue.y + y;
            p.setX(x);
            p.setY(y);
            return p;
        }
    }

    public PointPosition[] pushValue(PointPosition pointPosition) {
        if (pointPositionOffset <1) {
            pointPositions = new PointPosition[pointPositions.length];
        }
        if (pointPositionOffset < pointPositions.length) {
            pointPositions[pointPositionOffset] = pointPosition;
            pointPositionOffset ++;
        }
        else {
            for (int i=0; i< pointPositions.length - 1; i++) {
                pointPositions[i] = pointPositions[i+1];
            }
            pointPositions[pointPositions.length - 1] = pointPosition;
        }
        return pointPositions;
    }

    private OnMovingListener onMovingListener;

    public void setOnMovingListener(OnMovingListener onMovingListener) {
        this.onMovingListener = onMovingListener;
    }

    public void setDeceleration(boolean deceleration) {
        this.deceleration = deceleration;
    }


    private void decelerate() {
        float deltaX = 0;
        float deltaY = 0;
        long deltaTime = 0;

        int totPositions = (pointPositionOffset >=pointPositions.length) ? pointPositions.length : pointPositionOffset;

        if (totPositions >= pointPositions.length) {
            try {
                deltaX = pointPositions[totPositions - 1].getX() - pointPositions[0].getX();
                deltaY = pointPositions[totPositions - 1].getY() - pointPositions[0].getY();
                deltaTime = pointPositions[totPositions - 1].getTimestamp() - pointPositions[0].getTimestamp();

                if (deltaTime > 0) {
                    double speedX = deltaX / (deltaTime + 0.0d);
                    double speedY = deltaY / (deltaTime + 0.0d);
                    float endX = Float.parseFloat(speedX * 100+"");
                    float endY = Float.parseFloat(speedY * 100+"");
                    moveAnimatedTo(getX() + endX, getY() + endY, 200);
                }
            }
            catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

    }
}
