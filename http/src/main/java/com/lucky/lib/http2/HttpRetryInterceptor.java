package com.lucky.lib.http2;

import com.lucky.lib.http2.utils.HttpLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 重试拦截器
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/2/16 下午5:11
 */
final class HttpRetryInterceptor implements Interceptor {
    /**
     *最大重试次数
     */
    private int maxRetry;
    /**
     * 假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
     */
    private int retryNum = 0;

    public HttpRetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < maxRetry) {
            retryNum++;
            HttpLog.d("retryNum=" + retryNum);
            response = chain.proceed(request);
        }
        return response;
    }
}