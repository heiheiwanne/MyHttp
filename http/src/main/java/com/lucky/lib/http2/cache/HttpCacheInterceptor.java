package com.lucky.lib.http2.cache;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lucky.lib.cache.disk.DiskLruCache;
import com.lucky.lib.http2.HttpCacheLevel;
import com.lucky.lib.http2.utils.HttpLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @author xmq
 * 缓存拦截器
 */
@Deprecated
public class HttpCacheInterceptor implements Interceptor {

    /**
     * 网络缓存大小 default: 10M
     */
    private static final int MAX_SIZE = 10 * 1024 * 1024;
    private static final String Q = "q";
    private static final String UID = "uid";
    private static final String CACHE_LEVEL = "level";
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String UTF8_STR = "UTF-8";
    private static final Charset UTF8 = Charset.forName(UTF8_STR);
    private static final String EQUAL = "=";
    private static final String AND = "&";

    /**
     * 缓存json key
     */
    private static final String MEDIA_TYPE = "media_type";
    private static final String BODY = "body";
    private static final String PROTOCOL = "protocol";
    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String TIME = "time";
    private static final String URL = "url";

    /**
     * 是否启用缓存
     */
    private boolean openCache;
    private DiskLruCache mDiskLruCache;
    private File cacheFile;
    private int appVersion;

    public HttpCacheInterceptor(boolean openCache, File cacheFile, int appVersion) {
        this.cacheFile = cacheFile;
        this.appVersion = appVersion;
        setOpenCache(openCache);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        //关闭缓存
        if (!openCache) {
            return chain.proceed(request);
        }

        StringBuilder key = new StringBuilder();
        String url = request.url().toString();
        String levelHeader = request.header(CACHE_LEVEL);
        if (TextUtils.isEmpty(levelHeader)) {
            return chain.proceed(request);
        }
        request = request.newBuilder().removeHeader(CACHE_LEVEL).build();
        long level = HttpCacheLevel.NO_LOAD_NO_CACHE.time;
        try {
            level = Long.parseLong(levelHeader);
        } catch (NumberFormatException e) {
            HttpLog.wtf(e);
        }
        key.append(URLEncoder.encode(url, UTF8_STR));

        dealMethod(request, key, url);
        HttpLog.d("level: " + level);

        HttpDiskLruCacheManager diskLruCacheManager = new HttpDiskLruCacheManager(mDiskLruCache, key.toString());
        String asString = diskLruCacheManager.get();

        if (!TextUtils.isEmpty(asString) && level > HttpCacheLevel.NO_LOAD_CACHE.time) {
            try {
                JSONObject jsonObject = new JSONObject(asString);
                long aLong = System.currentTimeMillis() - jsonObject.getLong(TIME);
                HttpLog.d("time_sub:" + aLong);
                //缓存时间 < 当前时间 && 当前时间 - 缓存时间 >= 设置的缓存时间
                if (aLong >= 0 && aLong <= level) {
                    MediaType mediaType = MediaType.parse(jsonObject.getString(MEDIA_TYPE));
                    String content = jsonObject.getString(BODY);
                    ResponseBody responseBody = ResponseBody.create(mediaType, content);
                    Response.Builder builder = new Response.Builder().body(responseBody);
                    builder.protocol(Protocol.get(jsonObject.getString(PROTOCOL)));
                    builder.code(jsonObject.getInt(CODE));
                    builder.message(jsonObject.getString(MESSAGE));
                    builder.request(request);
                    return builder.build();
                }
            } catch (JSONException e) {
                HttpLog.wtf(e);
            }
        }

        //do proceed
        Response response = chain.proceed(request);

        if (level > HttpCacheLevel.NO_LOAD_NO_CACHE.time) {
            cacheData(diskLruCacheManager, response,url);
        }
        return response;
    }

    private void dealMethod(Request request, StringBuilder key, String url) {
        if (POST.equals(request.method())) {
            try {
                RequestBody body = request.body();
                if (body instanceof FormBody) {
                    FormBody formBody = (FormBody) body;
                    for (int size = formBody.size() - 1; size >= 0; size--) {
                        String name = formBody.encodedName(size);
                        if (Q.equals(name)
                                || UID.equals(name)) {
                            key.append(formBody.encodedValue(size));
                        }
                    }

                } else if (body instanceof MultipartBody) {
                    String q = Q + EQUAL;
                    if (url.indexOf(q) > 0 && url.indexOf(AND) > 0) {
                        key.append(request.url().toString().split(q)[1].split(AND)[0]);
                    }
                    String uid = UID + EQUAL;
                    if (url.indexOf(uid) > 0 && url.indexOf(AND) > 0) {
                        key.append(request.url().toString().split(uid)[1].split(AND)[0]);
                    }
                }
            } catch (Exception e) {
                HttpLog.d(e.toString());
            }
        } else if (GET.equals(request.method())) {
            try {
                String params = url.substring(url.indexOf("?") + 1);
                for (String param : params.split(AND)) {
                    if (param.startsWith(Q)) {
                        key.append(param.substring(2));
                    }
                    if (param.startsWith(UID)) {
                        key.append(param.substring(4));
                    }
                }
            } catch (Exception e) {
                HttpLog.d("获取请求参数失败");
            }
        }
    }

    public void setOpenCache(boolean openCache) {
        if (this.openCache == openCache) {
            return;
        }
        this.openCache = openCache;
        try {
            if (openCache) {
                //增加缓存拦截器
                if (!cacheFile.exists()) {
                    boolean mkdirs = cacheFile.mkdirs();
                }
                mDiskLruCache = DiskLruCache.open(cacheFile, appVersion, 1, MAX_SIZE);
            } else {
                if (mDiskLruCache !=null) {
                    mDiskLruCache.close();
                }
            }
        } catch (Exception e) {
            HttpLog.wtf(e);
        }
    }

    public void deleteCache() {
        openCache = false;
        if (mDiskLruCache !=null) {
            try {
                mDiskLruCache.delete();
            } catch (IOException e) {
                HttpLog.wtf(e);
            }
            mDiskLruCache =null;
        }
    }

    /**
     * 缓存数据
     */
    private void cacheData(HttpDiskLruCacheManager diskLruCacheManager, Response response, String url) throws IOException {
        JSONObject json = new JSONObject();
        //网络不成功  ||  body 为null  || response.body().contentType()  为null
        if (!response.isSuccessful() || response.body() == null || response.body().contentType() ==null) {
           return;
        }
        try {
            json.put(PROTOCOL, response.protocol().toString());
            json.put(CODE, response.code());

            json.put(MEDIA_TYPE, response.body().contentType().toString());
            json.put(BODY, getBody(response));
            json.put(MESSAGE, response.message());
            json.put(TIME, System.currentTimeMillis());
            json.put(URL,url);
            diskLruCacheManager.put(json);
        } catch (JSONException e) {
            HttpLog.wtf(e);
        }
    }

    /**
     * 读取网络结果
     */
    private String getBody(Response response) {
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        BufferedSource source = body.source();
        try {
            source.request(Long.MAX_VALUE);
        } catch (IOException e) {
            HttpLog.wtf(e);
        }
        Buffer buffer = source.buffer();
        Charset charset;
        MediaType contentType = body.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                HttpLog.wtf(e);
                return null;
            }
            return buffer.clone().readString(charset);
        }
        return null;
    }

}
