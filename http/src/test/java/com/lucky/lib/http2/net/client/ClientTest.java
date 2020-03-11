package com.lucky.lib.http2.net.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lucky.lib.http2.HttpEventListenerImpl;
import com.lucky.lib.http2.HttpBaseResponse;
import com.lucky.lib.http2.HttpRequestBody;
import com.lucky.lib.http2.utils.HttpLog;
import com.lucky.lib.http2.AbstractHttpCallBack;
import com.lucky.lib.http2.HttpClient;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * @Description: 客户端网络请求测试类
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/4/24 下午4:32
 */
public class ClientTest {
    /**
     * API版本
     */
    public static final String CID = "******";
    public static final String BASE_URL = "http://*****/";
    public static final String SIGN_KEY = "******";
    public static final String REGION_LIST = "*****";

    private static String uid = "";

    HttpClient mHttpClient;

    @Before
    public void before() {
        HttpClient.Builder builder = new HttpClient.Builder();
        builder
                .appVersion(String.valueOf(1000))
                .cid(CID)
                .baseUrl(BASE_URL)
                .sinKey(SIGN_KEY)
                .openCache(true)
                .cacheFile(new File("net_cache"))
                .uid(uid)
                .log(new HttpLog.ILog() {
                    @Override
                    public void d(String string) {
                        System.out.println(string);
                    }

                    @Override
                    public void e(String error) {
                        System.out.println(error);
                    }

                    @Override
                    public void wtf(Throwable tr) {
                        System.out.println(tr.getMessage());
                    }
                })
                .readTimeOutSecond(10)
                .writeTimeOutSecond(10)
                .connectionTimeOutSecond(15)
                .monitor(new HttpClient.LcMonitorListener() {
                    @Override
                    public void network(@NonNull HttpEventListenerImpl.NetWorkModel netWorkModel) {
                        System.out.println(netWorkModel.toString());
                    }

                    @Override
                    public void dns(@NonNull String domain, @NonNull String ip, @NonNull String hijackIp, @NonNull String remark) {

                    }

                    @Override
                    public void busiException(@NonNull String exceptionCode, @Nullable Throwable exceptionStack, @NonNull String remark) {

                    }

                    @Override
                    public void networkException(@NonNull String exceptionCode, @Nullable Throwable exceptionStack, @NonNull String remark) {

                    }
                });
        mHttpClient = builder.build();
        mHttpClient.sync(true);
        mHttpClient.openNightMareMode(true);
    }

    @Test
    public void start() {
        dohttp();
    }

    @Test
    public void upload() {
        mHttpClient.post()
                .url("resource/m/order/comment/add")
                .progressListener(new HttpRequestBody.IProgressListener() {
                    @Override
                    public void onProgress(long allBytes, long singleFileBytes, long uploadBytes) {
                        System.out.println("allBytes:"+ allBytes + "  singleFileBytes:"+singleFileBytes + "  uploadBytes:"+ uploadBytes);
                    }
                })
                .file("img1", new File("image1.png"), MediaType.parse("image/*"))
                .file("img2", new File("image2.png"), MediaType.parse("image/*"))
//                .file("img3", new File("image3.png"), MediaType.parse("image/*"))
                .enqueue(new AbstractHttpCallBack<Object>() {
                    @Override
                    public void onSuccess(HttpBaseResponse<Object> response) {
                        System.out.println(response.getBusiCode());
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg, @Nullable Throwable error) {
                        System.out.println(errorMsg);
                    }
                });
    }

    private void dohttp() {
        mHttpClient.post()
                .tag(this)
                .paramObject(new StartRequest(130, 122, "*****"))
                .param("version", 130)
                .param("deviceId", 122)
                .param("uniqueCode", "*****")
                .url("*******")
                .enqueue(new AbstractHttpCallBack<UpdateBean>() {
                    @Override
                    public void onSuccess(HttpBaseResponse<UpdateBean> response) {
                        System.out.println(response.getBusiCode());
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg, @Nullable Throwable error) {
                        System.out.println(errorMsg);
                    }
                });
    }

    @Test
    public void getRigonlist() {
        mHttpClient.get()
                .url(REGION_LIST)
                .enqueue(new AbstractHttpCallBack<RegionListBean>() {
                    @Override
                    public void onSuccess(HttpBaseResponse<RegionListBean> response) {
                        System.out.println("xmq");
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg, @Nullable Throwable error) {
                        System.out.println("xmq");
                    }
                });

    }


    @org.junit.Test
    public void getAsync() {
        String url = "http://wwww.baidu.com";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });

        sleep();
    }

    @org.junit.Test
    public void getSync() {
        String url = "http://wwww.baidu.com";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @org.junit.Test
    public void postStringAsync() {
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        String requestBody = "I am Jdqm.";
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(mediaType, requestBody))
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    System.out.println(headers.name(i) + ":" + headers.value(i));
                }
                System.out.println("onResponse: " + response.body().string());
            }
        });
        sleep();
    }

    @org.junit.Test
    public void postStream() {
        RequestBody requestBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MediaType.parse("text/x-markdown; charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("I am Jdqm.");
            }
        };

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    System.out.println(headers.name(i) + ":" + headers.value(i));
                }
                System.out.println("onResponse: " + response.body().string());
            }
        });
        sleep();
    }

    @org.junit.Test
    public void postFile() {
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        File file = new File("test.md");
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(mediaType, file))
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    System.out.println(headers.name(i) + ":" + headers.value(i));
                }
                System.out.println("onResponse: " + response.body().string());
            }
        });

        sleep();
    }

    @org.junit.Test
    public void postForm() {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    System.out.println(headers.name(i) + ":" + headers.value(i));
                }
                System.out.println("onResponse: " + response.body().string());
            }
        });
        sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(6 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
