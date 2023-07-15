package com.byd.dynaudio_app.bean.response;

public class MsCodeBean {
    /**
     * 返回code
     */
    private String code;
    /**
     * 验证码
     */
    private String data;
    /**
     * 描述
     */
    private String message;
    /**
     * 是否成功
     */
    private boolean success;

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getData() {
        return data;
    }

    public void setData(String value) {
        this.data = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean value) {
        this.success = value;
    }
}
