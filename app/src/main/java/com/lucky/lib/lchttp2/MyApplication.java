package com.lucky.lib.lchttp2;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lucky.lib.http2.HttpClient;
import com.lucky.lib.http2.HttpEventListenerImpl;
import com.lucky.lib.http2.dns.DnsHostBean;
import com.lucky.lib.http2.dns.HttpDnsListener;
import com.lucky.lib.http2.utils.HttpLog;

import java.io.File;
import java.util.List;

/**
 * @Description: TODO
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/2/20 14:01
 */
public class MyApplication extends Application {

    public static final String KEY_API = "capi";
    public static final String CAPI_DOMAIN = "******";


    public static final String CID_STORE = "****";
    public static final String BASE_URL_STORE = "http://******/";
    public static final String SIGN_KEY_STORE = "*****";
    public static final String GET_ROLLING_IMG_URL = "*****";

    public static HttpClient sHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        HttpClient.Builder builder = new HttpClient.Builder();
        builder.appVersion(String.valueOf(2411))
                .cid(CID_STORE)
                .baseUrl(BASE_URL_STORE)
                .sinKey(SIGN_KEY_STORE)
                .uid("97627b59-1b6d-497a-992f-ebcdb594a8041550304019096")
                .retryTime(2)
                .openCache(true)
                .cacheFile(new File(getCacheDir(), "net_cache"))
                .log(HttpLog.ILog.DEFAULT)
                .readTimeOutSecond(10)
                .writeTimeOutSecond(10)
                .connectionTimeOutSecond(15)
                .monitor(new HttpClient.LcMonitorListener() {
                    @Override
                    public void network(@NonNull HttpEventListenerImpl.NetWorkModel netWorkModel) {
                        Log.e("xmq", netWorkModel.toString());
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
        sHttpClient = builder.build();
        sHttpClient.initHost(this, new HttpDnsListener() {
            @Override
            public void hosts(List<DnsHostBean> hostBeanList) {

            }

            @Override
            @NonNull
            public DnsHostBean defaultApiDns() {
                return new DnsHostBean(KEY_API, CAPI_DOMAIN, "1*.**.**.2");
            }

        });
    }
}
