package com.byd.dynaudio_app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.TouchUtils;

public class FeedBackDialog extends Dialog {

    public FeedBackDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_feed_back);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView();
    }

    private void initView() {
        ImageView imgClose = findViewById(R.id.img_close);
        ImageView imgQrcode = findViewById(R.id.img_qr_code);
        TouchUtils.bindClickItem(imgClose);
        imgClose.setOnClickListener(view -> dismiss());
//        Glide.with(getContext()).load(R.drawable.qrcode).into(imgQrcode);
    }
}
