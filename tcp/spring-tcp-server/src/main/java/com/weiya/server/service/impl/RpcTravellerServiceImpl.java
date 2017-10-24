package com.weiya.server.service.impl;

import com.weiya.server.log.LogConverter;
import com.weiya.server.log.RpcLog;
import com.weiya.server.model.RpcTraveller;
import com.weiya.server.service.RpcTravellerService;
import io.vertx.core.Future;
import org.junit.Assert;
import org.springframework.stereotype.Service;

/**
 * rpcTarveller服务实现
 * Created by liuyin on 2017/10/14.
 */
@Service
public class RpcTravellerServiceImpl implements RpcTravellerService {

    /**
     * 得到一个新的已经降级的rpcTraveller
     * @param rpcTraveller
     * @return
     */
    @Override
    public Future<RpcTraveller> newLevelDownTraveller(RpcTraveller rpcTraveller) {

        Future<RpcTraveller> future = Future.future();
        Assert.assertNotNull(rpcTraveller);

        RpcTraveller newTraveller = new RpcTraveller();
        RpcLog rpcLog = rpcTraveller.getRpcLog();
        Assert.assertNotNull(rpcLog);

        RpcLog levelDownRpcLog = LogConverter.levelDown(rpcLog);
        newTraveller.setRpcLog(levelDownRpcLog);
        newTraveller.setExtMsg(rpcTraveller.getExtMsg());

        future.complete(newTraveller);
        return future;

    }
}
