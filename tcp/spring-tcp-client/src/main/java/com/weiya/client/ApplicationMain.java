package com.weiya.client;

import com.weiya.client.config.BaseScanPackages;
import com.weiya.client.verticle.MetricsVerticle;
import com.weiya.client.verticle.TcpClientVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 主启动类
 * Created by liuyin on 2017/10/15.
 */
public class ApplicationMain {

    // 其他日志
    public static final Logger otherLogger = LoggerFactory.getLogger("other." + ApplicationMain.class.getName());

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseScanPackages.class);
        Vertx vertx = context.getBean("vertx", Vertx.class);

        TcpClientVerticle tcpClientVerticle = context.getBean("tcpClientVerticle", TcpClientVerticle.class);
        MetricsVerticle metricsVerticle = context.getBean("metricsVerticle", MetricsVerticle.class);

        // 部署客户端
        vertx.deployVerticle(tcpClientVerticle, ar -> {
            if (ar.succeeded()) {
                otherLogger.info("tcpClientVerticle deploy success");
                // 部署客户端的metrics
                vertx.deployVerticle(metricsVerticle, metricsAr -> {
                    if (metricsAr.succeeded()) {
                        otherLogger.info("metricsVerticle deploy success");
                    } else {
                        otherLogger.error(metricsAr.cause().getMessage());
                        Future.failedFuture(ar.cause());
                    }
                });

            } else {
                otherLogger.error(ar.cause().getMessage());
                Future.failedFuture(ar.cause());
            }
        });
    }
}
