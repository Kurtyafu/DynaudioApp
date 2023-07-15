package com.byd.dynaudio_app.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.MainActivity;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.response.AlbumBean;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.network.INetworkListener;
import com.byd.dynaudio_app.network.NetworkObserver;
import com.byd.dynaudio_app.network.NetworkType;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;

public abstract class BaseFragment<T extends ViewDataBinding, V extends BaseViewModel> extends Fragment implements INetworkListener {
    protected T mDataBinding;
    protected V mViewModel;
    protected Context mContext;
    // 加载中
    private View loadingView;
    // 加载失败
    private View errorView;

    // 动画
    private AnimationDrawable mAnimationDrawable;

    // fragment的页面层级 首页0，聚合页1，详情页2  ---> 会影响到翻页的动画效果
    protected int level = 0;

    protected boolean needDisplayPlayer = true;

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_base, null);
        mDataBinding = DataBindingUtil.inflate(activity.getLayoutInflater(), getLayoutId(), null, false);
        mContext = requireContext();
        mViewModel = getViewModel();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDataBinding.getRoot().setLayoutParams(params);
        RelativeLayout mContainer = inflate.findViewById(R.id.container);
        mDataBinding.setLifecycleOwner(this);
        mContainer.addView(mDataBinding.getRoot());
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NetworkObserver.getInstance().addNetworkObserver(mContext, this);
        initView();
        initListener();
        initObserver();
        setLevel();
    }

    @Override
    public void onNetworkChanged(boolean isConnected, NetworkType type) {

    }

    /**
     * 展示加载中View
     */
    protected void showLoading() {
        ViewStub viewStub = getView(R.id.vs_loading);
        if (viewStub != null) {
            loadingView = viewStub.inflate();
            ImageView img = loadingView.findViewById(R.id.img_progress);
            mAnimationDrawable = (AnimationDrawable) img.getDrawable();
        }
        if (loadingView != null && loadingView.getVisibility() != View.VISIBLE) {
            loadingView.setVisibility(View.VISIBLE);
        }
        // 开始动画
        if (mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        if (mDataBinding.getRoot().getVisibility() != View.GONE) {
            mDataBinding.getRoot().setVisibility(View.GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
    }


    /**
     * 加载失败后点击后的操作
     */
    protected void onRefresh() {

    }

    /**
     * albumBean是否有数据
     *
     * @param albumBean
     * @return
     */
    protected boolean isHasData(AlbumBean albumBean) {
        return !(TextUtils.isEmpty(albumBean.getAlbumImgUrl()) || TextUtils.isEmpty(albumBean.getAlbumName()));
    }

    protected void showContentView() {
        if (loadingView != null && loadingView.getVisibility() != View.GONE) {
            loadingView.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (mDataBinding.getRoot().getVisibility() != View.VISIBLE) {
            mDataBinding.getRoot().setVisibility(View.VISIBLE);
        }
    }

    protected <T extends View> T getView(int id) {
        return (T) getView().findViewById(id);
    }


    /**
     * 展示加载失败View  没有加载出来数据
     */
    protected void showError() {
        ViewStub viewStub = getView(R.id.vs_error);
        if (viewStub != null) {
            errorView = viewStub.inflate();
            errorView.findViewById(R.id.bt_err).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLoading();
                    onRefresh();
                }
            });
//            // 点击加载失败布局
//            errorView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showLoading();
//                    onRefresh();
//                }
//            });
        }
        if (errorView != null) {
            errorView.setVisibility(View.VISIBLE);
        }
        if (loadingView != null && loadingView.getVisibility() != View.GONE) {
            loadingView.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (mDataBinding.getRoot().getVisibility() != View.GONE) {
            mDataBinding.getRoot().setVisibility(View.GONE);
        }
    }

    /**
     * 初始化界面组件
     */
    protected abstract void initView();

    /**
     * 初始化监听之类
     */
    protected abstract void initListener();

    /**
     * 初始化 观察者监听
     */
    protected abstract void initObserver();

    /**
     * 使用view model provider 构造view model
     */
    protected abstract V getViewModel();

    /**
     * 返回对应的布局id
     */
    protected abstract int getLayoutId();

    protected void setLevel() {

    }

    public int getLevel() {
        return level;
    }

    public void toFragment(@NonNull BaseFragment fragment) {
        MainActivity activity = (MainActivity) requireActivity();
        activity.toFragment(fragment, Gravity.RIGHT);
    }

    protected void noNeedDisplayPlayer() {
        this.needDisplayPlayer = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needDisplayPlayer) judgeIfMiniBar();
    }

    /**
     * 常驻显示mini bar
     * 如果没数据 就拿播放记录的数据
     * 如果播放记录也没数据 不显示
     */
    private void judgeIfMiniBar() {
        MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
        if (value != null) {
            // 这就说明现在就有数据 直接显示
            PlayerVisionManager.getInstance().showMiniPlayer();
        } else {
            // 在MusicPlayManager的init方法会自动更新本地数据到播放列表 所以上述过程会自动有数据
            // 所以到这里就直接不显示即可
            PlayerVisionManager.getInstance().hideMiniPlayer();
        }
    }
}
