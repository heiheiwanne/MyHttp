package com.lucky.lib.http2;

/**
 * @Description:  网络异常枚举类
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/4/24 上午10:28
 */
public enum HttpCodeEnum {
    /**
     * 网络异常类
     */
    DEFAULT_EXCEPTION(1000, "网络连接异常"),
    /**
     * 本地端口异常
     */
    BIND_EXCEPTION(1001, "本地端口异常"),
    /**
     * 服务器请求超时
     */
    CONNECT_EXCEPTION(1002, "服务器请求超时"),
    /**
     * 网络重试异常
     */
    HTTP_RETRY_EXCEPTION(1003, "网络重试异常"),
    /**
     * 请求地址有误
     */
    MALFORMED_URL_EXCEPTION(1004, "请求地址有误"),
    /**
     * 远程端口异常
     */
    NO_ROUTE_TO_HOST_EXCEPTION(1005, "远程端口异常"),
    /**
     * 主机不可达
     */
    PORT_UN_REACH_EXCEPTION(1006, "主机不可达"),
    /**
     * 网络协议异常
     */
    PROTOCOL_EXCEPTION(1007, "网络协议异常"),
    /**
     * socket异常断开
     */
    SOCKET_EXCEPTION(1008, "网络异常断开"),
    /**
     * 服务器响应超时
     */
    SOCKET_TIMEOUT_EXCEPTION(1009, "服务器响应超时"),
    /**
     * 找不到主机
     */
    UN_KNOWN_HOST_EXCEPTION(1010, "网络连接异常"),
    /**
     * 未知服务
     */
    UN_KNOWN_SERVICE_EXCEPTION(1011, "未知服务"),
    /**
     * 网络地址解析异常
     */
    URI_SYNTAX_EXCEPTION(1012, "网络地址解析异常"),
    /**
     * json 异常
     */
    JSON_EXCEPTION(1100,"数据格式化异常"),
    /**
     * 请求异常,未正确返回200
     */
    REQUEST_EXCEPTION(1101,"请求异常,未正确返回200");
    /**
     * 端口异常字符串
     */
    private static final String BIND_EP_STR ="BindException";
    /**
     * 连接异常字符串
     */
    private static final String CONNECT_EP_STR ="ConnectException";
    /**
     * 重试异常字符串
     */
    private static final String HTTP_RETRY_EP_STR ="HttpRetryException";
    /**
     * 请求地址异常字符串
     */
    private static final String MALFORMED_URL_EP_STR ="MalformedURLException";
    /**
     * 远程端口异常字符串
     */
    private static final String NO_ROUTE_TO_HOST_EP_STR ="NoRouteToHostException";
    /**
     * 主机不可达异常字符串
     */
    private static final String PORT_UNREACHABLE_EP_STR ="PortUnreachableException";
    /**
     * 网络协议异常字符串
     */
    private static final String PROTOCOL_EP_STR ="ProtocolException";
    /**
     * socket通道异常字符串
     */
    private static final String SOCKET_EP_STR ="SocketException";
    /**
     * 连接超时异常字符串
     */
    private static final String SOCKET_TIMEOUT_EP_STR ="SocketTimeoutException";
    /**
     * 未知主机异常字符串
     */
    private static final String UN_KNOWN_HOST_EP_STR ="UnknownHostException";
    /**
     * 未知服务异常字符串
     */
    private static final String UN_KNOWN_SERVICE_EP_STR ="UnknownServiceException";
    /**
     * 地址解析异常字符串
     */
    private static final String URI_SYNTAX_EP_STR ="URISyntaxException";
    /**
     * 错误码
     */
    public int mCode;
    /**
     * 错误描述
     */
    public String mDesc;

    /**
     * 网络异常枚举
     * @param code 错误码
     * @param desc 错误描述
     */
    HttpCodeEnum(int code, String desc) {
        this.mCode = code;
        this.mDesc =desc;
    }

    /**
     * 根据错误类型切换枚举
     * @param simpleName 错误类名
     * @return 异常枚举
     */
    public static HttpCodeEnum switchEnum(String simpleName) {
        switch (simpleName) {
            case BIND_EP_STR:
                return BIND_EXCEPTION;
            case CONNECT_EP_STR:
                return CONNECT_EXCEPTION;
            case HTTP_RETRY_EP_STR:
                return HTTP_RETRY_EXCEPTION;
            case MALFORMED_URL_EP_STR:
                return MALFORMED_URL_EXCEPTION;
            case NO_ROUTE_TO_HOST_EP_STR:
                return NO_ROUTE_TO_HOST_EXCEPTION;
            case PORT_UNREACHABLE_EP_STR:
                return PORT_UN_REACH_EXCEPTION;
            case PROTOCOL_EP_STR:
                return PROTOCOL_EXCEPTION;
            case SOCKET_EP_STR:
                return SOCKET_EXCEPTION;
            case SOCKET_TIMEOUT_EP_STR:
                return SOCKET_TIMEOUT_EXCEPTION;
            case UN_KNOWN_HOST_EP_STR:
                return UN_KNOWN_HOST_EXCEPTION;
            case UN_KNOWN_SERVICE_EP_STR:
                return UN_KNOWN_SERVICE_EXCEPTION;
            case URI_SYNTAX_EP_STR:
                return URI_SYNTAX_EXCEPTION;
            default:
                return DEFAULT_EXCEPTION;
        }
    }

}
