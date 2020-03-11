package com.lucky.lib.http2.net.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lucky.lib.http2.AbstractHttpCallBack;
import com.lucky.lib.http2.HttpBaseResponse;
import com.lucky.lib.http2.HttpClient;
import com.lucky.lib.http2.HttpEventListenerImpl;
import com.lucky.lib.http2.HttpRequestBody;
import com.lucky.lib.http2.MediaType;
import com.lucky.lib.http2.utils.HttpLog;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @Description: TODO
 * @Author: mingqiang.xu mingqiang.xu@lickincoffee.com
 * @Date: 2019-05-27 10:13
 */

public class EmployeeTest {

    /**
     * API版本
     */
    public static final String CID = "*******";
    public static final String BASE_URL = "https://******/";
    public static final String SIGN_KEY = "******";
    public static final String LOGIN = "********";

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

    private void dohttp() {
        mHttpClient.post()
                .tag(this)
                .paramObject(new StartRequest(130,122,"3ldsjlsd"))
                .param("version", 130)
                .param("deviceId", 122)
                .param("uniqueCode", "3ldsjlsd")
                .url("resource/m/sys/app/start2")
                .enqueue( new AbstractHttpCallBack<UpdateBean>() {
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
    public void upload() {
        mHttpClient.post()
                .progressListener(new HttpRequestBody.IProgressListener() {
                    @Override
                    public void onProgress(long allBytes, long singleFileBytes, long uploadBytes) {
                        System.out.println("singleFileBytes："+singleFileBytes +"   "+uploadBytes);
                    }
                })
                .paramGet("hello","world")
                .url("resource/empapi/file/upload")
                .file("file",new File("image1.png"), MediaType.MEDIA_TYPE_IMAGE)
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

    @Test
    public void login() {
        mHttpClient.get()
                .url(LOGIN)
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
}
