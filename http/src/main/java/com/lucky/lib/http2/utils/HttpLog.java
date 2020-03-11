package com.lucky.lib.http2.utils;

import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 作用描述: 网络日志处理类，以接口的形式抛出
 *
 * @author : xmq
 * @date : 2018/10/24 下午5:13
 */
public class HttpLog {
    private static final int MAX_LENGTH = 2000;
    private static final String TAG = "lchttp";
    /**
     * 日志打印接口，default:：不打印
     */
    private static ILog mILog;


    public static void setLog(@Nullable ILog mILog) {
        HttpLog.mILog = mILog;
    }

    public static void d(String message) {
        if (mILog != null && message != null) {
            mILog.d(message);
        }
    }

    public static void e(String message) {
        if (mILog != null && message != null) {
            mILog.e(message);
        }
    }

    public static void wtf(Throwable tr) {
        if (mILog != null && tr != null) {
            mILog.wtf(tr);
        }
    }


    public interface ILog {
        /**
         * 打印调试信息
         *
         * @param string 内容
         */
        void d(String string);

        /**
         * 打印错误日志
         * @param error error信息
         */
        void e(String error);

        /**
         * 打印错误异常数据
         *
         * @param tr tr
         */
        void wtf(Throwable tr);

        ILog DEFAULT = new ILog() {
            @Override
            public void d(String message) {
                if (message.length() > MAX_LENGTH) {
                    for (int i = 0; i < message.length(); i += MAX_LENGTH) {
                        //当前截取的长度<总长度则继续截取最大的长度来打印
                        if (i + MAX_LENGTH < message.length()) {
                            Log.i(TAG + i, message.substring(i, i + MAX_LENGTH));
                        } else {
                            //当前截取的长度已经超过了总长度，则打印出剩下的全部信息
                            Log.i(TAG + i, message.substring(i));
                        }
                    }
                } else {
                    //直接打印
                    Log.i(TAG, message);
                }
            }

            @Override
            public void e(String error) {
                Log.e(TAG,error);
            }

            @Override
            public void wtf(Throwable tr) {
                Log.wtf(TAG, tr);
            }
        };
    }

}
