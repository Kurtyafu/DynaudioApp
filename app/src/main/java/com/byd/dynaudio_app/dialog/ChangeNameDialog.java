package com.byd.dynaudio_app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.TouchUtils;

public class ChangeNameDialog extends Dialog {

    private ImageView imgClose;
    private EditText editView;
    private View clearView;
    private TextView txtLength;
    private TextView txtCancel;
    private TextView txtConfirm;
    private boolean isRight = false;
    private IChangeNameClickListener clickListener;

    private final String mBeforeName;

    public ChangeNameDialog(@NonNull Context context, String beforeName) {
        super(context);
        this.mBeforeName = beforeName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_change_name);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView();
        initData();
    }

    private void initView() {
        imgClose = findViewById(R.id.img_close);
        editView = findViewById(R.id.edit_name);
        clearView = findViewById(R.id.view_clear);
        txtLength = findViewById(R.id.txt_length);

        txtCancel = findViewById(R.id.txt_cancel);
        txtConfirm = findViewById(R.id.txt_confirm);

        TouchUtils.bindClickItem(imgClose, clearView, txtCancel, txtConfirm);

        imgClose.setOnClickListener(view -> dismiss());

        clearView.setOnClickListener(view -> {
            editView.setText(null);
            clearView.setVisibility(View.GONE);
        });

        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                txtLength.setText(str.length() + "/9");
                if (str.length() > 0) {
                    clearView.setVisibility(View.VISIBLE);
                    if (str.equals(mBeforeName)) {
                        if (txtConfirm.isSelected()) {
                            txtConfirm.setSelected(false);
                        }
                        isRight = false;
                    } else {
                        if (!txtConfirm.isSelected()) {
                            txtConfirm.setSelected(true);
                        }
                        isRight = true;
                    }
                } else {
                    clearView.setVisibility(View.GONE);
                    if (txtConfirm.isSelected()) {
                        txtConfirm.setSelected(false);
                    }
                    isRight = false;
                }
            }
        });

        txtCancel.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onNegativeButtonListener();
            }
            dismiss();
        });

        txtConfirm.setOnClickListener(view -> {
            if (clickListener != null && isRight) {
                clickListener.onSaveNameListener(editView.getText().toString());
                dismiss();
            }
        });

        editView.requestFocus();
    }

    private void initData() {
        editView.setText(mBeforeName);
        editView.setSelection(editView.getText().toString().length());
    }

    public void setClickListener(IChangeNameClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public abstract static class IChangeNameClickListener {

        public void onSaveNameListener(String newName) {
        }

        public void onNegativeButtonListener() {
        }
    }
}
