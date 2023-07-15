package com.byd.dynaudio_app.custom;


import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_3D_IMMERSION_SOUND;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_AUDIO_COLUMN;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_BLACK_PLASTIC;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.AlbumBean;
import com.byd.dynaudio_app.databinding.LayoutCardItem3dImmersionBinding;
import com.byd.dynaudio_app.databinding.LayoutCardItemAudioColumnBinding;
import com.byd.dynaudio_app.databinding.LayoutCardItemBlackPlasticBinding;
import com.byd.dynaudio_app.databinding.LayoutViewColumnCardBinding;
import com.byd.dynaudio_app.fragment.AggregationFragment;
import com.byd.dynaudio_app.fragment.AlbumDetailsFragment;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.JumpUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.util.ArrayList;
import java.util.List;

public class ColumnCard extends BaseView<LayoutViewColumnCardBinding> {

    private GridLayoutManager layoutManager;
    private ColumnCardAdapter adapter;

    public ColumnCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        layoutManager = new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false);
        mDataBinding.rvColumn.setLayoutManager(layoutManager);

        adapter = new ColumnCardAdapter(null);
        mDataBinding.rvColumn.setAdapter(adapter);
    }

    /**
     * 设置数据
     */
    public void setData(@NonNull List<AlbumBean> data) {
        adapter.setData(data);
    }

    public void setData(@NonNull List<AlbumBean> data, @NonNull String title) {
        setData(data);

        /**
         * 外层的title绑定
         */
        mDataBinding.tvTitle.setText(title);
    }

    public ColumnCardAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_column_card;
    }

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    class ColumnCardAdapter extends RecyclerView.Adapter<ColumnCardAdapter.ColumnCardViewHolder> {

        private List<AlbumBean> mData;


        public ColumnCardAdapter(List<AlbumBean> data) {
            this.mData = data;
        }

        @NonNull
        @Override
        public ColumnCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layoutId = 0;
            switch (type) {
                case ITEM_TYPE_BLACK_PLASTIC:
                    layoutId = R.layout.layout_card_item_black_plastic;
                    break;
                case ITEM_TYPE_3D_IMMERSION_SOUND:
                    layoutId = R.layout.layout_card_item_3d_immersion;
                    break;
                case ITEM_TYPE_AUDIO_COLUMN:
                    layoutId = R.layout.layout_card_item_audio_column;
                    break;
            }
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), layoutId, parent, false);
            return new ColumnCardViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull ColumnCardViewHolder holder, int position) {
            if (mData == null || position >= mData.size()) {
                return;
            }
            AlbumBean itemBean = mData.get(position);
            if (itemBean == null) return;

            switch (type) {
                case ITEM_TYPE_BLACK_PLASTIC:
                    holder.bindBlackPlastic(itemBean, position);
                    break;
                case ITEM_TYPE_3D_IMMERSION_SOUND:
                    holder.bind3dImmersion(itemBean, position);
                    break;
                case ITEM_TYPE_AUDIO_COLUMN:
                    holder.bindAudioColumn(itemBean, position);
                    break;
            }

            // tab 点击事件
            mDataBinding.vvTop.setOnClickListener(v -> {
                LiveDataBus.get().with(LiveDataBusConstants.aggregation_type, Integer.class).postValue(type);
                // 跳转到对应的界面  ---> 专辑聚合页都是用的aggregation fragment，使用不同的type值区分
                LiveDataBus.get().with(LiveDataBusConstants.to_fragment, BaseFragment.class)
                        .postValue(new AggregationFragment());
            });
        }

        @Override
        public int getItemCount() {
            return mData != null ? Math.min(mData.size(), 9) : 0;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void setData(@NonNull List<AlbumBean> data) {
            if (mData == null) mData = new ArrayList<>();
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }

        class ColumnCardViewHolder extends RecyclerView.ViewHolder {

            public ColumnCardViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            /**
             * 绑定黑胶专区item
             */
            public void bindBlackPlastic(AlbumBean itemBean, int position) {
                ViewDataBinding viewDataBinding = DataBindingUtil.findBinding(itemView);
                if (viewDataBinding instanceof LayoutCardItemBlackPlasticBinding) {
                    LayoutCardItemBlackPlasticBinding binding = (LayoutCardItemBlackPlasticBinding) viewDataBinding;

//                    TouchUtils.bindClickItem(binding.imgPlay);

                    // TODO 后续看标签是否需要改动
                    binding.tvLabel.setLabelImgPath(itemBean.getRecommendLabel());
                    binding.tvTitle.setText(itemBean.getAlbumName());
                    binding.tvSubtitle.setText(SPUtils.formatAuther(itemBean.getSingerList()));

                    // LogUtils.d("url : " + itemBean.getAlbumImgUrl());

//                    Glide.with(mContext).load(itemBean.getAlbumImgUrl()).into(binding.imgTopBg);
//                    ImageLoader.load(mContext, itemBean.getAlbumImgUrl(), binding.imgTopBg);

                    Glide.with(mContext)
                            .setDefaultRequestOptions(ImageLoader.getOptions())
                            .load(itemBean.getAlbumImgUrl())
                            .into(binding.imgTopBg);


                    binding.getRoot().setOnClickListener(v -> {
                        // 跳转到专辑详情页
                        JumpUtils.jumpToDetail(AlbumDetailsFragment.Type.blackGlue, itemBean.getId());
                    });

                    binding.imgPlay.setOnClickListener(v -> {
                        // 黑胶的播放按钮点击
                        boolean playing = MusicPlayManager.getInstance().isPlaying();
                        LogUtils.d("playing : " + playing);
                        if (MusicPlayManager.getInstance().isPlaying()
                                && String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                        ) {
                            MusicPlayManager.getInstance().pauseMusic();
                        } else {
                            MusicPlayManager.getInstance()
                                    .addToPlaylistAndPlay(String.valueOf(itemBean.getId()),
                                            itemBean.getLibraryStoreList());
                        }
                    });

                    LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class)
                            .observe((LifecycleOwner) mContext, bean -> {
                                MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                                binding.imgPlay.setImageResource(
                                        MusicPlayManager.getInstance().isPlaying()
                                                && (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                                                && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))
                                        )
                                                ? R.mipmap.ic_pause_small
                                                : R.mipmap.ic_play_small
                                );
                            });
                }
            }

            /**
             * 绑定有声专栏
             */
            @SuppressLint("SetTextI18n")
            public void bindAudioColumn(AlbumBean itemBean, int position) {
                ViewDataBinding viewDataBinding = DataBindingUtil.findBinding(itemView);
                if (viewDataBinding instanceof LayoutCardItemAudioColumnBinding) {
                    LayoutCardItemAudioColumnBinding binding = (LayoutCardItemAudioColumnBinding) viewDataBinding;
                    TouchUtils.bindClickItem(binding.imgPlay);

                    binding.getRoot().setOnClickListener(v -> JumpUtils.jumpToDetail(AlbumDetailsFragment.Type.blog, itemBean.getId()));

                    // Glide.with(mContext).load(itemBean.getAlbumImgUrl()).into(binding.imgTopBg);
//                    ImageLoader.load(mContext, itemBean.getAlbumImgUrl(), binding.imgTopBg);
                    Glide.with(mContext)
                            .setDefaultRequestOptions(ImageLoader.getOptions())
                            .load(itemBean.getAlbumImgUrl())
                            .into(binding.imgTopBg);

                    binding.tvTitle.setText(itemBean.getAlbumName());
                    binding.tvSubtitle.setText(itemBean.getAlbumSimpleDesc());

                    // 03.28 所有的都按照播客类来处理
                    // 获取最新一期的碎片 然后获取标题
                    List<MusicListBean> libraryStoreList = itemBean.getLibraryStoreList();
                    // todo 按照期数排序 0329后台期数为空


                    // 根据播客类或是有声类分别设置 --> 全部按照
                    // String type1 = itemBean.getType();
                    if (libraryStoreList != null && libraryStoreList.size() > 0) {
                        MusicListBean musicListBean = libraryStoreList.get(libraryStoreList.size() - 1);
                        binding.tvContent2.setText(
                                musicListBean.getBatch() != null
                                        ? "#" + musicListBean.getBatch()
                                        : ""
                                        + musicListBean.getName());
                    }
                    if (libraryStoreList != null && libraryStoreList.size() > 1) {
                        MusicListBean musicListBean = libraryStoreList.get(libraryStoreList.size() - 2);
                        binding.tvContent1.setText(
                                musicListBean.getBatch() != null
                                        ? "#" + musicListBean.getBatch()
                                        : ""
                                        + musicListBean.getName());
                    }

                    binding.tvLabel.setLabelImgPath(itemBean.getRecommendLabel());


                    binding.imgPlay.setOnClickListener(v -> {
                        // 黑胶的播放按钮点击
                        boolean playing = MusicPlayManager.getInstance().isPlaying();
                        LogUtils.d("playing : " + playing);
                        if (MusicPlayManager.getInstance().isPlaying()
                                && String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                        ) {
                            MusicPlayManager.getInstance().pauseMusic();
                        } else {
                            MusicPlayManager.getInstance()
                                    .addToPlaylistAndPlay(String.valueOf(itemBean.getId()),
                                            itemBean.getLibraryStoreList());
                        }
                    });

                    LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class)
                            .observe((LifecycleOwner) mContext, bean -> {
                                MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                                binding.imgPlay.setImageResource(
                                        MusicPlayManager.getInstance().isPlaying()
                                                && (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                                                && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))
                                        )
                                                ? R.mipmap.ic_pause_small
                                                : R.mipmap.ic_play_small
                                );
                            });
                }
            }

            /**
             * 绑定3d沉浸声的item
             */
            @SuppressLint("CheckResult")
            public void bind3dImmersion(@NonNull AlbumBean itemBean, int position) {
                ViewDataBinding viewDataBinding = DataBindingUtil.findBinding(itemView);
                if (viewDataBinding instanceof LayoutCardItem3dImmersionBinding) {
                    LayoutCardItem3dImmersionBinding binding = (LayoutCardItem3dImmersionBinding) viewDataBinding;
                    TouchUtils.bindClickItem(binding.imgPlay);

                    binding.getRoot().setOnClickListener(v -> JumpUtils.jumpToDetail(AlbumDetailsFragment.Type.immersionMusic, itemBean.getId()));


//                    Glide.with(mContext).load(itemBean.getAlbumImgUrl()).into(binding.imgTopBg);
//                    ImageLoader.load(mContext, itemBean.getAlbumImgUrl(), binding.imgTopBg);
                    Glide.with(mContext)
                            .setDefaultRequestOptions(ImageLoader.getOptions())
                            .load(itemBean.getAlbumImgUrl())
                            .into(binding.imgTopBg);

                    binding.tvTitle.setText(itemBean.getAlbumName());
                    binding.tvSubtitle.setText(SPUtils.formatAuther(itemBean.getSingerList()));

                    binding.tvLabel.setLabelImgPath(itemBean.getRecommendLabel());

                    binding.imgPlay.setOnClickListener(v -> {
                        // 黑胶的播放按钮点击
                        boolean playing = MusicPlayManager.getInstance().isPlaying();
                        LogUtils.d("playing : " + playing);
                        if (MusicPlayManager.getInstance().isPlaying()
                                && String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                        ) {
                            MusicPlayManager.getInstance().pauseMusic();
                        } else {
                            MusicPlayManager.getInstance()
                                    .addToPlaylistAndPlay(String.valueOf(itemBean.getId()),
                                            itemBean.getLibraryStoreList());
                        }
                    });

                    LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class)
                            .observe((LifecycleOwner) mContext, bean -> {
                                MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                                binding.imgPlay.setImageResource(
                                        MusicPlayManager.getInstance().isPlaying()
                                                && (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                                                && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))
                                        )
                                                ? R.mipmap.ic_pause_small
                                                : R.mipmap.ic_play_small
                                );
                            });
                }
            }
        }
    }
}
