package com.byd.dynaudio_app.custom.xpop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseRecyclerViewAdapter;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.databinding.LayoutItemSpeedBinding;
import com.byd.dynaudio_app.databinding.LayoutViewSpeedChooseBinding;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.lxj.xpopup.core.AttachPopupView;

import java.util.ArrayList;
import java.util.List;

public class SpeedChoosePopupView extends PopupWindow {
    private Context mContext;

    private LayoutViewSpeedChooseBinding mDataBinding;

    private List<SpeedBean> mSpeedList;

    public SpeedChoosePopupView(@NonNull Context context) {
        super(context);
        mContext = context;
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_speed_choose, null, false);
        setContentView(mDataBinding.getRoot());
        initView();

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.img_arrow_for_speed);
        mDataBinding.llSpeed.addView(imageView);
        setBackgroundDrawable(null);

//        attachPopupContainer.addView(mDataBinding.getRoot());
    }

//    @Override
//    protected void addInnerContent() {
//        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_speed_choose, attachPopupContainer, false);
//        initView();
//        attachPopupContainer.addView(mDataBinding.getRoot());
//    }

    private void initView() {
        mDataBinding.recycler.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mSpeedList = new ArrayList<>() {{
            add(new SpeedBean(0.75f, "0.75 x"));
            add(new SpeedBean(1.f, "1.0 x"));
            add(new SpeedBean(1.25f, "1.25 x"));
            add(new SpeedBean(1.5f, "1.5 x"));
            add(new SpeedBean(2.0f, "2.0 x"));
        }};
        mDataBinding.recycler.setAdapter(new BaseRecyclerViewAdapter<SpeedBean, LayoutItemSpeedBinding>(mContext, mSpeedList) {
            @Override
            protected int getLayoutId() {
                return R.layout.layout_item_speed;
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void bindItem(LayoutItemSpeedBinding dataBinding, SpeedBean bean, int position) {
                dataBinding.tvText.setText(bean.getText());

                dataBinding.getRoot().setOnClickListener(v -> {
                    dismiss();
                    if (onItemClick != null) {
                        onItemClick.onItemClick(dataBinding, bean, position);
                    }
                });

                boolean defaultCheck = bean.getSpeed() == 1.f;
                dataBinding.tvText.setTextColor(defaultCheck
                        ? Color.parseColor("#FFCF022D")
                        : Color.WHITE);
                dataBinding.getRoot().setBackgroundColor(defaultCheck
                        ? Color.parseColor("#FF2F3031")
                        : Color.parseColor("#FF000000"));

                LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class)
                        .observe((LifecycleOwner) mContext, playerBean -> {
                            if (playerBean != null) {
                                //  LogUtils.d("check speed : " + playerBean.getSpeed());
                                boolean isCheck = bean.getSpeed() == playerBean.getSpeed();
                                dataBinding.tvText.setTextColor(isCheck
                                        ? Color.parseColor("#FFCF022D")
                                        : Color.WHITE);
                                dataBinding.getRoot().setBackgroundColor(isCheck
                                        ? Color.parseColor("#FF2F3031")
                                        : Color.parseColor("#FF000000"));
                            }
                        });
            }
        });
    }

/*    @Override
    protected void onShow() {
        super.onShow();
//        LiveDataBus.get().with(LiveDataBusConstants.Player.PLAY_SPEED, Float.class).postValue(
//                LiveDataBus.get().with(LiveDataBusConstants.Player.PLAY_SPEED, Float.class).getValue()
//        );
    }*/

/*    @Override
    protected int getPopupWidth() {
        return DensityUtils.dp2Px(mContext, 122);
    }

    @Override
    protected int getPopupHeight() {
        return DensityUtils.dp2Px(mContext, 310);
    }*/

    private OnItemClick onItemClick;

    public SpeedChoosePopupView setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;

        return this;
    }

    public interface OnItemClick {

        void onItemClick(LayoutItemSpeedBinding dataBinding, SpeedBean bean, int position);
    }

    public String wrapFloat2SpeedStr(float value) {
        for (SpeedBean bean : mSpeedList) {
            if (bean.speed == value) {
                return bean.text;
            }
        }
        return "";
    }

    private boolean isShow = false;

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);

        isShow = true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        LogUtils.d("speed dis miss...");

        isShow = false;
    }

    public boolean isShow() {
        return isShow;
    }

    public int getPlanWidth() {
        return DensityUtils.dp2Px(mContext, 122);
    }

    public int getPlanHeight() {
        return DensityUtils.dp2Px(mContext, 60 * 5 + 20);
    }

    class SpeedBean {

        float speed;
        String text;

        public SpeedBean(float speed, String text) {
            this.speed = speed;
            this.text = text;
        }

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
