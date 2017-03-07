package com.example.lyk.forwater.activities.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import static android.animation.ObjectAnimator.ofFloat;

/**
 * Created by lyk on 2017/3/6.
 */

public class TouchListener implements View.OnTouchListener {

    private int oldx;
    private int oldy;
    private long time;
    private static final long dur = 500;
    private int oldTop;
    private int index;
    private boolean isFirst = true;
    private boolean isLong = false;
    private Map<Integer, View> views;
    private Drawable color;
    private ObjectAnimator objectAnimator;
    private ObjectAnimator objectAnimator2;
    private Handler mhander = new Handler();
    private AidTouchListener aidTouchListener;

    @Override
    public boolean onTouch(final View view, MotionEvent ev) {
        System.out.println(view.getTop());
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldTop = (int) view.getY();
                time = System.currentTimeMillis();
                isFirst = true;
                isLong = false;
                color = view.getBackground();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isFirst) {
                            if (System.currentTimeMillis() - time > dur) {
                                mhander.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.setBackground(new ColorDrawable(view.getResources().getColor(android.R.color.holo_blue_bright)));
                                    }
                                });
                                break;
                            }
                        }
                    }
                }).start();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isFirst && System.currentTimeMillis() - time > dur) {
                    isLong = true;
                }
                isFirst = false;
                if (isLong) {
                    if (Math.abs(x - oldx) < Math.abs(y - oldy) && view.getTop() + y - oldy >= 0) {
                        objectAnimator = objectAnimator.ofFloat(view, "y", view.getY(), view.getY() + y - oldy);
                        objectAnimator.setDuration(0);
                        objectAnimator.start();
                        int dis = (int) view.getY() - oldTop;
                        if (Math.abs(dis) > view.getHeight() * 3 / 4) {
                            if (dis > 0) {
                                if (index != views.size() - 1) {
                                    index++;
                                    final View v2 = views.get(index);
                                    final int temp = (int) v2.getY();
                                    objectAnimator2 = ObjectAnimator.ofFloat(v2, "y", (int) v2.getY(), oldTop);
                                    objectAnimator2.setDuration(200);
                                    objectAnimator2.start();
                                    oldTop = temp;
                                    views.put(index, view);
                                    views.put(index - 1, v2);

                                }

                            } else {
                                if (index != 0) {
                                    index--;
                                    final View v2 = views.get(index);
                                    final int temp = (int) v2.getY();
                                    objectAnimator2 = ObjectAnimator.ofFloat(v2, "y", (int) v2.getY(), oldTop);
                                    objectAnimator2.setDuration(200);
                                    objectAnimator2.start();
                                    oldTop = temp;
                                    views.put(index, view);
                                    views.put(index + 1, v2);
                                }
                            }
                        }
                    }
                } else {
                    if (Math.abs(x - oldx) < Math.abs(y - oldy)) {
                        ViewGroup viewGroup = (ViewGroup) view.getParent();
                        objectAnimator = ofFloat(viewGroup, "y", viewGroup.getY(), viewGroup.getY() + y - oldy);
                        objectAnimator.setDuration(0);
                        objectAnimator.start();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //点击
                if (isFirst && !isLong) {
                    isFirst = false;
                    view.setBackground(color);
                    aidTouchListener.start();
                    System.out.println(view.hashCode());
                }//长按 滑动
                else if (!isFirst && isLong) {
                    objectAnimator = ObjectAnimator.ofFloat(view, "y", view.getY(), oldTop);
                    objectAnimator.setDuration(500);
                    objectAnimator.start();
                    objectAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            view.setBackground(color);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                }//滑动
                else if (!isFirst && !isLong) {
                    ViewGroup viewGroup = (ViewGroup) view.getParent();
                    objectAnimator = ObjectAnimator.ofFloat(viewGroup, "y", viewGroup.getY(), 0);
                    objectAnimator.setDuration(500);
                    objectAnimator.start();

                }
                break;
        }
        oldx = x;
        oldy = y;
        return true;
        //36378464
    }

    public void setindex(int index) {
        this.index = index;
    }

    public void setViews(Map<Integer, View> views) {
        this.views = views;
    }

    public void setAidTouchListener(AidTouchListener aidTouchListener)
    {
        aidTouchListener.start();
    }
}
