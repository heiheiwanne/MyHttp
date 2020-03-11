package com.lucky.lib.http2.cache;

import com.lucky.lib.cache.disk.DiskLruCache;
import com.lucky.lib.cache.disk.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * 作用描述: 数据缓存的管理类
 * @author : xmq
 * @date : 2018/10/25 下午4:19
 */
public class HttpDiskLruCacheManager {

    private DiskLruCache mDiskLruCache;
    private String mKey;

    public HttpDiskLruCacheManager(DiskLruCache diskLruCache, String key) {
        mDiskLruCache = diskLruCache;
        mKey = getMD5String(key);
    }

    /**
     * this.request = response.request;
     * this.protocol = response.protocol;
     * this.code = response.code;
     * this.message = response.message;
     * this.handshake = response.handshake;
     * this.headers = response.headers.newBuilder();
     * this.body = response.body;
     * this.networkResponse = response.networkResponse;
     * this.cacheResponse = response.cacheResponse;
     * this.priorResponse = response.priorResponse;
     * this.sentRequestAtMillis = response.sentRequestAtMillis;
     * this.receivedResponseAtMillis = response.receivedResponseAtMillis;
     *
     * @param
     * @throws IOException
     */
    public void put(JSONObject json) throws IOException {
        DiskLruCache.Editor editor = null;
        if (mDiskLruCache ==null) {
            return;
        }
        try {
            editor = mDiskLruCache.edit(mKey);
            OutputStream outputStream = editor.newOutputStream(0);
            outputStream.write(json.toString().getBytes());
            editor.commit();
        } catch (Exception io) {
            io.printStackTrace();
            if (editor != null) {
                editor.abort();
            }
        }
    }



    public boolean remove() {
        if (mDiskLruCache ==null) {
            return false;
        }
        try {
            return mDiskLruCache.remove(mKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public String get() {
        if (mDiskLruCache ==null) {
            return null;
        }
        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(mKey);
            if (snapshot == null) {
                return null;
            }
            inputStream = snapshot.getInputStream(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String str = null;
        try {
            str = Util.readFully(new InputStreamReader(inputStream, Util.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return str;
    }


    private String getMD5String(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
