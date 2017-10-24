package com.weiya.client.verticle;

import com.alibaba.fastjson.JSON;
import com.weiya.client.constant.CustomProperties;
import com.weiya.client.log.*;
import com.weiya.client.model.RpcTraveller;
import com.weiya.client.service.LogService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 客户端执行类
 * Created by liuyin on 2017/10/11.
 */
@Component
public class TcpClientVerticle extends AbstractVerticle {
    // 远程调用日志
    public static final Logger rpcLogger = LoggerFactory.getLogger(TcpClientVerticle.class.getName());
    // 其他日志
    public static final Logger otherLogger = LoggerFactory.getLogger("other." + TcpClientVerticle.class.getName());

    @Autowired
    private LogService logService;
    @Autowired
    private NetClientOptions netClientOptions;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // 输出期望的总连接数
        int parallelNumber = CustomProperties.getParallelNumber();
        int recursionNumber = CustomProperties.getRecursionNumber();
        otherLogger.info("expect connect count is: "+(parallelNumber*recursionNumber));

        // 开始连接
        for(int i = 0; i < parallelNumber; i++){
            createClient(1+recursionNumber*i, recursionNumber*i+recursionNumber, netClientOptions, 0)
                    .setHandler(ar -> {
                        if (ar.failed()) {
                            Throwable cause = ar.cause();
                            otherLogger.error(cause.getMessage());
                            Future.failedFuture(cause);
                        }
                    });
        }
        startFuture.complete();
    }


    /**
     * 创建客户端
     * @param beginUserId 起始用户id
     * @param endUserId 结束用户id
     * @param options 连接配置
     * @param retry 用于记录重试次数
     * @return future
     */
    private Future<Void> createClient(final int beginUserId, final int endUserId, NetClientOptions options, final int retry) {
        Future<Void> createClientFuture = Future.future();
        Assert.assertTrue("beginUserId must le endUserId", beginUserId <= endUserId);
        NetClient client;

        // 加载连接配置
        if (Objects.nonNull(options)) {
            client = vertx.createNetClient(options);
        } else {
            client = vertx.createNetClient();
        }

        // 开始连接
        client.connect(CustomProperties.getTcpPort(), CustomProperties.getTcpHost(), ar -> {
            if (ar.succeeded()) {
                // 如果连接成功，继续下一个连接
                if (beginUserId < endUserId) {
                    final int newBeginUserId = beginUserId + 1;
                    createClient(newBeginUserId, endUserId, options, retry);
                }

                NetSocket socket = ar.result();
                // 记录连接成功的日志
                writeConnectSuccess(socket);

                vertx.eventBus().consumer(CustomProperties.Eventbus.getStartCommandAddress(), message -> {
                    String command = message.body().toString();
                    // 接收命令后启动抢红包
                    if ("begin".equals(command)) {
                        vertx.setPeriodic((long) Math.random() * 1500 + 500L, lo -> {
                            // 发送消息
                            logService.createRpcLogBySocket(socket) // 创建rpc日志
                                    .compose(rpcLog -> {
                                        // 创建rpcTraveller
                                        return logService.createRpcTraveller(rpcLog, String.valueOf(beginUserId));
                                    })
                                    .compose(rpcTraveller -> {

                                        return doPreLog(rpcTraveller) // 远程调用前日志记录
                                                .compose(v -> {
                                                    return writeSocket(rpcTraveller, socket); // 发送日志和用户id到服务器
                                                });
                                    })
                                    .setHandler(ar2 -> {
                                        if (ar2.failed()) {
                                            // 异常记录
                                            Throwable cause = ar.cause();
                                            otherLogger.error(cause.getMessage());
                                            Future.failedFuture(cause);
                                        }
                                    });

                        });
                    }
                });

                socket.handler(buffer -> {
                    getTravellerByBuffer(buffer) // buffer拆包并得到rpcTraveller
                            .compose(this::doPostLog) // 调用返回后，日志记录
                            .setHandler(ar3 -> {
                                if (ar3.failed()) {
                                    // 异常处理
                                    Throwable cause = ar3.cause();
                                    otherLogger.error(cause.getMessage());
                                    Future.failedFuture(cause);
                                }
                            });
                });
            } else {
                // 异常处理，连接失败，记录重试次数，并不断重试
                Future<Void> f = Future.future();
                final int nextRetry = retry + 1;
                otherLogger.warn("retry=" + nextRetry + ",beginUserId=" + beginUserId + ",endUserId=" + endUserId);
                createClient(beginUserId, endUserId, options, nextRetry);
                f.complete();
            }
        });

        createClientFuture.complete();
        return createClientFuture;
    }

    /**
     * 记录连接成功日志
     * @param socket socket
     * @return
     */
    private Future<Void> writeConnectSuccess(NetSocket socket) {
        Future<Void> fu = Future.future();
        Assert.assertNotNull(socket);
        SocketLog socketLog = WyLogFactory.createSocketLog(socket);
        otherLogger.info(socketLog.toLogString());
        fu.complete();
        return fu;
    }



    /**
     * 远程调用前的日志记录
     * @param rpcTraveller
     * @return
     */
    private Future<Void> doPreLog(RpcTraveller rpcTraveller) {
        Future<Void> fu = Future.future();
        Assert.assertNotNull(rpcTraveller);
        // 每一次调用，执行rpcInvoke方法，调用次数自增
        rpcTraveller.rpcInvoke();

        RpcLog rpcLog = rpcTraveller.getRpcLog();
        String userId = rpcTraveller.getExtMsg();

        LocalRpcLog localRpcLog = WyLogFactory.createLocalRpcLog(rpcLog);
        localRpcLog.setMessage("user " + userId + " get redpacket");

        rpcLogger.info(localRpcLog.toLogString());
        fu.complete();
        return fu;
    }

    /**
     * 通过socket将rpcTraveller转换成buffer并发送给服务器
     * @param rpcTraveller
     * @param socket
     * @return
     */
    private Future<Void> writeSocket(RpcTraveller rpcTraveller, NetSocket socket) {
        Future<Void> fu = Future.future();
        Assert.assertNotNull(socket);
        fu.compose(v -> {
            return createBufferByRpcTravellerWithSplitStr(rpcTraveller);
        }).compose(buffer -> {
            socket.write(buffer);
            fu.complete();
            return fu;
        });
        fu.complete();
        return fu;
    }

    /**
     * 将rpcTraveller对象转换成可以发送的buffer对象
     * @param rpcTraveller
     * @return
     */
    private Future<Buffer> createBufferByRpcTravellerWithSplitStr(RpcTraveller rpcTraveller) {
        Future<Buffer> fu = Future.future();
        Assert.assertNotNull(rpcTraveller);

        Buffer buffer = Buffer.buffer();
        // 转换成json字符串
        String rpcTravellerJson = JSON.toJSONString(rpcTraveller);
        // 添加分隔符
        buffer.appendString(rpcTravellerJson).appendString(CustomProperties.getSplitStr());
        fu.complete(buffer);
        return fu;
    }

    /**
     * 从buffer中拆包得到prcTraveller
     * @param buffer
     * @return
     */
    private Future<RpcTraveller> getTravellerByBuffer(Buffer buffer) {
        Future<RpcTraveller> fu = Future.future();
        Assert.assertNotNull(buffer);

        String splitStr = CustomProperties.getSplitStr();
        Assert.assertTrue(buffer.length() > splitStr.length());

        String bufferStr = buffer.toString();
        // 拆包
        String[] splitArray = bufferStr.split(splitStr, 2);
        Assert.assertTrue(splitArray.length > 0);

        String rpcTravellerJson = splitArray[0];
        // json转换
        RpcTraveller rpcTraveller = JSON.parseObject(rpcTravellerJson, RpcTraveller.class);
        fu.complete(rpcTraveller);
        return fu;
    }

    /**
     * 记录调用后的日志
     * @param rpcTraveller
     * @return
     */
    private Future<Void> doPostLog(RpcTraveller rpcTraveller) {
        Future<Void> fu = Future.future();
        Assert.assertNotNull(rpcTraveller);

        String message = rpcTraveller.getExtMsg();
        RpcLog rpcLog = rpcTraveller.getRpcLog();
        // 收到返回的日志，要先升级
        LogConverter.levelUp(rpcLog);

        LocalRpcLog localRpcLog = WyLogFactory.createLocalRpcLog(rpcLog);
        // 这里要把调用方向从“发送”改成“接收”
        localRpcLog.setOrientation(LocalRpcLog.ORIENTATION_RECEIVE);
        localRpcLog.setMessage(message);
        rpcLogger.info(localRpcLog.toLogString());
        fu.complete();
        return fu;
    }


}
