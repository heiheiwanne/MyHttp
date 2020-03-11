package com.lucky.lib.lchttp2;

import java.io.Serializable;

/**
 * 作用描述: update
 * @author : xmq
 * @date : 2018/11/1 下午1:46
 */
public class UpdateBean implements Serializable{
    public Boolean force;
    public Boolean upgrade;
    public String address;
    public String msg;
    public Integer newver;
}
