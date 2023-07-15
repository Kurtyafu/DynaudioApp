package com.byd.dynaudio_app.bean.request;

public class DeleteUserBody {
    /**
     * 注销原因
     */
    private String destroyReason;
    /**
     * 备注
     */
    private String destroyRemark;
    private String userId;

    public void setDestroyReason(String destroyReason) {
        this.destroyReason = destroyReason;
    }

    public void setDestroyRemark(String destroyRemark) {
        this.destroyRemark = destroyRemark;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
