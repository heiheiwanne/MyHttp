package com.lucky.lib.http2.net.client;

import java.io.Serializable;

/**
 * @Description: 国家/地区区号条目bean
 * @Author: xmq
 * @Date: 2018/12/14 16:32
 */
public class RegionListItemBean implements Serializable {

    /**
     * 地区ID
     */
    public String regionId;

    /**
     * 国家/地区
     */
    public String region;

    /**
     * 区号
     */
    public String code;
}
