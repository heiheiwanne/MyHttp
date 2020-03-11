package com.lucky.lib.http2;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lucky.lib.http2.utils.HttpLog;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Dns;

/**
 * @Description: Dns接口实现类
 * 此类有两种功效：1.对外做dns解析，自己维护dns列表  2.内部请求时写死了ip请求
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/4/24 上午11:26
 */
final class HttpDns implements Dns {
    /**
     * 集合初始化大小
     */
    private static final int INIT_SIZE = 8;
    /**
     * 当前采用降级策略时，将非本地dns解析设置为-1，方便统计查找
     */
    private static final String FAIL_IP = "-1";
    /**
     * 对外的网络暴漏接口
     */
    private HttpClient.LcMonitorListener mNetListener;
    /**
     * 内部dns集合
     */
    private HashMap<String, String> selfDnsMap;

    /**
     * Dns接口实现类
     * @param netListener 对外的网络回调接口
     */
    HttpDns(HttpClient.LcMonitorListener netListener) {
        mNetListener = netListener;
    }

    /**
     * 无参的dns构造类
     */
    HttpDns() {
    }

    /**
     * 设置私有的dns列表
     *
     * @param selfDnsMap dns列表，key: host   value:ip
     */
    public void setSelfDnsMap(HashMap<String, String> selfDnsMap) {
        this.selfDnsMap = selfDnsMap;
    }

    /**
     * 设置私有的ip 键值对
     *
     * @param host hostname
     * @param ip   对应的ip
     */
    public void setSelfDnsMap(String host, String ip) {
        if (selfDnsMap == null) {
            selfDnsMap = new HashMap<>(INIT_SIZE);
        }
        if (!selfDnsMap.containsKey(host)) {
            selfDnsMap.put(host, ip);
        }
    }

    @Override
    public List<InetAddress> lookup(@NonNull String hostname) throws UnknownHostException {
        if (selfDnsMap!=null && !selfDnsMap.isEmpty() && selfDnsMap.containsKey(hostname)) {
            return Arrays.asList(InetAddress.getAllByName(selfDnsMap.get(hostname)));
        }

        //本机解析的ip
        String address = getInetAddress(hostname);

        if (HttpDnsManager.isDnsEnable()) {
            String ip = HttpDnsManager.getHostIpFoDomain(hostname);
            HttpLog.d("hostname:" + hostname + "    ip:" + ip);
            if (!TextUtils.isEmpty(ip)) {
                if (!ip.equals(address) && mNetListener != null) {
                    mNetListener.dns(hostname, ip, address, "");
                }
                return Arrays.asList(InetAddress.getAllByName(ip));
            }
        }

        if (mNetListener != null) {
            //
            mNetListener.dns(hostname, FAIL_IP, address, "");
        }
        return SYSTEM.lookup(hostname);
    }


    /**
     * 由域名获得ip
     *
     * @param host 域名
     * @return 本机解析ip
     */
    private String getInetAddress(String host) {
        String iPAddress = "";
        InetAddress returnStr;
        try {
            returnStr = InetAddress.getByName(host);
            iPAddress = returnStr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return iPAddress;
        }
        return iPAddress;
    }

}



