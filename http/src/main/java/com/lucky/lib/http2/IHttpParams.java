package com.lucky.lib.http2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * 作用描述: 添加参数
 *
 * @author : xmq
 * @date : 2018/10/23 下午5:04
 */
public interface IHttpParams {
    /**
     * 添加参数map ,会直接对参数赋值，而不是{@link Map#clear()} and {@link Map#putAll(Map)}
     *
     * @param maps map以Sting为key，value
     * @return LcRequest
     */
    IHttpRequest params(Map<String, Object> maps);

    /**
     * 添加参数：键值对
     *
     * @param key   key
     * @param value value
     * @return LcRequest
     */
    IHttpRequest param(String key, Object value);

    /**
     * 添加参数：对象
     * @param value 数据对象
     * @return
     */
    IHttpRequest paramObject(@NonNull Object value);

    /**
     * 添加header：键值对
     *
     * @param key   key
     * @param value value
     * @return LcRequest
     */
    IHttpRequest header(@NonNull String key, @Nullable String value);

    /**
     * 添加 header map
     *
     * @param maps map以Sting为key，value
     * @return LcRequest
     */
    IHttpRequest headers(@NonNull Map<String, String> maps);

}
