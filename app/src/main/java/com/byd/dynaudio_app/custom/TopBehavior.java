package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class TopBehavior extends CoordinatorLayout.Behavior<ImageView> {
    private TextView mTextView;

    public TopBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull ImageView child, @NonNull View dependency) {
        return dependency instanceof TextView;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull ImageView child, @NonNull View dependency) {
        if (mTextView == null) {
            mTextView = (TextView) dependency;
        }
        float progress = mTextView.getY() / (dependency.getHeight() - mTextView.getHeight());
        float scale = 1.0f + progress;
        child.setScaleX(scale);
        child.setScaleY(scale);
        return true;
    }
}
