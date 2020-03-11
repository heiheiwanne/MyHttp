package com.lucky.lib.http2;


/**
 * @Description: 缓存等级
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/2/16 上午10:34
 */
public class HttpCacheLevel {
    public long time;

    public HttpCacheLevel(long time) {
        this.time = time;
    }

    /**
     * 不加载也不缓存
     */
    public static final HttpCacheLevel NO_LOAD_NO_CACHE = new HttpCacheLevel(-2);
    /**
     * 不加载却缓存
     */
    public static final HttpCacheLevel NO_LOAD_CACHE = new HttpCacheLevel(-1);
    /**
     * 缓存10s
     */
    public static final HttpCacheLevel CACHE_10S = new HttpCacheLevel(10 * 1000);
    /**
     * 缓存30s
     */
    public static final HttpCacheLevel CACHE_30S = new HttpCacheLevel(30 * 1000);
    /**
     * 缓存10min
     */
    public static final HttpCacheLevel CACHE_10MIN = new HttpCacheLevel(10 * 60 * 1000);
    /**
     * 缓存30min
     */
    public static final HttpCacheLevel CACHE_30MIN = new HttpCacheLevel(30 * 60 * 1000);
    /**
     * 缓存2h
     */
    public static final HttpCacheLevel CACHE_2H = new HttpCacheLevel(2 * 60 * 60 * 1000);
    /**
     * 缓存12h
     */
    public static final HttpCacheLevel CACHE_12H = new HttpCacheLevel(12 * 60 * 60 * 1000);

}
