package com.lucky.lib.http2;

import android.support.annotation.NonNull;

import com.lucky.lib.http2.utils.HttpLog;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Description: AES密码机。
 *  统一编码UTF8。
 * 128位密钥；ECB分组；PKCS7填充。
 *   密钥不足128位，补填0。
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/3/27 下午5:18
 */
class HttpAesCrypto {
    /**
     * 字符串长度
     */
    private static final int KEY_BIT_SIZE = 128;
    /**
     *字符串分段的长度
     */
    private static final int SUB_SIZE = 8;
    /**
     * AES加密方式
     */
    private static final String AES = "AES";
    /**
     * 加号
     */
    private static final char ADD_SYMBOL = '+';
    /**
     * 减号
     */
    private static final char MINUS_SYMBOL = '-';
    /**
     * 下划线
     */
    private static final char UNDER_LINE_SYMBOL = '_';
    /**
     * 斜杠
     */
    private static final char SLASH_SYMBOL = '/';

    /**
     * 加解密时的套接字
     */
    private static final Charset CHAR_SET = Charset.forName("utf-8");
    /**
     * AES，简单分组，填充7
     */
    private static final String ALGORITHM = "AES/ECB/PKCS7Padding";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 加密的秘钥
     */
    private static  String signKey;

    /**
     * 设置秘钥
     * @param signKey 秘钥
     */
    public static void setSignKey(@NonNull String signKey) {
        HttpAesCrypto.signKey = signKey;
    }

    /**
     * 获取秘钥
     * @return 秘钥
     */
    public static String getSignKey() {
        return signKey;
    }
    /**
     * 加密字符串。
     *
     * @param target 原始字符串
     * @return 加密结果字符串
     */
    public static String encrypt(String target) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, initKey(signKey));
            byte[] encryptResult = cipher.doFinal(target.getBytes(CHAR_SET));
            //兼容安卓环境的1.2codec

            String unsafeStr = new String(Base64.encode(encryptResult), CHAR_SET);
            return unsafeStr.replace(ADD_SYMBOL, MINUS_SYMBOL).replace(SLASH_SYMBOL, UNDER_LINE_SYMBOL);
        } catch (Exception e) {
            HttpLog.d("数据加密异常:" + e.toString());
        }
        return "";
    }

    /**
     * 解密字符串。
     * @param target 加密结果字符串
     * @param dataEncrypted 数据是否已解密
     * @return 原始字符串
     * @throws Exception 解密时的异常
     */
    public static String decrypt(String target, boolean dataEncrypted) throws Exception {
        if (!dataEncrypted) {
            return target;
        } else {
            return decrypt(target);
        }
    }

    /**
     * 真正的解密
     * @param target 要解密的字符串
     * @return 原始数据
     * @throws IOException 解密时的异常
     */
    public static String decrypt(String target) throws IOException{
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, initKey(signKey));
            String unsafeStr = target.replace(MINUS_SYMBOL, ADD_SYMBOL).replace(UNDER_LINE_SYMBOL, SLASH_SYMBOL);
            byte[] decryptResult = cipher.doFinal(Base64.decode(unsafeStr.getBytes(CHAR_SET)));
            return new String(decryptResult, CHAR_SET);
        } catch (Exception e) {
            HttpLog.d("数据解密异常:"+ e.toString());
            HttpLog.d("返回密文:"+target);
        }
        return "";
    }

    /**
     * 生成密钥字节数组，原始密钥字符串不足128位，补填0.
     * @param originalKey 解密用的key
     * @return 解密的SecretKeySpec
     */
    private static SecretKeySpec initKey(String originalKey) {
        byte[] keys = originalKey.getBytes(CHAR_SET);

        byte[] bytes = new byte[KEY_BIT_SIZE / SUB_SIZE];
        for (int i = 0; i < bytes.length; i++) {
            if (keys.length > i) {
                bytes[i] = keys[i];
            } else {
                bytes[i] = 0;
            }
        }

        return new SecretKeySpec(bytes, AES);
    }


}
