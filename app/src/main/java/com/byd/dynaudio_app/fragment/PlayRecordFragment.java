package com.byd.dynaudio_app.fragment;

import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.adapter.AudioListAdapter;
import com.byd.dynaudio_app.adapter.IItemOnClickListener;
import com.byd.dynaudio_app.adapter.MusicListAdapter;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.request.SaveOrDeleteBody;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.controller.PlayRecordController;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.custom.TabLayout;
import com.byd.dynaudio_app.database.DBController;
import com.byd.dynaudio_app.databinding.LayoutFragmentPlayRecordBinding;
import com.byd.dynaudio_app.dialog.CustomDialog;
import com.byd.dynaudio_app.dialog.IOnClickListener;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 播放记录
 */
public class PlayRecordFragment extends BaseFragment<LayoutFragmentPlayRecordBinding, BaseViewModel> {

    private final ArrayList<MusicListBean> mMusicList = new ArrayList<>();
    private final ArrayList<MusicListBean> mAudioList = new ArrayList<>();
    private final ArrayList<SaveOrDeleteBody> mOperateBodies = new ArrayList<>();
    private MusicListAdapter mMusicAdapter;
    private AudioListAdapter mAudioAdapter;
    private int mDelNumber;

    @Override
    protected void initView() {
        TouchUtils.bindClickItem(
                mDataBinding.btnBack,
                mDataBinding.txtPlayAll,
                mDataBinding.viewMultiple,
                mDataBinding.txtCancel,
                mDataBinding.llAllSelect,
                mDataBinding.txtDeleteNum);

        initMusicList();
        initAudioProgramList();
        mDataBinding.tabLayout.selectFirstTab();
        selectDataSource();
    }

