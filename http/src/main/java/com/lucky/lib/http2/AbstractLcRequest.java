package com.lucky.lib.http2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lucky.lib.cache.disk.Util;
import com.lucky.lib.http2.utils.HttpLog;
import com.lucky.lib.http2.utils.HttpSignUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.lucky.lib.http2.HttpCodeEnum.REQUEST_EXCEPTION;

/**
 * @Description: request 的基类
 * * <p/>
 * * 此类处理:
 * * 1.当请求池{@link #strictMode}开启时,管理请求池队列，进行增删。下边写法可能会造成又一个{@link WeakReference}对象泄漏。
 * * 2.回调数据处理
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/3/27 下午5:13
 */
public abstract class AbstractLcRequest implements IHttpRequest, IHttpParams {

    /**
     * 网络http200 code
     */
    private static final int SUCCESS_CODE = 200;
    /**
     * 噩梦模式读取的缓存数据
     */
    private static final String NIGHT_MODE = "night_mode";
    /**
     * 来源网络请求的数据
     */
    private static final String SUCCESS_STR = "200";
    /**
     * lucky header头，记录数据来源
     */
    private static final String LUCK_CACHE_FROM = "luck_cache_from";
    /**
     * 本地缓存
     */
    private static final String LOCATION_CACHE = "location_cache";

    /**
     * 网络错误
     */
    private static final String NET_ERROR = "network_error";
    /**
     * api数据错误
     */
    private static final String BUSI_ERROR = "busi_error";
    /**
     * 事件id
     */
    protected static final String EVENT_ID = "event_id";
    /**
     * URL后缀
     */
    private static final String URL_SUFFIX = "/";
    /**
     * &符号
     */
    static final String AND = "&";
    /**
     * =符号
     */
    static final String EQ = "=";
    /**
     * 网络请求追加参数的符号 ?
     */
    static final String PARAMS = "?";
    /**
     * app版本号
     */
    private static final String APP_VERSION = "appversion";
    /**
     * 各个端的cid参数
     */
    private static final String CID = "cid";
    /**
     * 每个手机对应的UID
     */
    private static final String UID = "uid";
    /**
     * 加密之后的数据
     */
    private static final String SIGN = "sign";
    /**
     * 网络参数Q
     */
    private static final String Q = "q";
    /**
     * 初始化大小
     */
    private static final int INIT_SIZE = 8;
    /**
     * 网络请求client
     */
    protected HttpClient mClient;
    /**
     * 此次请求独有的client
     */
    private OkHttpClient selfClient;
    /**
     * 网络tag
     */
    private Object tag;
    /**
     * 网络请求URL
     */
    private String url;
    /**
     * 网络请求host
     */
    private String baseUrl;
    /**
     * 请求状态
     */
    private AtomicBoolean requestState;
    /**
     * 严格模式，同一request是不能同时请求。 default :true
     */
    private boolean strictMode = true;
    /**
     * 懒加载，减少内存的占用
     */
    private Headers.Builder headersBuilder;
    /**
     * 参数map
     */
    private JSONObject params;
    /**
     * level缓存等级
     */
    private HttpCacheLevel cacheLevel = HttpCacheLevel.NO_LOAD_NO_CACHE;


    /**
     * 网络请求回调抽象类
     *
     * @param httpClient 网络请求的client
     */
    public AbstractLcRequest(@NonNull HttpClient httpClient) {
        requestState = new AtomicBoolean(false);
        params = new JSONObject();
        mClient = httpClient;
    }

    @Override
    public <E> void enqueue(@Nullable final AbstractHttpCallBack<E> lcNetCallBack) {
        if (strictMode && !requestState.compareAndSet(false, true)) {
            HttpLog.d("已启动严格模式，当前request已在请求队列中，无法执行此次请求. 请注意避免频繁请求!!!!");
            return;
        }
        async(new InnerCallBack<E>(lcNetCallBack));
    }


