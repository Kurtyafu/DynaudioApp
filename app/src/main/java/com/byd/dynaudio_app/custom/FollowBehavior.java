package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * 要去观察的组件装behavior
 */
public class FollowBehavior extends CoordinatorLayout.Behavior<ImageView> {
 
    public FollowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param parent 父
     * @param child this
     * @param dependency 被观察者
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof Button;
    }


    /**
     * @param parent 父
     * @param child this
     * @param dependency 被观察者
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {


 
//        child.setX(dependency.getX() + 200);
//        child.setY(dependency.getY() + 200);
        // child.setText("观察者:" + dependency.getX() + "," + dependency.getY());
        return true;
    }
}