package com.byd.dynaudio_app.bean;

public class TopBgBean {
    private String title;
    private String content;

    private String bgUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public TopBgBean(String title, String content, String bgUrl) {
        this.title = title;
        this.content = content;
        this.bgUrl = bgUrl;
    }
}
