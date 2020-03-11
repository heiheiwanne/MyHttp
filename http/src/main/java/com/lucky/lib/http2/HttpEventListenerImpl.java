package com.lucky.lib.http2;

import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
/**
 * @Description: 统计时间Event类
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/4/24 上午11:28
 */
public class HttpEventListenerImpl extends EventListener {

    /**
     * 连接开始时间
     */
    private long startConnectTime;
    /**
     * 连接时间: 握手时间 note:这里为https时包含{@link #tlsTime}
     */
    private String connectTime;

    /**
     * dns开始时间
     */
    private long startDnsTime;
    /**
     * dns解析总时间
     */
    private String dnsTime;

    /**
     * tls解析开始时间
     */
    private long startTlsTime;
    /**
     * tls解析总时间
     */
    private String tlsTime;

    /**
     * 读取开始时间
     */
    private long startReadTime;
    /**
     * 读取时间
     */
    private String readTime;

    /**
     * 请求开始时间
     */
    private long startRequestTime;
    /**
     * 请求总时间
     */
    private String requestTime;

    /**
     * 请求头大小
     */
    private String upHeaderSize;
    /**
     * 请求体大小
     */
    private String upBodySize;
    /**
     * 响应头大小
     */
    private String downHeaderSize;
    /**
     * 响应体大小
     */
    private String downBodySize;
    /**
     * 请求url
     */
    private String requestUrl;
    /**
     * 请求code
     */
    private String  code;

    /**
     * 总时间开始时间
     */
    private long startAllTime;
    /**
     * 总的时间
     */
    private String allTime;
    /**
     * 缓存标记：仅仅在200时是否读取缓存
     * 1、是读缓存
     * 0，是网络请求数据
     */
    public int isCache;

    /**
     * 网络性能数据收集器
     */
    public HttpEventListenerImpl() {
    }
    @Override
    public void callStart(Call call) {
        super.callStart(call);
        startAllTime = SystemClock.elapsedRealtime();
        requestUrl = call.request().url().toString();
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        super.dnsStart(call, domainName);
        startDnsTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
        dnsTime = String.valueOf(SystemClock.elapsedRealtime() - startDnsTime);
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        super.connectStart(call, inetSocketAddress, proxy);
        startConnectTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void secureConnectStart(Call call) {
        super.secureConnectStart(call);
        startTlsTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void secureConnectEnd(Call call, @Nullable Handshake handshake) {
        super.secureConnectEnd(call, handshake);
        tlsTime = String.valueOf(SystemClock.elapsedRealtime() - startTlsTime);
    }

    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
        connectTime = String.valueOf(SystemClock.elapsedRealtime() - startConnectTime);
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol, IOException ioe) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
        connectTime = String.valueOf(SystemClock.elapsedRealtime() - startConnectTime);
    }


    @Override
    public void requestHeadersStart(Call call) {
        super.requestHeadersStart(call);
        startRequestTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        super.requestHeadersEnd(call, request);
        upHeaderSize = String.valueOf(request.headers().toString().length());
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        super.requestBodyEnd(call, byteCount);
        upBodySize = String.valueOf(byteCount);
    }

    @Override
    public void responseHeadersStart(Call call) {
        super.responseHeadersStart(call);
        requestTime = String.valueOf(SystemClock.elapsedRealtime() - startRequestTime);
        startReadTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        super.responseHeadersEnd(call, response);
        downHeaderSize = String.valueOf(response.headers().toString().length());
        code = String.valueOf(response.code());
    }

    @Override
    public void responseBodyStart(Call call) {
        super.responseBodyStart(call);
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        super.responseBodyEnd(call, byteCount);
        downBodySize = String.valueOf(byteCount);
        readTime = String .valueOf(SystemClock.elapsedRealtime() - startReadTime);
    }

    @Override
    public void callEnd(Call call) {
        if (allTime ==null || allTime.length() ==0) {
            allTime = String.valueOf(SystemClock.elapsedRealtime() - startAllTime);
        }
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        super.callFailed(call, ioe);
        allTime = String.valueOf(SystemClock.elapsedRealtime() - startAllTime);
    }

    void code(String str) {
        if (code == null || code.length() == 0) {
            code = str;
        }
    }


    /**
     * 获得网络性能model
     * @param apiCode api的返回码
     * @param busiCode busi的返回码
     * @param apiMsg api的信息
     * @return 网络性能数据
     */
    public NetWorkModel createNetWorkModel(@Nullable String apiCode,@Nullable String busiCode,@Nullable String apiMsg,int isCache) {
        return new NetWorkModel(connectTime, dnsTime, tlsTime, readTime, requestTime, upHeaderSize, upBodySize, downHeaderSize, downBodySize, requestUrl,code, allTime,apiCode,busiCode,apiMsg,isCache);
    }

