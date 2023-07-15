package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.byd.dynaudio_app.R;

public class TabLayout extends LinearLayout {

    private RelativeLayout rlFirst;
    private RelativeLayout rlSecond;
    private TextView txtFirst;
    private TextView txtSecond;
    private View tabFirst;
    private View tabSecond;

    private IOnTabSelectedListener listener;

    public TabLayout(Context context) {
        super(context);
        initView();
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void addOnTabSelectedListener(IOnTabSelectedListener listener) {
        this.listener = listener;
    }

    private void initView() {
        initLayout();
        findView();
        initListener();
    }

    private void initLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_view_tab, this);
    }

    private void findView() {
        rlFirst = findViewById(R.id.rl_music_tab);
        rlSecond = findViewById(R.id.rl_audio_program_tab);
        txtFirst = findViewById(R.id.txt_music);
        txtSecond = findViewById(R.id.txt_audio_program);
        tabFirst = findViewById(R.id.view_music_tab);
        tabSecond = findViewById(R.id.view_audio_program_tab);

    }

    private void initListener() {
        rlFirst.setOnClickListener(view -> {
            selectFirstTab();
            if (listener != null) {
                listener.onFirstSelected();
            }
        });
        rlSecond.setOnClickListener(view -> {
            selectSecondTab();
            if (listener != null) {
                listener.onSecondSelected();
            }
        });
    }

    public void selectFirstTab() {
        txtFirst.setSelected(true);
        txtSecond.setSelected(false);
        txtFirst.getPaint().setFakeBoldText(true);
        txtSecond.getPaint().setFakeBoldText(false);
        tabFirst.setVisibility(View.VISIBLE);
        tabSecond.setVisibility(View.GONE);
    }

    public void selectSecondTab() {
        txtFirst.setSelected(false);
        txtSecond.setSelected(true);
        txtFirst.getPaint().setFakeBoldText(false);
        txtSecond.getPaint().setFakeBoldText(true);
        tabFirst.setVisibility(View.GONE);
        tabSecond.setVisibility(View.VISIBLE);
    }

    public void setFirstText(String text) {
        txtFirst.setText(text);
    }

    public void setSecondText(String text) {
        txtSecond.setText(text);
    }

    public void setFirstClickable(boolean clickable) {
        rlFirst.setClickable(clickable);
    }

    public void setSecondClickable(boolean clickable) {
        rlSecond.setClickable(clickable);
    }

    public boolean firstItemSelected() {
        return txtFirst.isSelected();
    }

    public boolean secondItemSelected() {
        return txtSecond.isSelected();
    }

    public interface IOnTabSelectedListener {
        void onFirstSelected();

        void onSecondSelected();
    }
}
