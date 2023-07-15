package com.byd.dynaudio_app.bean;

import com.stx.xhb.androidx.entity.BaseBannerInfo;

/**
 * 每一个专区的bean 例如：（有声电台+甄选金曲），黑胶专区，有声专栏，3d沉浸声
 */
public class ItemBean implements BaseBannerInfo {

    // 左上角的文字 如:全景声、独家上线、丹拿定制等
    private String label;

    // 播放按钮的样式：音乐或视频
    private boolean isMusic;

    // 标题
    private String title;
    // 副标题
    private String subTitle;
    // 内容
    private String content;
    private String subContent;

    private String showImg;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public void setMusic(boolean music) {
        isMusic = music;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubContent() {
        return subContent;
    }

    public void setSubContent(String subContent) {
        this.subContent = subContent;
    }

    public String getShowImg() {
        return showImg;
    }

    public void setShowImg(String showImg) {
        this.showImg = showImg;
    }


    @Override
    public Object getXBannerUrl() {
        return showImg;
    }

    @Override
    public String getXBannerTitle() {
        return title;
    }
}
