package com.weiya.server.common;

/**
 * 远程调用结果
 * Created by liuyin on 2017/10/15.
 */
public class RpcResult {

    // 状态码：成功
    public static final String CODE_SUCCESS = "0001";
    // 状态码：失败
    public static final String CODE_FAILED = "9999";
    // 状态码
    private String code;
    // 信息
    private String message;
    // 是否成功，默认成功
    private boolean success = true;
    // 要返回的业务数据
    private Object data;

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public RpcResult(){}

    public static RpcResult result(){
        return new RpcResult();
    }

    /**
     * 创建一个成功的结果
     * @return
     */
    public static RpcResult successResult(){
        RpcResult rpcResult = new RpcResult();
        rpcResult.setCode(CODE_SUCCESS);
        return rpcResult;
    }

    /**
     * 创建一个失败的结果
     * @return
     */
    public static RpcResult faildResult(){
        RpcResult rpcResult = new RpcResult();
        rpcResult.setCode(CODE_FAILED);
        rpcResult.setSuccess(false);
        return rpcResult;
    }

    // 下面的方法用于链式风格

    public RpcResult success(){
        this.setSuccess(true);
        this.setCode(CODE_SUCCESS);
        return this;
    }

    public RpcResult failed(){
        this.setSuccess(false);
        this.setCode(CODE_FAILED);
        return this;
    }

    public RpcResult message(String message){
        this.setMessage(message);
        return this;
    }

    public RpcResult data(Object data){
        this.setData(data);
        return this;
    }

}
