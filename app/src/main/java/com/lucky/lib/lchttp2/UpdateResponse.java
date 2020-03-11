package com.lucky.lib.lchttp2;




import java.io.Serializable;

/**
 * 作用描述: update
 * @author : xmq
 * @date : 2018/11/1 下午1:46
 */
public class UpdateResponse implements Serializable {
    public UpdateBean version;
    public Long timestamp;
    public String msg;
}
