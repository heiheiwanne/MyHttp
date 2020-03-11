package com.lucky.lib.http2;

import android.support.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Description: 作用描述: 请求回调 T：表示的content类型
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/3/27 下午5:10
 */
public abstract class AbstractHttpCallBack<T> {

    /**
     * content的类型信息
     */
    private Type entityType;
    /**
     * content的class类型
     */
    private Class<?> clazz;

    /**
     * 网络请求回调抽象类
     * @param itemClazz 转换的content类型参数
     */
    public AbstractHttpCallBack(Class<?> itemClazz) {
        this.entityType = itemClazz;
    }

    /**
     * 无参数的网络请求回调抽象类构造方法
     */
    public AbstractHttpCallBack() {
        try {
            Type genType = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            entityType = params[0];
        } catch (Exception ignore) {
        }
    }

    /**
     * 获取类型
     * @return content类型
     */
    Type getEntityType() {
        if (entityType == null) {
            return Object.class;
        }
        return entityType;
    }

    /**
     * 设置返回content的对象类型
     * @param clazz content的对象类型
     */
    public void setContentClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * 返回content的对象类型
     * @return content的对象类型
     */
    Class<?> getContentClazz() {
        return clazz;
    }

    /**
     * 成功时回调
     *
     * @param response 响应体
     */
    public abstract void onSuccess(HttpBaseResponse<T> response);

    /**
     * 失败时回调，此处的fail表示网络异常，数据解析异常 ，api逻辑异常不返回
     *
     * @param errorCode 失败状态码 ,可直接展示到UI
     * @param errorMsg  失败信息
     * @param error     失败异常
     */
    public abstract void onFailure(int errorCode, String errorMsg, @Nullable Throwable error);
}
