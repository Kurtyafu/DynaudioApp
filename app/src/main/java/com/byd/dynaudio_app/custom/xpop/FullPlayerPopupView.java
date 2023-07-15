package com.byd.dynaudio_app.custom.xpop;

import static com.byd.dynaudio_app.bean.MusicPlayerBean.PlayStatus.Paused;
import static com.byd.dynaudio_app.bean.MusicPlayerBean.PlayStatus.Playing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.MainActivity;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseRecyclerViewAdapter;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.SingerBean;
import com.byd.dynaudio_app.custom.lrc.MyLrc;
import com.byd.dynaudio_app.databinding.LayoutItemPlayListInPlayfragmentBinding;
import com.byd.dynaudio_app.databinding.LayoutItemSpeedBinding;
import com.byd.dynaudio_app.databinding.LayoutViewFullPlayBinding;
import com.byd.dynaudio_app.fragment.SoundSettingsFragment;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.SharedPreferencesUtil;
import com.byd.dynaudio_app.utils.TouchUtils;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;
import com.stx.xhb.androidx.holder.HolderCreator;
import com.stx.xhb.androidx.holder.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;

/**
 * vp的切换：无论是否显示 均可以进行vp的翻页
 * 黑胶唱片更新逻辑：
 * 若在全屏播放页面，切换会引起播放器跳转歌曲
 * 若在别的页面（未显示全屏播放器）全屏播放器会在后台切换歌曲列表的高亮及vp的item 此时不会引起播放器的功能变化
 * <p>
 * 唱片旋转：若播放的歌曲一致 且状态处于播放 就会旋转 不满足条件的不会旋转
 * 指针移动：根据vp的transformer程度进行偏移
 * 下方内容：设置为当前播放的内容
 */
public class FullPlayerPopupView extends BottomPopupView implements ViewPager.OnPageChangeListener {
    private static final int VP_SCROLL_DURATION = 1000;
    private static final int DY = 200;
    private Context mContext;

    private LayoutViewFullPlayBinding mDataBinding;

    //    private RecordAdapter recordVpAdapter;
    //    private MusicPlayManager.PlayStatus currentStatus;
    private SpeedChoosePopupView speedChoosePopupView;
    private BaseRecyclerViewAdapter<MusicListBean, LayoutItemPlayListInPlayfragmentBinding> playListAdapter;
    private MusicPlayerBean.PlayStatus currentPlayStatus = Paused;
    private float vpRecordTranslationY;
    private boolean isDragProgress;
    private String url;  // 高斯模糊使用的地址

    private MusicListBean currentMusic;
    private long lastTime;
    private int highLightPosition = -1;

    private List<MusicListBean> mData = new ArrayList<>();
    private int current = -1;
    private boolean isInVideo;
    private BannerHolder bannerHolder;
    private boolean hasFirstIn = false; // 如果是第一次进来全屏播放器 别进行pageSelect的跳转

    List<VideoView> videoViews = new ArrayList<>();
    private MediaPlayer currentMp;
    private BasePopupView speedChoosePop;
    private long lastChangeTime = -1; // 上一次修改vp的item的时间
    private boolean isSb;//点击播放列表后变为true
    private Runnable speedChooseDismissRunnable = new Runnable() {
        @Override
        public void run() {
            if (speedChoosePopupView != null && speedChoosePopupView.isShow()) {
                speedChoosePopupView.dismiss();
            }
        }
    };
    private boolean isDragPlayList = false;
    private long lastDragPlayListTime;

    public FullPlayerPopupView(@NonNull Context context) {
        super(context);
        mContext = context;
        LogUtils.d("create >... ");
    }

    @Override
    protected void addInnerContent() {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_full_play, bottomPopupContainer, false);
        bottomPopupContainer.addView(mDataBinding.getRoot());

        TouchUtils.bindClickItem(mDataBinding.imgCancel, mDataBinding.llAudioSetting);
        initView();
        initObserver();
        initListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        // 0509 适配加了透明状态栏的情况下 车机会产生的异常显示
//        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mDataBinding.incPlayer.getRoot().getLayoutParams();
//        layoutParams.bottomMargin = 2 * (SPUtils.isPad() ? 10 : 80);
//        mDataBinding.incPlayer.getRoot().setLayoutParams(layoutParams);
//
//        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) mDataBinding.clTop.getLayoutParams();
//        layoutParams2.bottomMargin = 2 * (SPUtils.isPad() ? 279 + 10 : 359);
//        mDataBinding.clTop.setLayoutParams(layoutParams2);

        LogUtils.d("full player init view...");
        mDataBinding.getRoot().postDelayed(() -> hasFirstIn = true, 200);

        bannerHolder = new BannerHolder();
        mDataBinding.vpRecord.setBannerData(new ArrayList<>(), bannerHolder);
        mDataBinding.vpRecord.setBannerCurrentItem(MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex(), false);
        mDataBinding.getRoot().postDelayed(() -> mDataBinding.vpRecord.setPageChangeDuration(VP_SCROLL_DURATION), 100);
        mDataBinding.vpRecord.setOnPageChangeListener(this);


