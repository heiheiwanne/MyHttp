package com.lucky.lib.http2.net.client;

public class StartRequest {
    public StartRequest(int version, int deviceId, String uniqueCode) {
        this.version = version;
        this.deviceId = deviceId;
        this.uniqueCode = uniqueCode;
    }

    public int version;
    public int deviceId;
    public String uniqueCode;
}
