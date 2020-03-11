package com.lucky.lib.http2.net.store;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mylo on 2017/11/20.
 */

public class CityListBean implements Serializable {
    public String name;
    public String id;
    public List<StoreListBean> storeList;
}
