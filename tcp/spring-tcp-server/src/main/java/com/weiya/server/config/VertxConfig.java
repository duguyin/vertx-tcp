package com.weiya.server.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetServerOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.MetricsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * vertx相关bean管理
 * Created by liuyin on 2017/10/11.
 */
@Configuration
public class VertxConfig {

    @Bean("vertx")
    public Vertx vertx(){
        return Vertx.vertx(new VertxOptions().setMetricsOptions(
                new DropwizardMetricsOptions().setEnabled(true)));
    }

    @Bean("redPacketServerOptions")
    public NetServerOptions netServerOptions(){
        return new NetServerOptions();
    }

    /**
     * 测量器
     * @return
     */
    @Bean
    public MetricsService metricsService(){
        MetricsService metricsService = MetricsService.create(vertx());
        return metricsService;
    }
}