    /**
     * 获得网络性能model
     * @return 网络性能数据
     */
    public NetWorkModel createNetWorkModel() {
        return new NetWorkModel(connectTime, dnsTime, tlsTime, readTime, requestTime, upHeaderSize, upBodySize, downHeaderSize, downBodySize, requestUrl,code, allTime,null,null,null,0);
    }

    /**
     * @Description: 网络性能bean
     * @Author: xmq mingqiang.xu@luckincoffee.com
     * @Date: 2019/4/24 下午2:41
     */
    public class NetWorkModel{
        /**
         * 连接时间: 握手时间 note:这里为https时包含{@link #tlsTime}
         */
        public String connectTime;
        /**
         * dns解析时间
         */
        public String dnsTime;
        /**
         * ssl验证时间
         */
        public String tlsTime;
        /**
         * 读取时间：响应体读取时间
         */
        public String readTime;
        /**
         * 请求时间：开始发送数据（通道已建立链接）到数据返回的时间
         */
        public String requestTime;
        /**
         * 请求头流量
         */
        public String upHeaderSize;
        /**
         * 请求体流量
         */
        public String upBodySize;
        /**
         * 下载头流量
         */
        public String downHeaderSize;
        /**
         * 下载体流量
         */
        public String downBodySize;
        /**
         * 请求url
         */
        public String requestUrl;
        /**
         * 响应码
         */
        public String code;
        /**
         * 请求的所有耗时
         */
        public String allTime;

        /**
         * api的返回码
         */
        public String apiCode;
        /**
         * busi的返回码
         */
        public String busiCode;
        /**
         * api的消息数据
         */
        public String apiMsg;
        /**
         * 缓存标记：仅仅在200时是否读取缓存
         * 1、是读缓存
         * 0，是网络请求数据
         */
        public int isCache;

        /**
         * 网络性能bean
         * @param connectTime 连接时间
         * @param dnsTime dns解析时间
         * @param tlsTime ssl验证时间
         * @param readTime 读取时间
         * @param requestTime 请求时间
         * @param upHeaderSize 请求头流量
         * @param upBodySize 请求体流量
         * @param downHeaderSize 下载头流量
         * @param downBodySize 下载体流量
         * @param requestUrl 请求url
         * @param code 响应码
         * @param allTime 请求的所有耗时
         * @param apiCode api的返回码
         * @param busiCode busi的返回码
         * @param apiMsg api的消息数据
         */
        public NetWorkModel(String connectTime, String dnsTime, String tlsTime, String readTime,
                            String requestTime, String upHeaderSize, String upBodySize,
                            String downHeaderSize, String downBodySize, String requestUrl,
                            String code, String allTime,String apiCode,String busiCode,String apiMsg,int isCache) {
            this.connectTime = connectTime;
            this.dnsTime = dnsTime;
            this.tlsTime = tlsTime;
            this.readTime = readTime;
            this.requestTime = requestTime;
            this.upHeaderSize = upHeaderSize;
            this.upBodySize = upBodySize;
            this.downHeaderSize = downHeaderSize;
            this.downBodySize = downBodySize;
            this.requestUrl = requestUrl;
            this.code = code;
            this.allTime = allTime;
            this.apiCode = apiCode;
            this.busiCode = busiCode;
            this.apiMsg = apiMsg;
            this.isCache = isCache;
        }

        @Override
        public String toString() {
            return "NetWorkModel{" +
                    "connectTime='" + connectTime + '\'' +
                    ", dnsTime='" + dnsTime + '\'' +
                    ", tlsTime='" + tlsTime + '\'' +
                    ", readTime='" + readTime + '\'' +
                    ", requestTime='" + requestTime + '\'' +
                    ", upHeaderSize='" + upHeaderSize + '\'' +
                    ", upBodySize='" + upBodySize + '\'' +
                    ", downHeaderSize='" + downHeaderSize + '\'' +
                    ", downBodySize='" + downBodySize + '\'' +
                    ", requestUrl='" + requestUrl + '\'' +
                    ", code='" + code + '\'' +
                    ", allTime='" + allTime + '\'' +
                    ", apiCode='" + apiCode + '\'' +
                    ", busiCode='" + busiCode + '\'' +
                    ", apiMsg='" + apiMsg + '\'' +
                    ", isCache=" + isCache +
                    '}';
        }
    }
}
