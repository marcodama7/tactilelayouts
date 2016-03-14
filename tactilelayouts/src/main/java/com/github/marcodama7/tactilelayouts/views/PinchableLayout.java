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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.marcodama7.tactilelayouts.listener.OnMovingListener;
import com.github.marcodama7.tactilelayouts.listener.OnPinchListener;
import com.github.marcodama7.tactilelayouts.utils.PointPosition;

import java.util.Date;

public class PinchableLayout extends MovableLayout {

    private static final String TAG = "pinchable";

    float startPositionX, startPositionY;
    int startWidth, startHeight = 0;
    private static final Integer MINSWAP_DP_X = 60;
    private static final Integer MINSWAP_DP_Y = 60;

    float minSwapDirectionX, minSwapDirectionY = 100;

    int minWidth = 80, minHeight = 80;
    int maxWidth = 99999999, maxHeight = 99999999;



    int parentWidth, parentHeight = 0;
    boolean deceleration = true;

    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    private boolean returnToStartPosition = false;

    OnPinchListener onPinchListener;
    static float minOffsetX = 80.0f;
    static float minOffsetY = 80.0f;


    public PinchableLayout(Context context) {
        super(context);
        init(context,null,0);
    }

    public PinchableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public PinchableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {


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

    double ratio = -1;
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
        if (ratio <0) {
            ratio = getMeasuredWidth() / (getMeasuredHeight() + 0.0d);
        }
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


    float lastX1 = -1;
    float lastY1 = -1;
    float lastX2 = -1;
    float lastY2 = -1;

    float offsetX1 = 0;
    float offsetY1 = 0;
    float offsetX2 = 0;
    float offsetY2 = 0;
    int numberOfPointers = 0;

    float firstX1 = 0;
    float firstY1 = 0;
    float firstX2 = 0;
    float firstY2 = 0;
    float firstPositionX = 0;
    float firstPositionY = 0;

    double firstPointerDistance = 0;
    double lastPointerDistance = 0;
    int firstWidth, firstHeight = 0;

    PointPosition[] pointPositions = new PointPosition[3];
    int pointPositionOffset = 0;

    int firstPointerId = -1;
    int secondPointerId = -1;
    boolean pinchStarted = false;
    PointPosition p1 = new PointPosition();
    PointPosition p2 = new PointPosition();

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
                firstPointerId = -1;
                secondPointerId = -1;
                firstPointerId = event.getPointerId(0);
                numberOfPointers = event.getPointerCount();
                p1 = PointPosition.getRawPoint(p1, this, event, firstPointerId);
                lastX1 = p1.getX();
                lastY1 = p1.getY();
                firstX1 = lastX1;
                firstY1 = lastY1;
                offsetX1 = 0;
                offsetY1 = 0;
                firstPositionX = getLeftMargin();
                firstPositionY = getTopMargin();
                pointPositionOffset = 0;
                firstPointerDistance = 0;
                lastPointerDistance = 0;
                pinchStarted = false;
                firstWidth = _getWidth();
                firstHeight = _getHeight();

                viewOldPositionX = null;
                viewOldPositionY = null;

                pushValue(new PointPosition(firstX1, firstY1, new Date().getTime()));
                if (onMovingListener != null) onMovingListener.onStart(new PointPosition(firstX1, firstY1));
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                numberOfPointers = event.getPointerCount();
                firstMarginLeft = getLeftMargin();
                firstMarginTop = getTopMargin();
                pinchRatio = 1.0d;
                pinchStarted = true;
                if (numberOfPointers ==2) {
                    for (int i=0; i< event.getPointerCount(); i++) {
                        if (firstPointerId != event.getPointerId(i)) {
                            secondPointerId = event.getPointerId(i);
                            break;
                        }
                    }
                    p2 = PointPosition.getRawPoint(p2, this, event, secondPointerId);

                    if (onPinchListener != null) {
                        onPinchListener.onStartPinch(p1, p2);
                    }

                    firstX2 = p2.getX();
                    firstY2 = p2.getY();
                    lastX2 = firstX2;
                    lastY2 = firstY2;
                    offsetX2 = 0;
                    offsetY2 = 0;
                    firstPointerDistance = getDistance(new PointPosition(lastX1, lastY1), new PointPosition(lastX2, lastY2));
                    lastPointerDistance = firstPointerDistance;
                    firstWidth = _getWidth();
                    firstHeight = _getHeight();
                }
                break;

            case MotionEvent.ACTION_UP:
                numberOfPointers = event.getPointerCount();
                if (!pinchStarted) {
                    p1 = PointPosition.getRawPoint(p1, this, event, firstPointerId);
                    if (p1 != null) {
                        lastX1 = p1.getX();
                        lastY1 = p1.getY();
                        pointPositions = pushValue(new PointPosition(lastX1, lastY1, new Date().getTime()));
                        pointPositionOffset++;
                        if (onMovingListener != null) onMovingListener.onMove(new PointPosition(lastX1, lastY1), new PointPosition(lastX1 - firstX1, lastY1 - firstY1));
                        decelerate();
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                numberOfPointers = event.getPointerCount();
                if (numberOfPointers < 2 && onPinchListener != null) {
                    onPinchListener.onEndPinch(pinchRatio);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                numberOfPointers = event.getPointerCount();
                if (numberOfPointers == 1 && (!pinchStarted)) {
                    p1 = PointPosition.getRawPoint(p1, this, event, firstPointerId);
                    if (lastX1 <0) {
                        lastX1 = p1.getX();
                        lastY1 = p1.getY();
                    }
                    if (onMovingListener != null) onMovingListener.onMove(
                            new PointPosition(p1.getX(), p1.getY()),
                            new PointPosition(p1.getX() - firstX1, p1.getY() - firstY1)
                    );
                    move(event);
                    pointPositions = pushValue(new PointPosition(p1.getX(), p1.getY(), new Date().getTime()));
                    pointPositionOffset++;
                }
                else if (numberOfPointers == 2) {
                    pinch(event);
                }
                break;
        }
        return true;
    }


    int firstMarginLeft = -1;
    int firstMarginTop = -1;
    double pinchRatio = 0.0d;

    private void pinch(MotionEvent event) {

        p1 = PointPosition.getRawPoint(p1, this, event, firstPointerId);
        p2 = PointPosition.getRawPoint(p2, this, event, secondPointerId);
        if (p1 != null && p2 !=null) {
            offsetX1 = lastX1 - p1.getX();
            offsetY1 = lastY1 - p1.getY();
            offsetX2 = lastX2 - p2.getX();
            offsetY2 = lastY2 - p2.getY();
            double distance = getDistance(p1, p2);
            int oldWidth = getMeasuredWidth();
            int oldHeight = getMeasuredHeight();
            double offset = distance - firstPointerDistance;
            lastPointerDistance = distance;

            double offsetWidth = offset;
            int newWidth = (int) Math.floor(firstWidth + offsetWidth);
            int newHeight = (int) Math.floor(newWidth / ratio);

            if ((newWidth < newHeight) && (newWidth < minWidth))  {
                newWidth = minWidth;
                newHeight = (int) Math.floor(newWidth / ratio);
            }
            else if (newWidth>=newHeight && newHeight < minHeight) {
                newHeight = minHeight;
                newWidth = (int) Math.floor(newWidth * ratio);
            }
            else if ((newWidth < newHeight) && (newWidth > maxWidth))  {
                newWidth = maxWidth;
                newHeight = (int) Math.floor(newWidth / ratio);
            }
            else if (newWidth>=newHeight && newHeight > maxHeight)  {
                newHeight = maxHeight;
                newWidth = (int) Math.floor(newWidth * ratio);
            }


            int offX = (int) Math.floor((newWidth - oldWidth) / (2.0d));
            int offY = (int) Math.floor((newHeight - oldHeight) / (2.0d));

            setMeasuredDimension(newWidth, newHeight);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = newWidth;
            params.height = newHeight;
            int left = getLeftMargin() - offX;
            int top = getTopMargin() - offY;

            int right = parentWidth - (left + newWidth);
            int bottom = parentHeight - (top + newHeight);
            setMargins(params, left,top, right,bottom);
            setLayoutParams(params);
            pinchRatio = (newWidth / (oldWidth + 0.0));
        }
        if (onPinchListener != null) {
            onPinchListener.onMovePinch(p1,p2,pinchRatio);
        }

    }



    private int _getWidth(){
        ViewGroup.LayoutParams params = getLayoutParams();
        return params.width;
    }

    private int _getHeight(){
        ViewGroup.LayoutParams params = getLayoutParams();
        return params.height;
    }


    Float viewOldPositionX = null, viewOldPositionY = null;

    private void move(float newX, float newY ) {
        ViewGroup.LayoutParams params = getLayoutParams();
        int parentWidth = ((ViewGroup) getParent()).getMeasuredWidth();
        int parentHeight = ((ViewGroup) getParent()).getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        if (newX < 0 && (newX + measuredWidth < minOffsetX)) {
            newX = - measuredWidth + minOffsetX;
        }
        else if (newX > (parentWidth-minOffsetX)) {
            newX = parentWidth - minOffsetX;
        }

        if (newY < 0 && (newY + measuredHeight < minOffsetY)) {
            newY = - measuredHeight + minOffsetY;
        }
        else if (newY > (parentHeight-minOffsetY)) {
            newY = parentHeight - minOffsetY;
        }

        int rightMargin = (int) Math.floor(parentWidth - (newX - getMeasuredWidth()));
        int bottomMargin = (int) Math.floor(parentHeight - (newY - getMeasuredHeight()));
        setMargins(params, (int)Math.floor(newX), (int)Math.floor(newY), rightMargin, bottomMargin);
    }

    private void move(MotionEvent event) {
        offsetX1 = lastX1 - event.getRawX();
        offsetY1 = lastY1 - event.getRawY();
        lastX1 = event.getRawX();
        lastY1 = event.getRawY();
        float newX = getLeftMargin() - offsetX1;
        float newY = getTopMargin() - offsetY1;

        move(newX, newY);
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

    public double getDistance(PointPosition p1, PointPosition p2) {
        return Math.sqrt(Math.pow(p2.x -p1.x,2) + Math.pow(p2.y -p1.y,2));
    }


    public void resetProportion() {
        ratio = -1;
    }


    public void setProportion(double ratio) {
        this.ratio = ratio;
    }

    private ViewGroup.LayoutParams setDimension(int newWidth, int newHeight) {
        return setDimension(null, newWidth, newHeight);
    }

    private ViewGroup.LayoutParams setDimension(ViewGroup.LayoutParams layoutParams, int newWidth, int newHeight) {
        ViewGroup.LayoutParams params =(layoutParams != null) ? layoutParams : getLayoutParams();
        if (params == null) {
            return null;
        }
        layoutParams.width = newWidth;
        layoutParams.height = newHeight;
        setLayoutParams(params);
        return params;
    }

    public int getLeftMargin() {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) return -1;
        if (params instanceof LinearLayout.LayoutParams)
            return ((LinearLayout.LayoutParams)params).leftMargin;
        else if (params instanceof RelativeLayout.LayoutParams)
            return ((RelativeLayout.LayoutParams)params).leftMargin;
        else if (params instanceof FrameLayout.LayoutParams)
            return ((FrameLayout.LayoutParams)params).leftMargin;
        return getLeft();
    }

    public int getTopMargin() {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) return -1;
        if (params instanceof LinearLayout.LayoutParams)
            return ((LinearLayout.LayoutParams)params).topMargin;
        else if (params instanceof RelativeLayout.LayoutParams)
            return ((RelativeLayout.LayoutParams)params).topMargin;
        else if (params instanceof FrameLayout.LayoutParams)
            return ((FrameLayout.LayoutParams)params).topMargin;
        return getTop();
    }

    public int getRightMargin() {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) return -1;
        if (params instanceof LinearLayout.LayoutParams)
            return ((LinearLayout.LayoutParams)params).rightMargin;
        else if (params instanceof RelativeLayout.LayoutParams)
            return ((RelativeLayout.LayoutParams)params).rightMargin;
        else if (params instanceof FrameLayout.LayoutParams)
            return ((FrameLayout.LayoutParams)params).rightMargin;
        return getRight();
    }

