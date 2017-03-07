package com.example.lyk.forwater.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by lyk on 2017/3/5.
 */

public class RelativeLayoutEx extends RelativeLayout{
    public RelativeLayoutEx(Context context) {
        super(context);

    }
    public RelativeLayoutEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayoutEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
       return false;
    }
}
