package com.baurine.simplepullrefreshlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by baurine on 1/31/17.
 */

public class SimplePullRefreshLayout extends ViewGroup {

    public interface RefreshStateView {
        void setPercent(float percent);

        void setRefreshing(boolean refreshing);
    }

    /////////////////////////////////////////////
    public void setInterpolator(Interpolator interpolator) {
        scroller = new Scroller(getContext(), interpolator);
    }

    /////////////////////////////////////////////
    private static final float DEF_DRAG_COEFFICIENT = 0.7f;
    // dragCoefficient should between 0.0 and 1.0
    private float dragCoefficient = DEF_DRAG_COEFFICIENT;

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    /////////////////////////////////////////////
    private int touchSlop;
    private Scroller scroller;

    public SimplePullRefreshLayout(Context context) {
        this(context, null);
    }

    public SimplePullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setInterpolator(new DecelerateInterpolator());
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SimplePullRefreshLayout);
        extraHeight = typedArray.getDimensionPixelSize(
                R.styleable.SimplePullRefreshLayout_extra_height, -1);
        dragCoefficient = typedArray.getFloat(
                R.styleable.SimplePullRefreshLayout_drag_coefficient, DEF_DRAG_COEFFICIENT);
        if (dragCoefficient < 0.0f || dragCoefficient > 1.0f) {
            throw new RuntimeException("drag_coefficient should between 0.0 and 1.0");
        }
        typedArray.recycle();
    }

    ///////////////////////////////////////////
    private RefreshStateView refreshStateView;
    private View stateView, target;

    private int stateViewHeight;
    private int extraHeight;
    private int totalStateHeight;

    private float startY;
    private float lastY;

    private boolean isRefreshing = false;

    private void ensureTarget() {
        if (target != null) {
            return;
        }
        int childCount = getChildCount();
        if (childCount != 1 && childCount != 2) {
            throw new RuntimeException("Must have one or two children!");
        }
        if (childCount == 1) {
            RelativeLayout refreshView = new SimpleRefreshStateView(getContext());
            refreshView.setGravity(Gravity.CENTER);
            refreshView.setLayoutParams(new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addView(refreshView, 0);
        }
        stateView = getChildAt(0);
        if (!(stateView instanceof RefreshStateView)) {
            throw new RuntimeException("First child must be RefreshStateView");
        }
        refreshStateView = (RefreshStateView) stateView;
        target = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ensureTarget();
        if (target == null)
            return;

        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ensureTarget();
        if (target == null)
            return;

        stateView.layout(0, -stateView.getMeasuredHeight(),
                stateView.getMeasuredWidth(), 0);
        target.layout(0, 0, target.getMeasuredWidth(),
                target.getMeasuredHeight());
        stateViewHeight = stateView.getHeight();
        totalStateHeight = extraHeight > 0 ? stateViewHeight + extraHeight : stateViewHeight * 2;
    }

    ///////////////////////////////////////////
    private boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(target, -1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || canChildScrollUp() || isRefreshing) {
            return false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getRawY();
                lastY = startY;
                break;
            case MotionEvent.ACTION_MOVE:
                int diff = (int) ((ev.getRawY() - startY) * dragCoefficient);
                if (diff > touchSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float curY = event.getRawY();
                int scrollY = (int) ((lastY - curY) * dragCoefficient);
                int totalY = getScrollY() + scrollY;
                if (totalY > -totalStateHeight
                        && totalY < 0) {
                    scrollBy(0, scrollY);
                    float percent = totalY * -1.0f / stateViewHeight;
                    refreshStateView.setPercent(percent);
                }
                lastY = curY;
                break;
            case MotionEvent.ACTION_UP:
                if (isRefreshing) {
                    scroller.startScroll(0, getScrollY(), 0, -getScrollY() - stateViewHeight);
                } else if (getScrollY() < -stateViewHeight) {
                    setRefreshing(true, true);
                } else {
                    scroller.startScroll(0, getScrollY(), 0, -getScrollY());
                }
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    ///////////////////////////////////////////
    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();

            float percent = getScrollY() * -1.0f / stateViewHeight;
            refreshStateView.setPercent(percent);
        }
    }

    ///////////////////////////////////////////
    public interface OnRefreshListener {
        void onRefresh();
    }

    private OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    ///////////////////////////////////////////
    public void setRefreshing(boolean refreshing) {
        if (isRefreshing != refreshing) {
            setRefreshing(refreshing, false);
        }
    }

    private void setRefreshing(boolean refreshing, boolean notify) {
        if (isRefreshing != refreshing) {
            isRefreshing = refreshing;
            if (isRefreshing) {
                refreshStateView.setRefreshing(true);
            }
            if (refreshing) {
                scroller.startScroll(0, getScrollY(), 0, -getScrollY() - stateViewHeight);
                if (notify && onRefreshListener != null) {
                    onRefreshListener.onRefresh();
                }
            } else {
                scroller.startScroll(0, getScrollY(), 0, -getScrollY());
            }
            invalidate();
        }
    }
}
