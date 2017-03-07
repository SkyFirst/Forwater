package com.example.lyk.forwater.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.example.lyk.forwater.utils.DataInfoUtils;

/**
 * Created by lyk on 2017/3/3.
 */

public class LinearLayoutEx extends LinearLayout {
    private int interceptOldx;
    private int oldx;
    private static int distance;
    public Scroller mScroller;
    private boolean isend = true;
    private TouchListener touchListener;
    public LinearLayoutEx(Context context) {
        super(context);
        distance = DataInfoUtils.dip2px(context, 160);
        mScroller = new Scroller(context);
    }

    public LinearLayoutEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        distance = DataInfoUtils.dip2px(context, 160);
        mScroller = new Scroller(context);

    }

    public LinearLayoutEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        distance = DataInfoUtils.dip2px(context, 160);
        mScroller = new Scroller(context);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - interceptOldx) != 0) {
                    oldx = -1;
                    intercept = true;
                } else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        interceptOldx = x;
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (ListViewEx.linearLayout != null && !ListViewEx.linearLayout.equals(this) && isend) {
                    isend = false;
                    ListViewEx.linearLayout.mScroller.startScroll(ListViewEx.linearLayout.getScrollX(), 0, -ListViewEx.linearLayout.getScrollX(), 0, 100);
                    ListViewEx.linearLayout.invalidate();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ListViewEx.linearLayout = null;
                            isend = true;
                        }
                    }, 100);
                    return true;
                }
                if (oldx != -1 && (ListViewEx.linearLayout == null || ListViewEx.linearLayout.equals(this))) {
                    int des = getScrollX() + oldx - x;
                    if (des <= distance && des >= 0)
                        scrollBy(oldx - x, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(oldx==-1||getScrollX()!=0) {
                    if (getScrollX() == distance)
                    {
                        mScroller.startScroll(distance,0,-distance,0,500);
                    }
                    else
                    {
                        int des;
                        if (getScrollX() > distance / 2) {
                            des = distance - getScrollX();
                        } else
                            des = -getScrollX();
                        mScroller.startScroll(getScrollX(), 0, des, 0, 500);
                    }
                    invalidate();
                }
                else if(touchListener!=null&&event.getAction()!=MotionEvent.ACTION_CANCEL)
                {
                    touchListener.onTouch();
                }
                break;

        }
        oldx = x;
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.getFinalX() != 0 && ListViewEx.linearLayout == null)
            ListViewEx.linearLayout = this;
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
    }
    public  void setTouchListener(TouchListener touchListener)
    {
        this.touchListener=touchListener;
    }

}
