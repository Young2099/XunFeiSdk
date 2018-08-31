package com.lanfeng.young.xunfeisdk;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by yf on 2018/8/31.
 */
public class MyLinayout extends LinearLayout {
    private boolean isFLAG;
    public MyLinayout(Context context) {
        this(context, null);
    }

    public MyLinayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLinayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            return false;
        } else {
            return true;
        }
    }

    public void setMoveFlag(boolean isMove) {
        isFLAG = isMove;
        Log.e("TAG", "setMoveFlag: ."+isMove);
    }
}