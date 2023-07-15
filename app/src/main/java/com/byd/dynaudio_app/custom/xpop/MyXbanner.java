package com.byd.dynaudio_app.custom.xpop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.stx.xhb.androidx.XBanner;

public class MyXbanner extends XBanner {
    private boolean isVpScrollEnabled = true;

    public MyXbanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isVpScrollEnabled() {
        return isVpScrollEnabled;
    }

    public void setVpScrollEnabled(boolean vpScrollEnabled) {
        isVpScrollEnabled = vpScrollEnabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isVpScrollEnabled) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public int getRealCount() {
        return super.getRealCount() != 0 ? super.getRealCount() : 1;
    }
}
