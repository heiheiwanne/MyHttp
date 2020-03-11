package com.lucky.lib.http2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;

/**
 * 作用描述: post 请求类
 *
 * @author : xmq
 * @date : 2018/10/23 下午4:32
 */
public class HttpPostRequestBuilder extends AbstractLcRequest {

    private Map<String, File> files;
    private Map<String, MediaType> mediaTypes;
    private JSONObject paramsGet;
    private HttpRequestBody.IProgressListener progressListener;


    public HttpPostRequestBuilder(HttpClient httpClient) {
        super(httpClient);
    }

    @Override
    protected Request getRequest() {
        return newPostRequest();
    }

    /**
     * 填充file参数
     *
     * @param key  key
     * @param file file
     * @return LcPostRequestBuilder
     */
    public HttpPostRequestBuilder file(String key, File file, MediaType mediaType) {
        if (files == null) {
            files = new HashMap<>(2);
        }
        if (mediaTypes == null) {
            mediaTypes = new HashMap<>(2);
        }
        files.put(key, file);
        mediaTypes.put(key, mediaType);
        return this;
    }

    public HttpPostRequestBuilder progressListener(HttpRequestBody.IProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    /**
     * 构建request
     *
     * @return request
     */
    @SuppressWarnings("unchecked")
    private Request newPostRequest() {
        Request.Builder builder = new Request.Builder();
        builder.url(getStringUrl());
        Headers head = headers();
        if (head != null) {
            builder.headers(head);
        }
        HttpRequestBody requestBody;

        Map<String, String> map = null;
        //有get参数 ，无post参数时，只在get参数上增加uid /cid 等公共参数， post不增加公共参数
        if (paramsGet == null || paramsGet.isEmpty() || !params().isEmpty()) {
            map = getRequestParams(params());
        }
        if (files != null && !files.isEmpty()) {
            requestBody = HttpRequestBody.createWithMultiForm(map, files, mediaTypes,progressListener);
        } else {
            requestBody = HttpRequestBody.createWithFormEncode(map);
        }
        //增加网络请求头的event_id
        String time = String.valueOf(System.currentTimeMillis());
        builder.header(EVENT_ID,time);
        builder.tag(tag());
        builder.post(requestBody.getRequestBody());
        return builder.build();
    }

    @NonNull
    private String getStringUrl() {
        StringBuilder urlSB;
        //单独处理一下dns请求的情况
        urlSB = new StringBuilder(baseUrl() + url());
        if (paramsGet == null || paramsGet.isEmpty()) {
            return urlSB.toString();
        }
        urlSB.append("?");
        Map<String, String> requestParams = getRequestParams(paramsGet);
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            if (!TextUtils.isEmpty(entry.getKey()) && !TextUtils.isEmpty(entry.getValue())) {
                urlSB.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        urlSB.deleteCharAt(urlSB.length() - 1);
        return urlSB.toString();
    }


    /**
     * 针对post的请求兼容Get的url参数
     *
     * @param key   键
     * @param value 数据
     * @return 当前类
     */
    public HttpPostRequestBuilder paramGet(@NonNull String key, @Nullable Object value) {
        if (key == null || value == null) {
            return this;
        }
        if (paramsGet == null) {
            paramsGet = new JSONObject();
        }
        paramsGet.put(key, value);
        return this;
    }

    /**
     * 针对post的请求兼容Get的url参数
     *
     * @param maps 数据集
     * @return 当前类
     */
    public HttpPostRequestBuilder paramsGet(@NonNull Map<String, Object> maps) {
        if (maps == null || maps.isEmpty()) {
            return this;
        }
        if (paramsGet == null) {
            paramsGet = new JSONObject();
        }
        paramsGet.putAll(maps);
        return this;
    }

    /**
     * 针对post的请求兼容Get的url参数
     *
     * @param value 数据集
     * @return 当前类
     */
    @SuppressWarnings("unchecked")
    public HttpPostRequestBuilder paramObjectGet(@NonNull Object value) {
        if (value == null) {
            return this;
        }
        if (paramsGet == null) {
            paramsGet = new JSONObject();
        }
        paramsGet.putAll((Map<? extends String, ? extends Object>) JSON.toJSON(value));
        return this;
    }

    /**
     * ====================为了外部方便调用重写下列方法======================
     */
    @Override
    public <T> void enqueue(@Nullable AbstractHttpCallBack<T> lcNetCallBack) {
        super.enqueue(lcNetCallBack);
    }

    @Override
    public HttpPostRequestBuilder tag(@NonNull Object o) {
        return (HttpPostRequestBuilder) super.tag(o);
    }

    @Override
    public HttpPostRequestBuilder param(@NonNull String key, @Nullable Object value) {
        return (HttpPostRequestBuilder) super.param(key, value);
    }

    @Override
    public HttpPostRequestBuilder params(@NonNull Map<String, Object> maps) {
        return (HttpPostRequestBuilder) super.params(maps);
    }

    @Override
    public HttpPostRequestBuilder paramObject(@NonNull Object value) {
        return (HttpPostRequestBuilder) super.paramObject(value);
    }

    @Override
    public HttpPostRequestBuilder url(@NonNull String url) {
        return (HttpPostRequestBuilder) super.url(url);
    }

    @Override
    public HttpPostRequestBuilder cache(@NonNull HttpCacheLevel level) {
        return (HttpPostRequestBuilder) super.cache(level);
    }

    @Override
    public HttpPostRequestBuilder header(@NonNull String key, String value) {
        return (HttpPostRequestBuilder) super.header(key, value);
    }
    /**
     * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^为了外部方便调用重写上面方法^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     */
}
