package com.lucky.lib.http2;

/**
 * @Description: 作用描述: 网络设置中的默认值
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/2/19 上午11:24
 */
class HttpConfig {
    /**
     * 最大链接数 default：32
     */
    static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 32;
    /**
     * 最大空闲链接数
     */
    static final int MAX_IDLE_CONNECTIONS = 5;
    /**
     * 每个host对应的连接数
     */
    static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 4;
    /**
     * 空闲连接数生存时间 （秒）
     */
    static final long KEEP_ALIVE_DURATION = 60;
    /**
     * 默认失败重试次数
     */
    static final int RETRY_TIME = 1;
    /**
     * 连接超时时间 (秒)
     */
    static final int CONNECT_TIME_OUT = 15;
    /**
     * 读超时时间 （秒）
     */
    static final int READ_TIME_OUT = 15;
    /**
     * 写超时时间 （秒）
     */
    static final int WRITE_TIME_OUT = 15;
    /**
     * 默认网络缓存大小
     */
    static final int DEFAULT_CACHE_SIZE = 10 * 1024 * 1024;
}
