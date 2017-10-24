package com.weiya.server.verticle;

import com.alibaba.fastjson.JSON;
import com.weiya.server.common.RpcResult;
import com.weiya.server.constant.CustomProperties;
import com.weiya.server.constant.RedpacketConstant;
import com.weiya.server.log.LocalRpcLog;
import com.weiya.server.log.LogConverter;
import com.weiya.server.log.RpcLog;
import com.weiya.server.model.RpcTraveller;
import com.weiya.server.service.RpcTravellerService;
import io.netty.util.internal.StringUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 红包Verticle
 * Created by liuyin on 2017/10/15.
 */
@Component("redPacketVerticle")
public class RedpacketVerticle extends AbstractVerticle {

    // 远程调用日志
    public static final Logger rpcLogger = LoggerFactory.getLogger(RedpacketVerticle.class.getName());
    // 其他日志
    public static final Logger otherLogger = LoggerFactory.getLogger("other."+RedpacketVerticle.class.getName());

    // 用户领取红包记录
    private Map<String, Integer> redPacketMap = new HashMap<>();
    // 红包总数，默认就10W。
    private static int redPacketCount = 100000;

    @Autowired
    private RpcTravellerService rpcTravellerService;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        otherLogger.info("redpacketVerticle init...");
        vertx.eventBus().consumer(CustomProperties.Eventbus.getRedpacketRepository(), (Message<String> message) -> {
            convertToRpcTraveller(message) // 消息拆包，转换成rpcTraveller
                    .compose(rpcTravellerService::newLevelDownTraveller) // 降级（这里模拟的是远程调用，所以降级）
                    .compose(rpcTraveller -> {
                        return doPreLog(rpcTraveller) // 调用前记录
                                .compose(v -> {
                                    return getOrSetRedpacket(rpcTraveller); // 获取红包，返回获取结果
                                })
                                .compose(rpcResult -> { // 调用后记录
                                    return doPostLog(rpcTraveller, rpcResult);
                                });
                    })
                    .setHandler(ar -> {
                        if (ar.succeeded()) {
                            RpcResult result = ar.result();
                            // 把获取结果返回
                            message.reply(result.getMessage());
                        } else {
                            // 异常记录
                            Future fu = Future.future();
                            Throwable cause = ar.cause();
                            otherLogger.error(cause.getMessage());
                            fu.fail(cause.getMessage());
                            fu.complete();
                        }
                    });
        });
        startFuture.complete();
    }

    /**
     * 消息合法性检查，并转换成rpcTraveller
     *
     * @param message
     * @return
     */
    private Future<RpcTraveller> convertToRpcTraveller(Message<String> message) {
        Future<RpcTraveller> future = Future.future();
        Assert.assertNotNull(message);
        String rpcTravellerJson = message.body();
        Assert.assertFalse(StringUtil.isNullOrEmpty(rpcTravellerJson));
        RpcTraveller rpcTraveller = JSON.parseObject(rpcTravellerJson, RpcTraveller.class);
        future.complete(rpcTraveller);
        return future;
    }

    /**
     * 方法执行（调用）前的日志
     * @param rpcTraveller
     * @return
     */
    private Future<Void> doPreLog(RpcTraveller rpcTraveller) {
        Future<Void> future = Future.future();
        Assert.assertNotNull(rpcTraveller);

        rpcTraveller.rpcInvoke();

        RpcLog rpcLog = rpcTraveller.getRpcLog();
        String userId =  rpcTraveller.getExtMsg();

        LocalRpcLog localRpcLog = LogConverter.toLocalRpcLog(rpcLog);
        localRpcLog.setMessage("user " + userId + " get redpacket");
        rpcLogger.info(localRpcLog.toLogString());
        future.complete();
        return future;
    }

    /**
     * 查询历史记录，获取红包
     * @param
     * @return
     */
    private Future<RpcResult> getOrSetRedpacket(RpcTraveller rpcTraveller) {
        Future<RpcResult> future = Future.future();
        Assert.assertNotNull(rpcTraveller);
        String userId = rpcTraveller.getExtMsg();
        RpcResult rpcResult = RpcResult.successResult();
        // 已经获取过
        if (redPacketMap.containsKey(userId)) {
            future.complete(rpcResult.message(RedpacketConstant.Result.ALREADY.getResult()));
        // 未获取，则获取红包并记录
        } else {
            redPacketCount--;
            redPacketMap.put(userId, 1);
            future.complete(rpcResult.message(RedpacketConstant.Result.SUCCESS.getResult()));
        }
        return future;
    }

    /**
     * 方法调用后的日志
     * @param rpcTraveller
     * @param rpcResult
     * @return
     */
    private Future<RpcResult> doPostLog(RpcTraveller rpcTraveller, RpcResult rpcResult) {
        Future<RpcResult> future = Future.future();
        Assert.assertNotNull(rpcResult);
        Assert.assertNotNull(rpcTraveller);

        String userId = rpcTraveller.getExtMsg();
        LocalRpcLog localRpcLog = LogConverter.toLocalRpcLog(rpcTraveller.getRpcLog());
        localRpcLog.setOrientation(LocalRpcLog.ORIENTATION_RECEIVE);
        if (rpcResult.isSuccess()) {
            localRpcLog.setMessage("user " + userId + " " + rpcResult.getMessage() + " get redpacket, left "+redPacketCount);
            rpcLogger.info(localRpcLog.toLogString());
        } else {
            localRpcLog.setMessage(rpcResult.getMessage());
            rpcLogger.warn(localRpcLog.toLogString());
        }
        future.complete(rpcResult);
        return future;
    }

}
