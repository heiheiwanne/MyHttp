package com.lucky.lib.http2.net.store;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lucky.lib.http2.AbstractLcRequest;
import com.lucky.lib.http2.HttpBaseResponse;
import com.lucky.lib.http2.AbstractHttpCallBack;
import com.lucky.lib.http2.HttpClient;
import com.lucky.lib.http2.HttpEventListenerImpl;
import com.lucky.lib.http2.HttpGetRequestBuilder;
import com.lucky.lib.http2.utils.HttpLog;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Http {

    public static final String CID_STORE = "*****";
    public static final String BASE_URL_STORE = "http://*****/";
    public static final String SIGN_KEY_STORE = "*****";
    public static final String SHOP_LIST = "*****";
    public static final String LOGIN = "*****";
    public static final String HOTSALE_URL = "****";
    public static final String DELIVERYMAN_LIST_URL = "*****";

    private HttpClient mHttpClient;
    private String uid;

    @Before
    public void httpStore() {
        HttpClient.Builder builder = new HttpClient.Builder();
        builder.appVersion(String.valueOf(2411))
                .cid(CID_STORE)
                .baseUrl(BASE_URL_STORE)
                .sinKey(SIGN_KEY_STORE)
                .uid("6707b168-15de-4adb-a320-8a97eb2dc3b11550476064236")
                .retryTime(2)
                .openCache(true)
                .cacheFile(new File("net_cache"))
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
//        mHttpClient.initHost(null, new HttpDnsListener() {
//            @Override
//            public void hosts(List<DnsHostBean> hostBeanList) {
//
//            }
//
//            @Override
//            public List<DnsHostBean> defaultHosts() {
//                return null;
//            }
//        });
//        for (int i = 0; i < 100; i++) {
//            login();
//        }
    }

    @Test
    public void shopList() {
        final HttpGetRequestBuilder getRequestBuilder = mHttpClient.get();
        getRequestBuilder
                .tag(new Object())
                .param("appversion", "2411")
                .url(SHOP_LIST)
                .openStrictMode(true)
                .enqueue(new AbstractHttpCallBack<SotreListResponse>() {
                    @Override
                    public void onSuccess(HttpBaseResponse<SotreListResponse> response) {
                        System.out.println();
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg, @Nullable Throwable error) {
                        System.out.println();
                    }
                });
        int size = 100;
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = 1;
        }
        Observable.fromArray(array)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        getRequestBuilder.enqueue(new AbstractHttpCallBack<SotreListResponse>() {
                            @Override
                            public void onSuccess(HttpBaseResponse<SotreListResponse> response) {

                            }

                            @Override
                            public void onFailure(int errorCode, String errorMsg, @Nullable Throwable error) {

                            }
                        });
                    }
                });

        try {
            Thread.sleep(10* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void login() {
        Map<String, Object> map = new HashMap<>();
        map.put("empLoginId", "****");
        map.put("userPass", "*****");
        map.put("origin", "2");
        map.put("deviceId", "android_87ccb2db14e56d83");
        mHttpClient.get()
                .tag(new Object())
                .params(map)
                .url(LOGIN)
                .enqueue(new AbstractHttpCallBack<SotreListResponse>() {
                    @Override
                    public void onSuccess(HttpBaseResponse<SotreListResponse> response) {
                        mHttpClient.uid(response.getUid());
                        System.out.println();
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg, @Nullable Throwable error) {
                        System.out.println();
                    }
                });
    }

    @Test
    public void hotSaleList() {
        AbstractHttpCallBack abstractHttpCallBack = new AbstractHttpCallBack(HotSaleBean.class) {
            @Override
            public void onSuccess(HttpBaseResponse response) {
                System.out.println();
            }

            @Override
            public void onFailure(int errorCode, String errorMsg, @Nullable Throwable error) {
                System.out.println();
            }
        };
        abstractHttpCallBack.setContentClazz(List.class);
        mHttpClient.get()
                .url(HOTSALE_URL)
                .param("promotion", 2)
                .enqueue(abstractHttpCallBack);
    }

    @Test
    public void getPostmanList() {
        mHttpClient.get()
                .url(DELIVERYMAN_LIST_URL)
                .enqueue(new AbstractHttpCallBack<List<DeliverymanInfo>>() {
                    @Override
                    public void onSuccess(HttpBaseResponse<List<DeliverymanInfo>> response) {

                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg, @Nullable Throwable error) {

                    }
                });
    }


    class TestInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String url = request.url().toString();
            System.out.println("url=" + url);
            Response response = null;
            if (url.contains(LOGIN)) {
                String responseString = "{\"message\":\"我是模拟的数据\"}";//模拟的错误的返回值
                response = new Response.Builder()
                        .code(400)
                        .request(request)
                        .protocol(Protocol.HTTP_1_0)
                        .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                        .addHeader("content-type", "application/json")
                        .build();
            } else {
                response = chain.proceed(request);
            }
            return response;
        }
    }

    @Test
    public void test() {
//        try {
//            Thread.sleep(6 * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
