package com.lucky.lib.lchttp2;

import java.io.Serializable;
import java.util.List;
/**
 * 作用描述: city
 * @author : xmq
 * @date : 2018/11/1 下午1:47
 */

public class CityListBean implements Serializable {
    public String name;
    public String id;
    public List<StoreListBean> storeList;
}
