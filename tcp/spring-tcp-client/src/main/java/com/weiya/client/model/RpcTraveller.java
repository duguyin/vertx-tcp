package com.weiya.client.model;

import com.weiya.client.log.RpcLog;

/**
 * 日志传输对象（日志旅行者）
 * Created by liuyin on 2017/10/14.
 */
public class RpcTraveller  {
    // 远程日志对象
    private RpcLog rpcLog;
    // 扩展信息
    private String extMsg;

    public RpcLog getRpcLog() {
        return rpcLog;
    }

    public void setRpcLog(RpcLog rpcLog) {
        this.rpcLog = rpcLog;
    }

    public String getExtMsg() {
        return extMsg;
    }

    public void setExtMsg(String extMsg) {
        this.extMsg = extMsg;
    }

    /**
     * 记录一次远程调用，这个方法的执行，相当于把成员rpcLog中的平行调用次数自增
     * @return
     */
    public RpcTraveller rpcInvoke(){
        RpcLog rpcLog = getRpcLog();
        if(null != rpcLog){
            setRpcLog(rpcLog.rpcInvoke());
        }
        return this;
    }

    @Override
    public String toString() {
        return "RpcTraveller{" +
                "rpcLog=" + rpcLog.toString() +
                ", extMsg='" + extMsg + '\'' +
                '}';
    }
}
