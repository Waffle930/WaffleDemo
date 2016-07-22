package com.example.waffle.waffledemo.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Waffle on 2016/7/19.
 */

public class MainViewPager extends ViewPager {

    private boolean scrollble = true;

    public MainViewPager(Context context) {
        super(context);
    }

    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollble) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    public boolean isScrollble() {
        return scrollble;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(scrollble){
            return super.onInterceptTouchEvent(ev);
        }else {
            return false;
        }

    }
}
