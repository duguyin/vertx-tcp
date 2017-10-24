package com.weiya.client.log;

/**
 * 本地日志对象
 * Created by liuyin on 2017/10/13.
 */
public class LocalRpcLog extends RpcLog {

    // 默认传输类型：发送
    public static final int ORIENTATION_SEND = 0;
    // 默认传输类型：接收
    public static final int ORIENTATION_RECEIVE = 1;

    // 标签
    private String tags;
    // 状态码
    private String code;
    // 信息
    private String message;
    // 方向，默认是发送
    private int orientation = ORIENTATION_SEND;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * 日志打印时使用此方法转换成string，而不是用toString
     * @return
     */
    public String toLogString() {
        return "traceID:" + getTraceId()
                + " rpcID:" + getRpcID()
                + " code:" + this.code
                + " tags:" + this.tags
                + " orientation:" + this.orientation
                + " message:" + this.message;
    }


}