    /**
     * 异步请求网络
     *
     * @param callback 回调接口
     */
    protected void async(Callback callback) {
        if (mClient.sync()) {
            execute(callback);
            return;
        }
        if (baseUrl == null) {
            baseUrl(mClient.baseUrl());
        }
        Request request = getRequest();
        OkHttpClient client = selfClient() != null ? selfClient() : mClient.getInnerHttpClient();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    @Override
    public <E> void execute(@Nullable final AbstractHttpCallBack<E> lcNetCallBack) {
        execute(new InnerCallBack<>(lcNetCallBack));
    }


    /**
     * 真正执行的网络请求
     *
     * @param callback 回调接口
     */
    private void execute(Callback callback) {
        if (baseUrl == null) {
            baseUrl(mClient.baseUrl());
        }
        Response response;
        Request request = getRequest();
        OkHttpClient client = selfClient() != null ? selfClient() : mClient.getInnerHttpClient();
        Call call = client.newCall(request);
        try {
            response = call.execute();
            callback.onResponse(call, response);
        } catch (IOException e) {
            callback.onFailure(call, e);
        }
    }

    /**
     * 组装请求参数，参数包含下列参数
     * <pre>
     *     q         ,接口的api的参数
     *     event_id  ,用于记录请求顺序
     *     cid       ,各个端的标记量
     *     uid       ,用户id
     *     sign      ,每次请求的签名
     * </pre>
     *
     * @param params 请求参数
     * @return 参数的map
     */
    protected Map<String, String> getRequestParams(@NonNull Map<String, Object> params) {

        HashMap<String, String> requestParams = new HashMap<>(INIT_SIZE);
        //version 版本号
        if (!TextUtils.isEmpty(mClient.appVersion())) {
            params.put(APP_VERSION, mClient.appVersion());
        }
        String content = JSON.toJSONString(params);
        String q = HttpAesCrypto.encrypt(content);
        //q ,接口的api的参数
        if (!TextUtils.isEmpty(q)) {
            requestParams.put(Q, q);
        }
        //cid , 各个端的标记量
        String cid = mClient.cid();
        if (!TextUtils.isEmpty(cid)) {
            requestParams.put(CID, cid);
        }
        //uid ， 用户id
        String uid = mClient.uid();
        if (!TextUtils.isEmpty(uid)) {
            requestParams.put(UID,uid);
        }
        //sign ，每次请求的签名
        requestParams.put(SIGN, HttpSignUtils.getSign(cid, uid, q, HttpAesCrypto.getSignKey()));
        return requestParams;
    }

    /**
     * 返回Request参数
     *
     * @return request请求体
     */
    protected abstract Request getRequest();


    @Override
    public IHttpRequest openStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
        return this;
    }

    @Override
    public IHttpRequest params(@NonNull Map<String, Object> maps) {
        if (maps != null && !maps.isEmpty()) {
            params.putAll(maps);
        }
        return this;
    }

    @Override
    public IHttpRequest paramObject(@NonNull Object value) {
        if (value != null) {
            params.putAll((Map<? extends String, ? extends Object>) JSON.toJSON(value));
        }
        return this;
    }

    @Override
    public IHttpRequest param(@NonNull String key, @Nullable Object value) {
        if (key != null && value != null) {
            params.put(key, value);
        }
        return this;
    }

    @NonNull
    public Map<String, Object> params() {
        return params.getInnerMap();
    }


    @Override
    public IHttpRequest tag(@NonNull Object o) {
        this.tag = o;
        return this;
    }

    @Override
    public Object tag() {
        return tag;
    }

    @Override
    @NonNull
    public String baseUrl() {
        if (TextUtils.isEmpty(baseUrl)) {
            HttpLog.e("请正确设置baseurl！！！");
        }
        return baseUrl;
    }

    @Override
    public IHttpRequest baseUrl(@NonNull String baseUrl) {
        if (!baseUrl.endsWith(URL_SUFFIX)) {
            baseUrl = baseUrl + URL_SUFFIX;
        }
        this.baseUrl = baseUrl;
        return this;
    }

    @Override
    public IHttpRequest url(@NonNull String url) {
        if (url.startsWith(URL_SUFFIX)) {
            throw new IllegalArgumentException("url不要以反斜杠开始");
        }
        this.url = url;
        return this;
    }

    @Override
    public String url() {
        if (url == null) {
            return "";
        }
        return url;
    }

    public OkHttpClient selfClient() {
        return selfClient;
    }

    @Override
    public AbstractLcRequest selfClient(OkHttpClient okHttpClient) {
        selfClient = okHttpClient;
        return this;
    }

    @Override
    public IHttpRequest header(@NonNull String key, String value) {
        if (headersBuilder == null) {
            headersBuilder = new Headers.Builder();
        }
        headersBuilder.add(key, value);
        return this;
    }

    @Nullable
    @Override
    public Headers headers() {
        if (headersBuilder == null) {
            return null;
        }
        return headersBuilder.build();
    }

