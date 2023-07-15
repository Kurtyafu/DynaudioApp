package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.TouchUtils;

public class NetworkErrorView extends LinearLayout {

    public TextView btnReload;

    public NetworkErrorView(Context context) {
        super(context);
        inflate();
    }

    public NetworkErrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate();
    }

    public NetworkErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
    }

    public NetworkErrorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate();
    }

    private void inflate() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_network_error, this);
        btnReload = findViewById(R.id.btn_reload);
        TouchUtils.bindClickItem(btnReload);
        setVisibility(GONE);
    }
}