        // 播放列表初始化
        mDataBinding.recyclerPlayList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        playListAdapter = new BaseRecyclerViewAdapter<>(mContext, new ArrayList<>()) {
            @Override
            protected int getLayoutId() {
                return R.layout.layout_item_play_list_in_playfragment;
            }

            @Override
            protected void bindItem(LayoutItemPlayListInPlayfragmentBinding dataBinding, MusicListBean itemBean, int position) {
//                LogUtils.d("play list bind item : " + position);
                dataBinding.tvNum.setText(String.valueOf(position + 1));
                dataBinding.tvTitle.setText(itemBean.getName());
                List<SingerBean> singerList = itemBean.getSingerList();
                if (singerList != null && singerList.size() > 0) {
                    itemBean.setSinger(SPUtils.formatAuther(singerList));
                }
                dataBinding.tvSubtitle.setText(itemBean.getSinger());// 歌手
                dataBinding.tvLabel.setLabelImgPath(itemBean.getQualityUrl());

                // item点击就播放当前音乐
                dataBinding.getRoot().setOnClickListener(v -> {
                    startPointAnimation(false, true);
                    MusicPlayManager.getInstance().playMusic(mData, itemBean, position, MusicPlayManager.getInstance().getCurrentAlbumId());
                    mDataBinding.vpRecord.setBannerCurrentItem(position);
                    startPlayBtnChange();// 屏蔽播放按钮变化1s
                });

                // 收藏按钮
                dataBinding.imgCollect.setImageResource(itemBean.isCollectFlag() ? R.drawable.img_collected : R.drawable.img_collect);
                dataBinding.imgCollect.setOnClickListener(v -> MusicPlayManager.getInstance().setCollect(itemBean, !itemBean.isCollectFlag()));

                LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, bean -> {
                    boolean isHighLight = bean != null && bean.getSpecialId() == (itemBean.getSpecialId())
                            /*   && TextUtils.equals(bean.getTypeName(), itemBean.getTypeName())*/;

//                    LogUtils.d("type 1 : " + bean.getTypeName() + " 2 : " + itemBean.getTypeName() + " id 1: " + bean.getSpecialId() + " id 2: " + itemBean.getSpecialId());

                    if (isHighLight && !isDragPlayList && System.currentTimeMillis() - lastDragPlayListTime > 2_000) {
                        mDataBinding.recyclerPlayList.smoothScrollToPosition(position);
                    }
                    // 序号
                    dataBinding.tvNum.setVisibility(isHighLight ? View.INVISIBLE : View.VISIBLE);
                    dataBinding.imgNum.setVisibility(isHighLight ? View.VISIBLE : View.INVISIBLE);
                    // 文字
                    dataBinding.tvTitle.setTextColor(isHighLight ? Color.parseColor("#FFFF3D46") : Color.parseColor("#73FFFFFF"));
                    dataBinding.tvSubtitle.setTextColor(isHighLight ? Color.parseColor("#FFFF3D46") : Color.parseColor("#73FFFFFF"));

                    if (isHighLight && System.currentTimeMillis() - lastChangeTime >= 1000 && position == MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex()) {  // 这里加上时间间隔解决一个bug：首次进入一个播放列表 点击item打开全屏播放器
                        highLightPosition = position;
                        url = bean.getImageUrl();
//                        int bannerCurrentItem = mDataBinding.vpRecord.getBannerCurrentItem();
//                        if (position != bannerCurrentItem && !isJustShow) {
////                            mDataBinding.getRoot().postDelayed(() -> dataBinding.getRoot().performClick(), 10);
//                            LogUtils.d("to pos : " + position);
////                            mDataBinding.vpRecord.setBannerCurrentItem(position, true);
//                            lastChangeTime = System.currentTimeMillis();
//                        }
                    }
                });

                LiveDataBus.get().with(LiveDataBusConstants.Player.ITEM_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, bean -> {
                    // 收藏
                    if (bean != null && bean.getSpecialId() == itemBean.getSpecialId()) {
                        boolean isCollected = bean.isCollectFlag();
                        dataBinding.imgCollect.setImageResource(isCollected ? R.drawable.img_collected : R.drawable.img_collect);
                        itemBean.setCollectFlag(isCollected);
                    }
                });
            }
        };
        mDataBinding.recyclerPlayList.setAdapter(playListAdapter);
        mDataBinding.recyclerPlayList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.top = DensityUtils.dp2Px(mContext, 44);
                }
                if (position == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = DensityUtils.dp2Px(mContext, 44);
                }
            }
        });
        mDataBinding.recyclerPlayList.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    isDragPlayList = true;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isDragPlayList = false;
                    lastDragPlayListTime = System.currentTimeMillis();
                    break;
            }
            return false;
        });