    @Override
    public IHttpRequest headers(@NonNull Map<String, String> maps) {
        if (headersBuilder == null) {
            headersBuilder = new Headers.Builder();
        }
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            headersBuilder.add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public IHttpRequest cache(@NonNull HttpCacheLevel level) {
        if (level == null) {
            return this;
        }
        cacheLevel = level;
        return this;
    }

    @NonNull
    @Override
    public HttpCacheLevel cache() {
        return cacheLevel;
    }

    /**
     * @Description: 解析处理网络请求结果 T :content类型
     * @Author: xmq mingqiang.xu@luckincoffee.com
     * @Date: 2019/3/27 下午5:45
     */
    protected class InnerCallBack<T> implements Callback {

        /**
         * 判断json串是否加密的前缀
         */
        private static final String TAG = "{\"";
        /**
         * 网络请求回调抽象类
         */
        private AbstractHttpCallBack lcNetCallBack;

        /**
         * 内部回调结果
         *
         * @param callback 网络回调
         */
        public InnerCallBack(AbstractHttpCallBack callback) {
            lcNetCallBack = callback;
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            HttpDnsManager.requestDns(mClient, url());
            if (strictMode) {
                requestState.compareAndSet(true, false);
            }
            if (lcNetCallBack == null) {
                return;
            }
            HttpCodeEnum codeEnum = HttpCodeEnum.switchEnum(e.getClass().getSimpleName());
            lcNetCallBack.onFailure(codeEnum.mCode, codeEnum.mDesc, e);
            mClient.monitorListener().networkException(NET_ERROR, e, "");
            collectNetWorkModel(call, null, null, null, null);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if (strictMode) {
                requestState.compareAndSet(true, false);
            }
            if (lcNetCallBack == null) {
                return;
            }
            if (!response.isSuccessful()) {
                //http 异常
                HttpLog.d("结果信息： "+response.toString());
                HttpDnsManager.requestDns(mClient, url());
                lcNetCallBack.onFailure(response.code(), REQUEST_EXCEPTION.mDesc, new IOException(response.toString()));
                mClient.monitorListener().busiException(BUSI_ERROR, null, response.toString());
                return;
            }
            ResponseBody body = response.body();
            String bodyStr = "";
            if (body != null) {
                bodyStr = body.string();
            }
            //当返回的是明文时,就不解密,json 返回时不解密,这里以有没有包含"{\"" 作为判断依据，仅测试时会出现不加密的数据
            if (bodyStr != null && !bodyStr.contains(TAG)) {
                bodyStr = HttpAesCrypto.decrypt(bodyStr);
            }
            HttpBaseResponse baseResponse = null;
            try {
                baseResponse = JSON.parseObject(bodyStr, new TypeReference<HttpBaseResponse<T>>(lcNetCallBack.getEntityType()) {
                });
            } catch (Exception e) {
                try {
                    baseResponse = JSON.parseObject(bodyStr, new TypeReference<HttpBaseResponse<T>>() {
                    });
                } catch (Exception innerE) {
                    dealJsonException(call, bodyStr, innerE);
                    return;
                }
            } finally {
                Util.closeQuietly(body);
            }
            //判断返回结果中是否为数组
            boolean isArray = baseResponse != null && baseResponse.getContent() instanceof JSONArray;
            boolean isList = lcNetCallBack.getContentClazz() == List.class || lcNetCallBack.getContentClazz() == ArrayList.class;
            if (isArray) {
                if (isList) {
                    //此逻辑主要针对范型传递为 class传递，而非callback中 <T> 传递造成的调用
                    baseResponse.setContent(JSONObject.parseArray(((JSONArray) baseResponse.getContent()).toJSONString(), (Class<?>) lcNetCallBack.getEntityType()));
                } else {
                    HttpLog.e("警告! 警告! 警告! content的类型错误，当前返回结果中content类型为数组");
                }
            }
            //容错 ，防止解析之后为null
            if (baseResponse == null) {
                dealJsonException(call, bodyStr, null);
                return;
            }
            //防止出现空的content list列表
            boolean isNullList = baseResponse.getContent() == null && (lcNetCallBack.getEntityType() instanceof ParameterizedType || isList);
            if (isNullList) {
                baseResponse.setContent(Collections.emptyList());
            }
            //处理api异常，上报monitor 数据
            if (baseResponse.getCode() != 1) {
                mClient.monitorListener().busiException(BUSI_ERROR, null, baseResponse.toString());
            }

            collectNetWorkModel(call, String.valueOf(baseResponse.getCode()), baseResponse.getBusiCode(), baseResponse.getMsg(), response);
            lcNetCallBack.onSuccess(baseResponse);
        }

        /**
         * 数据解析异常失败回调并上报monitor
         *
         * @param bodyStr 请求数据
         * @param innerE  解析异常
         */
        private void dealJsonException(Call call, String bodyStr, Exception innerE) {
            HttpDnsManager.requestDns(mClient, url());
            lcNetCallBack.onFailure(HttpCodeEnum.JSON_EXCEPTION.mCode, HttpCodeEnum.JSON_EXCEPTION.mDesc, innerE);
            mClient.monitorListener().busiException(BUSI_ERROR, innerE, bodyStr);
            collectNetWorkModel(call, null, null, null, null);
            HttpLog.wtf(innerE);
        }

        /**
         * 网络性能统计，这里的调用过于繁琐冗余，等monitor重构完成删除部分方法
         *
         * @param call     调用call
         * @param apiCode  api的返回码
         * @param busiCode busi的返回码
         * @param apiMsg   api的返回信息
         */
        private void collectNetWorkModel(Call call, String apiCode, String busiCode, String apiMsg, @Nullable Response response) {
            HttpEventListenerImpl eventListener = ((HttpEventListenerImpl) call.listener());
            //区分code = 200时的网络是否走的网络，或者仅仅走的磁盘
            int isCache = 0;
            if (response != null) {
                if (response.code() ==SUCCESS_CODE && response.headers() !=null) {
                    if (NIGHT_MODE.equals(response.headers().get(LUCK_CACHE_FROM))
                            || LOCATION_CACHE.equals(response.headers().get(LUCK_CACHE_FROM))) {
                        isCache = 1;
                    }
                }
                eventListener.callEnd(call);
                eventListener.code(String.valueOf(response.code()));
            }
            mClient.monitorListener().network(eventListener.createNetWorkModel(apiCode, busiCode, apiMsg, isCache));
        }
    }
}
