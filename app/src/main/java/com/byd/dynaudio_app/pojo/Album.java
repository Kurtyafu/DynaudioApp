package com.byd.dynaudio_app.pojo;

import java.util.List;

/**
 * 专辑对象 黑胶、有声、沉浸声 返回的data就是一个包含该对象的list
 */
public class Album {
    /**
     * 专辑描述
     */
    private String albumDesc;
    /**
     * 背景图片地址
     */
    private Object albumImgUrl;
    /**
     * 专辑名称
     */
    private String albumName;
    /**
     * 专辑标签
     */
    private String albumQuality;
    /**
     * 专辑类型
     */
    private String albumType;
    /**
     * 属性标签
     */
    private String attrLabel;
    /**
     * 歌手姓名
     */
    private String auther;
    /**
     * 专辑id
     */
    private Long id;
    /**
     * 专辑曲目列表
     */
    private List<LibraryStoreList> libraryStoreList;
    /**
     * 限定车型
     */
    private String limitCar;
    /**
     * 推荐标签（1热、2荐、3新）
     */
    private Long recommendLabel;
    /**
     * 排序
     */
    private Long sort;

    public String getAlbumDesc() { return albumDesc; }
    public void setAlbumDesc(String value) { this.albumDesc = value; }

    public Object getAlbumImgUrl() { return albumImgUrl; }
    public void setAlbumImgUrl(Object value) { this.albumImgUrl = value; }

    public String getAlbumName() { return albumName; }
    public void setAlbumName(String value) { this.albumName = value; }

    public String getAlbumQuality() { return albumQuality; }
    public void setAlbumQuality(String value) { this.albumQuality = value; }

    public String getAlbumType() { return albumType; }
    public void setAlbumType(String value) { this.albumType = value; }

    public String getAttrLabel() { return attrLabel; }
    public void setAttrLabel(String value) { this.attrLabel = value; }

    public String getAuther() { return auther; }
    public void setAuther(String value) { this.auther = value; }

    public Long getid() { return id; }
    public void setid(Long value) { this.id = value; }

    public List<LibraryStoreList> getLibraryStoreList() { return libraryStoreList; }
    public void setLibraryStoreList(List<LibraryStoreList> value) { this.libraryStoreList = value; }

    public String getLimitCar() { return limitCar; }
    public void setLimitCar(String value) { this.limitCar = value; }

    public Long getRecommendLabel() { return recommendLabel; }
    public void setRecommendLabel(Long value) { this.recommendLabel = value; }

    public Long getSort() { return sort; }
    public void setSort(Long value) { this.sort = value; }
}
