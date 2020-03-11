package com.lucky.lib.lchttp2;

/**
 * 作用描述: 数据解析或者api出错异常类
 *
 * @author : xmq
 * @date : 2018/10/26 下午6:48
 */
public class ApiException extends Exception {
    /**
     * 服务器错误类型
     */
    private int code;
    /**
     * message 错误信息
     */
    private String msg;
    /**
     * api 远程调用异常
     */
    private String busiCode;
    /**
     * 可UI展示码
     */
    private int outputCode;
    /**
     * 原始异常信息
     */
    private String oriMsg;


    public ApiException(int code, String msg, String busiCode) {
        this(code, msg, busiCode, 0, null);
    }

    public ApiException(int code, String msg, int outputCode, String oriMsg) {
        this.code = code;
        this.msg = msg;
        this.outputCode = outputCode;
        this.oriMsg = oriMsg;
    }

    public ApiException(int code, String msg, String busiCode, int outputCode, String oriMsg) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.busiCode = busiCode;
        this.outputCode = outputCode;
        this.oriMsg = oriMsg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public String getOriMsg() {
        return oriMsg;
    }

    public int getOutputCode() {
        return outputCode;
    }

    @Override
    public String toString() {
        return "ApiException{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", busiCode='" + busiCode + '\'' +
                ", outputCode=" + outputCode +
                ", oriMsg='" + oriMsg + '\'' +
                '}';
    }
}
