package com.byd.dynaudio_app.bean.request;

public class SaveOrDeleteBody {
    /**
     * 库id
     */
    private String libraryId;
    /**
     * 库类型
     */
    private String libraryType;
    /**
     * 用户id
     */
    private String userId;

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }

    public void setLibraryType(String libraryType) {
        this.libraryType = libraryType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
