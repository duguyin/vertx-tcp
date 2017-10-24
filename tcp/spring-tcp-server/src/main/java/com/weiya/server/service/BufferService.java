package com.weiya.server.service;

import com.weiya.server.model.RpcTraveller;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

/**
 * Buffer服务
 * Created by liuyin on 2017/10/13.
 */
public interface BufferService {

    /**
     * 从buffer中获取有效信息（拆包）
     * @param buffer
     * @return
     */
    Future<String> getValidMessageFromBuffer(Buffer buffer);

    /**
     * 将有效信息转换成rpcTraverller
     * @param validMessage
     * @return
     */
    Future<RpcTraveller>  toRpcTraverller(String validMessage);
}
