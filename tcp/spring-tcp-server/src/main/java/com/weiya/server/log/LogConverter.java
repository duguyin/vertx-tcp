package com.weiya.server.log;

import org.junit.Assert;
import java.util.Objects;

/**
 * 日志转换器
 * Created by liuyin on 2017/10/14.
 */
public class LogConverter {

    /**
     * 把远程日志转换成本地日志
     * @param rpcLog
     * @return
     */
    public static LocalRpcLog toLocalRpcLog(RpcLog rpcLog){
        LocalRpcLog localRpcLog = new LocalRpcLog();
        if(Objects.nonNull(rpcLog)){
            localRpcLog.setTraceId(rpcLog.getTraceId());
            localRpcLog.setParallelInvokeCount(rpcLog.getParallelInvokeCount());
            localRpcLog.setCurrentRpcID(rpcLog.getCurrentRpcID());
        }
        return localRpcLog;
    }

    /**
     * 远程日志降级。当接收到上一级发来的rpcLog后，要首先降级才能使用
     * 如：上一级rpcID是0.1，这里降级以后就是0.1.1
     * @param rpcLog
     * @return
     */
    public static RpcLog levelDown( RpcLog rpcLog) {
        Assert.assertNotNull(rpcLog);
        String currentRpcID = rpcLog.getCurrentRpcID();
        Assert.assertNotNull(currentRpcID);
        if(currentRpcID.length() == 0){
            currentRpcID = String.valueOf(rpcLog.getParallelInvokeCount());
        }else{
            currentRpcID = currentRpcID + "." + rpcLog.getParallelInvokeCount();
        }
        rpcLog.setCurrentRpcID(currentRpcID);
        rpcLog.setParallelInvokeCount(RpcLog.INIT_PARALLEL_INVOKE_COUNT);
        return rpcLog;
    }

    /**
     * 远程日志升级。当接收到下一级返回的rpcLog后，要首先升级才能使用
     * 如：上一级rpcID是0.1，这里升级后就是0
     * @param rpcLog
     * @return
     */
    public static RpcLog levelUp(RpcLog rpcLog) {
        Assert.assertNotNull(rpcLog);

        String currentRpcID = rpcLog.getCurrentRpcID();
        int index = currentRpcID.lastIndexOf(".");

        if(index <= 0){
            rpcLog.setParallelInvokeCount(Integer.parseInt(currentRpcID));
            rpcLog.setCurrentRpcID(RpcLog.INIT_CurrentRpcID);
        }else{
            rpcLog.setParallelInvokeCount(Integer.parseInt(currentRpcID.substring(index + 1, currentRpcID.length())));
            rpcLog.setCurrentRpcID(currentRpcID.substring(0, index));
        }
        return rpcLog;
    }


}