//        Boolean showListValue = (Boolean) LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST).getValue();
//        boolean showList = showListValue != null ? showListValue : false;
//        if (showList) {
////            LogUtils.d();
//            mDataBinding.getRoot().postDelayed(() -> showPlayList(), 30);
//        }

    }

    private void initObserver() {
        if (mContext instanceof MainActivity) {
            MainActivity activity = (MainActivity) mContext;
            // 界面ui更新
            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).observe(activity, bean -> {
                currentMusic = bean;
                if (bean == null) {
                    LogUtils.e("play bean null...");
                    return;
                }

                // 0410 如果过来的是台宣 就看看下一个是不是有 且非台宣 就显示下一个
                if ("2".equals(bean.getLibraryType())) {
                    return;
                }
                // 1.切换页面的时候 重新更新界面ui的显示和隐藏
                // 0505需求 电台：不能随机播放 不可调速
                String typeName = bean.getTypeName();
                String libraryType = bean.getLibraryType();
                if ("dt".equals(typeName)) {
                    libraryType = "";
                }
//                LogUtils.d("lib type : " + libraryType);
                switch (libraryType) {
                    case "3":
//                        Boolean value = LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST, Boolean.class).getValue();
//                        boolean isPlayListShow = value != null ? value : false;
                        mDataBinding.incPlayer.imgMode.setVisibility(INVISIBLE);
                        mDataBinding.incPlayer.tvSpeed.setVisibility(VISIBLE);
                        mDataBinding.incPlayer.imgPrevious15.setVisibility(VISIBLE);
                        mDataBinding.incPlayer.imgNext30.setVisibility(VISIBLE);
                        mDataBinding.incPlayer.imgVideoOrMusic.setVisibility(bean.getVideoUrl() != null ? VISIBLE : GONE);
                        break;
                    case "1":
                    case "2":
                    case "4":
                    default:
                        mDataBinding.incPlayer.imgPrevious15.setVisibility(GONE);
                        mDataBinding.incPlayer.imgNext30.setVisibility(GONE);
                        mDataBinding.incPlayer.imgVideoOrMusic.setVisibility(GONE);
                        mDataBinding.incPlayer.imgMode.setVisibility(VISIBLE);
                        mDataBinding.incPlayer.tvSpeed.setVisibility(INVISIBLE);
                        break;
                }

                boolean hasLabel = bean.getQualityUrl() != null && !bean.getQualityUrl().isEmpty();
                // mDataBinding.incPlayer.tvTitle.setMaxWidth(hasLabel ? mDataBinding.incPlayer.llTitle.getWidth() - mDataBinding.incPlaye.tvLabel.getWidth() - DensityUtils.dp2Px(mContext, 10 + 80) : mDataBinding.incPlayer.llTitle.getWidth() - -DensityUtils.dp2Px(mContext, 80));

                // 以下是通用的更新

                // 2. 标题、副标题、标签
                if (!TextUtils.equals(mDataBinding.incPlayer.tvTitle.getText().toString(), bean.getName())) {
                    mDataBinding.incPlayer.tvTitle.setText(bean.getName());
                }

                String sub = SPUtils.formatAuther(bean.getSingerList());
                mDataBinding.incPlayer.tvSubtitle.setText(sub);
                mDataBinding.incPlayer.tvLabel.setLabelImgPath(!TextUtils.equals("ys", bean.getTypeName()) ? bean.getQualityUrl() : null);

//                Boolean showListValue = (Boolean) LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST).getValue();
//                boolean showList = showListValue != null ? showListValue : false;
//                mDataBinding.incPlayer.tvLabel.setVisibility(showList ? INVISIBLE : VISIBLE);

                if (!TextUtils.isEmpty(bean.getBeginTime())) {
                    mDataBinding.incPlayer.tvCurrentTime.setText(bean.getBeginTime());
                }
                if (!TextUtils.isEmpty(bean.getEndTime()) && !bean.getEndTime().startsWith("-")/*不显示时间以-开头*/) {
                    mDataBinding.incPlayer.tvTotalTime.setText(bean.getEndTime());
                }

                // 3. 是否收藏
                mDataBinding.incPlayer.imgCollect.setImageResource(bean.isCollectFlag() ? R.drawable.img_collected : R.drawable.img_collect);

                mDataBinding.imgPoint.setVisibility(bean.getVideoUrl() != null ? INVISIBLE : VISIBLE);

                // 4.是否显示歌词的
                boolean b = bean.getWordUrl() != null && !bean.getWordUrl().isEmpty();
//                mDataBinding.getRoot().postDelayed(() -> setPointMove(b, isShow()), 1);
            });
            LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_FULLPLAY, Boolean.class).observe(this, aBoolean -> {
                if (aBoolean == null) return;
                mDataBinding.vpRecord.getViewPager().setBannerCurrentItemInternal(mDataBinding.vpRecord.getViewPager().getCurrentItem(), false);
            });
            LiveDataBus.get().with(LiveDataBusConstants.Player.ITEM_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, bean -> {
                // 收藏
                if (bean != null && currentMusic != null && currentMusic.getSpecialId() == bean.getSpecialId()) {
                    mDataBinding.incPlayer.imgCollect.setImageResource(bean.isCollectFlag() ? R.drawable.img_collected : R.drawable.img_collect);
                }
            });

            // 播放器相关
            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe(activity, player -> {
                // 播放状态
//                LogUtils.d("" + currentPlayStatus + "status : " + player.getPlayStatus());
                boolean startPlayAnimation = false;
                if (currentPlayStatus != player.getPlayStatus()) {
                    startPlayAnimation = true;
                    currentPlayStatus = player.getPlayStatus();
                }
                switch (player.getPlayStatus()) {
                    case Playing:
                        if (canPlayBtnChange) {
                            mDataBinding.incPlayer.imgPlay.setImageResource(R.drawable.img_pause_with_circle);
                        }
                        if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
                            startPointAnimation(true, isShow());
                        }
                        break;
                    case Loading:
                        // todo show loading
                        break;
                    case Paused:
                        if (canPlayBtnChange) {
                            mDataBinding.incPlayer.imgPlay.setImageResource(R.drawable.img_play);
                            if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
                                startPointAnimation(false, isShow());
                            }
                        }
                        break;
                }

                // 4. 进度
                if (!isDragProgress && !isInVideo && isShow()) {
                    if (mDataBinding.incPlayer.progress.getProgress() != player.getProgress()) {
                        // LogUtils.d("播放进度" + player.getProgress());
                        mDataBinding.incPlayer.progress.setProgress(player.getProgress());
                    }
                }
                // 5.倍速
                // 读取倍速
                mDataBinding.incPlayer.tvSpeed.setText(String.format("%s x", player.getSpeed()));


                // 6.播放模式
                // LogUtils.d("play mode : " + player.getPlaybackMode());
                int res;
                switch (player.getPlaybackMode()) {
                    case Single_Cycle:
                        res = R.drawable.img_single_cycle;
                        break;
                    case Random_Cycle:
                        res = R.drawable.img_random_cycle;
                        break;
                    default:
                        res = R.drawable.img_list_loop;
                        break;
                }
                mDataBinding.incPlayer.imgMode.setImageResource(res);

                List<MusicListBean> playList = player.getPlayList();
                if (playList == null) playList = new ArrayList<>();
                if (mData != playList) {
                    mData = playList;

//                    for (MusicListBean bean : playList) {
//                        LogUtils.d("name : " + bean.getName()
//                                + " lib type : " + bean.getLibraryType());
//                    }
                }
                // 7.播放列表 ---> 呼起过全屏播放器且切放播放列表
                List<MusicListBean> currentPlayData = playListAdapter.getData();
                // 后面改成这种方式 现在先不改：                if (TouchUtils.isDiffData(currentPlayData,playList)) {
                if (currentPlayData == null || currentPlayData.size() != playList.size()) {
                    // 去除台宣
                    playList.removeIf(bean -> "2".equals(bean.getLibraryType()));


                    playListAdapter.setData(playList);
                    mDataBinding.vpRecord.setBannerData(playList);
                    mDataBinding.vpRecord.setBannerCurrentItem(
                            MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex(), isShow());
                    mDataBinding.vpRecord.getViewPager().setOffscreenPageLimit(playList.size() - 1);
//                    mDataBinding.vpRecord.setBannerCurrentItem(
//                            MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex()); // 不考虑台宣的情况 设置vp item
                } else {
                    for (int i = 0; i < currentPlayData.size(); i++) {
                        if (currentPlayData.get(i).getSpecialId() != playList.get(i).getSpecialId()) {
                            playListAdapter.setData(player.getPlayList());
                            mDataBinding.vpRecord.setBannerData(playList);
                            mDataBinding.vpRecord.setBannerCurrentItem(MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex(), isShow());
                            mDataBinding.vpRecord.getViewPager().setOffscreenPageLimit(playList.size() - 1);
//                            mDataBinding.vpRecord.setBannerCurrentItem(
//                                    MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex());
                        }
                    }
                }
            });

            LiveDataBus.get().with(LiveDataBusConstants.Player.PLAY_SPEED, Float.class).observe(this, aFloat -> {
                if (aFloat == null) return;
                mDataBinding.incPlayer.tvSpeed.setText(speedChoosePopupView.wrapFloat2SpeedStr(aFloat));
            });

