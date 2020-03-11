package com.lucky.lib.http2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lucky.lib.http2.dns.HttpDnsListener;
import com.lucky.lib.http2.utils.HttpLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.lucky.lib.http2.HttpConfig.DEFAULT_MAX_TOTAL_CONNECTIONS;

/**
 * @Description: 网络请求客户端
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/3/27 下午5:28
 */
public class HttpClient {

    /**
     * 全局唯一的key，此处设为静态常量
     * 加解密的秘钥
     */
    private static String signKey;
    /**
     * 网络cid
     */
    private String cid;
    /**
     * 基础URL
     */
    private String baseUrl;
    /**
     * app版本号
     */
    private String appVersion;
    /**
     * 请求的UID
     */
    private String uid;
    /**
     * 数据上报时的接口类
     */
    private LcMonitorListener mMonitorListener;
    /**
     * 是否同步或者异步请求 default: false 异步请求 ，true 同步请求
     */
    private boolean sync;

    /**
     * 内部client
     */
    private OkHttpClient httpClient;

    /**
     * 初始化host
     * @param context app上下文
     * @param listener DNS回调接口
     */
    public void initHost(@NonNull Context context, @NonNull HttpDnsListener listener) {
        HttpDnsManager.initHosts(context, listener, this);
    }


    /**
     * 网络客户端
     * @param builder 构造类
     */
    public HttpClient(Builder builder) {
        cid(builder.cid);
        baseUrl(builder.baseUrl);
        appVersion(builder.appVersion);
        //此处为静态变量赋值
        HttpAesCrypto.setSignKey(builder.signKey);
        uid(builder.uid);
        httpClient = getOkHttpClient(builder);

        HttpLog.setLog(builder.log);
        monitorListener(builder.mMonitorListener);
    }

    /**
     * 返回内部网络client
     *
     * @return 网络客户端{@link OkHttpClient}
     */
    public OkHttpClient getInnerHttpClient() {
        return httpClient;
    }

    /**
     * 新的call
     * @param request 请求体
     * @return 网络调用call
     */
    public Call newCall(Request request) {
        return httpClient.newCall(request);
    }


    /**
     * 设置是否为同步
     * @param isSync 是否为同步  true: 是 false:不是
     */
    public void sync(boolean isSync) {
        this.sync = isSync;
    }

    /**
     * 获取当前状态
     * @return 是否为同步  true: 是 false:不是
     */
    public boolean sync() {
        return sync;
    }

    /**
     * 设置cid
     * @param cid 端的对应capi 的cid
     */
    private void cid(String cid) {
        this.cid = cid;
    }

    /**
     * 获取cid
     * @return 端的对应capi 的cid
     */
    @NonNull
    public String cid() {
        return cid;
    }

    /**
     * 获取baseURL
     * @return 网络请求基础URL
     */
    @NonNull
    public String baseUrl() {
        if (baseUrl == null || baseUrl.length() == 0) {
            throw new IllegalArgumentException("baseUrl is null");
        }
        return baseUrl;
    }

    /**
     * 设置baseURL
     * @param baseurl 网络请求基础URL
     */
    private void baseUrl(String baseurl) {
        baseUrl = baseurl;
    }

    /**
     * 获取uid
     * @return 标记用户的uid
     */
    @NonNull
    public String uid() {
        return uid;
    }

    /**
     * 设置uid
     * @param uid 网络请求的uid
     * @return 网络客户端
     */
    public HttpClient uid(@NonNull String uid) {
        this.uid = uid;
        return this;
    }

    /**
     * 设置app版本号
     * @param appVersion app版本号
     */
    private void appVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    /**
     * 获取app版本号
     * @return app版本号
     */
    public String appVersion() {
        return appVersion;
    }


    /**
     * 获取monitor监听
     * @return monitor监听
     */
    LcMonitorListener monitorListener() {
        return mMonitorListener;
    }

    /**
     * 设置monitor监听
     * @param monitorListener monitor监听
     */
    private void monitorListener(LcMonitorListener monitorListener) {
        mMonitorListener = monitorListener;
    }

    /**
     * 开启噩梦模式，此模式下所有的get请求强制走本地缓存
     * @param isOpen true 开启 false 关闭
     */
    public void openNightMareMode(boolean isOpen) {
        if (httpClient != null && httpClient.cache() != null) {
            httpClient.cache().openNightMareMode(isOpen);
        }
    }

    /**
     * 是否为噩梦模式
     * @return true 开启 false 关闭
     */
    public boolean isNightMareMode() {
        if (httpClient != null && httpClient.cache() != null) {
            return httpClient.cache().getNightMareMode();
        }
        return false;
    }

    /**
     * 发起get 请求
     * @return 网络客户端{@link HttpGetRequestBuilder}
     */
    public HttpGetRequestBuilder get() {
        return new HttpGetRequestBuilder(this);
    }

    /**
     * 发起post 请求
     *
     * @return 网络客户端{@link HttpPostRequestBuilder}
     */
    public HttpPostRequestBuilder post() {
        return new HttpPostRequestBuilder(this);
    }