    @Override
    protected void initListener() {
        mDataBinding.btnBack.setOnClickListener(view -> {
            LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1);
        });
        mDataBinding.tabLayout.addOnTabSelectedListener(new TabLayout.IOnTabSelectedListener() {
            @Override
            public void onFirstSelected() {
                selectMusicTab();
            }

            @Override
            public void onSecondSelected() {
                selectAudioProgramTab();
            }
        });
        mDataBinding.txtPlayAll.setOnClickListener(view -> {/*顺序播放*/
            if (mDataBinding.tabLayout.firstItemSelected()) {
                MusicListBean bean = mMusicList.get(0);
                MusicPlayManager.getInstance().playMusic(mMusicList, bean, 0, "record");
            } else {
                //有声节目播放
                MusicListBean bean = mAudioList.get(0);
                MusicPlayManager.getInstance().playMusic(mAudioList, bean, 0, "record");
            }
        });
        mDataBinding.viewMultiple.setOnClickListener(view -> switchToEditMode());//编辑模式（删除）
        mDataBinding.txtCancel.setOnClickListener(view -> cancelEditMode());//取消编辑模式
        mDataBinding.llAllSelect.setOnClickListener(view -> selectAllItem());//全选
        mDataBinding.txtDeleteNum.setOnClickListener(view -> deletePlayRecord());//删除选中item
        initMusicListClickListener();
        initAudioListClickListener();
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
        return R.layout.layout_fragment_play_record;
    }

    private void initMusicList() {
        mDataBinding.tabLayout.setFirstText("音乐 " + mMusicList.size());
        mMusicAdapter = new MusicListAdapter(mContext, mMusicList);
        mMusicAdapter.setShowCollectBtn(true);
        mDataBinding.recyclerMusic.setLayoutManager(new LinearLayoutManager(mContext));
        mDataBinding.recyclerMusic.setAdapter(mMusicAdapter);
    }

    private void initAudioProgramList() {
        mDataBinding.tabLayout.setSecondText("有声节目 " + mAudioList.size());
        mAudioAdapter = new AudioListAdapter(mContext, mAudioList);
        mAudioAdapter.setShowCollectBtn(true);
        mDataBinding.recyclerAudioProgram.setLayoutManager(new LinearLayoutManager(mContext));
        mDataBinding.recyclerAudioProgram.setAdapter(mAudioAdapter);
    }

    /**
     * 登录查后台，未登录查本地
     */
    private void selectDataSource() {
        if (UserController.getInstance().isLoginStates()) {
            requestMusicData();
            requestAudioData();
        } else {
            List<MusicListBean> musicList = DBController.queryMusicRecord(mContext);
            List<MusicListBean> audioList = DBController.queryAudioRecord(mContext);
            overlayMusicData(musicList);
            overlayAudioData(audioList);
            mMusicList.addAll(musicList);
            mAudioList.addAll(audioList);
            mMusicAdapter.notifyDataSetChanged();
            mAudioAdapter.notifyDataSetChanged();
            mDataBinding.tabLayout.setFirstText("音乐 " + mMusicList.size());
            mDataBinding.tabLayout.setSecondText("有声节目 " + mAudioList.size());
            // 默认显示音乐列表，如果音乐列表没有数据显示有声数据
            if (mMusicList.size() > 0) {
                selectMusicTab();
                mDataBinding.tabLayout.selectFirstTab();
            } else if (mAudioList.size() > 0) {
                selectAudioProgramTab();
                mDataBinding.tabLayout.selectSecondTab();
            } else {
                selectMusicTab();
                mDataBinding.tabLayout.selectFirstTab();
            }
        }
    }

    /**
     * 查询返回的数据中是否有未登录用户的收藏歌曲 覆盖原有的collectFlag值
     */
    private void overlayMusicData(List<MusicListBean> list) {
        for (MusicListBean bean : list) {
            boolean collect = DBController.queryMusicCollect(mContext, bean);
            bean.setCollectFlag(collect);
        }
    }

    private void overlayAudioData(List<MusicListBean> list) {
        for (MusicListBean bean : list) {
            boolean collect = DBController.queryAudioCollect(mContext, bean);
            bean.setCollectFlag(collect);
        }
    }

    private void requestMusicData() {
        String userId = UserController.getInstance().getUserId();
        ApiClient.getInstance().getMusicRecord(userId).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<List<MusicListBean>> bean) {
                if (bean.getData() != null) {
                    List<MusicListBean> list = bean.getData();
                    Collections.reverse(list);
                    mMusicList.clear();
                    mMusicList.addAll(list);
                    mMusicAdapter.notifyDataSetChanged();
                }
                mDataBinding.tabLayout.setFirstText("音乐 " + mMusicList.size());
                if (mMusicList.size() > 0) {
                    selectMusicTab();
                    mDataBinding.tabLayout.selectFirstTab();
                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void requestAudioData() {
        String userId = UserController.getInstance().getUserId();
        ApiClient.getInstance().getAudioRecord(userId).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<List<MusicListBean>> bean) {
                if (bean.getData() != null) {
                    List<MusicListBean> list = bean.getData();
                    Collections.reverse(list);
                    mAudioList.clear();
                    mAudioList.addAll(list);
                    mAudioAdapter.notifyDataSetChanged();
                }
                mDataBinding.tabLayout.setSecondText("有声节目 " + mAudioList.size());
                if (mMusicList.size() == 0 && mAudioList.size() > 0) {
                    selectAudioProgramTab();
                    mDataBinding.tabLayout.selectSecondTab();
                } else {
                    selectMusicTab();
                    mDataBinding.tabLayout.selectFirstTab();
                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void selectMusicTab() {
        mDataBinding.recyclerAudioProgram.setVisibility(View.GONE);
        if (mMusicList.size() > 0) {
            mDataBinding.scrollView.setVisibility(View.VISIBLE);
            mDataBinding.recyclerMusic.setVisibility(View.VISIBLE);
            mDataBinding.txtPlayAll.setVisibility(View.VISIBLE);
            mDataBinding.llPlaceholder.setVisibility(View.GONE);
        } else {
            mDataBinding.scrollView.setVisibility(View.GONE);
            mDataBinding.llPlaceholder.setVisibility(View.VISIBLE);
            mDataBinding.txtPlayAll.setVisibility(View.GONE);
        }
    }

    private void selectAudioProgramTab() {
        mDataBinding.recyclerMusic.setVisibility(View.GONE);
        if (mAudioList.size() > 0) {
            mDataBinding.scrollView.setVisibility(View.VISIBLE);
            mDataBinding.recyclerAudioProgram.setVisibility(View.VISIBLE);
            mDataBinding.txtPlayAll.setVisibility(View.VISIBLE);
            mDataBinding.llPlaceholder.setVisibility(View.GONE);
        } else {
            mDataBinding.scrollView.setVisibility(View.GONE);
            mDataBinding.llPlaceholder.setVisibility(View.VISIBLE);
            mDataBinding.txtPlayAll.setVisibility(View.GONE);
        }
    }

    private void initMusicListClickListener() {
        mMusicAdapter.setClickListener(new IItemOnClickListener() {
            @Override
            public void onClick(int position) {
                //音乐播放
                MusicListBean bean = mMusicList.get(position);
                MusicPlayManager.getInstance().playMusic(mMusicList, bean, position, "record");
            }

            @Override
            public void onSelect(int position, boolean isSelect) {
                mDelNumber = 0;
                for (int i = 0; i < mMusicList.size(); i++) {
                    if (mMusicList.get(i).isSelected()) {
                        mDelNumber++;
                    }
                }
                if (!isSelect) {//如果有反选全选按钮重置
                    mDataBinding.viewAllSelect.setSelected(false);
                }
                if (mDelNumber == mMusicList.size()) {//全选
                    mDataBinding.viewAllSelect.setSelected(true);
                }
                mDataBinding.txtDeleteNum.setText("删除(共" + mDelNumber + "条)");
                mDataBinding.txtDeleteNum.setSelected(mDelNumber != 0);
            }

            @Override
            public void onDeleteClick(int position) {
                mDataBinding.recyclerMusic.closeMenu();
                deleteSingleMusicItem(position);
            }
        });
    }

    private void initAudioListClickListener() {
        mAudioAdapter.setClickListener(new IItemOnClickListener() {
            @Override
            public void onClick(int position) {
                //有声节目播放
                MusicListBean bean = mAudioList.get(position);
                MusicPlayManager.getInstance().playMusic(mAudioList, bean, position, "record");
            }

            @Override
            public void onSelect(int position, boolean isSelect) {
                mDelNumber = 0;
                for (int i = 0; i < mAudioList.size(); i++) {
                    if (mAudioList.get(i).isSelected()) {
                        mDelNumber++;
                    }
                }
                if (!isSelect) {//如果有反选全选按钮重置
                    mDataBinding.viewAllSelect.setSelected(false);
                }
                if (mDelNumber == mAudioList.size()) {//全选
                    mDataBinding.viewAllSelect.setSelected(true);
                }
                mDataBinding.txtDeleteNum.setText("删除(共" + mDelNumber + "条)");
                mDataBinding.txtDeleteNum.setSelected(mDelNumber != 0);
            }

            @Override
            public void onDeleteClick(int position) {
                mDataBinding.recyclerAudioProgram.closeMenu();
                deleteSingleAudioItem(position);
            }
        });
    }

    private void switchToEditMode() {
        if (mDataBinding.tabLayout.firstItemSelected()) {
            if (mMusicList.size() > 0) {
                for (MusicListBean bean : mMusicList) {
                    bean.setEditMode(true);
                    bean.setSelected(false);
                }
                mMusicAdapter.notifyDataSetChanged();
                mDataBinding.recyclerMusic.setEnable(false);
            } else return;
        } else {
            if (mAudioList.size() > 0) {
                for (MusicListBean bean : mAudioList) {
                    bean.setEditMode(true);
                    bean.setSelected(false);
                }
                mAudioAdapter.notifyDataSetChanged();
                mDataBinding.recyclerAudioProgram.setEnable(false);
            } else return;
        }
        mDataBinding.btnBack.setVisibility(View.GONE);
        mDataBinding.viewMultiple.setVisibility(View.GONE);
        mDataBinding.txtCancel.setVisibility(View.VISIBLE);
        mDataBinding.txtPlayAll.setVisibility(View.GONE);
        mDataBinding.llAllSelect.setVisibility(View.VISIBLE);
        mDataBinding.txtDeleteNum.setVisibility(View.VISIBLE);
        mDataBinding.viewAllSelect.setSelected(false);
        mDataBinding.tabLayout.setFirstClickable(false);
        mDataBinding.tabLayout.setSecondClickable(false);
        mDelNumber = 0;
        mDataBinding.txtDeleteNum.setText("删除(共" + mDelNumber + "条)");
        mDataBinding.txtDeleteNum.setSelected(false);

        PlayerVisionManager.getInstance().hideMiniPlayer();
    }

    public void cancelEditMode() {
        mDataBinding.btnBack.setVisibility(View.VISIBLE);
        mDataBinding.viewMultiple.setVisibility(View.VISIBLE);
        mDataBinding.txtCancel.setVisibility(View.GONE);
        mDataBinding.txtPlayAll.setVisibility(View.VISIBLE);
        mDataBinding.llAllSelect.setVisibility(View.GONE);
        mDataBinding.txtDeleteNum.setVisibility(View.GONE);
        mDataBinding.tabLayout.setFirstClickable(true);
        mDataBinding.tabLayout.setSecondClickable(true);
        if (mDataBinding.tabLayout.firstItemSelected()) {
            for (MusicListBean bean : mMusicList) {
                bean.setEditMode(false);
                bean.setSelected(false);
            }
            mMusicAdapter.notifyDataSetChanged();
            mDataBinding.recyclerMusic.setEnable(true);
        } else {
            for (MusicListBean bean : mAudioList) {
                bean.setEditMode(false);
                bean.setSelected(false);
            }
            mAudioAdapter.notifyDataSetChanged();
            mDataBinding.recyclerAudioProgram.setEnable(true);
        }

        PlayerVisionManager.getInstance().showMiniPlayer();
    }

    private void selectAllItem() {
        if (mDataBinding.tabLayout.firstItemSelected()) {
            if (mDataBinding.viewAllSelect.isSelected()) {
                mDataBinding.viewAllSelect.setSelected(false);
                for (MusicListBean bean : mMusicList) {
                    bean.setSelected(false);
                }
                mDelNumber = 0;
                mDataBinding.txtDeleteNum.setText("删除(共0条)");
                mDataBinding.txtDeleteNum.setSelected(false);
            } else {
                mDataBinding.viewAllSelect.setSelected(true);
                for (MusicListBean bean : mMusicList) {
                    bean.setSelected(true);
                }
                mDelNumber = mMusicList.size();
                mDataBinding.txtDeleteNum.setText("删除(共" + mDelNumber + "条)");
                mDataBinding.txtDeleteNum.setSelected(true);
            }
            mMusicAdapter.notifyDataSetChanged();
        } else {
            if (mDataBinding.viewAllSelect.isSelected()) {
                mDataBinding.viewAllSelect.setSelected(false);
                for (MusicListBean bean : mAudioList) {
                    bean.setSelected(false);
                }
                mDelNumber = 0;
                mDataBinding.txtDeleteNum.setText("删除(共0条)");
                mDataBinding.txtDeleteNum.setSelected(false);
            } else {
                mDataBinding.viewAllSelect.setSelected(true);
                for (MusicListBean bean : mAudioList) {
                    bean.setSelected(true);
                }
                mDelNumber = mAudioList.size();
                mDataBinding.txtDeleteNum.setText("删除(共" + mDelNumber + "条)");
                mDataBinding.txtDeleteNum.setSelected(true);
            }
            mAudioAdapter.notifyDataSetChanged();
        }
    }

    private void deletePlayRecord() {
        if (mDelNumber == 0) return;
        CustomDialog dialog = new CustomDialog(mContext);
        dialog.setTitle("删除播放记录");
        dialog.setPositiveText("删除");
        dialog.setContent("确认要删除选中的" + mDelNumber + "条内容吗?");
        dialog.setClickListener(new IOnClickListener() {
            @Override
            public void onPositiveButtonListener() {
                mOperateBodies.clear();
                // 登录删后台
                if (UserController.getInstance().isLoginStates()) {
                    if (mDataBinding.tabLayout.firstItemSelected()) {
                        for (MusicListBean bean : mMusicList) {
                            if (bean.isSelected()) {
                                SaveOrDeleteBody body = new SaveOrDeleteBody();
                                body.setUserId(UserController.getInstance().getUserId());
                                body.setLibraryId(String.valueOf(bean.getLibraryId()));
                                body.setLibraryType(bean.getLibraryType());
                                mOperateBodies.add(body);
                            }
                        }
                    } else {
                        for (MusicListBean bean : mAudioList) {
                            if (bean.isSelected()) {
                                SaveOrDeleteBody body = new SaveOrDeleteBody();
                                body.setUserId(UserController.getInstance().getUserId());
                                body.setLibraryId(String.valueOf(bean.getLibraryId()));
                                body.setLibraryType(bean.getLibraryType());
                                mOperateBodies.add(body);
                            }
                        }
                    }
                    PlayRecordController.getInstance().requestDelete(mOperateBodies);
                }
                if (mDataBinding.tabLayout.firstItemSelected()) {
                    for (MusicListBean bean : mMusicList) {
                        if (bean.isSelected()) {
                            PlayRecordController.getInstance().deleteDatabase(bean);
                        }
                    }
                    deleteSelectedMusicItem();
                } else {
                    for (MusicListBean bean : mAudioList) {
                        if (bean.isSelected()) {
                            PlayRecordController.getInstance().deleteDatabase(bean);
                        }
                    }
                    deleteSelectedAudioItem();
                }
                deleteComplete();
                CustomToast.makeText(mContext, "成功删除" + mDelNumber + "条播放记录", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void deleteSingleMusicItem(int position) {
        mMusicList.get(position).setSelected(true);
        PlayRecordController.getInstance().deleteRecord(mMusicList.get(position));
        deleteSelectedMusicItem();
        deleteComplete();
    }

    private void deleteSingleAudioItem(int position) {
        mAudioList.get(position).setSelected(true);
        PlayRecordController.getInstance().deleteRecord(mAudioList.get(position));
        deleteSelectedAudioItem();
        deleteComplete();
    }

    private void deleteSelectedMusicItem() {
        mMusicList.removeIf(MusicListBean::isSelected);
    }

    private void deleteSelectedAudioItem() {
        mAudioList.removeIf(MusicListBean::isSelected);
    }

    private void deleteComplete() {
        mDataBinding.tabLayout.setFirstClickable(true);
        mDataBinding.tabLayout.setSecondClickable(true);
        mDataBinding.btnBack.setVisibility(View.VISIBLE);
        mDataBinding.viewMultiple.setVisibility(View.VISIBLE);
        mDataBinding.llAllSelect.setVisibility(View.GONE);
        mDataBinding.txtCancel.setVisibility(View.GONE);
        mDataBinding.txtDeleteNum.setVisibility(View.GONE);
        if (mDataBinding.tabLayout.firstItemSelected()) {
            restoreMusicListInitialState();
        } else {
            restoreAudioListInitialState();
        }
    }

    private void restoreMusicListInitialState() {
        if (mMusicList.size() > 0) {
            mDataBinding.scrollView.setVisibility(View.VISIBLE);
            mDataBinding.txtPlayAll.setVisibility(View.VISIBLE);
            for (MusicListBean bean : mMusicList) {
                bean.setEditMode(false);
            }
            mMusicAdapter.notifyDataSetChanged();
        } else {
            mDataBinding.scrollView.setVisibility(View.GONE);
            mDataBinding.recyclerMusic.setVisibility(View.GONE);
            mDataBinding.txtPlayAll.setVisibility(View.GONE);
            mDataBinding.llPlaceholder.setVisibility(View.VISIBLE);
        }
        mDataBinding.tabLayout.setFirstText("音乐 " + mMusicList.size());
    }

    private void restoreAudioListInitialState() {
        if (mAudioList.size() > 0) {
            mDataBinding.scrollView.setVisibility(View.VISIBLE);
            mDataBinding.txtPlayAll.setVisibility(View.VISIBLE);
            for (MusicListBean bean : mAudioList) {
                bean.setEditMode(false);
            }
            mAudioAdapter.notifyDataSetChanged();
        } else {
            mDataBinding.scrollView.setVisibility(View.GONE);
            mDataBinding.recyclerAudioProgram.setVisibility(View.GONE);
            mDataBinding.txtPlayAll.setVisibility(View.GONE);
            mDataBinding.llPlaceholder.setVisibility(View.VISIBLE);
        }
        mDataBinding.tabLayout.setSecondText("有声节目 " + mAudioList.size());
    }

    public boolean isEditMode() {
        return mDataBinding.viewMultiple.getVisibility() == View.GONE;
    }
}