//            LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST, Boolean.class).observe(this, aBoolean -> {
////                LogUtils.d("show list : " + aBoolean);
//                if (aBoolean == null) return;
//                if (!aBoolean) {  // 收起播放列表
//                    hidePlayList();
//                } else {  // 展开播放列表
//                    showPlayList();
//                }
//                mDataBinding.getRoot().postDelayed(() -> {
//                    int vis = aBoolean ? INVISIBLE : VISIBLE;
//                    mDataBinding.incPlayer.tvTitle.setVisibility(vis);
//                    mDataBinding.incPlayer.tvLabel.setVisibility(vis);
//                    mDataBinding.incPlayer.tvSubtitle.setVisibility(vis);
//                    mDataBinding.incPlayer.llIcon.setVisibility(vis);
//                }, 100);
//            });

            // 无论在前台后台 只要切换了音乐 就去切换vp
            LiveDataBus.get().with(LiveDataBusConstants.PLAY_INDEX, Integer.class).observe((LifecycleOwner) mContext, integer -> {
                if (integer != null) {
                    mDataBinding.vpRecord.setBannerCurrentItem(integer, isShow());
                }
            });
        }

        mDataBinding.getRoot().postDelayed(() -> {
            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY).postValue(LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY).getValue());
            LiveDataBus.get().with(LiveDataBusConstants.Player.ITEM_PLAY).postValue(LiveDataBus.get().with(LiveDataBusConstants.Player.ITEM_PLAY).getValue());
            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER).postValue(LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER).getValue());
            LiveDataBus.get().with(LiveDataBusConstants.Player.PLAY_SPEED).postValue(LiveDataBus.get().with(LiveDataBusConstants.Player.PLAY_SPEED).getValue());
