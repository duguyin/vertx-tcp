package com.weiya.server.constant;

/**
 * 红包相关常量
 * Created by liuyin on 2017/10/15.
 */
public class RedpacketConstant {

    /**
     * 抢红包结果
     */
    public static enum Result{
        SUCCESS,
        ALREADY,
        ;

        public String getResult(){
            return this.name();
        }

    }
}
