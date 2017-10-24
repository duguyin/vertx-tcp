package com.weiya.server.verticle;

import com.weiya.server.constant.CustomProperties;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 度量Verticle
 * Created by liuyin on 2017/10/17.
 */
@Component
public class MetricsVerticle extends AbstractVerticle {
    // 度量日志
    public static final Logger metricsLogger = LoggerFactory.getLogger("metrics."+MetricsVerticle.class.getName());

    @Autowired
    private MetricsService metricsService;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        String metrics = "vertx.net.servers." + CustomProperties.getTcpHost() + ":" + CustomProperties.getTcpPort();

        // 每5秒记录一次
        vertx.setPeriodic(5000L, h->{
            JsonObject metricsSnapshot = metricsService.getMetricsSnapshot(metrics);
            metricsLogger.info(metricsSnapshot.encode());
        });

        startFuture.complete();
    }
}