//            LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST).postValue(LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST).getValue());
        }, 100);
    }

    private void initListener() {
        // 隐藏全屏播放器
        mDataBinding.imgCancel.setOnClickListener(v -> {
            PlayerVisionManager.getInstance().hideFullPlayer();
        });

        // 进度条
        mDataBinding.incPlayer.progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LogUtils.d("on progress : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragProgress = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragProgress = false;
                // LogUtils.d("drag progress : " + seekBar.getProgress());
                MusicPlayManager.getInstance().setProgress(seekBar.getProgress());
                LiveDataBus.get().with("refresh_video_progress", Integer.class).postValue((int) (seekBar.getProgress() / 100.f));
            }
        });

        // 播放模式点击
        mDataBinding.incPlayer.imgMode.setOnClickListener(v -> {
            MusicPlayerBean.PlaybackMode toMode;

            MusicListBean currentItem = MusicPlayManager.getInstance().getCurrentItem();
            if (currentItem != null) {
                String typeName = currentItem.getTypeName();
                if ("dt".equals(typeName)) {
                    switch (MusicPlayManager.getInstance().getPlaybackMode()) {
                        case List_Loop:
                            toMode = MusicPlayerBean.PlaybackMode.Single_Cycle;
                            break;
                        default:
                            toMode = MusicPlayerBean.PlaybackMode.List_Loop;
                            break;
                    }
                    MusicPlayManager.getInstance().setPlaybackMode(toMode);
                    return;
                }
            }

            switch (MusicPlayManager.getInstance().getPlaybackMode()) {
                case List_Loop:
                    toMode = MusicPlayerBean.PlaybackMode.Single_Cycle;
                    break;
                case Single_Cycle:
                    toMode = MusicPlayerBean.PlaybackMode.Random_Cycle;
                    break;
                default:
                    toMode = MusicPlayerBean.PlaybackMode.List_Loop;
                    break;
            }
            MusicPlayManager.getInstance().setPlaybackMode(toMode);
        });

        // 倍速按钮点击
        mDataBinding.incPlayer.tvSpeed.setOnClickListener(v -> {
            if (speedChoosePopupView == null) {
                speedChoosePopupView = new SpeedChoosePopupView(mContext);
                speedChoosePopupView.setOnItemClick(new SpeedChoosePopupView.OnItemClick() {
                    @Override
                    public void onItemClick(LayoutItemSpeedBinding dataBinding, SpeedChoosePopupView.SpeedBean bean, int position) {
                        // 设置倍数 可能需要设置
                        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(mContext);
                        sharedPreferencesUtil.putFloat(LiveDataBusConstants.Player.PLAY_SPEED, bean.getSpeed());
                        MusicPlayManager.getInstance().setSpeed(bean.getSpeed());
                    }
                });
            }

            removeCallbacks(speedChooseDismissRunnable);

            if (speedChoosePopupView.isShow()) {
                speedChoosePopupView.dismiss();
            } else {
                int xOffset = (speedChoosePopupView.getPlanWidth() - mDataBinding.incPlayer.tvSpeed.getWidth()) / 2;
                int margin = DensityUtils.dp2Px(mContext, 0);
                int yOffset = speedChoosePopupView.getPlanHeight() + mDataBinding.incPlayer.tvSpeed.getHeight() + margin;
                String text = mDataBinding.incPlayer.tvSpeed.getText().toString();

                speedChoosePopupView.showAsDropDown(mDataBinding.incPlayer.tvSpeed,
                        -xOffset, -yOffset);
                postDelayed(speedChooseDismissRunnable, 10_000);
            }
        });

        // 上一首
        mDataBinding.incPlayer.imgPrevious.setOnClickListener(v -> {
            // 当前在台宣 点击无效
            if (MusicPlayManager.getInstance().isInTaixuan()) return;

            // 做一个屏蔽播放按钮变化的时间屏蔽 如果是暂停的状态 不需要切换
            if (MusicPlayManager.getInstance().isPlaying()) {
                startPlayBtnChange();
            }
            if (MusicPlayManager.getInstance().isInRandomCycle()) {
                MusicPlayManager.getInstance().playRandom();
                return;
            }
            startPointAnimation(false, true);
            mDataBinding.vpRecord.getViewPager().setCurrentItem(mDataBinding.vpRecord.getViewPager().getCurrentItem() - 1, true);
        });

        // 下一首
        mDataBinding.incPlayer.imgNext.setOnClickListener(v -> {
            // 当前在台宣 点击无效
            if (MusicPlayManager.getInstance().isInTaixuan()) return;
            // 做一个屏蔽播放按钮变化的时间屏蔽 如果是暂停的状态 不需要切换
            if (MusicPlayManager.getInstance().isPlaying()) {
                startPlayBtnChange();
            }
            if (MusicPlayManager.getInstance().isInRandomCycle()) {
                MusicPlayManager.getInstance().playRandom();
                return;
            }
            startPointAnimation(false, true);
            mDataBinding.vpRecord.getViewPager().setCurrentItem(mDataBinding.vpRecord.getViewPager().getCurrentItem() + 1, true);
        });

        // 播放按钮点击
        mDataBinding.incPlayer.imgPlay.setOnClickListener(v -> {
            // 当前在台宣 点击无效
            if (MusicPlayManager.getInstance().isInTaixuan()) return;
            // 当前是点击下一曲/上一曲的情况下 播放按钮点击后立即切换状态
            if (!canPlayBtnChange) {
                mDataBinding.getRoot().removeCallbacks(playBtnChangeRunnable);
                mDataBinding.getRoot().post(playBtnChangeRunnable);
            }

            if (MusicPlayManager.getInstance().isPlaying()) {
                MusicPlayManager.getInstance().pauseMusic();
//                startPointAnimation(false);
            } else {
                MusicPlayManager.getInstance().playMusic();
//                startPointAnimation(true);
            }
        });

        // 菜单按钮点击
        mDataBinding.incPlayer.imgMenu.setOnClickListener(v -> {
//            Boolean value = LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST, Boolean.class).getValue();
//            boolean currentShowList = value != null ? value : false;
//            LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST).postValue(!currentShowList);
//            if (!currentShowList) {
//                isSb = true;
//            }
            // 05 18 需求 半瓶的一个播放列表显示
            if (PlayerVisionManager.getInstance().getPlayList().isShow()) {
                PlayerVisionManager.getInstance().dismissPlayList();
            } else {
                PlayerVisionManager.getInstance().showPlayList();
            }
        });

        // 收藏按钮点击
        mDataBinding.incPlayer.imgCollect.setOnClickListener(v -> MusicPlayManager.getInstance().setCollect(!MusicPlayManager.getInstance().isCollect()));

        // 视频音频切换按钮点击
        mDataBinding.incPlayer.imgVideoOrMusic.setOnClickListener(v -> {
            // todo 视频播放时 切换视频和音频
        });

        // 前15s按钮点击
        mDataBinding.incPlayer.imgPrevious15.setOnClickListener(v -> {
            // 当前在台宣 点击无效
            if (MusicPlayManager.getInstance().isInTaixuan()) return;
            MusicPlayManager.getInstance().playPrevious(-15);
        });


        // 后30s按钮点击
        mDataBinding.incPlayer.imgNext30.setOnClickListener(v -> {
            // 当前在台宣 点击无效
            if (MusicPlayManager.getInstance().isInTaixuan()) return;
            MusicPlayManager.getInstance().playPrevious(30);
        });

        // 声音设置按钮
        mDataBinding.llAudioSetting.setOnClickListener(v -> {
            LiveDataBus.get().with(LiveDataBusConstants.to_fragment).postValue(new SoundSettingsFragment());

            LiveDataBus.get().with(LiveDataBusConstants.to_soundSettings_from_fullPlayer).postValue(true);

            mDataBinding.getRoot().postDelayed(() -> PlayerVisionManager.getInstance().hideFullPlayer(), 400);
        });
    }

    private boolean canPlayBtnChange = true;

    /**
     * 开始计时 时间到后才运行按钮状态发生
     */
    private void startPlayBtnChange() {
        mDataBinding.getRoot().removeCallbacks(playBtnChangeRunnable);
        canPlayBtnChange = false;
        mDataBinding.getRoot().postDelayed(playBtnChangeRunnable, 1000);
    }

    private Runnable playBtnChangeRunnable = () -> canPlayBtnChange = true;

    /**
     * 显示播放列表
     */
    private void showPlayList() {
        setClTopTranslationY(true);
        mDataBinding.incPlayer.clBottom.setBackgroundColor(Color.TRANSPARENT);
        mDataBinding.clTop.setBackgroundColor(Color.TRANSPARENT);
        mDataBinding.clPlayerList.setBackgroundColor(Color.TRANSPARENT);
        mDataBinding.clTop.setVisibility(INVISIBLE);
        mDataBinding.incPlayer.tvLabel.setVisibility(INVISIBLE);
        mDataBinding.incPlayer.imgVideoOrMusic.setVisibility(INVISIBLE);
        mDataBinding.incPlayer.imgPrevious15.setVisibility(INVISIBLE);
        mDataBinding.incPlayer.imgNext30.setVisibility(INVISIBLE);
        mDataBinding.incPlayer.imgCollect.setVisibility(INVISIBLE);
        mDataBinding.recyclerPlayList.scrollToPosition(MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex());

        Glide.with(this).load(url).apply(RequestOptions.bitmapTransform(new BlurTransformation(18, 13))).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                // 创建一个新的 Drawable
                Drawable maskedDrawable = new ColorDrawable(getResources().getColor(R.color.list_color));
                // 使用 PorterDuff.Mode.SRC_OVER 模式将原始图片和蒙版组合起来
                Drawable[] layers = {resource, maskedDrawable};
                LayerDrawable layerDrawable = new LayerDrawable(layers);
                layerDrawable.setLayerGravity(1, Gravity.FILL);
                // 设置新的 Drawable 为根布局的背景
                mDataBinding.clPlayerList.setBackground(layerDrawable);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    /**
     * 隐藏播放列表
     */
    private void hidePlayList() {
        setClTopTranslationY(false);
        mDataBinding.clTop.setVisibility(VISIBLE);
        mDataBinding.incPlayer.tvLabel.setVisibility(VISIBLE);
        mDataBinding.incPlayer.clBottom.setBackgroundColor(getResources().getColor(R.color.cl_bottom_color));
        mDataBinding.clTop.setBackground(mContext.getDrawable(R.drawable.img_full_player_bg));
        mDataBinding.incPlayer.imgCollect.setVisibility(VISIBLE);
        // 是有声 才会显示前15s后30s
        MusicListBean currentItem = MusicPlayManager.getInstance().getCurrentItem();
        if (currentItem != null && "3".equals(currentItem.getLibraryType())) {
//            mDataBinding.incPlayer.imgVideoOrMusic.setVisibility(VISIBLE);
            mDataBinding.incPlayer.imgPrevious15.setVisibility(VISIBLE);
            mDataBinding.incPlayer.imgNext30.setVisibility(VISIBLE);
        }
    }

    /**
     * 设置cl top的
     *
     * @param isToTop 是否向上
     */
    private void setClTopTranslationY(boolean isToTop) {
        LogUtils.d("current ty : " + mDataBinding.vpRecord.getTranslationY());
//        for (int i = 0; i < mDataBinding.clTop.getChildCount(); i++) {
//            View child = mDataBinding.clTop.getChildAt(i);
        int toTranslationY = (int) (mDataBinding.getRoot().getHeight() * (isToTop ? -1.f : 0.f));

        ObjectAnimator.ofFloat(mDataBinding.clPlayerList, "translationY", toTranslationY).start();
//        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private ObjectAnimator pointRotation;
    private ObjectAnimator pointTranslationX;

    //    private ObjectAnimator imgShowRotation;
    public static final int LEFT = 0;
    public static final int TO_LEFT = 1;
    public static final int TO_RIGHT = 2;
    public static final int RIGHT = 3;

    private int pointStatus = 0;

    /**
     * 播放指针旋转
     *
     * @param toRight 是否偏右
     * @param smooth  是否丝滑移动
     */
    private void startPointAnimation(boolean toRight, boolean smooth) {
//        LogUtils.d("start point animation : " + toRight
//                + " smooth : " + smooth);
        mDataBinding.getRoot().post(() -> {
            if (pointRotation != null) {
                switch (pointStatus) {
                    case LEFT:
                    case TO_LEFT:
                        if (!toRight) return;
                        break;
                    case RIGHT:
                    case TO_RIGHT:
                        if (toRight) return;
                        break;
                }

                pointRotation.cancel();
            }

            mDataBinding.imgPoint.setPivotX(mDataBinding.imgPoint.getWidth() / 2.f);
            mDataBinding.imgPoint.setPivotY(mDataBinding.imgPoint.getHeight() * 0.15f);
            pointRotation = ObjectAnimator.ofFloat(mDataBinding.imgPoint, "rotation", mDataBinding.imgPoint.getRotation(), toRight ? -15 : 0);
            pointRotation.setDuration(smooth ? VP_SCROLL_DURATION / 2 : 1);
            pointRotation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    pointStatus = toRight ? RIGHT : LEFT;
                }
            });
            pointRotation.start();
        });
    }

    /**
     * 值含义通pointStatus
     */
    private int pointXStatus = 0;

    /**
     * 播放指针横向移动
     *
     * @param newHasLrc 是否有歌词（有歌词就在左侧，无歌词在右侧）
     * @param smooth    是否丝滑移动
     */
    private void setPointMove(boolean newHasLrc, boolean smooth) {
//        LogUtils.d("point move : " + newHasLrc + " smooth : " + smooth);
        mDataBinding.getRoot().post(() -> {
            if (pointTranslationX != null) {
                switch (pointXStatus) {
                    case LEFT:
                    case TO_LEFT:
                        if (newHasLrc) return;
                        break;
                    case RIGHT:
                    case TO_RIGHT:
                        if (!newHasLrc) return;
                        break;
                }

                pointTranslationX.cancel();
            }
            pointTranslationX = ObjectAnimator.ofFloat(mDataBinding.imgPoint, "translationX", mDataBinding.imgPoint.getTranslationX(), newHasLrc ? DensityUtils.dp2Px(mContext, SPUtils.isPad() ? -166 - 33 : -137) : DensityUtils.dp2Px(mContext, SPUtils.isPad() ? -33 : 0));
            pointTranslationX.setDuration(smooth ? VP_SCROLL_DURATION / 2 : 1);
            pointTranslationX.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    pointXStatus = !newHasLrc ? RIGHT : LEFT;
                }
            });
            pointTranslationX.start();
            pointXStatus = !newHasLrc ? TO_RIGHT : TO_LEFT;
        });

