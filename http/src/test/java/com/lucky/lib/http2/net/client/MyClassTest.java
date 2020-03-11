package com.lucky.lib.http2.net.client;

import android.os.Message;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description:  缓存测试
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/4/24 下午4:34
 */
public class MyClassTest {

    @Test
    public void test() {
        List<Message> list = new ArrayList<>();
        list.add(new Message());
        list.getClass().getGenericSuperclass();
//        a();
    }

    public void a() {
        //缓存文件夹
        File cacheFile = new File("cache");
        //缓存大小为10M
        int cacheSize = 10 * 1024 * 1024;
        //创建缓存对象
        final Cache cache = new Cache(cacheFile, cacheSize);


        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        //官方的一个示例的url
        String url = "********";

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call1 = client.newCall(request);
        Response response1 = null;
        try {
            //第一次网络请求
            response1 = call1.execute();
            response1.body().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Call call12 = client.newCall(request);

        try {
            //第二次网络请求
            Response response2 = call12.execute();
            response2.body().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
