package com.lucky.lib.http2.net.client;




import java.io.Serializable;

public class UpdateResponse implements Serializable {
    public UpdateBean version;
    public Long timestamp;
    public String msg;
}
