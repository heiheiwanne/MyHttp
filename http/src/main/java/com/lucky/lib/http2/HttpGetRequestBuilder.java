package com.lucky.lib.http2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * @Description:  Get请求的处理类
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/4/24 上午11:29
 */
public class HttpGetRequestBuilder extends AbstractLcRequest {

    /**
     * Get请求的处理
     * @param httpClient 网络请求client
     */
    public HttpGetRequestBuilder(@NonNull HttpClient httpClient) {
        super(httpClient);
    }

    @Override
    protected Request getRequest() {
        return newGetRequest();
    }

    /**
     * 组装get request
     * @return 请求request
     */
    @SuppressWarnings("unchecked")
    private Request newGetRequest() {
        Request.Builder builder = new Request.Builder();
        StringBuilder url = getStringUrl();
        builder.url(url.toString());
        Headers head = headers();
        if (head != null) {
            builder.headers(head);
        }
        //增加网络请求头的event_id
        String time = String.valueOf(System.currentTimeMillis());
        builder.header(EVENT_ID,time);
        builder.tag(tag());
        builder.get();
        return builder.build();
    }

    /**
     * 获取get请求的url
     * @return get请求的url
     */
    @NonNull
    private StringBuilder getStringUrl() {
        StringBuilder url = new StringBuilder(baseUrl() + url());
        url.append(PARAMS);
        Map<String, String> requestParams = getRequestParams(params());
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            if (!TextUtils.isEmpty(entry.getKey()) && !TextUtils.isEmpty(entry.getValue())) {
                url.append(entry.getKey()).append(EQ).append(entry.getValue()).append(AND);
            }
        }
        url.deleteCharAt(url.length() - 1);
        return url;
    }

    @Override
    public <T> void enqueue(@Nullable AbstractHttpCallBack<T> lcNetCallBack) {
        super.enqueue(lcNetCallBack);
    }

    @Override
    public HttpGetRequestBuilder tag(@NonNull Object o) {
        return (HttpGetRequestBuilder) super.tag(o);
    }

    @Override
    public HttpGetRequestBuilder param(@NonNull String key, @Nullable Object value) {
        return (HttpGetRequestBuilder) super.param(key, value);
    }

    @Override
    public HttpGetRequestBuilder params(@NonNull Map<String, Object> maps) {
        return (HttpGetRequestBuilder) super.params(maps);
    }

    @Override
    public HttpGetRequestBuilder paramObject(@NonNull Object value) {
        return (HttpGetRequestBuilder) super.paramObject(value);
    }

    @Override
    public HttpGetRequestBuilder url(@NonNull String url) {
        return (HttpGetRequestBuilder) super.url(url);
    }

    @Override
    public HttpGetRequestBuilder cache(@NonNull HttpCacheLevel level) {
        return (HttpGetRequestBuilder) super.cache(level);
    }

    @Override
    public HttpGetRequestBuilder header(@NonNull String key, String value) {
        return (HttpGetRequestBuilder) super.header(key, value);
    }

}
