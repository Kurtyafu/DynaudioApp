package com.byd.dynaudio_app.bean.response;

public class SalesPresentationBean {

    /**
     * title : 智能音乐座舱
     * firstVideoUrl : https://stream7.iqilu.com/10339/article/202002/16/3be2e4ef4aa21bfe7493064a7415c34d.mp4
     * secondVideoUrl : https://stream7.iqilu.com/10339/upload_transcode/202002/16/20200216050645YIMfjPq5Nw.mp4
     */

    private String title;
    private String firstVideoUrl;
    private String secondVideoUrl;

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getFirstVideoUrl() {return firstVideoUrl;}

    public void setFirstVideoUrl(String firstVideoUrl) {this.firstVideoUrl = firstVideoUrl;}

    public String getSecondVideoUrl() {return secondVideoUrl;}

    public void setSecondVideoUrl(String secondVideoUrl) {this.secondVideoUrl = secondVideoUrl;}

    @Override
    public String toString() {
        return "SalesPresentationBean{" +
                "title='" + title + '\'' +
                ", firstVideoUrl='" + firstVideoUrl + '\'' +
                ", secondVideoUrl='" + secondVideoUrl + '\'' +
                '}';
    }
}
