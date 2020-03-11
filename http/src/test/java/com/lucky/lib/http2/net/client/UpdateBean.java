package com.lucky.lib.http2.net.client;

import java.io.Serializable;

public class UpdateBean implements Serializable{
    public Boolean force;
    public Boolean upgrade;
    public String address;
    public String msg;
    public Integer newver;
}
