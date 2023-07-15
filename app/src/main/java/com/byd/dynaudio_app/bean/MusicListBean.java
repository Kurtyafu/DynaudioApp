package com.byd.dynaudio_app.bean;


import android.text.TextUtils;

import com.byd.dynaudio_app.bean.response.SingerBean;
import com.byd.dynaudio_app.utils.TestUtils;
import com.stx.xhb.androidx.entity.BaseBannerInfo;

import java.util.List;
import java.util.Objects;

public class MusicListBean implements BaseBannerInfo {

    /**
     * 属性标签
     */
    private String attrLabel;
    /**
     * 音频地址
     */
    private String audioUrl;
    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 时长
     */
    private Long duration;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 碎片库id
     */
    private int libraryId;
    /**
     * 库类别 1音乐库 2台宣库 3播客库 4资讯库
     */
    private String libraryType;

    // 每一个对象的唯一id 如果一致 则认为是同一个对象
    private int specialId;

    /**
     * 碎片（曲目）名称
     */
    private String name;
    /**
     * 表演者
     */
    private String singer;
    /**
     * 表演者 list
     */
    private List<SingerBean> singerList;
    /**
     * 大小
     */
    private Long size;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 视频地址
     */
    private String videoUrl;
    /**
     * 歌词地址
     */
    private String wordUrl;
    /**
     * 音质
     */
    private String quality;
    /**
     * 音质
     */
    private String musicQuality;
    /**
     * 音质图片
     */
    private String qualityUrl;
    /**
     * 是否为收藏状态
     */
    private boolean collectFlag;

    /**
     * 图片地址
     */
    private String imageUrl;

    private String updateTime;

    /**
     * 前方、后方 台宣
     */
    private String beforeAudioUrl, afterAudioUrl;

    private boolean editMode;//编辑模式批量删除
    private boolean selected;//是否选中
    private boolean showCollect = true;//是否显示收藏按钮
    private boolean playState;//播放状态
    private String batch;//期数

    private String recommendCause;

    private String ruleId;

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getAttrLabel() {
        return attrLabel;
    }

    public void setAttrLabel(String attrLabel) {
        this.attrLabel = attrLabel;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public Long getDuration() {
        return duration != null ? duration : 0L;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(int libraryId) {
        this.libraryId = libraryId;
    }

    public String getLibraryType() {
        return libraryType;
    }

    public void setLibraryType(String libraryType) {
        this.libraryType = libraryType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public List<SingerBean> getSingerList() {
        return singerList;
    }

    public void setSingerList(List<SingerBean> singerList) {
        this.singerList = singerList;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getWordUrl() {
        if ("a.lrc".equals(wordUrl)) { // 4.7 错误歌词文件
            wordUrl = null;
        }
        return wordUrl;
    }

    public void setWordUrl(String wordUrl) {
        this.wordUrl = wordUrl;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isShowCollect() {
        return showCollect;
    }

    public void setShowCollect(boolean showCollect) {
        this.showCollect = showCollect;
    }

    public boolean isPlayState() {
        return playState;
    }

    public void setPlayState(boolean playState) {
        this.playState = playState;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getMusicQuality() {
        return musicQuality;
    }

    public void setMusicQuality(String musicQuality) {
        this.musicQuality = musicQuality;
    }

    public String getQualityUrl() {
        return qualityUrl;
    }

    public void setQualityUrl(String qualityUrl) {
        this.qualityUrl = qualityUrl;
    }

    public boolean isCollectFlag() {
        return collectFlag;
    }

    public void setCollectFlag(boolean collectFlag) {
        this.collectFlag = collectFlag;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MusicListBean() {
        if (getLibraryType() == null) setLibraryType("1");
        setSpecialId(libraryType.hashCode() + libraryId);
    }

    public void setSpecialId(int specialId) {
        this.specialId = specialId;
    }

    /**
     * 获取它独特的id 由库+碎片id拼接 唯一
     */
    public int getSpecialId() {
        return libraryType.hashCode() * 1000 + libraryId;
    }

//    @Override
//    public String toString() {
//        return "MusicListBean{" + "attrLabel='" + attrLabel + '\'' + ", audioUrl='" + audioUrl + '\'' + ", beginTime='" + beginTime + '\'' + ", duration=" + duration + ", endTime='" + endTime + '\'' + ", libraryId=" + libraryId + ", libraryType='" + libraryType + '\'' + ", name='" + name + '\'' + ", singer='" + singer + '\'' + ", size=" + size + ", userId='" + userId + '\'' + ", videoUrl='" + videoUrl + '\'' + ", wordUrl='" + wordUrl + '\'' + ", quality='" + quality + '\'' + ", collectFlag=" + collectFlag + ", editMode=" + editMode + ", selected=" + selected + ", playState=" + playState + ", batch='" + batch + '\'' + '}';
//    }


    @Override
    public String toString() {
        return "MusicListBean{" +
                "libraryId=" + getSpecialId() +
                ", name='" + name + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    public String getRecommendCause() {
        return recommendCause;
    }

    public void setRecommendCause(String recommendCause) {
        this.recommendCause = recommendCause;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicListBean bean = (MusicListBean) o;
        return specialId == bean.getSpecialId() && TextUtils.equals(typeName, bean.getTypeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(attrLabel, audioUrl, beginTime, duration, endTime, libraryId, libraryType, specialId, name, singer, singerList, size, userId, videoUrl, wordUrl, quality, collectFlag, imageUrl, editMode, selected, playState, batch, recommendCause, ruleId);
    }

    private long currentTime;

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public Object getXBannerUrl() {
        return null;
    }

    @Override
    public String getXBannerTitle() {
        return null;
    }

    public String getBeforeAudioUrl() {
        return beforeAudioUrl;
    }

    public void setBeforeAudioUrl(String beforeAudioUrl) {
        this.beforeAudioUrl = beforeAudioUrl;
    }

    public String getAfterAudioUrl() {
        return afterAudioUrl;
    }

    public void setAfterAudioUrl(String afterAudioUrl) {
        this.afterAudioUrl = afterAudioUrl;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
