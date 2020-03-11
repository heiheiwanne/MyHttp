package com.lucky.lib.http2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import okhttp3.Headers;
import okhttp3.OkHttpClient;


/**
 * @Description: request 请求类
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/2/20 下午3:27
 */
public interface IHttpRequest {

    /**
     * 发起网络请求 ,异步执行
     *
     * @param callBack 网络回调
     * @param <E>      解析的content的类或者ArrayList的范型
     */
    <E> void enqueue(@Nullable AbstractHttpCallBack<E> callBack);

    /**
     * 发起网络请求，同步执行
     *
     * @param lcNetCallBack 网络回调
     * @return Response
     */
    <E> void execute(@Nullable final AbstractHttpCallBack<E> lcNetCallBack);

    /**
     * 网络请求的tag，用于cancel
     *
     * @return Object
     */
    Object tag();

    /**
     * 网络请求的tag
     *
     * @param t 网络请求中的tag，可以以此tag取消网络请求
     * @return HttpRequest
     */
    IHttpRequest tag(@NonNull Object t);

    /**
     * 网络请求的基础url，不设置此值时将使用{@link HttpClient#baseUrl()}
     * @return url
     */
    String baseUrl();

    /**
     * 设置baseUrl 不设置此值时将使用{@link HttpClient#baseUrl()}
     * @param baseUrl baseUrl eg: http://capi.luckincoffee.com/
     * @return HttpRequest
     */
    IHttpRequest baseUrl(@NonNull String baseUrl);
    /**
     * 网络请求的url
     *
     * @param url url
     * @return HttpRequest
     */
    IHttpRequest url(@NonNull String url);

    /**
     * 网络请求的url
     *
     * @return 网络请求的url
     */
    String url();

    /**
     * 缓存时间设置
     *
     * @param level {@link HttpCacheLevel}
     * @return HttpRequest
     */
    IHttpRequest cache(@NonNull HttpCacheLevel level);

    /**
     * 获取cache
     *
     * @return HttpCacheLevel
     */
    @NonNull
    HttpCacheLevel cache();

    /**
     * 获取headers 请求头
     *
     * @return Headers
     */
    @Nullable
    Headers headers();

    /**
     * 是否加入请求池中
     *
     * @param strictMode true：加入  false：不加入
     * @return 当前请求request
     */
    IHttpRequest openStrictMode(boolean strictMode);

    /**
     * 设置当前请求自己的client，使用之后连接池、并发数将无限制。注意！！！此方法慎用。
     *
     * @param okHttpClient {@link OkHttpClient}
     */
    IHttpRequest selfClient(OkHttpClient okHttpClient);

}
