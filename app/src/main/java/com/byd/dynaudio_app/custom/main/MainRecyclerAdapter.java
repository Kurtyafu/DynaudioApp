package com.byd.dynaudio_app.custom.main;


import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.bean.ColumnBean;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.AlbumBean;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.ModuleBean;
import com.byd.dynaudio_app.bean.response.ModuleInfoBean;
import com.byd.dynaudio_app.custom.ColumnCard;
import com.byd.dynaudio_app.custom.HifiCard;
import com.byd.dynaudio_app.custom.NewsCard;
import com.byd.dynaudio_app.databinding.LayoutViewHifiCardBinding;
import com.byd.dynaudio_app.databinding.LayoutViewSoundAndGoldenBinding;
import com.byd.dynaudio_app.fragment.GoldenSongDetailFragment;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MainRecyclerViewHolder> {
    private List<ColumnBean> mData;
    private Context mContext;
    private HifiCard goldenSongsBinding;
    private NewsCard audioStationBinding;

    public MainRecyclerAdapter(@NonNull List<ColumnBean> mData, @NonNull Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData == null || position >= mData.size()) {
            return super.getItemViewType(position);
        }
        return mData.get(position).getType();
    }

    @NonNull
    @Override
    public MainRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case ITEM_TYPE_BLACK_PLASTIC:
            case ITEM_TYPE_3D_IMMERSION_SOUND:
            case ITEM_TYPE_AUDIO_COLUMN:
                ColumnCard columnCard = new ColumnCard(mContext, null);
                columnCard.setType(viewType);
                view = columnCard;
                break;
            case ITEM_TYPE_SELECTION_OF_GOLDEN_SONGS:
            case ITEM_TYPE_AUDIO_STATION:
                ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                        R.layout.layout_view_sound_and_golden, parent, false);
                view = binding.getRoot();
                break;
        }
        return new MainRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainRecyclerViewHolder holder, int position) {
        if (mData == null || position >= mData.size()) {
            return;
        }
        ColumnBean bean = mData.get(position);
        if (bean == null) return;
        switch (bean.getType()) {
            case ITEM_TYPE_BLACK_PLASTIC:
                requestBlackGlueData(holder, position);
                break;
            case ITEM_TYPE_3D_IMMERSION_SOUND:
                request3dImmersionData(holder, position);
                break;
            case ITEM_TYPE_AUDIO_COLUMN:
                requestAudioColumn(holder, position);
                break;
            case ITEM_TYPE_SELECTION_OF_GOLDEN_SONGS:
            case ITEM_TYPE_AUDIO_STATION:
                holder.bindTYpeTwoCard(bean, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    class MainRecyclerViewHolder extends RecyclerView.ViewHolder {

        public MainRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindColumnCard(@NonNull List<AlbumBean> list, int position, String title) {
            if (itemView instanceof ColumnCard) {
                ColumnCard columnCard = (ColumnCard) itemView;
                columnCard.setData(list, title);
            }
        }

        @SuppressLint("CheckResult")
        public void bindTYpeTwoCard(@NonNull ColumnBean bean, int position) {
            ViewDataBinding binding = DataBindingUtil.getBinding(itemView);
            if (binding instanceof LayoutViewSoundAndGoldenBinding) {
                LayoutViewSoundAndGoldenBinding dataBinding = (LayoutViewSoundAndGoldenBinding) binding;

                if (SPUtils.isPad()) {
                    ViewGroup.LayoutParams layoutParams = dataBinding.goldenSongs.getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams.width = DensityUtils.dp2Px(mContext, 500);
                        dataBinding.goldenSongs.setLayoutParams(layoutParams);
                    }
                }


                // 甄选金曲
                goldenSongsBinding = dataBinding.goldenSongs;
                dataBinding.goldenSongs.setOnClickListener(v -> {
                    LiveDataBus.get().with(LiveDataBusConstants.to_fragment).postValue(new GoldenSongDetailFragment());
                });

                requestGoldenSongData();


                // 有声电台
                audioStationBinding = dataBinding.audioStation;

                requestAudioStationData();
            }
        }


    }

    private void requestGoldenSongData() {
        LayoutViewHifiCardBinding hifiCardBinding = goldenSongsBinding.getDataBinding();
        ApiClient.getInstance().getGoldenSongs().subscribe(new BaseObserver<>() {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onSuccess(BaseBean<ModuleBean> responseData) {
                LogUtils.d("data2223 " + responseData);
                ModuleBean data = responseData.getData();
                if (data == null) {
                    LogUtils.d("data is null...");
                    return;
                }

                ModuleInfoBean moduleInfo = data.getModuleInfo();
                if (moduleInfo == null) {
                    LogUtils.d("module info is null...");
                    return;
                }

                hifiCardBinding.tvTopContent.setText(moduleInfo.getHomeDesc());

                Glide.with(mContext)
                        .setDefaultRequestOptions(ImageLoader.getOptions())
                        .load(moduleInfo.getHomeImgUrl())
                        .centerInside()
                        .into(hifiCardBinding.imgTitleBg);


                List<AlbumBean> albumBeanList = data.getModuleData();
                if (albumBeanList == null) {
                    LogUtils.d("albumBeanList is null...");
                    return;
                } else {
                    if (albumBeanList.size() <= 0) {
                        LogUtils.d("albumBeanList is empty...");
                        return;
                    } else {
                        String id = String.valueOf(albumBeanList.get(0).getId());
                        LiveDataBus.get().with(LiveDataBusConstants.golden_detail_id).postValue(id);
                        List<MusicListBean> musicBeanList = albumBeanList.get(0).getLibraryStoreList();

                        // 点击播放按钮 添加到播放列表
                        hifiCardBinding.imgPlay.setOnClickListener(v -> {
                            if (MusicPlayManager.getInstance().isPlaying()
                                    && id.equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                            ) {
                                MusicPlayManager.getInstance().pauseMusic();
                            } else {
                                MusicPlayManager.getInstance().addToPlaylistAndPlay(id, musicBeanList);
                                MusicPlayManager.getInstance().playMusic();
                            }
                        });

                        LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class)
                                .observe((LifecycleOwner) mContext, musicPlayerBean ->
                                        ImageLoader.load(mContext,
                                                MusicPlayManager.getInstance().isPlaying()
                                                        && id.equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                                                        ? R.drawable.img_pause_with_circle
                                                        : R.drawable.img_play, hifiCardBinding.imgPlay));
                    }
                }
            }

            @Override
            protected void onFail(Throwable e) {
                LogUtils.e("data222" + e.toString());
            }
        });

        // ImageLoader.load(mContext, "", hifiCardBinding.imgTitleBg);
    }

    private void requestAudioStationData() {
        ApiClient.getInstance().getAudioStation().subscribe(new BaseObserver<>() {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onSuccess(BaseBean<ModuleBean> responseData) {
                LogUtils.d("data222 " + responseData.toString());
                if (responseData == null) {
                    LogUtils.e("responseData null...");
                    return;
                }
                ModuleBean data = responseData.getData();
                if (data == null) {
                    LogUtils.d("data is null...");
                    return;
                }

//                LogUtils.d("data : " + data.getAudioStationList());

                List<AlbumBean> albumBeanList = data.getModuleData();
                if (albumBeanList == null) {
                    LogUtils.d("albumBeanList is null...");
                    return;
                } else {
                    if (albumBeanList.size() <= 0) {
                        LogUtils.d("albumBeanList is empty...");
                        return;
                    } else {
                        int size = albumBeanList.size();
                        ArrayList<AlbumBean> list = new ArrayList<>();

                        AlbumBean moduleData1 = size >= 1 ? albumBeanList.get(0) : null;
                        AlbumBean moduleData2 = size >= 2 ? albumBeanList.get(1) : null;
                        AlbumBean moduleData3 = size >= 3 ? albumBeanList.get(2) : null;
                        if (moduleData1 != null) list.add(moduleData1);
                        if (moduleData2 != null) list.add(moduleData2);
                        if (moduleData3 != null) list.add(moduleData3);

                        // 前台不用排序了 后端进行排序
//                        if (albumBeanList != null && albumBeanList.size() > 0) {
//                            AlbumBean bean = albumBeanList.get(0);
//                            List<MusicListBean> libraryStoreList = bean.getLibraryStoreList();
//                            libraryStoreList.sort((o1, o2) -> {
//                                Integer index1 = Integer.valueOf(o1.getRuleId());
//                                Integer index2 = Integer.valueOf(o2.getRuleId());
//
//                                int index_1 = index1 != null ? index1 : 0;
//                                int index_2 = index1 != null ? index2 : 0;
//                                return index_1 - index_2;
//                            });
//                            bean.setLibraryStoreList(libraryStoreList);
//                            albumBeanList.set(0, bean);
//                        }

                        audioStationBinding.setData(albumBeanList);
                    }
                }
            }

            @Override
            protected void onFail(Throwable e) {
                LogUtils.e(e.toString());
            }
        });

//        audioStationBinding.setData(new ArrayList<>() {{
//            add(new AlbumBean());
//        }});
    }

    private void requestBlackGlueData(MainRecyclerViewHolder holder, int position) {
        ApiClient.getInstance().getBlackGlue().subscribe(new BaseObserver<>() {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onSuccess(BaseBean<ModuleBean> responseData) {
                LogUtils.d("data222 " + responseData.toString());
                if (responseData == null) {
                    LogUtils.e("responseData null...");
                    return;
                }
                ModuleBean data = responseData.getData();
                if (data == null) {
                    LogUtils.d("data is null...");
                    return;
                }

                if (!data.isShowFlag()) {
                    LogUtils.d("hide module : " + position);
                    holder.itemView.setVisibility(View.GONE);
                    return;
                }

                holder.bindColumnCard(data.getModuleData(), position, mContext.getString(R.string.black_glue));
            }

            @Override
            protected void onFail(Throwable e) {
                LogUtils.e(e.toString());
            }
        });
        holder.bindColumnCard(new ArrayList<>() {{
            add(new AlbumBean());
        }}, position, mContext.getString(R.string.black_glue));
    }

    private void requestAudioColumn(MainRecyclerViewHolder holder, int position) {
        ApiClient.getInstance().getAudioColumn().subscribe(new BaseObserver<>() {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onSuccess(BaseBean<ModuleBean> responseData) {
                LogUtils.d("data222 " + responseData.toString());
                if (responseData == null) {
                    LogUtils.e("responseData null...");
                    return;
                }
                ModuleBean data = responseData.getData();
                if (data == null) {
                    LogUtils.d("data is null...");
                    return;
                }

                if (!data.isShowFlag()) {
                    LogUtils.d("hide module : " + position);
                    holder.itemView.setVisibility(View.GONE);
                    return;
                }
                holder.bindColumnCard(data.getModuleData(), position, mContext.getString(R.string.audio_column));
            }

            @Override
            protected void onFail(Throwable e) {
                LogUtils.e(e.toString());
            }
        });
        holder.bindColumnCard(new ArrayList<>() {{
            add(new AlbumBean());
        }}, position, mContext.getString(R.string.audio_column));
    }

    private void request3dImmersionData(MainRecyclerViewHolder holder, int position) {
        ApiClient.getInstance().getImmersion().subscribe(new BaseObserver<>() {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onSuccess(BaseBean<ModuleBean> responseData) {
                LogUtils.d("data222 " + responseData.toString());
                if (responseData == null) {
                    LogUtils.e("responseData null...");
                    return;
                }
                ModuleBean data = responseData.getData();
                if (data == null) {
                    LogUtils.d("data is null...");
                    return;
                }

                if (!data.isShowFlag()) {
                    LogUtils.d("hide module : " + position);
                    holder.itemView.setVisibility(View.GONE);
                    return;
                }
                holder.bindColumnCard(data.getModuleData(), position, mContext.getString(R.string.immersion_special));
            }

            @Override
            protected void onFail(Throwable e) {
                LogUtils.e(e.toString());
            }
        });
        holder.bindColumnCard(new ArrayList<>() {{
            add(new AlbumBean());
        }}, position, mContext.getString(R.string.immersion_special));
    }
}
