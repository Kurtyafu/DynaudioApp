package com.byd.dynaudio_app.custom.smart_refresh_layout;

import static com.jeremyliao.liveeventbus.utils.AppUtils.init;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.LogUtils;
import com.scwang.smartrefresh.header.internal.pathview.PathsView;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.util.SmartUtil;

@SuppressLint("RestrictedApi")
public class HFooter extends LinearLayout implements RefreshFooter {
    private Context mContext;
    private ImageView mProgressView;//刷新动画视图
    private ProgressDrawable mProgressDrawable;//刷新动画
    private TextView mLoadingText;

    public HFooter(Context context) {
        this(context, null);
    }

    public HFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SmartUtil.dp2px(800)));
        setPadding(0, SmartUtil.dp2px(50), 0, SmartUtil.dp2px(50));


        mProgressDrawable = new ProgressDrawable();
        mProgressView = new ImageView(mContext);
        mProgressView.setImageDrawable(mProgressDrawable);
        mLoadingText = new TextView(mContext);
        mLoadingText.setTextColor(Color.parseColor("#73FFFFFF"));
        mLoadingText.setTextSize(12);

        addView(mProgressView, SmartUtil.dp2px(20), SmartUtil.dp2px(20));
        addView(mLoadingText);
        LinearLayout.LayoutParams layoutParams = (LayoutParams) mLoadingText.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.leftMargin = SmartUtil.dp2px(10);
            mLoadingText.setLayoutParams(layoutParams);
        }
    }


    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        mProgressDrawable.start();//开始动画
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        // mProgressDrawable.stop();//停止动画
//        if (success){
//            mHeaderText.setText("刷新完成");
//        } else {
//            mHeaderText.setText("刷新失败");
//        }
        return 0;//延迟500毫秒之后再弹回
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
//        LogUtils.d("on state change : " + newState);
//        switch (newState) {
//            case None:
//            case PullDownToRefresh:
//                mProgressView.setVisibility(GONE);//隐藏动画
//                mLoadingText.setText(R.string.loaded_all);
//                break;
//            case Loading:
//            case LoadReleased:
                mProgressView.setVisibility(VISIBLE);//显示加载动画
                mLoadingText.setText(R.string.loading);
//                break;
//            case ReleaseToRefresh:
//                mProgressView.setVisibility(VISIBLE);//显示加载动画
//                break;
//        }
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        return false;
    }
}
