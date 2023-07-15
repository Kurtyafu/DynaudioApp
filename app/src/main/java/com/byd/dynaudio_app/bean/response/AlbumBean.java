package com.byd.dynaudio_app.bean.response;

import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_3D_IMMERSION_SOUND;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_AUDIO_COLUMN;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_BLACK_PLASTIC;

import android.text.TextUtils;

import com.byd.dynaudio_app.bean.MusicListBean;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.List;

public class AlbumBean implements MultiItemEntity, Serializable {
    private int id;
    @SerializedName(value = "albumName", alternate = {"name"})
    private String albumName;
    private String albumDesc;
    private String albumImgUrl;

    private String albumDescImgUrl;

    // 简介
    private String albumSimpleDesc;
    @SerializedName(value = "auther", alternate = {"singer"})
    private String auther;
    private List<SingerBean> singerList;
    private String limitCar;
    @SerializedName(value = "recommendLabel", alternate = {"quality"})
    private String recommendLabel;
    private int sort;
    private List<MusicListBean> libraryStoreList;

    private String typeName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumDesc() {
        return albumDesc;
    }

    public void setAlbumDesc(String albumDesc) {
        this.albumDesc = albumDesc;
    }

    public String getAlbumImgUrl() {
        return albumImgUrl;
    }

    public void setAlbumImgUrl(String albumImgUrl) {
        this.albumImgUrl = albumImgUrl;
    }

    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public List<SingerBean> getSingerList() {
        return singerList;
    }

    public void setSingerList(List<SingerBean> singerList) {
        this.singerList = singerList;
    }

    public String getLimitCar() {
        return limitCar;
    }

    public void setLimitCar(String limitCar) {
        this.limitCar = limitCar;
    }

    public String getRecommendLabel() {
        return recommendLabel;
    }

    public void setRecommendLabel(String recommendLabel) {
        this.recommendLabel = recommendLabel;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<MusicListBean> getLibraryStoreList() {
        return libraryStoreList;
    }

    public void setLibraryStoreList(List<MusicListBean> libraryStoreList) {
        this.libraryStoreList = libraryStoreList;
    }

    public String getAlbumSimpleDesc() {
        return albumSimpleDesc;
    }

    public void setAlbumSimpleDesc(String albumSimpleDesc) {
        this.albumSimpleDesc = albumSimpleDesc;
    }

    public String getAlbumDescImgUrl() {
        return albumDescImgUrl;
    }

    public void setAlbumDescImgUrl(String albumDescImgUrl) {
        this.albumDescImgUrl = albumDescImgUrl;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public int getItemType() {
        int resType = 0;
        if (TextUtils.equals(typeName, "黑胶")) {
            resType = ITEM_TYPE_BLACK_PLASTIC;
        } else if (TextUtils.equals(typeName, "有声")) {
            resType = ITEM_TYPE_AUDIO_COLUMN;
        } else if (TextUtils.equals(typeName, "沉浸音")) {
            resType = ITEM_TYPE_3D_IMMERSION_SOUND;
        }
        return resType;
    }
}
