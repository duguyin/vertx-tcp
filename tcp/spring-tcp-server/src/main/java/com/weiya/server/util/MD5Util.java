package com.weiya.server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5摘要工具
 * Created by liuyin on 2017/10/10.
 */
public class MD5Util {

    /**
     * md5摘要算法
     * @param data 字节数组
     * @return 摘要后的字符串
     * @throws NoSuchAlgorithmException
     */
    public static String md5(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        byte[] mdResult = md5.digest();
        return bytesToHex(mdResult);

    }

    /**
     * 字节数组转字符串
     * @param resultBytes
     * @return
     */
    public static String bytesToHex(byte[] resultBytes){
        StringBuilder sb = new StringBuilder();
        for(int i =0; i< resultBytes.length; i++){
            String s = Integer.toHexString(0xFF & resultBytes[i]);
            if(s.length() == 1){
                sb.append("0").append(s);
            }else{
                sb.append(s);
            }
        }
        return sb.toString();
    }

}
