package com.byd.dynaudio_app.bean;

import com.byd.dynaudio_app.manager.MusicPlayManager;

import java.util.Objects;

public class PlayItemBean{

    private int id;
    private String title;
    private String subTitle;
    private String recommend;
    boolean isCollect;

    private String imgShow;

    int progress;

    private String videoPath;
    private String musicPath;
    private String LrcPath;


    private float speed;

    private String singer;

    private String size;

    private String duration;

    public PlayItemBean() {
        speed = 1.f;
    }

    public PlayItemBean(int id, String title, String subTitle, String recommend, boolean isCollect, String imgShow, int progress, String videoPath, String musicPath, String lrcPath) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.recommend = recommend;
        this.isCollect = isCollect;
        this.imgShow = imgShow;
        this.progress = progress;
        this.videoPath = videoPath;
        this.musicPath = musicPath;
        LrcPath = lrcPath;
    }

    @Override
    public String toString() {
        return "PlayItemBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", recommend='" + recommend + '\'' +
                ", isCollect=" + isCollect +
                ", imgShow='" + imgShow + '\'' +
                ", progress=" + progress +
                ", videoPath='" + videoPath + '\'' +
                ", musicPath='" + musicPath + '\'' +
                ", LrcPath='" + LrcPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayItemBean that = (PlayItemBean) o;
        return id == that.id && isCollect == that.isCollect && progress == that.progress && Objects.equals(title, that.title) && Objects.equals(subTitle, that.subTitle) && Objects.equals(recommend, that.recommend) && Objects.equals(imgShow, that.imgShow) && Objects.equals(videoPath, that.videoPath) && Objects.equals(musicPath, that.musicPath) && Objects.equals(LrcPath, that.LrcPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, subTitle, recommend, isCollect, imgShow, progress, videoPath, musicPath, LrcPath);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public String getImgShow() {
        return imgShow;
    }

    public void setImgShow(String imgShow) {
        this.imgShow = imgShow;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getLrcPath() {
        return LrcPath;
    }

    public void setLrcPath(String lrcPath) {
        LrcPath = lrcPath;
    }

//    public MusicPlayManager.ItemType getType() {
//        return type;
//    }
//
//    public void setType(MusicPlayManager.ItemType type) {
//        this.type = type;
//    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
