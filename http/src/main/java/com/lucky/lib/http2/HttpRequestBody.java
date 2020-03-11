package com.lucky.lib.http2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import static com.lucky.lib.http2.MediaType.MEDIA_TYPE_JSON;

/**
 * 作用描述: post 提交时使用的requestBody
 * 用户端使用MediaType为application/x-www-form-urlencoded  跟 multipart/form-data;两种提交格式
 * 其中multipart/form-data; 中主要使用image/*
 * <p/>
 * 对应的两个方法为:：
 * application/x-www-form-urlencoded ： {@link #createWithFormEncode(Map)}
 * multipart/form-data; : {@link #createWithMultiForm(Map, String, File, MediaType, IProgressListener)} {@link #createWithMultiForm(Map, Map, Map, IProgressListener)}
 * <p/>
 *
 * @author : xmq
 * @date : 2018/10/24 下午2:25
 */
public final class HttpRequestBody {

    private RequestBody mRequestBody;

    public HttpRequestBody(RequestBody requestBody) {
        this.mRequestBody = requestBody;
    }

    public RequestBody getRequestBody() {
        return this.mRequestBody;
    }

    /**
     * 填充String内容，创建 requestBody
     *
     * @param content string
     * @return LcRequestBody
     */
    public static HttpRequestBody createWithString(String content) {
        HttpRequestBody requestBody = new HttpRequestBody(RequestBody.create(com.lucky.lib.http2.MediaType.MEDIA_TYPE_MARKDOWN, content));
        return requestBody;
    }

    /**
     * 填充json内容，创建requestBody
     * <pre>
     *      POST http://www.example.com HTTP/1.1
     *      Content-Type: application/json;charset=utf-8
     *      {"title":"test","sub":[1,2,3]}
     * </pre>
     *
     * @param json json 字符串
     * @return LcRequestBody
     */
    public static HttpRequestBody createWithJson(String json) {
        HttpRequestBody requestBody = new HttpRequestBody(RequestBody.create(MEDIA_TYPE_JSON, json));
        return requestBody;
    }

    /**
     * 表单提交 ：k,v 的方式提交
     * <pre>
     *     POST http://www.example.com HTTP/1.1
     *     Content-Type: application/x-www-form-urlencoded;charset=utf-8
     *     title=test&sub%5B%5D=1&sub%5B%5D=2&sub%5B%5D=3
     * <pre/>
     * @param kv
     * @return
     */
    public static HttpRequestBody createWithFormEncode(Map<String, String> kv) {
        if (kv != null && !kv.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();

            for (Map.Entry<String, String> entry : kv.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }

            return new HttpRequestBody(builder.build());
        } else {
            throw new IllegalArgumentException("kv is empty!");
        }
    }

    /**
     * 分块上传 k，v
     *
     * @param kv 键值对map
     * @return
     */
    public static HttpRequestBody createWithMultiForm(Map<String, String> kv) {
        return createWithMultiForm(kv, (String) null, (File) null, (MediaType) null,null);
    }

    /**
     * 分块上传，一般用于文件上传
     * <pre>
     *     POST http://www.example.com HTTP/1.1
     *     Content-Type:multipart/form-data; boundary=----WebKitFormBoundaryrGKCBY7qhFd3TrwA
     *     ------WebKitFormBoundaryrGKCBY7qhFd3TrwA 分界线
     *     Content-Disposition: form-data; name="text" 第一块内容
     *     title
     *     ------WebKitFormBoundaryrGKCBY7qhFd3TrwA 第二块内容
     *     Content-Disposition: form-data; name="file"; filename="chrome.png"
     *     Content-Type: image/png
     *     PNG ... content of chrome.png ...
     *     ------WebKitFormBoundaryrGKCBY7qhFd3TrwA--
     * </pre>
     *
     * @return
     */
    public static HttpRequestBody createWithMultiForm(Map<String, String> kv, String fileKey, File file, MediaType fileType, IProgressListener progressListener) {
        Map<String, File> map = new HashMap<>(2);
        map.put(fileKey,file);
        Map<String, MediaType> mediaTypes  = new HashMap<>(2);
        mediaTypes.put(fileKey,fileType);
        return createWithMultiForm(kv,map,mediaTypes,progressListener);
    }

    /**
     * 上传多文件
     *
     * @param kv         kv
     * @param files      files
     * @param mediaTypes mediaTypes
     * @return LcRequestBody
     */
    public static HttpRequestBody createWithMultiForm(@Nullable Map<String, String> kv, Map<String, File> files, Map<String, MediaType> mediaTypes, final IProgressListener progressListener) {
        okhttp3.MultipartBody.Builder builder = new okhttp3.MultipartBody.Builder();
        if (kv != null && !kv.isEmpty()) {
            for (Map.Entry<String, String> entry : kv.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        final long[] fileBytes = {0,0};
        if (files != null && !files.isEmpty()) {
            for (Map.Entry<String, File> fileEntry : files.entrySet()) {
                File file = fileEntry.getValue();
                fileBytes[0] += file.length();
                RequestBody body = createCustomRequestBody(mediaTypes.get(fileEntry.getKey()), file, new ProgressListener() {
                    @Override
                    public void onProgress(long totalBytes, long uploadBytes, boolean done) {
                        if (progressListener !=null) {
                            progressListener.onProgress(fileBytes[0],totalBytes,fileBytes[1]+ uploadBytes);
                        }
                        if (done) {
                            fileBytes[1] += totalBytes;
                        }
                    }
                });
                builder.addFormDataPart(fileEntry.getKey(), file.getName(), body);
            }
        }

        builder.setType(MultipartBody.FORM);
        return new HttpRequestBody(builder.build());
    }

    public static RequestBody createCustomRequestBody(final MediaType contentType, final File file, final ProgressListener listener) {
        return new RequestBody() {
            @Override public MediaType contentType() {
                return contentType;
            }

            @Override public long contentLength() {
                return file.length();
            }

            @Override public void writeTo(@NonNull BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    int twoKb = 2048;
                    long alReadCount=0;
                    for (long readCount; (readCount = source.read(buf, twoKb)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(),  alReadCount+=readCount, alReadCount == contentLength());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private interface ProgressListener {
        /**
         * 进度回调
         * @param singleFileBytes 当前文件的大小
         * @param uploadBytes 剩余文件大小
         * @param done 是否下载完成 true:完成 false:未完成
         */
        void onProgress(long singleFileBytes, long uploadBytes, boolean done);
    }

    public interface IProgressListener {
        /**
         * 进度回调
         * @param allBytes 所有文件的大小
         * @param singleFileBytes 当前文件的大小
         * @param uploadBytes 剩余文件大小
         */
        void onProgress(long allBytes,long singleFileBytes, long uploadBytes);
    }


}
