package com.weiya.client.service;

import com.weiya.client.log.RpcLog;
import com.weiya.client.model.RpcTraveller;
import io.vertx.core.Future;
import io.vertx.core.net.NetSocket;

/**
 * 日志服务
 * Created by admin on 2017/10/11.
 */
public interface LogService {

    /**
     * 根据socket生成rpcLog
     * @param socket
     * @return
     */
    Future<RpcLog> createRpcLogBySocket(NetSocket socket);

    /**
     * 根据rpcLog和扩展信息，生成rpcTraveller
     * @param rpcLog 远程日志对象
     * @param extMsg 扩展信息
     * @return rpcTraveller
     */
    Future<RpcTraveller> createRpcTraveller(RpcLog rpcLog, String extMsg);


}
