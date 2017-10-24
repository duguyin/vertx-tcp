package com.weiya.server.log;

/**
 * 远程调用日志
 * Created by liuyin on 2017/10/13.
 */
public class RpcLog {
    // 初始当前调用id
    public static final String INIT_CurrentRpcID = "";
    // 初始平行调用次数
    public static final int INIT_PARALLEL_INVOKE_COUNT = 0;

    // 全链路唯一Id
    private String traceId;
    // 当前调用Id，相当于调用深度，初始化时不存在调用，为""
    private String currentRpcID = INIT_CurrentRpcID;
    // 平行(同一级)调用次数
    private int parallelInvokeCount = INIT_PARALLEL_INVOKE_COUNT;
    // 远程调用ID，记录调用深度(下一级)和同级调用次数
    private String rpcID;

    public static int getInitParallelInvokeCount() {
        return INIT_PARALLEL_INVOKE_COUNT;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getCurrentRpcID() {
        return currentRpcID;
    }

    public void setCurrentRpcID(String currentRpcID) {
        this.currentRpcID = currentRpcID;
    }

    public int getParallelInvokeCount() {
        return parallelInvokeCount;
    }

    public void setParallelInvokeCount(int parallelInvokeCount) {
        this.parallelInvokeCount = parallelInvokeCount;
    }

    /**
     * 得到rpcId，rpcId实际就是当前rpcId + 平行调用次数
     * @return
     */
    public String getRpcID() {
        if(currentRpcID.length() == 0){
            return String.valueOf(parallelInvokeCount);
        }else{
            return currentRpcID + "." + parallelInvokeCount;
        }
    }

    public void setRpcID(String rpcID) {
        this.rpcID = rpcID;
    }

    /**
     * 日志打印时使用此方法转换成string，而不是用toString
     * @return
     */
    public String toLogString(){
        return "traceId:"+this.traceId+" rpcID:"+getRpcID();
    }

    /**
     * 当要进行一次rpc调用时执行此方法，会将平行调用次数自增
     * @return
     */
    public RpcLog rpcInvoke(){
        this.parallelInvokeCount ++;
        return this;
    }

    @Override
    public String toString() {
        return "RpcLog{" +
                "traceId='" + traceId + '\'' +
                ", currentRpcID='" + currentRpcID + '\'' +
                ", parallelInvokeCount=" + parallelInvokeCount +
                ", rpcID='" + getRpcID() + '\'' +
                '}';
    }
}
