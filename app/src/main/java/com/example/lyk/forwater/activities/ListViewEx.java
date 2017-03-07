package com.example.lyk.forwater.activities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.lyk.forwater.R;
import com.example.lyk.forwater.utils.DataInfoUtils;

/**
 * Created by lyk on 2017/3/3.
 */

public class ListViewEx extends ListView {
    private int oldx;
    private int oldy;
    private int mTouchSlop;
    private int oldy2;
    private int oldheight;
    public static LinearLayoutEx linearLayout;
    private static int maxdistance;
    private Scroller mScroller;
    private int distance;
    private RelativeLayout header;
    private ValueAnimator valueAnimator;
    private boolean isShowPush = false;
    private boolean isShowPull = false;
    private boolean isFullItem;
    private boolean isLoading=false;
    private boolean isOpenRefresh=true;
    private Runnable runnable;
    private Animation loadAnimation;

    public ListViewEx(Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        maxdistance = -DataInfoUtils.dip2px(context, 250);
        mScroller = new Scroller(context);
    }

    public ListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        maxdistance = -DataInfoUtils.dip2px(context, 250);
        mScroller = new Scroller(context);
    }

    public ListViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        maxdistance = -DataInfoUtils.dip2px(context, 250);
        mScroller = new Scroller(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                if (valueAnimator != null && valueAnimator.isRunning()) {
                    intercept = true;
                    valueAnimator.cancel();
                }
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - oldx) < Math.abs(y - oldy) && Math.abs(y - oldy) > mTouchSlop) {
                    intercept = true;
                    oldy2 = -1;
                } else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        oldx = x;
        oldy = y;
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (oldy2 == -1) {
                    break;
                }
                if (isOpenRefresh &&!isLoading) {
                    int dis = -(header.getHeight() + (y - oldy2) / 3);
                    if((getScrollY() <= 0 && dis < 0 && dis >= maxdistance))
                    {
                        ViewGroup.MarginLayoutParams marginLayoutParams = (MarginLayoutParams) header.getLayoutParams();
                        marginLayoutParams.height = -dis;
                        header.setLayoutParams(marginLayoutParams);
                        if (dis > maxdistance / 6) {
                            header.setVisibility(View.INVISIBLE);
                        } else if (header.getVisibility() == INVISIBLE && dis <= maxdistance / 6 && dis < oldheight)//下拉显示
                        {
                            isShowPush = false;
                            header.setVisibility(VISIBLE);
                            header.findViewById(R.id.pullOrPush).setBackgroundResource(R.mipmap.pull);
                            ((TextView) header.findViewById(R.id.ticker)).setText("下拉刷新");
                        } else if (!isShowPush && dis <= maxdistance / 3 && dis < oldheight)//释放
                        {
                            isShowPush = true;
                            isShowPull = false;
                            Animation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            animation.setDuration(100);
                            header.findViewById(R.id.pullOrPush).startAnimation(animation);
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    header.findViewById(R.id.pullOrPush).setBackgroundResource(R.mipmap.push);
                                }
                            }, 100);
                            ((TextView) header.findViewById(R.id.ticker)).setText("释放刷新");
                        } else if (!isShowPull && dis >= maxdistance / 3 && dis > oldheight) {
                            isShowPull = true;
                            isShowPush = false;
                            Animation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            animation.setDuration(100);
                            header.findViewById(R.id.pullOrPush).startAnimation(animation);
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    header.findViewById(R.id.pullOrPush).setBackgroundResource(R.mipmap.pull);
                                }
                            }, 100);
                            ((TextView) header.findViewById(R.id.ticker)).setText("下拉刷新");

                        }

                        oldheight = dis;
                    }

                }
                if (getScrollY() + oldy2 - y >= 0&&header.getHeight()==0) {
                    scrollBy(0, oldy2 - y);
                } else {
                    scrollTo(0, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isOpenRefresh&&header.getHeight() != 0) {
                    if (header.getHeight() > -maxdistance / 3) {
                        valueAnimator = ValueAnimator.ofInt(header.getHeight(), -maxdistance / 6);
                    } else {
                        valueAnimator = ValueAnimator.ofInt(header.getHeight(), 0);
                    }
                    valueAnimator.setDuration(500);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) header.getLayoutParams();
                            marginLayoutParams.height = (int) valueAnimator.getAnimatedValue();
                            header.setLayoutParams(marginLayoutParams);
                            if((int)valueAnimator.getAnimatedValue()!=0&&(int)valueAnimator.getAnimatedValue()==-maxdistance/6)
                            {
                                header.findViewById(R.id.pullOrPush).setBackgroundResource(R.drawable.src_load);
                                loadAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                loadAnimation.setDuration(1000);
                                loadAnimation.setRepeatCount(Animation.INFINITE);
                                header.findViewById(R.id.pullOrPush).startAnimation(loadAnimation);
                                ((TextView) header.findViewById(R.id.ticker)).setText("正在刷新");
                                isLoading=true;
                                new Thread(runnable).start();
                            }
                        }
                    });
                    valueAnimator.start();
                }
                if (getScrollY() != 0) {
                    isFullItem=getFirstVisiblePosition()==0&&getLastVisiblePosition()==getCount()-1;
                    if (!isFullItem) {
                        if (getLastVisiblePosition() == getCount() - 1) {
                            int des = getHeight();
                            if (getScrollY() < getHeight())
                                des = getScrollY();
                            mScroller.startScroll(0, getScrollY(), 0, -des / 2, 500);
                            invalidate();
                        }

                    } else {
                        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 500);
                        invalidate();
                    }

                }

                break;
        }
        oldy2 = y;
        return true;
    }

    public void setHeader(RelativeLayout header) {
        this.header = header;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }
    //添加刷新回调
    public void setLoadingEvent(Runnable runnable)
    {
        this.runnable=runnable;
    }
    //刷新之后调用
    public void setIsLoadingFalse(boolean success)
    {
        if(loadAnimation==null)
            return;
        loadAnimation.cancel();
        if(success)
        {
            header.findViewById(R.id.pullOrPush).setBackgroundResource(R.mipmap.e_15);
            ((TextView) header.findViewById(R.id.ticker)).setText("刷新成功");
        }
        else
        {
            header.findViewById(R.id.pullOrPush).setBackgroundResource(R.mipmap.e_11);
            ((TextView) header.findViewById(R.id.ticker)).setText("刷新失败");
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoading=false;
                ViewGroup.MarginLayoutParams marginLayoutParams = (MarginLayoutParams) header.getLayoutParams();
                marginLayoutParams.height = 0;
                header.setLayoutParams(marginLayoutParams);
            }
        },1000);

    }
    public void setRefreshFalse()
    {
        isOpenRefresh=false;
    }

}
