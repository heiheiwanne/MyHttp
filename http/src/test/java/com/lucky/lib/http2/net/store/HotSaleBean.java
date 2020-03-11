package com.lucky.lib.http2.net.store;

import java.io.Serializable;

/**
 * 作用描述:
 *
 * @author : xmq
 * @date : 2017/7/27
 */
public class HotSaleBean implements Serializable {
    /**
     * 商品ID
     */
    public Integer goodsId;
    /**
     * 商品名称
     */
    public String goodsName;
    /**
     * 商品英文名称
     */
    public String goodsEngName;
    /**
     * 是否热卖商品 2 全部 0 否 1 是
     */
    public Integer promotion;
    /**
     * 特效排序
     */
    public Integer promotionOrde;

}
