package com.weiya.client.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetClientOptions;
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

    @Bean
    public Vertx vertx( ){
        Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
                new DropwizardMetricsOptions().setEnabled(true)));
        return vertx;
    }

    @Bean(name = "metricsService")
    public MetricsService metricsService(){
        MetricsService metricsService = MetricsService.create(vertx());
        return metricsService;
    }

    @Bean
    public NetClientOptions netClientOptions(){
        NetClientOptions options = new NetClientOptions();
        options.setReconnectInterval(500);
        options.setReconnectAttempts(10);
        return options;
    }
}
