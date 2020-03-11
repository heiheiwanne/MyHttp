package com.lucky.lib.lchttp2;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lucky.lib.http2.HttpEventListenerImpl;
import com.lucky.lib.http2.HttpClient;
import com.lucky.lib.http2.dns.DnsHostBean;
import com.lucky.lib.http2.dns.HttpDnsListener;
import com.lucky.lib.http2.utils.HttpLog;

import java.io.File;
import java.util.List;
/**
 * 作用描述: main
 * @author : xmq
 * @date : 2018/11/1 下午1:46
 */
public class MainActivity extends Activity {

    public static final String CID = "*****";
    public static final String BASE_URL = "https://*****/";
    public static final String SIGN_KEY = "*****";

    public static final String SHOP_LIST = "resource/s/ehr/shopList";
    private HttpClient mHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        httpClientInit();
        int max = 1000;
        for (int i = 0; i < max; i++) {
            if (i%3==0) {
                mHttpClient.uid("0f1912df-a4ca-4028-90d6-c3841e4c864b1557198463802");
            }
            if (i%6 ==0) {
                mHttpClient.uid("");
            }
            doClient();
        }
//        httpStore();
    }


    public void httpStore() {

        MyApplication.sHttpClient.post()
                .tag(new Object())
                .param("appversion", "2411")
                .url(SHOP_LIST)
                .enqueue(new AbstractNetCallBack<SotreListResponse>() {
                    @Override
                    public void success(SotreListResponse sotreListResponse) {
                        Log.e("xmq",sotreListResponse.cityList.toString());
                    }

                    @Override
                    public void fail(int errorCode, String errorMsg, String busiCode, @Nullable Throwable error) {
                        Log.e("xmq",errorMsg);
                    }
                });
    }


    public void doClient() {
        mHttpClient.post()
                .tag(new Object())
                .param("version", "130")
                .param("deviceId", "122")
                .param("uniqueCode", "3ldsjlsd")
                .url("resource/m/sys/app/start2")
                .enqueue(new AbstractNetCallBack<UpdateBean>() {
                    @Override
                    public void success(UpdateBean o) {
                        Log.d("net", o.msg);
                    }

                    @Override
                    public void fail(int errorCode, String errorMsg, String busiCode, @Nullable Throwable error) {
                        Log.e("error", errorMsg);
                    }
                });
    }
    public void httpClientInit() {
        HttpClient.Builder builder = new HttpClient.Builder();
        builder.appVersion(String.valueOf(1000))
                .cid(CID)
//                .baseUrl("http://wwww.baidu.com")
                .baseUrl( BASE_URL)
                .sinKey(SIGN_KEY)
                .uid("")
                .retryTime(2)
                .openCache(true)
                .cacheFile(new File(getCacheDir(), "net_cache"))
                .log(new HttpLog.ILog() {
                    @Override
                    public void d(String string) {
                        Log.d("net", string);
                    }

                    @Override
                    public void e(String error) {

                    }

                    @Override
                    public void wtf(Throwable tr) {
                        Log.wtf("net", tr);
                    }
                })
                .readTimeOutSecond(10)
                .writeTimeOutSecond(10)
                .connectionTimeOutSecond(15)
                .monitor(new HttpClient.LcMonitorListener() {
                    @Override
                    public void network(@NonNull HttpEventListenerImpl.NetWorkModel netWorkModel) {

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
    }
}
