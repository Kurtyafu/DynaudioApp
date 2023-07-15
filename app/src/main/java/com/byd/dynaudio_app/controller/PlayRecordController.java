package com.byd.dynaudio_app.controller;

import android.content.Context;

import com.byd.dynaudio_app.DynaudioApplication;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.request.SaveOrDeleteBody;
import com.byd.dynaudio_app.database.DBController;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.user.UserController;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放记录控制器
 */
public class PlayRecordController {

    private static class LazierHolder {
        static PlayRecordController instance = new PlayRecordController();
    }

    public static PlayRecordController getInstance() {
        return LazierHolder.instance;
    }

    public void record(MusicListBean bean) {
        // 本地和后台都存
        saveDatabase(bean);
        if (UserController.getInstance().isLoginStates()) {
            requestSave(bean);
        }
    }

    public void deleteRecord(MusicListBean bean) {
        deleteDatabase(bean);
        if (UserController.getInstance().isLoginStates()) {
            requestDelete(bean);
        }
    }

    /**
     * 记录播放记录(已登录) 存储后台
     */
    private void requestSave(MusicListBean bean) {
        SaveOrDeleteBody body = new SaveOrDeleteBody();
        body.setUserId(UserController.getInstance().getUserId());
        body.setLibraryId(String.valueOf(bean.getLibraryId()));
        body.setLibraryType(bean.getLibraryType());
        List<SaveOrDeleteBody> bodyList = new ArrayList<>();
        bodyList.add(body);
        requestSave(bodyList);
    }

    /**
     * 记录播放记录(已登录)，登录后同步本地记录的请求，上传多条记录
     */
    public void requestSave(List<SaveOrDeleteBody> bodyList) {
        ApiClient.getInstance().saveRecord(bodyList).subscribe();
    }

    /**
     * 删除收藏记录(已登录) 存储后台
     */
    private void requestDelete(MusicListBean bean) {
        SaveOrDeleteBody body = new SaveOrDeleteBody();
        body.setUserId(UserController.getInstance().getUserId());
        body.setLibraryId(String.valueOf(bean.getLibraryId()));
        body.setLibraryType(bean.getLibraryType());
        List<SaveOrDeleteBody> bodyList = new ArrayList<>();
        bodyList.add(body);
        requestDelete(bodyList);
    }

    public void requestDelete(List<SaveOrDeleteBody> bodyList) {
        ApiClient.getInstance().deleteRecord(bodyList).subscribe();
    }

    /**
     * 记录播放记录(未登录)，存储sqlite
     */
    private void saveDatabase(MusicListBean bean) {
        // 库类别 1音乐库 2台宣库 3播客库 4资讯库
        String type = bean.getLibraryType();
        if (type.equals("1")) {
            DBController.insertMusicRecord(getContext(), bean);
        } else {
            DBController.insertAudioRecord(getContext(), bean);
        }
    }

    /**
     * 删除播放记录(未登录)，删除sqlite
     */

    public void deleteDatabase(MusicListBean bean) {
        // 库类别 1音乐库 2台宣库 3播客库 4资讯库
        String type = bean.getLibraryType();
        if (type.equals("1")) {
            DBController.deleteMusicRecord(getContext(), bean);
        } else {
            DBController.deleteAudioRecord(getContext(), bean);
        }
    }

    private Context getContext() {
        return DynaudioApplication.getContext();
    }
}
