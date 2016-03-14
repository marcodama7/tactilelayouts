package com.github.marcodama7.tactilelayouts.views;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.marcodama7.tactilelayouts.utils.PointPosition;


public class SwappableLayout extends FrameLayout {
    private static final String TAG = "swappable";

    Boolean isHorizontal = null;

    float startPositionX, startPositionY;
    private static final Integer MINSWAP_DP_X = 80;
    private static final Integer MINSWAP_DP_Y = 80;

    float minSwapDirectionX, minSwapDirectionY = 100;
    float ratioSwapX = 0.20f, ratioSwapY = 0.20f;
    int parentWidth, parentHeight = 0;

    private enum DIRECTION {
        LEFT, TOP, RIGHT, BOTTOM
    }

    public enum SWIPE_ORIENTATION {
        HORIZONTAL, VERTICAL, BOTH
    }

    public SwappableLayout(Context context) {
        super(context);
        init(context,null,0);
    }

    public SwappableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public SwappableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        isHorizontal = null;
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

        minSwapDirectionX = (int)(Math.floor((MINSWAP_DP_X * (getResources().getDisplayMetrics().densityDpi / 160))));
        minSwapDirectionY = (int)(Math.floor((MINSWAP_DP_Y * (getResources().getDisplayMetrics().densityDpi / 160))));


    }

    public void setSwipeOrientation(SWIPE_ORIENTATION swipeOrientation) {
        if (swipeOrientation != null) {
            switch (swipeOrientation) {
                case HORIZONTAL:
                    setOnlyHorizontal();
                    break;
                case VERTICAL:
                    setOnlyVertical();
                    break;
                case BOTH:
                    setHorizontalVertical();
                    break;
            }
        }
    }


    private void setOnlyHorizontal() {
        isHorizontal = true;
    }

    private void setOnlyVertical() {
        isHorizontal = false;
    }

    private void setHorizontalVertical() {
        isHorizontal = null;
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
    double swapDistance = 0;
    double swapOffset = 0;
    int numberOfPointers = 0;
    boolean isSwapping = false;
    float firstPositionX;
    float firstPositionY;

    float firstX = 0;
    float firstY = 0;
    int pointerId = -1;

    PointPosition p = new PointPosition();
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
                    firstPositionX = getX();
                    firstPositionY = getY();
                    offsetX = 0;
                    offsetY = 0;
                    isSwapping = false;
                    swapDistance  = -1;
                    movingHorizontal = null;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                numberOfPointers = event.getPointerCount();
                if (numberOfPointers > 1) {
                    isSwapping = true;
                    swapDistance = 0;
                }
                swapDistance  = -1;
                break;

            case MotionEvent.ACTION_UP:
                numberOfPointers = event.getPointerCount();
                p = PointPosition.getRawPoint(p, this, event, pointerId);
                if ( p != null) {
                    DIRECTION direction = getSwapDirection(p.getX(), getY());
                    lastX = p.getX();
                    lastY = p.getY();
                    isSwapping = false;
                    swapDistance = -1;
                    swipeTo(direction);
                    movingHorizontal = null;
                    //returnToStartPosition();
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
                    numberOfPointers = event.getPointerCount();
                    if (numberOfPointers   < 2) {
                        offsetX = lastX - p.getX();
                        offsetY = lastY - p.getY();
                        move(event);
                    }
                }
                break;
        }

        return true;
    }

    private DIRECTION getSwapDirection(float currentX, float currentY) {
        DIRECTION direction = null;
        if (isHorizontal == null) {
            if (movingHorizontal == null) {
                return null;
            }
            else {
                if (movingHorizontal) {
                    if (Math.abs(currentX - firstX) > minSwapDirectionX) {
                        return (currentX>firstX) ? DIRECTION.RIGHT : DIRECTION.LEFT;
                    }
                    return null;
                }
                else {
                    if (Math.abs(currentY - firstY) > minSwapDirectionY) {
                        return (currentY>firstY) ? DIRECTION.BOTTOM : DIRECTION.TOP;
                    }
                    return null;
                }
            }
        }
        else if (isHorizontal) {
            if (Math.abs(currentX - firstX) > minSwapDirectionX) {
                return (currentX>firstX) ? DIRECTION.RIGHT : DIRECTION.LEFT;
            }
            return null;
        }
        else {
            if (Math.abs(currentY - firstY) > minSwapDirectionY) {
                return (currentY>firstY) ? DIRECTION.BOTTOM : DIRECTION.TOP;
            }
            return null;
        }
    }

    Boolean movingHorizontal = null;

    private void move(MotionEvent event) {
        p = PointPosition.getRawPoint(p, this, event, pointerId);
        if (p != null) {
            if (isHorizontal == null) {
                if (movingHorizontal == null) {
                    offsetX = lastX - p.getX();
                    offsetY = lastY - p.getY();
                    lastX = p.getX();
                    lastY = p.getY();
                    if (Math.max(Math.abs(lastX - firstX), Math.abs(lastY - firstY)) > 5) {
                        if (Math.abs(lastX - firstX) > Math.abs(lastY - firstY)) {
                            movingHorizontal = true;
                        } else {
                            movingHorizontal = false;
                        }
                    }
                } else {
                    offsetX = (movingHorizontal) ? lastX - p.getX() : 0;
                    offsetY = (movingHorizontal) ? 0 : lastY - p.getY();
                    lastX = p.getX();
                    lastY = p.getY();
                }
            }
            else {
                offsetX = (isHorizontal) ? lastX - p.getX() : 0;
                offsetY = (!isHorizontal) ? lastY - p.getY() : 0;
                lastX = p.getX();
                lastY = p.getY();
            }
            setX(getX()-offsetX);
            setY(getY()-offsetY);
        }

    }

    public void returnToStartPosition() {
        if (isHorizontal == null) {
            if (movingHorizontal == null) {
                moveAnimatedTo(startPositionX, startPositionY, 200, null);
            }
            else {
                if (movingHorizontal) {
                    moveAnimatedTo(startPositionX, getY(), 200, null);
                }
                else {
                    moveAnimatedTo(getX(), startPositionY, 200, null);
                }
            }
        }
        else if (isHorizontal) {
            moveAnimatedTo(startPositionX, getY(), 200, null);
        }
        else {
            moveAnimatedTo(getX(), startPositionY, 200, null);
        }
    }

    private void swipeTo(DIRECTION direction) {
        if (direction == null) returnToStartPosition();
        else {

            switch (direction) {
                case TOP:
                    moveAnimatedTo(firstPositionX, -getHeight(), 200, new OnSwipeAnimationEndListener() {
                        @Override
                        public void onEndAnimation() {
                            if (onSwipeVerticalListener != null) onSwipeVerticalListener.onSwipeTop();
                        }
                    });
                    break;
                case LEFT:
                    moveAnimatedTo(-getWidth(), firstPositionY, 200, new OnSwipeAnimationEndListener() {
                        @Override
                        public void onEndAnimation() {
                            if (onSwipeHorizontalListener != null) onSwipeHorizontalListener.onSwipeLeft();
                        }
                    });
                    break;
                case BOTTOM:
                    moveAnimatedTo(firstPositionX, parentHeight, 200, new OnSwipeAnimationEndListener() {
                        @Override
                        public void onEndAnimation() {
                            if (onSwipeVerticalListener != null) onSwipeVerticalListener.onSwipeBottom();
                        }
                    });
                    break;
                case RIGHT:
                    moveAnimatedTo(parentWidth, firstPositionY, 200, new OnSwipeAnimationEndListener() {
                        @Override
                        public void onEndAnimation() {
                            if (onSwipeHorizontalListener != null) onSwipeHorizontalListener.onSwipeRight();
                        }
                    });
                    break;
            }
        }
    }

    private void moveAnimatedTo(float x, float y, long duration, final OnSwipeAnimationEndListener onSwipeAnimationEndListener) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(pointPositionEvaluator, new PointPosition(getX(),getY()),  new PointPosition(x,y));
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointPosition p = (PointPosition) animation.getAnimatedValue();
                setX(p.getX());
                setY(p.getY());
            }
        });
        if (onSwipeAnimationEndListener != null) {
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    onSwipeAnimationEndListener.onEndAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
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


    public interface OnSwipeHorizontalListener {
        void onSwipeLeft();
        void onSwipeRight();
    }

    public interface OnSwipeVerticalListener {
        void onSwipeTop();
        void onSwipeBottom();
    }

    private OnSwipeHorizontalListener onSwipeHorizontalListener;
    private OnSwipeVerticalListener onSwipeVerticalListener;


    public void setOnSwipeVerticalListener(OnSwipeVerticalListener onSwipeVerticalListener) {
        this.onSwipeVerticalListener = onSwipeVerticalListener;
    }

    public void setOnSwipeHorizontalListener(OnSwipeHorizontalListener onSwipeHorizontalListener) {
        this.onSwipeHorizontalListener = onSwipeHorizontalListener;
    }

    private interface OnSwipeAnimationEndListener {
        void onEndAnimation();
    }
}
