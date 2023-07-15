
// LibraryStoreList.java

package com.byd.dynaudio_app.pojo;

public class LibraryStoreList {
    /**
     * 专辑id
     */
    private long albumId;
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
    private long libraryId;
    /**
     * 库类别 1音乐库 2台宣库 3播客库 4资讯库
     */
    private String libraryType;
    /**
     * 碎片（曲目）名称
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
     * 大小
     */
    private Long size;
    /**
     * 类别
     */
    private String type;
    /**
     * 视频地址
     */
    private String videoUrl;
    /**
     * 歌词地址
     */
    private String wordUrl;

    public long getAlbumId() { return albumId; }
    public void setAlbumId(long value) { this.albumId = value; }

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

    public long getLibraryId() { return libraryId; }
    public void setLibraryId(long value) { this.libraryId = value; }

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
}