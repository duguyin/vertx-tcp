package com.weiya.server.config;

import com.weiya.server.constant.CustomProperties;
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
@PropertySource("classpath:server.properties")
public class PropertiesConfig {

    // 服务器发布地址
    @Value("${vertx.server.host}")
    private String tcpHost;
    // 服务器发布端口
    @Value("${vertx.server.port}")
    private int tcpPort;
    // 拆包分隔符
    @Value("${vertx.splitstr}")
    private String splitStr;
    // 红包仓库总线地址
    @Value("${vertx.eventbus.address.redpacket.repository}")
    private String redpacketRepositoryEventbusAddress;

    /**
     * 将配置文件信息写入自定义配置类
     */
    @PostConstruct
    public void setProperties(){
        CustomProperties.setTcpHost(getTcpHost());
        CustomProperties.setTcpPort(getTcpPort());
        CustomProperties.setSplitStr(getSplitStr());
        CustomProperties.Eventbus.setRedpacketRepository(getRedpacketRepositoryEventbusAddress());
    }

    public String getRedpacketRepositoryEventbusAddress() {
        return redpacketRepositoryEventbusAddress;
    }

    public void setRedpacketRepositoryEventbusAddress(String redpacketRepositoryEventbusAddress) {
        this.redpacketRepositoryEventbusAddress = redpacketRepositoryEventbusAddress;
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
     * 配置文件读取器
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }
}
