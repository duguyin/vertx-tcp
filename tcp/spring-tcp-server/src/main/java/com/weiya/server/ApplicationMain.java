package com.weiya.server;

import com.weiya.server.config.BaseScanPackage;
import com.weiya.server.constant.CustomProperties;
import com.weiya.server.verticle.MetricsVerticle;
import com.weiya.server.verticle.RedpacketVerticle;
import com.weiya.server.verticle.TcpServerVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 应用启动入口
 * Created by liuyin on 2017/10/13.
 */
public class ApplicationMain {

    // 其他日志
    public static final Logger otherLogger = LoggerFactory.getLogger("other."+ApplicationMain.class.getName());

    /**
     * 服务器地址和端口号
     * @return
     */
    public static String getServerInfo(){
        return CustomProperties.getTcpHost()+":"+CustomProperties.getTcpPort();
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseScanPackage.class);
        // 输出配置文件中的服务器地址和端口号
        otherLogger.info("init server:"+getServerInfo());

        // 如果从命令输入，换成命令输入的服务器地址和端口号
        if(null != args && args.length == 2){
            CustomProperties.setTcpHost(args[0]);
            CustomProperties.setTcpPort(Integer.parseInt(args[1]));
        }
        otherLogger.info("custom server:"+getServerInfo());

        Vertx vertx = context.getBean("vertx", Vertx.class);
        TcpServerVerticle tcpServerVerticle = context.getBean("tcpServerVerticle", TcpServerVerticle.class);
        RedpacketVerticle redpacketVerticle = context.getBean("redPacketVerticle", RedpacketVerticle.class);
        MetricsVerticle metricsVerticle = context.getBean("metricsVerticle",MetricsVerticle.class);


        // 部署tcp服务器
        vertx.deployVerticle(tcpServerVerticle, handler -> {
            if (handler.succeeded()) {
                otherLogger.info("tcpServerVerticle succeeded");
                // 部署红包仓库
                vertx.deployVerticle(redpacketVerticle, handler2->{
                    if(handler2.succeeded()){
                        otherLogger.info("redpacketVerticle succeeded");
                    }else{
                        Throwable cause = handler2.cause();
                        otherLogger.error(cause.getMessage());
                        Future.failedFuture(cause);
                    }
                });

                // 部署性能计量
                vertx.deployVerticle(metricsVerticle,handler3->{
                    if(handler3.succeeded()){
                        otherLogger.info("metricsVerticle succeeded");
                    }else{
                        Throwable cause = handler3.cause();
                        otherLogger.error(cause.getMessage());
                        Future.failedFuture(cause);
                    }
                });
            } else {
                Throwable cause = handler.cause();
                otherLogger.error(cause.getMessage());
                Future.failedFuture(cause);
            }
        });
    }
}
