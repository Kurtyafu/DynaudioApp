package com.byd.dynaudio_app.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseRecyclerViewAdapter;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.ItemBean;
import com.byd.dynaudio_app.bean.PlayItemBean;
import com.byd.dynaudio_app.databinding.LayoutFragmentPlayBinding;
import com.byd.dynaudio_app.databinding.LayoutItemPlayListInPlayfragmentBinding;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.TestUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 全屏播放页
 */
public class PlayFragment extends BaseFragment<LayoutFragmentPlayBinding, BaseViewModel> {


    // 播放类型 ： 音乐类、语言类
    public static final int PLAY_TYPE_MUSIC = 0;
    public static final int PLAY_TYPE_LANGUAGE = 1;

    // 是否展示了播放列表
    private boolean showPlayList = true;

    @SuppressLint("ObjectAnimatorBinding")
    private ObjectAnimator pointRotation;
    private ObjectAnimator imgShowRotation;
    private long currentPointPlayTime;
    private long currentImgShowPlayTime;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initView() {
        // 获取播放类型 默认为音乐类型
        Integer typeValue = LiveDataBus.get().with(LiveDataBusConstants.play_type, Integer.class).getValue();
        int type = typeValue != null ? typeValue : PLAY_TYPE_MUSIC;

        ItemBean testItemBean = TestUtils.getTestBlackPlasticList().get(0);

        // 是否有歌词  ---> 应该从音乐对象中获取
        boolean hasLyrics = true;
        switch (type) {
            case PLAY_TYPE_LANGUAGE:
                initLanguagePlayView();
                break;
            default:
                initMusicPlayView(testItemBean, hasLyrics);
                break;
        }


        mDataBinding.tvCurrentTime.setText("01:40");
        mDataBinding.tvTotalTime.setText("04:20");
        mDataBinding.progress.setProgress(30);

        mDataBinding.tvTitle.setText("朗朗巴黎大师课");
        mDataBinding.tvSubtitle.setText("丹拿主编");
        // mDataBinding.tvLabel.setBgColor(R.color.label_color_red);
        mDataBinding.tvLabel.setLabelImgPath("独家");
//        mDataBinding.getRoot().postDelayed(() -> Glide.with(mContext).load(TestUtils.getTestSingerPic()).into(new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                mDataBinding.clTop.setBackground(resource);
//            }
//        }), 100);

//        Glide.with(mContext).load(testItemBean.getShowImg()).into(mDataBinding.imgShow);
        ImageLoader.load(mContext,testItemBean.getShowImg(),mDataBinding.imgShow);

        if (showPlayList) {
//            mDataBinding.clTop.setVisibility(View.INVISIBLE);
            mDataBinding.lvLyrics.setVisibility(View.INVISIBLE);
//            mDataBinding.clRecord.setVisibility(View.INVISIBLE);
            mDataBinding.recyclerPlayList.setVisibility(View.VISIBLE);
        }
        mDataBinding.recyclerPlayList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mDataBinding.recyclerPlayList.setAdapter(new BaseRecyclerViewAdapter<PlayItemBean, LayoutItemPlayListInPlayfragmentBinding>(mContext, TestUtils.getTestPlayList()) {
            @Override
            protected int getLayoutId() {
                return R.layout.layout_item_play_list_in_playfragment;
            }

            @Override
            protected void bindItem(LayoutItemPlayListInPlayfragmentBinding dataBinding, PlayItemBean playItemBean, int position) {
                dataBinding.tvNum.setText(String.valueOf(position + 1));
                dataBinding.tvTitle.setText(playItemBean.getTitle());
                dataBinding.tvSubtitle.setText(playItemBean.getSubTitle());
                dataBinding.tvLabel.setLabelImgPath(playItemBean.getRecommend());
            }
        });

        // 加载 top部分背景为高斯模糊
        Glide.with(mContext).setDefaultRequestOptions(ImageLoader.getOptions()).load(TestUtils.getTestBg()).apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 35))).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                Drawable current = resource.getCurrent();
                current.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                mDataBinding.getRoot().setBackground(current);
            }
        });

        LiveDataBus.get().with(LiveDataBusConstants.is_playing, Boolean.class).observe(this, flag -> {
            boolean isPlaying = flag != null ? flag : false;
            mDataBinding.imgPlay.setImageResource(isPlaying ? R.drawable.img_pause : R.drawable.img_play);
        });

        LiveDataBus.get().with(LiveDataBusConstants.is_playing, Boolean.class).postValue(false);
    }

    /**
     * 初始化语言类播放
     */
    private void initLanguagePlayView() {

    }

    /**
     * 初始化上方播放界面
     *
     * @param testItemBean
     * @param hasLyrics    是否有歌词
     */
    private void initMusicPlayView(ItemBean testItemBean, boolean hasLyrics) {
        if (hasLyrics) {

        } else {


            // playStartAnimator();
        }

//         1.如果有歌词 需要把record往左移动136dp
//        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mDataBinding.clRecord.getLayoutParams();
//        if (layoutParams != null) {
//            layoutParams.rightMargin = DensityUtils.dp2Px(mContext, hasLyrics ? 136 : 0);
////            mDataBinding.clRecord.setLayoutParams(layoutParams);
//        }
        // 2.如果有歌词 显示歌词
        // testLyrics();
        // todo 设置歌词 解析lrc文件 生产bean类 设置数据
    }

    private void testLyrics() {
//        mDataBinding.getRoot().postDelayed(() -> {
//            int currentPos = mDataBinding.lvLyrics.getHighlightPosition();
//            currentPos++;
//            mDataBinding.lvLyrics.setHighlightPosition(currentPos);
//            testLyrics();
//        }, 1000);
    }

    /**
     * 播放指针动画
     */
    private void startPlayAnimator() {
        mDataBinding.getRoot().post(() -> {
            if (pointRotation == null) {
                mDataBinding.imgPoint.setPivotX(mDataBinding.imgPoint.getWidth() / 2.f);
                mDataBinding.imgPoint.setPivotY(mDataBinding.imgPoint.getHeight() * 0.15f);
                pointRotation = ObjectAnimator.ofFloat(mDataBinding.imgPoint, "rotation", 0, -17);
                pointRotation.setDuration(1000);
            }
            if (imgShowRotation == null) {
                imgShowRotation = ObjectAnimator.ofFloat(mDataBinding.imgShow, "rotation", 0, 360);
                imgShowRotation.setDuration(5000);
                imgShowRotation.setRepeatCount(-1);
                imgShowRotation.setRepeatMode(ValueAnimator.RESTART);
                imgShowRotation.setInterpolator(new LinearInterpolator());
                imgShowRotation.start();
            }

            pointRotation.start();
            imgShowRotation.resume();
        });
    }

    /**
     * 停止动画
     */
    private void stopPlayAnimator() {
        if (pointRotation != null) {
            pointRotation.reverse();
        }
        if (imgShowRotation != null) imgShowRotation.pause();
    }

    @Override
    protected void initListener() {
        mDataBinding.imgPlay.setOnClickListener(v -> {
            Boolean value = LiveDataBus.get().with(LiveDataBusConstants.is_playing, Boolean.class).getValue();
            boolean isPlaying = value != null ? value : false;

            mDataBinding.imgPlay.setImageResource(isPlaying ? R.drawable.img_pause : R.drawable.img_play);

            if (isPlaying) {  // 暂停相关逻辑
                stopPlayAnimator();
            } else {  // 播放相关逻辑
                startPlayAnimator();
                testLyrics();
            }

            // todo 根据真实播放状态来更改
            LiveDataBus.get().with(LiveDataBusConstants.is_playing).postValue(!isPlaying);
        });

        mDataBinding.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showPlayList) {  // 如果播放列表展开，就向下收起播放列表
//                    mDataBinding.recyclerPlayList.setPivotX(mDataBinding.recyclerPlayList.getWidth()/2);
//                    mDataBinding.recyclerPlayList.setPivotY(mDataBinding.recyclerPlayList.getHeight());
//                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(mDataBinding.recyclerPlayList, "scaleY", 1, 0.0f);
//                    scaleY.setDuration(1000);
//                    scaleY.start();

                    mDataBinding.recyclerPlayList.setVisibility(View.INVISIBLE);
                    mDataBinding.clTop.setVisibility(View.VISIBLE);
//                    mDataBinding.clRecord.setVisibility(View.VISIBLE);
                    mDataBinding.lvLyrics.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void initObserver() {

    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_play;
    }
}
