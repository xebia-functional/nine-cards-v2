package com.fortysevendeg.ninecardslauncher.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class MyFrameLayout extends FrameLayout {
    public MyFrameLayout(Context context) {
        super(context);
    }

    public MyFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
//        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
//        super.setPaddingRelative(start, top, end, bottom);
    }

    @Override
    public void invalidate() {
        super.setPadding(0, 0, 0, 0);
        super.invalidate();
    }

    @Override
    public void forceLayout() {
        super.setPadding(0, 0, 0, 0);
        super.forceLayout();
    }

    @Override
    public void requestLayout() {
        super.setPadding(0, 0, 0, 0);
        super.requestLayout();
    }
}
