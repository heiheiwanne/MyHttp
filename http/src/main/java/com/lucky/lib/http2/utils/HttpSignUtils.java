package com.lucky.lib.http2.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Description: sign 签名
 * @Author: xmq
 * @Date: 2019-04-26 10:31
 */
public class HttpSignUtils {
    private static final int DEFAULT_LENGTH = 16;

    /**
     * 获得签名串
     * @param cid cid各端不一致
     * @param uid sessionId 状态id
     * @param q 参数
     * @param key 盐串
     * @return 签名结果
     */
    public static String getSign(@Nullable String cid,@Nullable String uid,@Nullable String q,@NonNull String key) {
        Map<String, Object> params = new HashMap<String, Object>(8);
        if (!TextUtils.isEmpty(cid)) {
            params.put("cid", cid);
        }
        if (!TextUtils.isEmpty(uid)) {
            params.put("uid", uid);
        }
        if (!TextUtils.isEmpty(q)) {
            params.put("q", q);
        }
        String signStr = String.format("%s%s", getSignStr(params), key);
        return doMD5Sign(signStr);
    }

    /**
     * 获得签名串
     * @param paramMap 数据map
     * @return sign结果
     */
    private static String getSignStr(Map<String, Object> paramMap) {
        SortedMap<String, String> signMap = new TreeMap<String, String>();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            signMap.put(entry.getKey(),entry.getValue().toString());
        }
        return getSignString(signMap);
    }

    /**
     * 获得签名串
     * @param signMap 数据map
     * @return sign结果
     */
    private static String getSignString(SortedMap<String, String> signMap) {
        StringBuilder sb = new StringBuilder();
        for (String key : signMap.keySet()) {
            if ((!TextUtils.isEmpty(signMap.get(key))) &&
                    (!"sign".equals(key)) && (!"aid".equals(key))) {
                String value = signMap.get(key);
                sb.append(key).append("=").append(value).append(";");
            }
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 做md5签名
     * @param targetStr 目标数据
     * @return 计算结果
     */
    private static String doMD5Sign(String targetStr) {

        byte[] md5Result = new byte[0];
        try {
            md5Result = MessageDigest.getInstance("MD5").digest(targetStr.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (md5Result.length != DEFAULT_LENGTH) {
            throw new IllegalArgumentException("MD5加密结果字节数组错误");
        }
        Integer first = Math.abs(bytesToInt(md5Result, 0));
        Integer second = Math.abs(bytesToInt(md5Result, 4));
        Integer third = Math.abs(bytesToInt(md5Result, 8));
        Integer fourth = Math.abs(bytesToInt(md5Result, 12));
        return first.toString() + second.toString() + third.toString() + fourth.toString();
    }

    /**
     * 做字节码偏移
     * @param src 源数据
     * @param offset 偏移量
     * @return 计算结果
     */
    private static int bytesToInt(byte[] src, int offset) {
        int value;
        value = ((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8) | (src[offset + 3] & 0xFF);
        return value;
    }

    /**
     * 已废弃，暂时未使用
     * @param origin 数据源
     * @return md5计算后串
     */
    @Deprecated
    public static byte[] md5EncodeByte(String origin) {
        byte[] resultString = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = md.digest(origin.getBytes(Charset.forName("utf-8")));
        } catch (Exception ignored) {
        }
        return resultString;
    }
}
