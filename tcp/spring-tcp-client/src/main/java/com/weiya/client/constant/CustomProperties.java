package com.weiya.client.constant;

/**
 * 自定义配置类，初始数据来源于配置文件
 * Created by liuyin 2017/10/13.
 */
public class CustomProperties {

    // 远程服务器地址
    private static String tcpHost;
    // 远程服务器端口
    private static int tcpPort;

    // 拆包用的分割字符串
    private static String splitStr;

    // 连接器递归次数
    private static int recursionNumber;
    // 同时并发连接数（相当于连接器数量）
    private static int parallelNumber;


    /**
     * 总线的一些自定义配置
     */
    public static class Eventbus {
        // 抢红包命令地址
        private static String startCommandAddress;

        public static String getStartCommandAddress() {
            return startCommandAddress;
        }

        public static void setStartCommandAddress(String startCommandAddress) {
            Eventbus.startCommandAddress = startCommandAddress;
        }
    }

    public static int getRecursionNumber() {
        return recursionNumber;
    }

    public static void setRecursionNumber(int recursionNumber) {
        CustomProperties.recursionNumber = recursionNumber;
    }

    public static int getParallelNumber() {
        return parallelNumber;
    }

    public static void setParallelNumber(int parallelNumber) {
        CustomProperties.parallelNumber = parallelNumber;
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
