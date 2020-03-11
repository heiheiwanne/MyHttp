coffee通用网络库：
##### 使用方式：
注：真正的网络库代码在http lib下，使用方式也可以看 [ClientTest](./http/test/com/lucky/lib/http2/net/client/ClientTest)
```
 mHttpClient.get()
                .url(regionlist)
                .enqueue(new AbstractHttpCallBack<RegionListBean>() {
                    @Override
                    public void onSuccess(HttpBaseResponse<RegionListBean> response) {
                        System.out.println("xmq");
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg, @Nullable Throwable error) {
                        System.out.println("xmq");
                    }
                });
```
```
mHttpClient.post()
                .tag(this)
                .paramObject(new StartRequest(130, 122, "*****"))
                .param("***", 130)
                .param("***", 122)
                .param("***", "*****")
                .url("*******")
                .enqueue(new AbstractHttpCallBack<UpdateBean>() {
                    @Override
                    public void onSuccess(HttpBaseResponse<UpdateBean> response) {
                        System.out.println(response.getBusiCode());
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg, @Nullable Throwable error) {
                        System.out.println(errorMsg);
                    }
                });
```

##### 内部集成
  * dns防劫持策略
  * 网络缓存支持（自定义/系统）
  * 网络性能统计
  * 数据加解密