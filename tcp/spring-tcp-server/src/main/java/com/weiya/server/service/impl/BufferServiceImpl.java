package com.weiya.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.weiya.server.constant.CustomProperties;
import com.weiya.server.model.RpcTraveller;
import com.weiya.server.service.BufferService;
import io.netty.util.internal.StringUtil;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.springframework.stereotype.Service;

/**
 * buffer服务实现
 * Created by liuyin on 2017/10/14.
 */
@Service
public class BufferServiceImpl implements BufferService {
    
    @Override
    public Future<String> getValidMessageFromBuffer(Buffer buffer) {
        Future<String> future = Future.future();
        int length = buffer.length();
        Assert.assertTrue( length > 0);

        String message = buffer.getString(0, length);
        // 拆包
        String[] splitArray = message.split(CustomProperties.getSplitStr());
        Assert.assertTrue(splitArray.length > 0);

        String validMessage = splitArray[0];
        Assert.assertFalse(StringUtil.isNullOrEmpty(validMessage));

        future.complete(validMessage);
        return future;
    }

    @Override
    public Future<RpcTraveller> toRpcTraverller(String validMessage) {
        Future<RpcTraveller> future = Future.future();
        // Json转换
        RpcTraveller rpcTraveller = JSON.parseObject(validMessage, RpcTraveller.class);
        future.complete(rpcTraveller);
        return future;
    }


}
