// ModuleDatum.java

package com.byd.dynaudio_app.pojo;

import java.util.List;

public class ModuleData {
    /**
     * 专辑id
     */
    private Long albumId;
    /**
     * 属性标签
     */
    private String attrLabel;
    /**
     * 音频文件地址
     */
    private String audioUrl;
    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 碎片时长
     */
    private Long duration;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 碎片库id
     */
    private Long libraryId;
    /**
     * 库类别 1音乐库 2台宣库 3播客库 4资讯库
     */
    private String libraryType;
    /**
     * 碎片名称
     */
    private String name;
    /**
     * 品质标签
     */
    private String quality;
    /**
     * 表演者
     */
    private String singer;
    /**
     * 碎片大小
     */
    private Long size;
    /**
     * 类别
     */
    private String type;
    /**
     * 视频文件地址
     */
    private String videoUrl;
    /**
     * 歌词地址
     */
    private String wordUrl;

    public Long getAlbumId() { return albumId; }
    public void setAlbumId(Long value) { this.albumId = value; }

    public String getAttrLabel() { return attrLabel; }
    public void setAttrLabel(String value) { this.attrLabel = value; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String value) { this.audioUrl = value; }

    public String getBeginTime() { return beginTime; }
    public void setBeginTime(String value) { this.beginTime = value; }

    public Long getDuration() { return duration; }
    public void setDuration(Long value) { this.duration = value; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String value) { this.endTime = value; }

    public Long getLibraryId() { return libraryId; }
    public void setLibraryId(Long value) { this.libraryId = value; }

    public String getLibraryType() { return libraryType; }
    public void setLibraryType(String value) { this.libraryType = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public String getQuality() { return quality; }
    public void setQuality(String value) { this.quality = value; }

    public String getSinger() { return singer; }
    public void setSinger(String value) { this.singer = value; }

    public Long getSize() { return size; }
    public void setSize(Long value) { this.size = value; }

    public String getType() { return type; }
    public void setType(String value) { this.type = value; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String value) { this.videoUrl = value; }

    public String getWordUrl() { return wordUrl; }
    public void setWordUrl(String value) { this.wordUrl = value; }

    /**
     * 专辑相关的属性
     * @return
     */

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
    private String recommendLabel;
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


    public String getAuther() { return auther; }
    public void setAuther(String value) { this.auther = value; }

    public Long getid() { return id; }
    public void setid(Long value) { this.id = value; }

    public List<LibraryStoreList> getLibraryStoreList() { return libraryStoreList; }
    public void setLibraryStoreList(List<LibraryStoreList> value) { this.libraryStoreList = value; }

    public String getLimitCar() { return limitCar; }
    public void setLimitCar(String value) { this.limitCar = value; }

    public String getRecommendLabel() { return recommendLabel; }
    public void setRecommendLabel(String value) { this.recommendLabel = value; }

    public Long getSort() { return sort; }
    public void setSort(Long value) { this.sort = value; }

    @Override
    public String toString() {
        return "ModuleData{" +
                "albumId=" + albumId +
                ", attrLabel='" + attrLabel + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", duration=" + duration +
                ", endTime='" + endTime + '\'' +
                ", libraryId=" + libraryId +
                ", libraryType='" + libraryType + '\'' +
                ", name='" + name + '\'' +
                ", quality='" + quality + '\'' +
                ", singer='" + singer + '\'' +
                ", size=" + size +
                ", type='" + type + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", wordUrl='" + wordUrl + '\'' +
                ", albumDesc='" + albumDesc + '\'' +
                ", albumImgUrl=" + albumImgUrl +
                ", albumName='" + albumName + '\'' +
                ", albumQuality='" + albumQuality + '\'' +
                ", albumType='" + albumType + '\'' +
                ", auther='" + auther + '\'' +
                ", id=" + id +
                ", libraryStoreList=" + libraryStoreList +
                ", limitCar='" + limitCar + '\'' +
                ", recommendLabel=" + recommendLabel +
                ", sort=" + sort +
                '}';
    }
}