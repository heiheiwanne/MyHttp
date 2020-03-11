package com.lucky.lib.http2.dns;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * @author xmq
 * date: 2018/10/26
 * explain: dns回调接口
 */
public interface HttpDnsListener {
    /**
     * 网络请求回来的host列表
     *
     * @param hostBeanList  host请求结果列表
     */
    void hosts(List<DnsHostBean> hostBeanList);

    /**
     * 默认的api对应的ip
     *
     * @return api的DnsHostBean
     */
    @NonNull
    DnsHostBean defaultApiDns();
}
