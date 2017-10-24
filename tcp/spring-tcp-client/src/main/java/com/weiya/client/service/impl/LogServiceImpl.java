package com.weiya.client.service.impl;

import com.weiya.client.log.RpcLog;
import com.weiya.client.log.WyLogFactory;
import com.weiya.client.model.RpcTraveller;
import com.weiya.client.service.LogService;
import io.vertx.core.Future;
import io.vertx.core.net.NetSocket;
import org.junit.Assert;
import org.springframework.stereotype.Service;

/**
 * Created by liuyin on 2017/10/11.
 */
@Service("logService")
public class LogServiceImpl implements LogService{

    /**
     * 用socket获取本机地址+时间戳，来生成唯一rpcLog
     * @param socket
     * @return
     */
    @Override
    public Future<RpcLog> createRpcLogBySocket(NetSocket socket) {
        Future<RpcLog> fu = Future.future();
        Assert.assertNotNull(socket);

        String address = socket.localAddress().toString();
        long currentTime = System.currentTimeMillis();
        RpcLog rpcLog = WyLogFactory.createRpcLog(address+currentTime);
        fu.complete(rpcLog);
        return fu;
    }

    /**
     * 生成rpcTraveller
     * @param rpcLog 远程日志对象
     * @param extMsg 扩展信息
     * @return
     */
    @Override
    public Future<RpcTraveller> createRpcTraveller(RpcLog rpcLog, String extMsg) {
        Future<RpcTraveller> fu = Future.future();

        RpcTraveller rpcTraveller = new RpcTraveller();
        rpcTraveller.setExtMsg(extMsg);
        rpcTraveller.setRpcLog(rpcLog);
        fu.complete(rpcTraveller);
        return fu;
    }


}
