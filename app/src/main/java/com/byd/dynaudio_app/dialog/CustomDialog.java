package com.byd.dynaudio_app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.TouchUtils;

public class CustomDialog extends Dialog {

    private CharSequence title;
    private CharSequence content;
    private CharSequence positiveText;
    private CharSequence negativeText;
    private IOnClickListener clickListener;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_custom);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView();
    }

    private void initView() {
        TextView txtTitle = findViewById(R.id.txt_title);
        TextView txtContent = findViewById(R.id.txt_content);
        TextView txtCancel = findViewById(R.id.txt_cancel);
        TextView txtConfirm = findViewById(R.id.txt_confirm);

        TouchUtils.bindClickItem(txtCancel, txtConfirm);

        if (title != null) txtTitle.setText(title);
        if (content != null) {
            txtContent.setMovementMethod(LinkMovementMethod.getInstance());
            txtContent.setText(content);
            // 设置高亮颜色透明，因为点击会变色
            txtContent.setHighlightColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        }
        if (negativeText != null) txtCancel.setText(negativeText);
        if (positiveText != null) txtConfirm.setText(positiveText);

        txtCancel.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onNegativeButtonListener();
            }
            dismiss();
        });

        txtConfirm.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onPositiveButtonListener();
            }
            dismiss();
        });
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public void setContent(CharSequence content) {
        this.content = content;
    }

    public void setPositiveText(CharSequence positiveText) {
        this.positiveText = positiveText;
    }

    public void setNegativeText(CharSequence negativeText) {
        this.negativeText = negativeText;
    }

    public void setClickListener(IOnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
