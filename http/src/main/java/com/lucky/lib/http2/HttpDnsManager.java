package com.lucky.lib.http2;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lucky.lib.http2.dns.DnsHostBean;
import com.lucky.lib.http2.dns.HttpDnsListener;
import com.lucky.utils.app.SharedUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.OkHttpClient;

/**
 * @Description: DNS防劫持 Manager
 * <p/>
 * 获取域名与IP对应mapping， 进行网络访问时，替换原有域名为IP
 * 接入时：
 * 1.调用{@link #initHosts(Context, HttpDnsListener, HttpClient)} 并实现HttpDnsI
 * 2.在{@link HttpDnsListener#hosts(List)}方法中，判断{KEY_API}变化时通知主页发生改变
 * <p/>
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/4/24 上午11:26
 */
public final class HttpDnsManager {

    /**
     * 集合初始化大小
     */
    private static final int INIT_SIZE = 8;
    /**
     * 数据缓存sp的key
     */
    private static final String SP_KEY_ENABLE = "SP_KEY_DNS_ENABLE";
    /**
     * 是否开启DNS
     */
    private static boolean dnsEnable;
    /**
     * DNS请求重试次数
     */
    private static final int RETRY_MAX = 1;
    /**
     * host 接口请求地址
     */
    private static final String HOST_URL = "resource/m/sys/base/hosts";
    /**
     * dns 的包含对象
     */
    private static DnsHostBean defaultApiDns;
    /**
     * host列表
     */
    private static ConcurrentHashMap<String, DnsHostBean> sHostBeanMap;
    /**
     * dns监听
     */
    private static HttpDnsListener sListener;
    /**
     * 上下文，这里是application
     */
    private static Context sContext;
    /**
     * dns请求体，防止多次调用出现问题
     */
    private static AbstractLcRequest sDnsRequest;

    /**
     * DNS劫持管理类入口，务必调用此方法！！！
     * <p/>
     * 优先级顺序：代码中默认值 < sharePreference本地保存 < 网络请求
     * 降级：当网络没有返回相应的请求结果或者进行本地解析失败时会使用手机系统默认DNS解析
     *
     * @param context    Context
     * @param listener   此接口实现需在Application中实现，否则会造成内存泄漏 ,不能为null
     * @param httpClient httpClient
     */
    static void initHosts(@NonNull Context context, @NonNull HttpDnsListener listener, @NonNull HttpClient httpClient) {
        SharedUtil.instance(context.getApplicationContext());
        sListener = listener;
        sContext = context.getApplicationContext();
        dnsEnable = SharedUtil.getBoolean(SP_KEY_ENABLE, true);

        if (sHostBeanMap == null) {
            sHostBeanMap = new ConcurrentHashMap<>(INIT_SIZE);
        }

        defaultApiDns = listener.defaultApiDns();
        refreshHosts(0, listener, httpClient);
    }


    /**
     * 内部调用方法 ，当网络出现问题或者数据格式化出现问题时将进行dns列表拉取
     *
     * @param httpClient http请求类
     * @param url        请求的url
     */
    static void requestDns(final HttpClient httpClient, String url) {
        if (url.contains(HOST_URL)) {
            return;
        }
        if (sContext != null && sListener != null && isNetWorkEnable(sContext)) {
            refreshHosts(1, sListener, httpClient);
        }
    }