    /**
     * cancel 根据tag取消某一网络请求。注意此处的tag不为null
     * @param tag tag不能为null tag is no null
     */
    public void cancel(@NonNull Object tag) {
        Dispatcher dispatcher = httpClient.dispatcher();
        for (Call call : dispatcher.queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
                return;
            }
        }
        for (Call call : dispatcher.runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
                return;
            }
        }
    }


    /**
     * @Description: 网络请求builder类
     * @Author: xmq mingqiang.xu@luckincoffee.com
     * @Date: 2019/3/27 下午5:42
     */
    public static class Builder {
        /**
         * 端的对应capi 的cid
         */
        private String cid;
        /**
         * host url
         */
        private String baseUrl;
        /**
         * 版本号
         */
        private String appVersion;

        /**
         * 用户uid
         */
        private String uid;

        /**
         * 是否启用缓存，默认关闭 default: false
         */
        private boolean openCache;
        /**
         * 缓存目录
         */
        private File cacheFile;
        /**
         * 缓存大小
         */
        private int cacheSize;

        /**
         * 加密 密钥
         */
        private String signKey;

        /**
         * 重试次数
         */
        private int retryTime;

        /**
         * 日志回调接口
         */
        private HttpLog.ILog log;

        /**
         * 超时时间 默认6秒
         */
        private int connectionTimeOut;
        /**
         * 读超时 默认10s
         */
        private int readTimeOut;
        /**
         * 写超时 默认10s
         */
        private int writeTimeOut;

        /**
         * 打点回调接口
         */
        private LcMonitorListener mMonitorListener;

        /**
         * 拦截器列表
         */
        private List<Interceptor> mInterceptorList;
        /**
         * network拦截器
         */
        private List<Interceptor> networkInterceptors;


        /**
         * 网络请求builder类
         */
        public Builder() {
            connectionTimeOut = HttpConfig.CONNECT_TIME_OUT;
            readTimeOut = HttpConfig.READ_TIME_OUT;
            writeTimeOut = HttpConfig.WRITE_TIME_OUT;
            retryTime = HttpConfig.RETRY_TIME;
            cacheSize = cacheSize == 0 ? HttpConfig.DEFAULT_CACHE_SIZE : cacheSize;
        }

        /**
         * 设置端的对应capi 的cid
         * @param cid 端的对应capi 的cid
         * @return 网络builder类
         */
        public Builder cid(String cid) {
            this.cid = cid;
            return this;
        }

        /**
         * 设置baseURL
         * @param baseurl baseurl基础请求
         * @return 网络builder类
         */
        public Builder baseUrl(String baseurl) {
            baseUrl = baseurl;
            return this;
        }

        /**
         * 设置signKey签名
         * @param signKey 签名
         * @return 网络builder类
         */
        public Builder sinKey(String signKey) {
            this.signKey = signKey;
            return this;
        }

        /**
         * 设置uid
         * @param uid 用户uid
         * @return 网络builder类
         */
        public Builder uid(String uid) {
            this.uid = uid;
            return this;
        }

        /**
         * 重试次数
         * @param retryTime 重试的次数
         * @return 网络builder类
         */
        public Builder retryTime(int retryTime) {
            this.retryTime = retryTime;
            return this;
        }

        /**
         * 开启缓存
         * @param openCache 缓存开关 true：开启 false:关闭
         * @return 网络builder类
         */
        public Builder openCache(boolean openCache) {
            this.openCache = openCache;
            return this;
        }

        /**
         * 设置缓存文件
         * @param cacheFile 缓存文件
         * @return 网络builder类
         */
        public Builder cacheFile(File cacheFile) {
            this.cacheFile = cacheFile;
            return this;
        }

        /**
         * 设置版本号
         * @param appVersion 版本号
         * @return 网络builder类
         */
        public Builder appVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        /**
         * 设置monitor
         * @param monitorListener monitor回调监听
         * @return 网络builder类
         */
        public Builder monitor(LcMonitorListener monitorListener) {
            mMonitorListener = monitorListener;
            return this;
        }

        /**
         * 设置连接超时时间
         * @param connectionTimeOut 超时时间
         * @return 网络builder类
         */
        public Builder connectionTimeOutSecond(int connectionTimeOut) {
            if (connectionTimeOut > 0) {
                this.connectionTimeOut = connectionTimeOut;
            }
            return this;
        }

        /**
         * 设置读超时时间
         * @param readTimeOut 超时时间
         * @return 网络builder类
         */
        public Builder readTimeOutSecond(int readTimeOut) {
            if (readTimeOut > 0) {
                this.readTimeOut = readTimeOut;
            }
            return this;
        }

        /**
         * 设置写超时时间
         * @param writeTimeOut 超时时间
         * @return 网络builder类
         */
        public Builder writeTimeOutSecond(int writeTimeOut) {
            if (writeTimeOut > 0) {
                this.writeTimeOut = writeTimeOut;
            }
            return this;
        }

        /**
         * 设置日志接口
         * @param log 日志接口
         * @return 网络builder类
         */
        public Builder log(HttpLog.ILog log) {
            this.log = log;
            return this;
        }

        /**
         * 增加拦截器
         * @param interceptor 拦截器
         */
        public void addInterceptor(Interceptor interceptor) {
            if (mInterceptorList == null) {
                mInterceptorList = new ArrayList<>();
            }
            mInterceptorList.add(interceptor);
        }

        /**
         * 网络拦截器
         * @return 网络拦截器列表
         */
        public List<Interceptor> networkInterceptors() {
            return this.networkInterceptors;
        }

        /**
         * 增加网络拦截器
         * @param interceptor 拦截器
         * @return 网络builder
         */
        public Builder addNetworkInterceptor(Interceptor interceptor) {
            if (interceptor == null) {
                throw new IllegalArgumentException("interceptor == null");
            } else {
                if (networkInterceptors ==null) {
                    networkInterceptors = new ArrayList<>();
                }
                this.networkInterceptors.add(interceptor);
                return this;
            }
        }

        /**
         * 构造网络client
         * @return 网络client
         */
        public HttpClient build() {
            return new HttpClient(this);
        }
    }

    /**
     * 获得Client
     * @param innerBuilder 构造类
     * @return 用户Client类型
     */
    @NonNull
    private OkHttpClient getOkHttpClient(final Builder innerBuilder) {

        //设置连接池数
        ConnectionPool connectionPool = new ConnectionPool(HttpConfig.MAX_IDLE_CONNECTIONS, HttpConfig.KEEP_ALIVE_DURATION, TimeUnit.SECONDS);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectionPool(connectionPool);

        //设置并发数
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(DEFAULT_MAX_TOTAL_CONNECTIONS);
        dispatcher.setMaxRequestsPerHost(HttpConfig.DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
        builder.dispatcher(dispatcher);

        //手动创建一个OkHttpClient并设置超时时间缓存等设置
        builder.connectTimeout(innerBuilder.connectionTimeOut, TimeUnit.SECONDS);
        builder.readTimeout(innerBuilder.readTimeOut, TimeUnit.SECONDS);
        builder.writeTimeout(innerBuilder.writeTimeOut, TimeUnit.SECONDS);

        //日志打印拦截器
        HttpLogInterceptor interceptor = new HttpLogInterceptor();
        if (innerBuilder.log != null) {
            interceptor.setLevel(HttpLogInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        //设置重试机制
        HttpRetryInterceptor retryInterceptor = new HttpRetryInterceptor(innerBuilder.retryTime);
        builder.addInterceptor(retryInterceptor);

        //网络性能收集
        builder.eventListenerFactory(new EventListener.Factory() {
            @Override
            public EventListener create(Call call) {
                return new HttpEventListenerImpl();
            }
        });
        //增加DNS解析
        builder.dns(new HttpDns(innerBuilder.mMonitorListener));
        //增加okhttp缓存（仅支持get型请求）
        if (innerBuilder.openCache) {
            builder.cache(new Cache(innerBuilder.cacheFile,innerBuilder.cacheSize));
        }

        if (innerBuilder.mInterceptorList !=null) {
            for (Interceptor in : innerBuilder.mInterceptorList) {
                builder.addInterceptor(in);
            }
        }
        if (innerBuilder.networkInterceptors !=null) {
            for (Interceptor networkInterceptor : innerBuilder.networkInterceptors) {
                builder.addNetworkInterceptor(networkInterceptor);
            }
        }
        return builder.build();
    }

    /**
     * @Description: 网络monitor统计
     * @Author: xmq mingqiang.xu@luckincoffee.com
     * @Date: 2019/3/27 下午5:52
     */
    public interface LcMonitorListener {
        /**
         * 网络执行时间统计，各个参数表示在{@link HttpEventListenerImpl.NetWorkModel}中注释
         *
         * @param netWorkModel 网络事件监听
         */
        void network(@NonNull HttpEventListenerImpl.NetWorkModel netWorkModel);

        /**
         * dns打点,子类dns解析回调
         *
         * @param domain 域名
         * @param ip 网络解析ip
         * @param hijackIp 本地劫持ip
         * @param remark 备注
         */
        void dns(@NonNull String domain, @NonNull String ip, @NonNull String hijackIp, @NonNull String remark);

        /**
         * api 异常打点，网络api请求异常时子类回调
         *
         * @param exceptionCode 异常码
         * @param exceptionStack 异常栈信息
         * @param remark 备注
         */
        void busiException(@NonNull String exceptionCode, @Nullable Throwable exceptionStack, @NonNull String remark);

        /**
         * 网络异常打点，网络通道异常时子类回调
         *
         * @param exceptionCode 异常码
         * @param exceptionStack 异常栈信息
         * @param remark 备注
         */
        void networkException(@NonNull String exceptionCode, @Nullable Throwable exceptionStack, @NonNull String remark);
    }

}
