package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.ImageLoader;

public class SingerInfoView extends RelativeLayout {

    private ImageView imgAvatar;
    private TextView txtSinger;
    private TextView txtSingerInfo;

    public SingerInfoView(Context context) {
        super(context);
        initView();
    }

    public SingerInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SingerInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public SingerInfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        initLayout();
        findView();
    }

    private void initLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_view_singer_info, this);
    }

    private void findView() {
        imgAvatar = findViewById(R.id.img_avatar);
        txtSinger = findViewById(R.id.txt_singer);
        txtSingerInfo = findViewById(R.id.txt_singer_info);
    }

    public void setImgAvatar(String url) {
        ImageLoader.loadRound(getContext(), url, imgAvatar);
    }

    public void setTxtSinger(String str) {
        txtSinger.setText(str);
    }

    public void setTxtSingerInfo(String str) {
        txtSingerInfo.setText(str);
    }
}
