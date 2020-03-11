package com.lucky.lib.http2;

import java.io.Serializable;

/**
 * @Description: 请求最外层实体类 T ：content的类型
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/3/27 下午5:25
 */
public class HttpBaseResponse<T> implements Serializable {
    /**
     * api远程数据码
     */
    private String busiCode;
    /**
     * 网路数据码
     */
    private int code;
    /**
     * 内容
     */
    private T content;
    /**
     * 数据消息
     */
    private String msg;
    /**
     * 网络状态
     */
    private String status;
    /**
     * 网络UID
     */
    private String uid;
    /**
     * 版本号
     */
    private String version;

    /**
     * 获取api码
     * @return api远程数据码
     */
    public String getBusiCode() {
        return busiCode;
    }

    /**
     * 设置 api远程数据码
     * @param busiCode api远程数据码
     */
    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    /**
     * 获取网路数据码
     * @return 网路数据码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置网路数据码
     * @param code 网路数据码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取内容
     * @return 内容
     */
    public T getContent() {
        return content;
    }

    /**
     * 设置内容
     * @param content 内容
     */
    public void setContent(T content) {
        this.content = content;
    }

    /**
     * 获取数据消息
     * @return 数据消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置 数据消息
     * @param msg 数据消息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取网络状态
     * @return 网络状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置网络状态
     * @param status 网络状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取网络UID
     * @return 网络UID
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置网络UID
     * @param uid 网络UID
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取版本号
     * @return 版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置版本号
     * @param version 版本号
     */
    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public String toString() {
        return "LcNetBaseResponse{" +
                "busiCode='" + busiCode + '\'' +
                ", code=" + code +
                ", content=" + content +
                ", msg='" + msg + '\'' +
                ", status='" + status + '\'' +
                ", uid='" + uid + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
