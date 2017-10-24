package com.weiya.server.verticle;

import com.alibaba.fastjson.JSON;
import com.weiya.server.common.RpcResult;
import com.weiya.server.constant.CustomProperties;
import com.weiya.server.constant.RedpacketConstant;
import com.weiya.server.log.LocalRpcLog;
import com.weiya.server.log.LogConverter;
import com.weiya.server.log.RpcLog;
import com.weiya.server.log.WyLogFactory;
import com.weiya.server.model.RpcTraveller;
import com.weiya.server.service.BufferService;
import com.weiya.server.service.RpcTravellerService;
import io.netty.util.internal.StringUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * tcp服务端verticle
 * Created by liuyin on 2017/10/13.
 */
@Component("tcpServerVerticle")
public class TcpServerVerticle extends AbstractVerticle {

    // 远程调用日志
    public static final Logger rpcLogger = LoggerFactory.getLogger(TcpServerVerticle.class.getName());
    // 其他日志
    public static final Logger otherLogger = LoggerFactory.getLogger("other."+TcpServerVerticle.class.getName());

    @Autowired
    private NetServerOptions redPacketServerOptions;
    @Autowired
    private BufferService bufferService;
    @Autowired
    private RpcTravellerService rpcTravellerService;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // 服务器创建
        NetServer server = vertx.createNetServer(redPacketServerOptions);


        server.connectHandler(socket -> {
            socket.handler(buffer -> {
                this.convertBufferToRpcTraveller(buffer) // 将buffer转换成rpcTraveller
                        .compose(rpcTraveller -> {
                            return writePreLog(rpcTraveller) // 调用前记录
                                    .compose(v -> {
                                        // 模拟rpc：通过总线发送给红包仓库获取红包
                                        return sendRpcTraveller(rpcTraveller);
                                    })
                                    .compose(rpcResult -> {
                                        // 将获取的结果返回给client端
                                        return writeSocketAndPostLog(rpcTraveller, socket, rpcResult);
                                    });
                        })
                        .setHandler(ar -> {
                            if (ar.succeeded()) {

                            } else {
                                Future.failedFuture(ar.cause());
                            }
                        });
            });

        }).listen(CustomProperties.getTcpPort(), CustomProperties.getTcpHost(), asyRes -> {
            if (asyRes.succeeded()) {
                otherLogger.info("localhost:16666 verticle is begining");

            } else {
                Throwable cause = asyRes.cause();
                otherLogger.error("localhost:16666 verticle is failed,cause is " + cause.getMessage());
                Future.failedFuture(cause);
            }
        });
        startFuture.complete();
    }

    /**
     * 根据抢红包的结果，记录日志并返回给client端
     * @param rpcTraveller
     * @param socket
     * @param rpcResult
     * @return
     */
    private Future<Void> writeSocketAndPostLog(RpcTraveller rpcTraveller, NetSocket socket, RpcResult rpcResult) {
        Future<Void> future = Future.future();
        Assert.assertNotNull(rpcResult);

        // 转换成本地日志
        LocalRpcLog localRpcLog = LogConverter.toLocalRpcLog(rpcTraveller.getRpcLog());
        // 结果是从红包仓库返回的，这里的日志方向是：收到
        localRpcLog.setOrientation(LocalRpcLog.ORIENTATION_RECEIVE);
        String rpcResultMessage = rpcResult.getMessage();
        localRpcLog.setMessage(rpcResultMessage);

        rpcTraveller.setExtMsg(rpcResultMessage);

        if (rpcResult.isSuccess()) {
            rpcLogger.info(localRpcLog.toLogString());
        } else {
            rpcLogger.warn(localRpcLog.toLogString());
        }
        Buffer buffer = Buffer.buffer();
        String rpcTravellerJson = JSON.toJSONString(rpcTraveller);
        buffer.appendString(rpcTravellerJson).appendString(CustomProperties.getSplitStr());
        // 返回rpcTraveller，业务信息在extMsg里
        socket.write(buffer);
        future.complete();
        return future;
    }

    ;

    /**
     * 将rpcTraveller发送给红包仓库获取红包
     * 注：这里发送给红包仓库是模拟一次rpc调用，所以在红包仓库的Verticle里面，日志要降级再用
     * @param rpcTraveller
     * @return
     */
    private Future<RpcResult> sendRpcTraveller(RpcTraveller rpcTraveller) {
        Future<RpcResult> future = Future.future();
        Assert.assertNotNull(rpcTraveller);

        String userId = rpcTraveller.getExtMsg();
        String rpcTravellerJson = JSON.toJSONString(rpcTraveller);

        vertx.eventBus().send(CustomProperties.Eventbus.getRedpacketRepository(), rpcTravellerJson, ar -> {
            RpcResult rpcResult = RpcResult.result();
            if (ar.succeeded()) {
                Message<Object> message = ar.result();
                String resultMsg = message.body().toString();

                if (RedpacketConstant.Result.SUCCESS.getResult().equals(resultMsg)) {
                    future.complete(rpcResult.success().message("user " + userId + " " + RedpacketConstant.Result.SUCCESS.getResult()));
                } else if (RedpacketConstant.Result.ALREADY.getResult().equals(resultMsg)) {
                    future.complete(rpcResult.success().message("user " + userId + " " + RedpacketConstant.Result.ALREADY.getResult()));
                } else {
                    future.fail("unknown message: " + resultMsg);
                }
            } else {
                future.fail(ar.cause());
            }
        });
        return future;
    }

    /**
     * 调用前记录
     * @param rpcTraveller
     * @return
     */
    private Future<Void> writePreLog(RpcTraveller rpcTraveller) {
        Future<Void> fu = Future.future();
        Assert.assertNotNull(rpcTraveller);

        rpcTraveller.rpcInvoke();

        RpcLog rpcLog = rpcTraveller.getRpcLog();
        Assert.assertNotNull(rpcLog);
        LocalRpcLog localRpcLog = WyLogFactory.createLocalRpcLog(rpcLog);
        String userId = rpcTraveller.getExtMsg();
        Assert.assertFalse(StringUtil.isNullOrEmpty(userId));
        localRpcLog.setMessage("user " + userId + "get redpacket");
        rpcLogger.info(localRpcLog.toLogString());
        fu.complete();
        return fu;
    }

    /**
     * buffer转换rpcTraveller
     * @param buffer
     * @return
     */
    private Future<RpcTraveller> convertBufferToRpcTraveller(Buffer buffer) {
        Future<RpcTraveller> future = Future.future();
        Assert.assertNotNull(buffer);
        bufferService
                .getValidMessageFromBuffer(buffer) // 拆包
                .compose(msg -> {
                    return bufferService.toRpcTraverller(msg); // 得到rpcTraveller
                })
                .compose(traveller -> {
                    return rpcTravellerService.newLevelDownTraveller(traveller); // rpcTraveller降级
                })
                .setHandler(as -> {
                    if (as.succeeded()) {
                        RpcTraveller t = as.result();
                        future.complete(t);
                    } else {
                        future.fail(as.cause());
                    }
                });
        return future;

    }
}