//        if (isPointMoving) return;
////        LogUtils.d("has lrc : " + newHasLrc);
//        if (newHasLrc) {  // 无歌词到歌词 需要左移
//            if (pointXStatus == 1) return;
//            ObjectAnimator animator = ObjectAnimator
//                    .ofFloat(mDataBinding.imgPoint, "translationX",
//                            mDataBinding.imgPoint.getTranslationX(), DensityUtils.dp2Px(mContext, SPUtils.isPad() ? -166 - 33 : -300));
//            animator.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    super.onAnimationStart(animation);
//                    isPointMoving = true;
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    isPointMoving = false;
//                    pointXStatus = 1;
//                }
//            });
//            animator.start();
//        } else { // 有歌词到无歌词
//            if (pointXStatus == 2) {
//                return;
//            }
//            ObjectAnimator animator = ObjectAnimator
//                    .ofFloat(mDataBinding.imgPoint, "translationX", mDataBinding.imgPoint.getTranslationX(),
//                            DensityUtils.dp2Px(mContext, SPUtils.isPad() ? -33 : 0));
//            animator.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    super.onAnimationStart(animation);
//                    isPointMoving = true;
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    isPointMoving = false;
//                    pointXStatus = 2;
//                }
//            });
//            animator.start();
//        }
    }


    @Override
    protected int getPopupWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getPopupHeight() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        LogUtils.d("position : " + position + "high light pos : " + highLightPosition + " play pos : " + MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex());

        // position表示vp现在真正的position 如果和播放的item不一样 就播放对应的歌曲
        boolean isDif = position != MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex();

        if (/*isUserScrolling() &&*/ isDif && isShow()) {
            // 这说明当前播放的不是同一首歌 需要跳转播放
            mDataBinding.incPlayer.progress.setProgress(0);
            MusicPlayManager.getInstance().playMusic(mData, mData.get(position), position, MusicPlayManager.getInstance().getCurrentAlbumId());
        }


        MusicListBean bean = MusicPlayManager.getInstance().getCurrentItem();
        String videoUrl = bean.getVideoUrl();
        String wordUrl = bean.getWordUrl();

        boolean newHasLrc = wordUrl != null && !wordUrl.isEmpty();

        setPointMove(newHasLrc, isShow());

        // 使用glide 设置背景高斯模糊 + 65透明黑色蒙层
        Glide.with(mContext).load(bean.getImageUrl()).apply(RequestOptions.bitmapTransform(new MultiTransformation<>(new BlurTransformation(40, 6), new ColorFilterTransformation(Color.parseColor("#A6131313"))))).into(new ViewTarget<View, Drawable>(mDataBinding.vpRecord) {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                this.view.setBackground(resource);
            }
        });
    }

    private boolean isPointMoving;

    private boolean isUserScrolling = false;
    private int scrollState;

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_SETTLING) {
            startPlayBtnChange();// 屏蔽播放按钮变化1s
            // 用户手动拖拽后松手，界面开始自动滑动 --> 延时后再恢复 用于select判断是手拖动的还是代码跳转的
            mDataBinding.getRoot().postDelayed((Runnable) () -> isUserScrolling = false, (long) (VP_SCROLL_DURATION * 1.1f));
            mDataBinding.getRoot().postDelayed(() -> scrollState = ViewPager.SCROLL_STATE_IDLE, 500);
            // 指针开始抬起动画
            startPointAnimation(false, true);
        } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            // 用户手动拖拽开始
            isUserScrolling = true;
            // 指针开始抬起动画
            startPointAnimation(false, true);
        } else if (state == ViewPager.SCROLL_STATE_IDLE) {
//            startPointAnimation(true);
        }
        scrollState = state;
    }

    private long showTime;

    private boolean isShow = false;

    @Override
    public boolean isShow() {
        return isShow;
    }

    @Override
    public BasePopupView show() {
        isShow = true;
        BasePopupView show = super.show();
        if (MusicPlayManager.getInstance().getCurrentItem() != null
                && MusicPlayManager.getInstance().isPlaying()) {
            postDelayed(() -> {
                        MusicPlayManager.getInstance().tryRequestFocus();
                        MusicPlayManager.getInstance().playMusic();
                    },
                    popupInfo.animationDuration + 100);
        }
        return show;
    }

    @Override
    protected void onShow() {
        super.onShow();

        // 设置recycler 和 cl top 一样高
        ViewGroup.LayoutParams layoutParams = mDataBinding.clPlayerList.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = mDataBinding.getRoot().getHeight();
            mDataBinding.clPlayerList.setLayoutParams(layoutParams);
        }

        if (MusicPlayManager.getInstance().isInTaixuan()) {
            mDataBinding.getRoot().postDelayed(() -> setPointMove(false, isShow()), 100);
        }