    /**
     * 刷新本地域名与IP mapping
     * 进行网路请求获得Hosts，若失败或者网络异常时重试{@link #RETRY_MAX}次
     * 处理逻辑由当前方法回调
     * <p/>
     *
     * @param currentRetryTime 当前重试第几次 ，总共循环到1次（从0开始）
     * @param listener         dns回调监听
     * @param httpClient       网络请求的client
     */
    static void refreshHosts(final int currentRetryTime, final HttpDnsListener listener, final HttpClient httpClient) {
        if (defaultApiDns == null) {
            return;
        }
        //内部dns请求将单独使用自己的httpclient
        HttpDns httpDns = new HttpDns();
        httpDns.setSelfDnsMap(defaultApiDns.getDomain(), defaultApiDns.getIp());
        OkHttpClient okHttpClient = httpClient.getInnerHttpClient()
                .newBuilder()
                .dns(httpDns)
                .build();
        if (sDnsRequest == null) {
            sDnsRequest = httpClient.post();
        }
        sDnsRequest
                .url(HOST_URL)
                .selfClient(okHttpClient)
                .openStrictMode(true)
                .enqueue(new AbstractHttpCallBack<List<DnsHostBean>>() {
                    @Override
                    public void onSuccess(HttpBaseResponse<List<DnsHostBean>> response) {
                        List<DnsHostBean> hosts = response.getContent();
                        if (hosts != null && !hosts.isEmpty()) {
                            listener.hosts(hosts);
                            saveHostsMapping(hosts);
                            setDnsEnableState(true);
                        } else {
                            //如果大于等于重试最大次数
                            if (currentRetryTime >= RETRY_MAX) {
                                setDnsEnableState(false);
                            } else {
                                refreshHosts(currentRetryTime + 1, listener, httpClient);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String errorMsg,Throwable error) {
                        error.printStackTrace();
                        if (currentRetryTime >= RETRY_MAX) {
                            setDnsEnableState(false);
                        } else {
                            //递归重试
                            refreshHosts(currentRetryTime + 1, listener, httpClient);
                        }
                    }
                });
    }

    /**
     * 存储本地Mapping （SP）
     *
     * @param hosts mapping列表
     */
    public static void saveHostsMapping(List<DnsHostBean> hosts) {
        if (sHostBeanMap == null) {
            sHostBeanMap = new ConcurrentHashMap<>(INIT_SIZE);
        }
        sHostBeanMap.clear();
        for (DnsHostBean host : hosts) {
            sHostBeanMap.put(host.getKey(), host);
            SharedUtil.saveString(host.getKey(), JSON.toJSONString(host));
        }
    }

    /**
     * 设置DNS enable 状态
     *
     * @param dnsEnableState 是否开启  true:开启  false:不开启
     */
    public static void setDnsEnableState(boolean dnsEnableState) {
        dnsEnable = dnsEnableState;
        SharedUtil.saveBoolean(SP_KEY_ENABLE, dnsEnableState);
    }

    /**
     * 是否开启dns
     *
     * @return true:开启  false:不开启
     */
    public static boolean isDnsEnable() {
        return dnsEnable;
    }

    /**
     * 清除本地 域名、ip 存储
     */
    public static void clear() {
        if (sHostBeanMap != null) {
            for (String key : sHostBeanMap.keySet()) {
                SharedUtil.remove(key);
            }
            sHostBeanMap.clear();
        }
        SharedUtil.remove(SP_KEY_ENABLE);
    }

    /**
     * 根据key获得本地存储的域名，
     *
     * @param key      {KEY_API} :普通api
     *                 {KEY_PUSH} :push 域名
     *                 {KEY_MONITOR} :打点日志域名
     *                 {KEY_WEB} :web请求域名
     * @param defaultS 当本地未存储时获得此默认值
     * @return 域名
     */
    public static String getDomain(String key, String defaultS) {
        if (TextUtils.isEmpty(key) || sHostBeanMap == null) {
            return defaultS;
        }
        DnsHostBean dnsHostBean = sHostBeanMap.get(key);
        String value = null;
        if (dnsHostBean != null) {
            value = dnsHostBean.getDomain();
        }
        if (TextUtils.isEmpty(value)) {
            return defaultS;
        }
        return value;
    }

    /**
     * 根据key获得IP
     *
     * @param key      {KEY_API} :普通api
     *                 {KEY_PUSH} :push 域名
     *                 {KEY_MONITOR} :打点日志域名
     *                 {KEY_WEB} :web请求域名
     * @param defaultS 当本地未存储时获得此默认值
     * @return host解析出的ip
     */
    @CheckResult
    public static String getHostIp(@Nullable String key, @Nullable String defaultS) {
        if (TextUtils.isEmpty(key) || sHostBeanMap == null) {
            return defaultS;
        }
        DnsHostBean dnsHostBean = sHostBeanMap.get(key);
        String value = null;
        if (dnsHostBean != null) {
            value = dnsHostBean.getIp();
        }
        if (TextUtils.isEmpty(value)) {
            return defaultS;
        }
        return value;
    }

    /***
     * 根据域名获得IP
     * @param hostName 域名
     * @return ip地址
     */
    @CheckResult
    public static String getHostIpFoDomain(@Nullable String hostName) {
        if (TextUtils.isEmpty(hostName) || sHostBeanMap == null) {
            return "";
        }
        String value = "";
        for (DnsHostBean dnsHostBean : sHostBeanMap.values()) {
            if (hostName.equals(dnsHostBean.getDomain())) {
                value = dnsHostBean.getIp();
            }
        }
        return value;
    }

    /**
     * 网络是否可用
     *
     * @param context 上下文
     * @return 网络是否可用  true:可用  false:不可用
     */
    static boolean isNetWorkEnable(@Nullable Context context) {
        if (context ==null) {
            return false;
        }
        //在有些安卓版本上，直接使用activity对应的context会有内存泄露
        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }

        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return info.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }
}
