package com.weiya.client.config;

import com.weiya.client.constant.CustomProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.annotation.PostConstruct;

/**
 * 配置文件注入
 * Created by liuyin on 2017/10/11.
 */
@Configuration
@PropertySource("classpath:client.properties")
public class PropertiesConfig {


    @Value("${vertx.server.host}")
    private String tcpHost; // 远程服务器地址
    @Value("${vertx.server.port}")
    private int tcpPort; // 远程服务器端口
    @Value("${vertx.splitstr}")
    private String splitStr; // 拆包用的分割字符串
    @Value("${vertx.eventbus.address.command.start}")
    private String startCommandEventbusAddress; // 发送命令的总线地址
    @Value("${vertx.client.connect.parallel.number}")
    private int parallelNumber; // 并行连接数，相当于连接器数
    @Value("${vertx.client.connect.recursion.number}")
    private int recursionNumber; // 每个连接器递归执行连接数


    /**
     * 自定义配置类数据初始化
     */
    @PostConstruct
    public void setProperties() {

        CustomProperties.setTcpHost(getTcpHost());
        CustomProperties.setTcpPort(getTcpPort());
        CustomProperties.setSplitStr(getSplitStr());

        CustomProperties.setParallelNumber(getParallelNumber());
        CustomProperties.setRecursionNumber(getRecursionNumber());

        CustomProperties.Eventbus.setStartCommandAddress(getStartCommandEventbusAddress());
    }

    public int getParallelNumber() {
        return parallelNumber;
    }

    public void setParallelNumber(int parallelNumber) {
        this.parallelNumber = parallelNumber;
    }

    public int getRecursionNumber() {
        return recursionNumber;
    }

    public void setRecursionNumber(int recursionNumber) {
        this.recursionNumber = recursionNumber;
    }

    public String getStartCommandEventbusAddress() {
        return startCommandEventbusAddress;
    }

    public void setStartCommandEventbusAddress(String startCommandEventbusAddress) {
        this.startCommandEventbusAddress = startCommandEventbusAddress;
    }

    public String getTcpHost() {
        return tcpHost;
    }

    public void setTcpHost(String tcpHost) {
        this.tcpHost = tcpHost;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public String getSplitStr() {
        return splitStr;
    }

    public void setSplitStr(String splitStr) {
        this.splitStr = splitStr;
    }

    /**
     * 配置文件读取
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