//        mDataBinding.vpRecord.setBannerCurrentItem(MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex(), false);

//        startPointAnimation(MusicPlayManager.getInstance().isPlaying());
    }

    @Override
    protected void onDismiss() {
        isShow = false;
        super.onDismiss();

        currentPlayStatus = Paused;
//        LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST).postValue(false);
        // 回收视频
        if (currentMp != null) {
            try {
                currentMp.pause();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isHasFirstIn() {
        return hasFirstIn;
    }

    public void toNext() {
        mDataBinding.getRoot().post(() -> mDataBinding.vpRecord.getViewPager().setBannerCurrentItemInternal(mDataBinding.vpRecord.getViewPager().getCurrentItem() + 1, true));
    }

    public void toBefore() {
        mDataBinding.vpRecord.getViewPager().setCurrentItem(mDataBinding.vpRecord.getViewPager().getCurrentItem() - 1);
    }

    public void toIndex(int index, boolean smooth) {
        mDataBinding.vpRecord.setBannerCurrentItem(index, smooth);
    }

    public MyXbanner getXBanner() {
        return mDataBinding.vpRecord;
    }

    public class BannerHolder implements HolderCreator<BannerHolder>, ViewHolder<MusicListBean> {
        private Map<Integer, View> mViews;
        private Map<Integer, ObjectAnimator> mAnimators;

        public BannerHolder() {
            mViews = new HashMap<>();
            mAnimators = new HashMap<>();
        }

        @Override
        public BannerHolder createViewHolder(int viewType) {
            return this;
        }

        @Override
        public int getViewType(int position) {
            return position;
        }

        @Override
        public int getLayoutId() {
            return R.layout.layout_item_record;
        }

        @Override
        public void onBind(View itemView, MusicListBean data, int position) {
            bindItem(itemView, null, position, data);
        }

        @SuppressLint("ClickableViewAccessibility")
        protected void bindItem(View view, ViewGroup container, int position, MusicListBean bean) {
//            LogUtils.d("position : " + position + " view : " + view);

            // 满足xbanner区域可以下拉
            view.setOnTouchListener((view1, motionEvent) -> bottomPopupContainer.onTouchEvent(motionEvent));

            int realPos = position;
//            saveView(view, position);
//            saveRotationAnimation(view, position);

            if (bean == null) {
                return;
            }

            String videoUrl = bean.getVideoUrl();
            String audioUrl = bean.getAudioUrl();

//            videoUrl = "http://dynaudio.oss-cn-hangzhou.aliyuncs.com/SalesDemo/SalesDemoMute.mp4";
            VideoView vPlay = view.findViewById(R.id.vplay);
            ImageView imgShow = view.findViewById(R.id.img_show);
            ImageView imgRecord = view.findViewById(R.id.img_record);
            MyLrc lrcView = view.findViewById(R.id.lv_lyrics);
//            View bottom = view.findViewById(R.id.mask_bottom);
            ImageLoader.load(mContext, bean.getImageUrl(), imgShow);
            ObjectAnimator animator = ObjectAnimator.ofFloat(imgShow, "rotation", 0, 360);
            animator.setDuration(20_000);
            animator.setRepeatCount(-1);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setInterpolator(new LinearInterpolator());
            mAnimators.put(position, animator);

            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, bean1 -> {
                if (bean1 != null && bean.getSpecialId() == bean1.getSpecialId()) {
                    lrcView.updateTime(bean1.getCurrentTime());
                }
            });

            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe((LifecycleOwner) mContext, player -> {
                if (player == null) return;
                if (position == MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex()) {
                    if (player.getPlayStatus() == Playing && !MusicPlayManager.getInstance().isInTaixuan()) {
                        if (animator.isStarted()) {
                            animator.resume();
                        } else {
                            animator.start();
                        }
                    } else {
                        animator.pause();
                    }
                } else {
                    animator.cancel();
                }
            });

            vPlay.setVisibility(videoUrl != null ? VISIBLE : INVISIBLE);
            imgShow.setVisibility(videoUrl != null ? INVISIBLE : VISIBLE);
            imgRecord.setVisibility(videoUrl != null ? INVISIBLE : VISIBLE);

            if (videoUrl != null && audioUrl == null) {  // 仅视频  0415 把音频静音
                playVideo(vPlay, videoUrl, position);
                isInVideo = true;
            } else if (videoUrl == null && audioUrl != null) {  // 仅音频
                isInVideo = false;
            } else if (videoUrl != null && audioUrl != null) {  // 音频视频都有

            }


            String wordUrl = bean.getWordUrl();

            if (wordUrl != null && !wordUrl.isEmpty()) {
                // 有歌词
                lrcView.setLabel(mContext.getString(R.string.lrc_loading));
                lrcView.setVisibility(VISIBLE);
                // 解析歌词 装填
                lrcView.loadLrcByUrl(bean.getWordUrl());


                lrcView.setOnTapListener((view12, showText, time) -> {
                    if (showText != null) {
                        MusicPlayManager.getInstance().setTime(time);
                    }
                });

                lrcView.setDraggable(true, (view13, time) -> false);

                imgRecord.setTranslationX(DensityUtils.dp2Px(mContext, SPUtils.isPad() ? -166 : -137));
                imgShow.setTranslationX(DensityUtils.dp2Px(mContext, SPUtils.isPad() ? -166 : -137));
                lrcView.setTranslationX(DensityUtils.dp2Px(mContext, SPUtils.isPad() ? 40 : 0));
//                bottom.setTranslationX(DensityUtils.dp2Px(mContext, -36));

            } else {
                lrcView.setVisibility(INVISIBLE);
//                bottom.setTranslationX(DensityUtils.dp2Px(mContext, -36));
            }

//            Log.e("黑胶图案", "设置专辑封面");

        }

        private void playVideo(VideoView vplay, String videoUrl, int position) {
            vplay.setVideoURI(Uri.parse(videoUrl));

            videoViews.add(vplay);

            // 监听视频加载的进度
            vplay.setOnPreparedListener(mp -> {
                if (position == highLightPosition) {
                    currentMp = mp;

                    mp.setVolume(0, 0);
                    mp.seekTo((int) MusicPlayManager.getInstance().getPlayer().getCurrentPosition());

                    // LogUtils.d("is playing222 : " + MusicPlayManager.getInstance().isPlaying());
                    // if (MusicPlayManager.getInstance().isPlaying() && CarManager.getInstance().isPMode()) {
                    // 开始播放视频
                    mp.start();
                } else {
                    mDataBinding.getRoot().postDelayed(() -> {
                        try {
                            mp.pause();
                        } catch (IllegalStateException stateException) {
                            stateException.printStackTrace();
                        }
                    }, 300);
                }
                mp.setOnInfoListener((mp1, what, extra) -> {
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        return true;
                    } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    }
                    return false;
                });


                mp.setOnErrorListener((mp1, what, extra) -> {
                    LogUtils.d("on error..." + what + " extra : " + extra);
                    return true;
                });

                LiveDataBus.get().with("refresh_video_progress", Integer.class).observe((LifecycleOwner) mContext, integer -> {
                    try {
                        int progress = integer;
                        // 获取当前播放进度
                        long duration = mp.getDuration();

                        // 计算指定进度的位置
                        int seekPosition = (int) (duration * (progress / 100.0f));

                        // 将播放进度跳转到指定的位置
                        mDataBinding.incPlayer.progress.setProgress(progress);
                        mp.seekTo(seekPosition);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                });
            });

            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe((LifecycleOwner) mContext, musicPlayerBean -> {
                if (musicPlayerBean != null && highLightPosition == position) {
                    LogUtils.d("status : " + musicPlayerBean.getPlayStatus());

                    switch (musicPlayerBean.getPlayStatus()) {
                        case Playing:
                            if (!vplay.isPlaying()/* && CarManager.getInstance().isPMode()*/) {
                                vplay.start();
                            }
                            break;
                        case Paused:
                            if (vplay.isPlaying()) {
                                vplay.pause();
                            }
                            break;
                    }
                }
            });
        }
    }

//    /**
//     * 傻B需求
//     */
//    @Override
//    protected boolean processKeyEvent(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//            Boolean value = LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST, Boolean.class).getValue();
//            boolean currentShowList = value != null ? value : false;
//            if (currentShowList && isSb) {
//                isSb = false;
//                LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST).postValue(false);
//                return true;
//            }
//        } /*else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
//            MusicPlayManager.getInstance().playPrevious();// 上一首
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
//            MusicPlayManager.getInstance().playNext();// 下一首
//            return true;
//        }*/
//        return super.processKeyEvent(keyCode, event);
//    }
}
