package com.byd.dynaudio_app.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.AlbumBean;
import com.byd.dynaudio_app.databinding.LayoutViewItemBannerBinding;
import com.byd.dynaudio_app.databinding.LayoutViewNewsCardBinding;
import com.byd.dynaudio_app.fragment.StationDetailFragment;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.network.NetworkObserver;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.ToastUtis;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class NewsCard extends BaseView<LayoutViewNewsCardBinding> {
    private BannerAdapter adapter;

    private int selectTextSize = 18, unselectTextColor = 15;

    public NewsCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void init(AttributeSet attrs) {
        adapter = new BannerAdapter();
        mDataBinding.vpBanner.setAdapter(adapter);
        mDataBinding.vpBanner.setPageTransformer(false, new BannerTransformer());

        mDataBinding.stlTitle.setViewPager(mDataBinding.vpBanner);

        mDataBinding.vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // LogUtils.d("change : " + position);
                List<AlbumBean> data = adapter.getData();
                if (data != null) {
                    for (int i = 0; i < data.size(); i++) {
                        mDataBinding.stlTitle.getTitleView(i).setTextSize(position == i ? selectTextSize : unselectTextColor);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Glide.with(mContext)
                .load("")
                .into(mDataBinding.imgBg);
    }

    public void setData(@NonNull List<AlbumBean> data) {
        adapter.setData(data);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_news_card;
    }

    public class BannerAdapter extends PagerAdapter {
        private List<AlbumBean> mData = new ArrayList<>();

        @Override
        public int getCount() {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutViewItemBannerBinding bannerBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_item_banner, container, false);
            AlbumBean bean = mData.get(position);

            String albumImgUrl = bean.getAlbumImgUrl();

//            // 0407 适配pad的图 pad上使用不同的资源 只有这张图使用
//            if ("http://dynaudio.oss-cn-hangzhou.aliyuncs.com/radio_dyna.png".equals(albumImgUrl)) {
//                albumImgUrl = "http://dynaudio.oss-cn-hangzhou.aliyuncs.com/radio_dyna_pad.png";
//            }
            if (!TextUtils.isEmpty(albumImgUrl)) {
                bannerBinding.imgPlay.setVisibility(VISIBLE);
                bannerBinding.tvContent1.setBackgroundColor(getResources().getColor(R.color.transport));
                bannerBinding.tvContent2.setBackgroundColor(getResources().getColor(R.color.transport));
                bannerBinding.tvContent3.setBackgroundColor(getResources().getColor(R.color.transport));
            }

            Glide.with(mContext)
                    .load(albumImgUrl)
                    .apply(new RequestOptions().centerCrop().override(bannerBinding.imgBanner.getWidth(), bannerBinding.imgBanner.getHeight()))
                    .into(bannerBinding.imgBanner);

//            ImageLoader.load(mContext, bean.getAlbumImgUrl(), bannerBinding.imgBanner);

            // LogUtils.d("22222 1" + bean.getAlbumImgUrl());

            List<MusicListBean> libraryStoreList = bean.getLibraryStoreList();


            if (libraryStoreList != null) {
                if (libraryStoreList.size() > 1) {
                    // 先记录下信息 每首非台宣的前方及后方是否为台宣
                    for (int i = 0; i < libraryStoreList.size(); i++) {
                        if (!"2".equals(libraryStoreList.get(i).getLibraryType())) {
                            if (i >= 1) {
                                // 判断前方
                                if ("2".equals(libraryStoreList.get(i - 1).getLibraryType())) {
                                    libraryStoreList.get(i).setBeforeAudioUrl(libraryStoreList.get(i - 1).getAudioUrl());
                                }
                            }
                            if (i <= libraryStoreList.size() - 2) {
                                // 判断后方
                                if ("2".equals(libraryStoreList.get(i + 1).getLibraryType())) {
                                    libraryStoreList.get(i).setAfterAudioUrl(libraryStoreList.get(i + 1).getAudioUrl());
                                }
                            }

                            // 如果最后一首不是台宣 且第一首是台宣 设置最后一首的after为第一首
                            if (i == libraryStoreList.size() - 1 && "2".equals(libraryStoreList.get(0).getLibraryType())) {
                                libraryStoreList.get(i).setAfterAudioUrl(libraryStoreList.get(0).getAudioUrl());
                            }
                        }
                    }

                    // 列表里直接去除台宣 在每次播放的时候 判断是否需要播放台宣
                    libraryStoreList.removeIf(bean1 -> bean1 != null && "2".equals(bean1.getLibraryType()));
                    int count = 0;
                    for (int i = 0; i < libraryStoreList.size(); i++) {
                        MusicListBean musicListBean = libraryStoreList.get(i);
                        if (!"2".equals(musicListBean.getLibraryType())) {
                            // 是台宣 不显示
                            count++;
                            // LogUtils.d("bean : " + musicListBean.getName());
                            switch (count) {
                                case 1:
                                    bannerBinding.tvContent1.setText(musicListBean.getName());
                                    break;
                                case 2:
                                    bannerBinding.tvContent2.setText(musicListBean.getName());
                                    break;
                                case 3:
                                    bannerBinding.tvContent3.setText(musicListBean.getName());
                                    break;
                            }
                        }
                    }
                }
            }

            TouchUtils.bindClickItem(bannerBinding.imgPlay);


            // 如果只有一个item 显示第三行
            boolean showLine3 = adapter != null && adapter.getData() != null && adapter.getData().size() == 1;
            bannerBinding.tvTime3.setVisibility(showLine3 ? VISIBLE : INVISIBLE);
            bannerBinding.tvContent3.setVisibility(showLine3 ? VISIBLE : INVISIBLE);

            bannerBinding.getRoot().setOnClickListener(v -> {
                if (!NetworkObserver.getInstance().isConnectNormal(mContext)) { // 没网不跳转
                    ToastUtis.noNetWorkToast(mContext);
                    return;
                }
                LiveDataBus.get().with(LiveDataBusConstants.station_data).postValue(bean);

                LiveDataBus.get().with(LiveDataBusConstants.station_detail_id).postValue(String.valueOf(bean.getId()));

                LiveDataBus.get().with(LiveDataBusConstants.to_fragment)
                        .postValue(new StationDetailFragment());
            });

            // 点击播放按钮 添加到播放列表
            bannerBinding.imgPlay.setOnClickListener(v -> {
                MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();

                if (String.valueOf(bean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                        && (value != null && TextUtils.equals(value.getTypeName(), libraryStoreList.get(0).getTypeName()))) { // 操作是当前的专辑
                    if (MusicPlayManager.getInstance().isPlaying()) {
                        MusicPlayManager.getInstance().pauseMusic();
                    } else {
                        MusicPlayManager.getInstance().playMusic();
                    }
                } else { // 不是当前的专辑 直接切换专辑
                    MusicPlayManager.getInstance().addToPlaylistAndPlay(String.valueOf(bean.getId()), libraryStoreList);
                    MusicPlayManager.getInstance().playMusic();
                }
            });

            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class)
                    .observe((LifecycleOwner) mContext, musicPlayerBean ->
                            ImageLoader.load(mContext,
                                    MusicPlayManager.getInstance().isPlaying()
                                            && String.valueOf(bean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                                            ? R.mipmap.ic_pause_small
                                            : R.mipmap.ic_play_small, bannerBinding.imgPlay));


            container.addView(bannerBinding.getRoot());
            return bannerBinding.getRoot();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (mData != null && position < mData.size()) {
                return mData.get(position).getAlbumName();
            }

            return super.getPageTitle(position);
        }

        public void setData(List<AlbumBean> data) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();

            if (data.size() > 1) {
                mDataBinding.stlTitle.setVisibility(VISIBLE);
                String[] titles = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    titles[i] = data.get(i).getAlbumName();
                }

                mDataBinding.stlTitle.setViewPager(mDataBinding.vpBanner, titles);
                if (titles.length > 1) {
                    mDataBinding.vpBanner.setCurrentItem(1, true);
                    mDataBinding.stlTitle.getTitleView(1).setTextSize(selectTextSize);
                }
            } else {
                mDataBinding.stlTitle.setVisibility(GONE);
            }
        }

        public List<AlbumBean> getData() {
            return mData;
        }
    }

    public class BannerTransformer implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.8F;

        @Override
        public void transformPage(@NonNull View view, float position) {
            Float scale = Math.max(1 - Math.abs(position), MIN_SCALE);
            if (scale != null) {
                view.setScaleY(scale);
                view.setScaleX(scale);
            }
        }
    }
}
