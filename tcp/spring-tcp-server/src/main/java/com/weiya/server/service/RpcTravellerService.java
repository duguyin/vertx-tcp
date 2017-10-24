package com.weiya.server.service;

import com.weiya.server.model.RpcTraveller;
import io.vertx.core.Future;

/**
 * rpcTraveller服务
 * Created by liuyin on 2017/10/14.
 */
public interface RpcTravellerService {

    /**
     * 得到一个降级的新traveller
     * @param rpcTraveller
     * @return
     */
    Future<RpcTraveller> newLevelDownTraveller(RpcTraveller rpcTraveller);



}
