// ResponseData.java

package com.byd.dynaudio_app.pojo;

public class ResponseData {
    /**
     * 200成功 500失败
     */
    private String code;
    /**
     * 返参
     */
    private Data data;
    /**
     * 返回结果
     */
    private String message;
    /**
     * 是否成功
     */
    private boolean success;

    public String getCode() { return code; }
    public void setCode(String value) { this.code = value; }

    public Data getData() { return data; }
    public void setData(Data value) { this.data = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public boolean getSuccess() { return success; }
    public void setSuccess(boolean value) { this.success = value; }

    @Override
    public String toString() {
        return "ResponseData{" +
                "code='" + code + '\'' +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}



