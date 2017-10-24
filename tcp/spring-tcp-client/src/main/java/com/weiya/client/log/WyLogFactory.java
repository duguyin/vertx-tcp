package com.weiya.client.log;

import com.weiya.client.util.IDGenerator;
import io.vertx.core.net.NetSocket;
import org.junit.Assert;

import java.security.NoSuchAlgorithmException;

/**
 * 威亚日志工厂
 * Created by liuyin on 2017/10/13.
 */
public class WyLogFactory {

    /**
     * 创建一个远程调用日志
     * @param str 要加密的字符串，将根据这个字符串来md5摘要生成全链路唯一id
     * @return
     */
    public static RpcLog createRpcLog(String str) {
        RpcLog rpcLog = new RpcLog();
        try {
            rpcLog.setTraceId(IDGenerator.createByMD5AndRandom(str));
        } catch (NoSuchAlgorithmException e) {
            rpcLog.setTraceId(System.currentTimeMillis()+"");
        }
        return rpcLog;
    }

    /**
     * 将远程调用日志转换成本地日志
     * @param rpcLog
     * @return
     */
    public static LocalRpcLog createLocalRpcLog(RpcLog rpcLog) {
        Assert.assertNotNull(rpcLog);
        LocalRpcLog localRpcLog = new LocalRpcLog();

        localRpcLog.setTraceId(rpcLog.getTraceId());
        localRpcLog.setCurrentRpcID(rpcLog.getCurrentRpcID());
        localRpcLog.setParallelInvokeCount(rpcLog.getParallelInvokeCount());

        return localRpcLog;
    }

    /**
     * 根据socket的信息来创建一个相关socket日志
     * @param netSocket
     * @return
     */
    public static SocketLog createSocketLog(NetSocket netSocket){
        Assert.assertNotNull(netSocket);

        SocketLog socketLog = new SocketLog();
        socketLog.setHandlerID(netSocket.writeHandlerID());
        socketLog.setLocalAddress(netSocket.localAddress().toString());
        socketLog.setRemoteAddress(netSocket.remoteAddress().toString());

        return socketLog;
    }
}
