package com.weiya.client.util;

import io.netty.util.internal.ThreadLocalRandom;

import java.security.NoSuchAlgorithmException;

/**
 * ID生成器
 * Created by liuyin on 2017/10/10.
 */
public class IDGenerator {
    // 随机因子
    public static final ThreadLocalRandom  RANDOM =ThreadLocalRandom.current();

    /**
     * MD5摘要
     * @param str 明文
     * @return 摘要
     * @throws NoSuchAlgorithmException
     */
    public static String createByMD5(String str) throws NoSuchAlgorithmException {
        if(str == null){
            str = String.valueOf(System.currentTimeMillis());
        }
        return MD5Util.md5(str.getBytes());
    }

    /**
     * 在md5摘要的结果上在加上随机数
     * @param str 明文
     * @return 摘要-随机数
     * @throws NoSuchAlgorithmException
     */
    public static String createByMD5AndRandom(String str) throws NoSuchAlgorithmException {
        int i = RANDOM.nextInt(100, 10000);
        return createByMD5(str)+"-"+i;
    }
}
