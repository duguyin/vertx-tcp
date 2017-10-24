package com.weiya.server.log;

/**
 * socket连接日志
 * Created by liuyin on 2017/10/13.
 */
public class SocketLog {

    // 对应socket的writeHandlerID
    private String handlerID;
    // 本地地址，包括host和port
    private String localAddress;
    // 远程地址，包括host和port
    private String remoteAddress;

    public String getHandlerID() {
        return handlerID;
    }

    public void setHandlerID(String handlerID) {
        this.handlerID = handlerID;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * 日志打印时调用此方法，而不是toString
     * @return
     */
    public String toLogString() {
        return "handlerID:" + this.handlerID + " localAddress:" + this.localAddress + " remoteAddress:" + this.remoteAddress;
    }
}
