package com.weiya.server.constant;

/**
 * 自定义配置类，初始数据来源于配置文件
 * Created by liuyin 2017/10/13.
 */
public class CustomProperties {
    // 服务器发布地址
    private static String tcpHost;
    // 服务器发布端口
    private static int tcpPort;
    // 拆包分割字符
    private static String splitStr;

    /**
     * 总线相关配置
     */
    public static class Eventbus{
        // 红包仓库总线地址
        private static String redpacketRepository;

        public static String getRedpacketRepository() {
            return redpacketRepository;
        }

        public static void setRedpacketRepository(String redpacketRepository) {
            Eventbus.redpacketRepository = redpacketRepository;
        }
    }

    public static String getTcpHost() {
        return tcpHost;
    }

    public static void setTcpHost(String tcpHost) {
        CustomProperties.tcpHost = tcpHost;
    }

    public static int getTcpPort() {
        return tcpPort;
    }

    public static void setTcpPort(int tcpPort) {
        CustomProperties.tcpPort = tcpPort;
    }

    public static String getSplitStr() {
        return splitStr;
    }

    public static void setSplitStr(String splitStr) {
        CustomProperties.splitStr = splitStr;
    }


}
