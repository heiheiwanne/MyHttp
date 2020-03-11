package com.lucky.lib.http2.dns;

import java.io.Serializable;

/**
 * @author xmq
 * date: 2018/10/26
 * explain: dns
 */
public class DnsHostBean implements Serializable {
    /**
     * 域名
     */
    private String domain;
    /**
     * 对应Ip
     */
    private String ip;
    /**
     * key
     */
    private String key;

    public DnsHostBean() {
    }

    public DnsHostBean(String key, String domain, String ip) {
        this.key = key;
        this.domain = domain;
        this.ip = ip;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 转化为String
     */
    @Override
    public String toString() {
        return "{"
                + "\"domain\":\"" + domain + "\",\"ip\":\"" + ip + "\""
                + "}";
    }
}
