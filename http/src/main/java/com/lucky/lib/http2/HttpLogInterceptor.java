/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lucky.lib.http2;



import com.lucky.lib.http2.utils.HttpJsonUtil;
import com.lucky.lib.http2.utils.HttpLog;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * 作用描述: 日志拦截器，显示请求和响应的数据
 * @author : xmq
 * @date : 2018/10/25 下午4:00
 */
public class HttpLogInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String ERROR = "level is null. Use Level.NONE instead.";
    private static final String POST ="POST";
    private static final String GET ="GET";
    private static final String AND ="&";
    private static final String EQUAL ="=";
    private static final String Q= "q";
    private static final String TAG = "{\"";


    public enum Level {
        /**
         * 不打印日志
         */
        NONE,
        /**
         * 打印全部日志
         */
        BODY
    }


    public interface Logger {
        /**
         * 日志打印
         * @param message
         */
        void log(String message);

        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = new Logger() {
            @Override
            public void log(String message) {
                Platform.get().log(INFO, message, null);
            }
        };
    }

    public HttpLogInterceptor() {
        this(Logger.DEFAULT);
    }

    public HttpLogInterceptor(Logger logger) {
        this.logger = logger;
    }

    private final Logger logger;

    private volatile Level level = Level.BODY;

    /**
     * Change the level at which this interceptor logs.
     */
    public HttpLogInterceptor setLevel(Level level) {
        if (level == null) { throw new NullPointerException(ERROR);}
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;

        Request request = chain.request();

        if (level == Level.NONE) {
            return chain.proceed(request);
        }
        StringBuilder builder = new StringBuilder();

        deal(request, builder);

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            builder.append("-- 请求失败: ").append(e).append("\n");
            HttpLog.d(builder.toString());
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        builder.append("响应--时长：").append(tookMs).append("ms--").append("\n");


        BufferedSource source = responseBody.source();
        try {
            // Buffer the entire body.
            source.request(Long.MAX_VALUE);
        }catch (Exception e) {
            builder.append("Transfer-Encoding:chunked ,无法输出请求结果日志");
            HttpLog.d(builder.toString());
            HttpLog.wtf(e);
            return response;
        }

        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                builder.append("返回数据编码异常" + "\n");
                HttpLog.d(builder.toString());
                return response;
            }
        }
        if (contentLength != 0) {
            String responsStr = buffer.clone().readString(charset);

            //当放回的是明文时,就不解密
            if (responsStr.contains(TAG)) {

            } else {
                responsStr = HttpAesCrypto.decrypt(responsStr);
            }

            String format = HttpJsonUtil.formatJson(responsStr);
            builder.append(format).append("\n");
        }

        HttpLog.d(builder.toString());
        return response;
    }

    private void deal(Request request, StringBuilder builder) {
        StringBuilder postString = new StringBuilder();
        String postParams = "";
        if (POST.equals(request.method())) {
            try {
                RequestBody body = request.body();
                if (body instanceof FormBody) {
                    FormBody formBody = (FormBody) body;
                    for (int i = 0; i < formBody.size(); i++) {
                        postString.append(formBody.encodedName(i)).append("=").append(formBody.encodedValue(i));
                        if (i < (formBody.size() - 1)) {
                            postString.append("&");
                        }
                        if ("q".equals(formBody.encodedName(i))) {
                            postParams = getRequestPostParams(formBody.encodedValue(i));
                        }
                    }
                } else if (body instanceof MultipartBody) {
                    String urlT = request.url().toString();
                    if (urlT.indexOf(Q+EQUAL) > 0 && urlT.indexOf(AND) > 0) {
                        postParams = request.url().toString().split("q=")[1].split("&")[0];
                    }
                }
            } catch (Exception e) {
                HttpLog.d(e.toString());
            }


            String requestStartMessage = request.method() + "请求：" + request.url() + "?" + postString;
            builder.append(requestStartMessage).append("\n");
            builder.append("POST请求参数：").append(postParams).append("\n").append("\n");
            builder.append("GET请求参数：").append(getRequestParams(request.url().toString())).append("\n").append("\n");

        } else {
            String requestStartMessage = request.method() + "请求：" + request.url();
            builder.append(requestStartMessage).append("\n");
            builder.append("GET请求参数：").append(getRequestParams(request.url().toString())).append("\n").append("\n");
        }
    }


    private String getRequestParams(String url) {
        try {
            String params = url.substring(url.indexOf("?") + 1);
            if (params ==null || params.length() ==0) {
                return "";
            }
            for (String param : params.split(AND)) {
                if (param.startsWith(Q)) {
                    String content = param.substring(2);
                    content = URLDecoder.decode(content, "utf-8");
                    return HttpAesCrypto.decrypt(content);
                }
            }
        } catch (Exception e) {
            HttpLog.d("获取请求参数失败");
        }
        return "";
    }

    private String getRequestPostParams(String content) {
        try {
            content = URLDecoder.decode(content, "utf-8");
            return HttpAesCrypto.decrypt(content);
        } catch (Exception e) {
            HttpLog.d("获取请求参数失败");
        }
        return "";
    }

}
