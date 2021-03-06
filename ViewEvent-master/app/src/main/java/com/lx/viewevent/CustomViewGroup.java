package com.lx.viewevent;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class CustomViewGroup extends FrameLayout {

    private Context context;
    public CustomViewGroup(Context context, OnTouchListener onTouchListener) {
        super(context);
        this.context = context;
        initResouce(context, onTouchListener);
    }

    private void initResouce(Context context, OnTouchListener onTouchListener) {
        inflate(context, R.layout.viewgroup, this);

        findViewById(R.id.tv_group_1).setOnTouchListener(onTouchListener);
        findViewById(R.id.tv_group_2).setOnTouchListener(onTouchListener);
        findViewById(R.id.tv_group_3).setOnTouchListener(onTouchListener);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean bRet = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                bRet = super.onInterceptTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                bRet = true;
                break;
            case MotionEvent.ACTION_UP:
                bRet = super.onInterceptTouchEvent(ev);
                break;
            default:
                break;
        }
        return bRet;
    }

    public int getViewHeight() {
        return Utils.dip2px(context, 92);
    }

    public int getViewWidth() {
        return Utils.dip2px(context, 70);
    }

}