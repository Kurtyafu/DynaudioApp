package com.byd.dynaudio_app.bean.response;

public class SingerBean {
    /**
     * 类型 ： 歌手/作词
     */
    private String autherType;
    /**
     * 姓名
     */
    private String autherName;
    /**
     * 头像
     */
    private String headUrl;
    /**
     * 描述
     */
    private String autherDesc;

    public String getAutherType() {
        return autherType;
    }

    public void setAutherType(String autherType) {
        this.autherType = autherType;
    }

    public String getAutherName() {
        return autherName;
    }

    public void setAutherName(String autherName) {
        this.autherName = autherName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getAutherDesc() {
        return autherDesc;
    }

    public void setAutherDesc(String autherDesc) {
        this.autherDesc = autherDesc;
    }
}
