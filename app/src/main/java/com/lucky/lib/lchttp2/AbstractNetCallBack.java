package com.lucky.lib.lchttp2;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.lucky.lib.http2.HttpBaseResponse;
import com.lucky.lib.http2.AbstractHttpCallBack;

/**
 * 作用描述: 网络回调
 *
 * @author : xmq
 * @date : 2018/10/29 下午3:38
 */
public abstract class AbstractNetCallBack<T> extends AbstractHttpCallBack<T> {

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onSuccess(final HttpBaseResponse response) {
        if (response.getCode() == 1) {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    success((T) response.getContent());
                }
            });
        } else {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    fail(response.getCode(), response.getMsg(), response.getBusiCode(), null);
                }
            });
        }
    }

    @Override
    public void onFailure(final int errorCode, final String errorMsg, @Nullable final Throwable error) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                fail(errorCode, errorMsg, "", error);
            }
        });
    }

    /**
     * 成功回调
     *
     * @param t
     */
    public abstract void success(T t);

    /**
     * 失败回调
     *
     * @param errorCode
     * @param errorMsg
     * @param busiCode
     * @param error
     */
    public abstract void fail(int errorCode, String errorMsg, String busiCode, @Nullable Throwable error);

}
