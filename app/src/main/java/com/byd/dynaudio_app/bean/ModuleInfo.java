package com.byd.dynaudio_app.bean;

import java.util.List;

/**
 * 模块信息 包含模块名等
 */
public class ModuleInfo {
    private String moduleName;

    private String title;
    private String subTitle;

    private GoldenDetail goldenDetail;

    private StationDetail stationDetail;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
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

    public GoldenDetail getGoldenDetail() {
        return goldenDetail;
    }

    public void setGoldenDetail(GoldenDetail goldenDetail) {
        this.goldenDetail = goldenDetail;
    }

    public StationDetail getStationDetail() {
        return stationDetail;
    }

    public void setStationDetail(StationDetail stationDetail) {
        this.stationDetail = stationDetail;
    }

    public static class GoldenDetail {
        String label;
        String text;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class StationDetail {
        List<Info> data;

        public List<Info> getData() {
            return data;
        }

        public void setData(List<Info> data) {
            this.data = data;
        }

        public static class Info {
            String title;
            String imgPath;
            List<String> news;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getImgPath() {
                return imgPath;
            }

            public void setImgPath(String imgPath) {
                this.imgPath = imgPath;
            }

            public List<String> getNews() {
                return news;
            }

            public void setNews(List<String> news) {
                this.news = news;
            }
        }
    }
}
