package com.weiya.client.verticle;

import com.weiya.client.constant.CustomProperties;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 性能监控
 * Created liuyin on 2017/10/15.
 */
@Component
public class MetricsVerticle extends AbstractVerticle {

    // metrics日志
    public static final Logger metricsLogger = LoggerFactory.getLogger("metrics." + MetricsVerticle.class.getName());
    public static final Logger otherLogger = LoggerFactory.getLogger("other." + MetricsVerticle.class.getName());

    @Autowired
    private MetricsService metricsService;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // 每隔5秒输出到日志
        final long id = vertx.setPeriodic(5000L, h -> {
            JsonObject metricsSnapshot = metricsService.getMetricsSnapshot("vertx.net.clients");
            metricsLogger.info(metricsSnapshot.encode());
        });

        // 每一秒输出一下连接数
        final long periodicId = vertx.setPeriodic(1000L, h -> {
            JsonObject ms = metricsService.getMetricsSnapshot("vertx.net.clients.open-netsockets");
            // 当前连接数
            int currentConnect = ms.getJsonObject("vertx.net.clients.open-netsockets").getInteger("count");
            // 期望的连接数
            int expectConnect = CustomProperties.getParallelNumber() * CustomProperties.getRecursionNumber();
            otherLogger.info("[currentConnect:" + currentConnect + " expectConnect:" + expectConnect + "]");
            if (currentConnect == expectConnect) {
                otherLogger.info("publish get redpacket command");
                // 发消息，停止统计每一秒当前连接数
                vertx.eventBus().send("periodicAddress", "begin");
                // 发布命令给客户端，客户端开始抢红包
                vertx.eventBus().publish(CustomProperties.Eventbus.getStartCommandAddress(), "begin");
            }
        });

        // 停止循环任务
        vertx.eventBus().consumer("periodicAddress", msg -> {
            String command = msg.body().toString();
            if ("begin".equals(command)) {
                vertx.cancelTimer(periodicId);
            }
        });
        startFuture.complete();
    }
}