    public int getBottomMargin() {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) return -1;
        if (params instanceof LinearLayout.LayoutParams)
            return ((LinearLayout.LayoutParams)params).bottomMargin;
        else if (params instanceof RelativeLayout.LayoutParams)
            return ((RelativeLayout.LayoutParams)params).bottomMargin;
        else if (params instanceof FrameLayout.LayoutParams)
            return ((FrameLayout.LayoutParams)params).bottomMargin;
        return getBottom();
    }

    public void setLeftMargin(int leftMargin) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            setLeftMargin(leftMargin);
            return;
        }
        else if (params instanceof LinearLayout.LayoutParams)
            ((LinearLayout.LayoutParams)params).leftMargin = leftMargin;
        else if (params instanceof RelativeLayout.LayoutParams)
            ((RelativeLayout.LayoutParams)params).leftMargin = leftMargin;
        else if (params instanceof FrameLayout.LayoutParams)
            ((FrameLayout.LayoutParams)params).leftMargin = leftMargin;
        setLayoutParams(params);
    }


    public ViewGroup.LayoutParams setMargins(int left, int top, int right, int bottom) {
        return setMargins(null, left, top, right, bottom);
    }

    public ViewGroup.LayoutParams setMargins(ViewGroup.LayoutParams layoutParams, int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams params = (layoutParams != null) ? layoutParams : getLayoutParams();
        if (params == null) {
            setTop(top);
            setBottom(bottom);
            setRight(right);
            setLeft(left);
            return null;
        }
        else if (params instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) params).topMargin = top;
            ((LinearLayout.LayoutParams) params).bottomMargin = bottom;
            ((LinearLayout.LayoutParams) params).leftMargin = left;
            ((LinearLayout.LayoutParams) params).rightMargin = right;
        }
        else if (params instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) params).topMargin = top;
            ((RelativeLayout.LayoutParams) params).bottomMargin = bottom;
            ((RelativeLayout.LayoutParams) params).leftMargin = left;
            ((RelativeLayout.LayoutParams) params).rightMargin = right;

        }
        else if (params instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) params).topMargin = top;
            ((FrameLayout.LayoutParams) params).bottomMargin = bottom;
            ((FrameLayout.LayoutParams) params).leftMargin = left;
            ((FrameLayout.LayoutParams) params).rightMargin = right;
        }
        setLayoutParams(params);
        return layoutParams;
    }



    public void setVerticalMargin(int top, int bottom) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            setTop(top);
            setBottom(bottom);
            return;
        }
        else if (params instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) params).topMargin = top;
            ((LinearLayout.LayoutParams) params).bottomMargin = bottom;
        }
        else if (params instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) params).topMargin = top;
            ((RelativeLayout.LayoutParams) params).bottomMargin = bottom;
        }
        else if (params instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) params).topMargin = top;
            ((FrameLayout.LayoutParams) params).bottomMargin = bottom;
        }
        setLayoutParams(params);
    }

    public void setHorizontallMargin(int left, int right) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            setLeft(left);
            setRight(right);
            return;
        }
        else if (params instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) params).leftMargin = left;
            ((LinearLayout.LayoutParams) params).rightMargin = right;
        }
        else if (params instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) params).leftMargin = left;
            ((RelativeLayout.LayoutParams) params).rightMargin = right;
        }
        else if (params instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) params).leftMargin = left;
            ((FrameLayout.LayoutParams) params).rightMargin = right;
        }
        setLayoutParams(params);
    }

    public void setTopMargin(int topMargin) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            setTop(topMargin);
            return;
        }
        else if (params instanceof LinearLayout.LayoutParams)
            ((LinearLayout.LayoutParams)params).topMargin = topMargin;
        else if (params instanceof RelativeLayout.LayoutParams)
            ((RelativeLayout.LayoutParams)params).topMargin = topMargin;
        else if (params instanceof FrameLayout.LayoutParams)
            ((FrameLayout.LayoutParams)params).topMargin = topMargin;
        setLayoutParams(params);
    }

    public void setRightMargin(int rightMargin) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            setRight(rightMargin);
            return;
        }
        else if (params instanceof LinearLayout.LayoutParams)
            ((LinearLayout.LayoutParams)params).rightMargin = rightMargin;
        else if (params instanceof RelativeLayout.LayoutParams)
            ((RelativeLayout.LayoutParams)params).rightMargin = rightMargin;
        else if (params instanceof FrameLayout.LayoutParams)
            ((FrameLayout.LayoutParams)params).rightMargin = rightMargin;
        setLayoutParams(params);
    }

    public void setBottomMargin(int bottomMargin) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            setBottom(bottomMargin);
            return;
        }
        else if (params instanceof LinearLayout.LayoutParams)
            ((LinearLayout.LayoutParams)params).bottomMargin = bottomMargin;
        else if (params instanceof RelativeLayout.LayoutParams)
            ((RelativeLayout.LayoutParams)params).bottomMargin = bottomMargin;
        else if (params instanceof FrameLayout.LayoutParams)
            ((FrameLayout.LayoutParams)params).bottomMargin = bottomMargin;
        setLayoutParams(params);
    }

    public void setOnPinchListener(OnPinchListener onPinchListener) {
        this.onPinchListener = onPinchListener;
    }


    @Override
    public void setX(float x) {
        //super.setX(x);
        int right = (int) Math.floor(parentWidth - (x + getMeasuredWidth()));
        setMargins((int)Math.floor(x), getTopMargin(), right, getBottomMargin());
    }

    @Override
    public void setY(float y) {
        //super.setX(x);
        int bottom = (int) Math.floor(parentHeight - (y + getMeasuredHeight()));
        setMargins(getLeftMargin(), getTopMargin(), (int)Math.floor(y), bottom);
    }


    @Override
    public float getX() {
        //return super.getX();
        return getLeftMargin();
    }

    @Override
    public float getY() {
        return getTopMargin();
    }
}
