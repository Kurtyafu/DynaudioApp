package com.byd.dynaudio_app.bean.response;

import java.io.Serializable;

public class BannerBean implements Serializable {

    /**
     * title : 智能音乐座舱
     * assistantTitle : 智能音乐座舱副标题
     * imgUrl : https://img1.baidu.com/it/u=1037728192,3163097467&fm=253&fmt=auto&app=138&f=PNG?w=987&h=500
     * skipPath : null
     * sort : null
     */

    private String title;
    private String assistantTitle;
    private String imgUrl;
    private Object skipPath;
    private Object sort;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssistantTitle() {
        return assistantTitle;
    }

    public void setAssistantTitle(String assistantTitle) {
        this.assistantTitle = assistantTitle;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Object getSkipPath() {
        return skipPath;
    }

    public void setSkipPath(Object skipPath) {
        this.skipPath = skipPath;
    }

    public Object getSort() {
        return sort;
    }

    public void setSort(Object sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "BannerBean{" +
                "title='" + title + '\'' +
                ", assistantTitle='" + assistantTitle + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", skipPath=" + skipPath +
                ", sort=" + sort +
                '}';
    }
}
