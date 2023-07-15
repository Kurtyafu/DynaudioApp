package com.byd.dynaudio_app.bean.response;

import com.byd.dynaudio_app.bean.MusicListBean;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModuleBean {

    private ModuleInfoBean moduleInfo;
    private List<AlbumBean> moduleData;
    private boolean showFlag;

    public ModuleInfoBean getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(ModuleInfoBean moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    public List<AlbumBean> getModuleData() {
        return moduleData;
    }

    public void setModuleData(List<AlbumBean> moduleData) {
        this.moduleData = moduleData;
    }

    public boolean isShowFlag() {
        return showFlag;
    }

    public void setShowFlag(boolean showFlag) {
        this.showFlag = showFlag;
    }

    @SerializedName("libraryStoreList")
    // 有声电台特供
    private List<MusicListBean> audioStationList;


    public List<MusicListBean> getAudioStationList() {
        return audioStationList;
    }

    public void setAudioStationList(List<MusicListBean> audioStationList) {
        this.audioStationList = audioStationList;
    }
}
