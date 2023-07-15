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
 * 收藏记录控制器
 */
public class CollectController {

    private static class LazierHolder {
        static CollectController instance = new CollectController();
    }

    public static CollectController getInstance() {
        return LazierHolder.instance;
    }

    public void collect(boolean isCollect, MusicListBean bean) {
        if (UserController.getInstance().isLoginStates()) {
            if (isCollect) {
                requestSave(bean);
            } else {
                requestDelete(bean);
            }
        } else {
            if (isCollect) {
                saveDatabase(bean);
            } else {
                deleteDatabase(bean);
            }
        }
    }

    /**
     * 记录收藏记录(已登录) 存储后台
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
     * 记录收藏记录(已登录)，登录后同步本地记录的请求，上传多条记录
     */
    public void requestSave(List<SaveOrDeleteBody> bodyList) {
        ApiClient.getInstance().saveCollect(bodyList).subscribe();
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

    private void requestDelete(List<SaveOrDeleteBody> bodyList) {
        ApiClient.getInstance().deleteCollect(bodyList).subscribe();
    }

    /**
     * 记录收藏记录(未登录)，存储sqlite
     */
    public void saveDatabase(MusicListBean bean) {
        // 库类别 1音乐库 2台宣库 3播客库 4资讯库
        String type = bean.getLibraryType();
        if (type.equals("1")) {
            DBController.insertMusicCollect(getContext(), bean);
        } else {
            DBController.insertAudioCollect(getContext(), bean);
        }
    }

    /**
     * 删除收藏记录(未登录)，删除sqlite
     */

    public void deleteDatabase(MusicListBean bean) {
        // 库类别 1音乐库 2台宣库 3播客库 4资讯库
        String type = bean.getLibraryType();
        if (type.equals("1")) {
            DBController.deleteMusicCollect(getContext(), bean);
        } else {
            DBController.deleteAudioCollect(getContext(), bean);
        }
    }

    private Context getContext() {
        return DynaudioApplication.getContext();
    }
}
