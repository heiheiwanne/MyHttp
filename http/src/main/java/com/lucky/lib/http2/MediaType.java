package com.lucky.lib.http2;

/**
 * 作用描述: 表单提交时格式{@link okhttp3.MediaType}
 * @author : xmq
 * @date : 2018/11/26 下午3:30
 */
public interface MediaType {
    /**
     * markdown 格式
     */
    okhttp3.MediaType MEDIA_TYPE_MARKDOWN = okhttp3.MediaType.parse("text/x-markdown; charset=utf-8");
    /**
     * json格式
     */
    okhttp3.MediaType MEDIA_TYPE_JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
    /**
     * html格式
     */
    okhttp3.MediaType MEDIA_TYPE_THML = okhttp3.MediaType.parse("text/xml; charset=utf-8");
    /**
     * image格式
     */
    okhttp3.MediaType MEDIA_TYPE_IMAGE = okhttp3.MediaType.parse("image/*");
    /**
     * audio音频格式
     */
    okhttp3.MediaType MEDIA_TYPE_AUDIO = okhttp3.MediaType.parse("audio/*");
    /**
     * octest流格式
     */
    okhttp3.MediaType MEDIA_TYPE_STREAM = okhttp3.MediaType.parse("application/octet-stream");
    /**
     * form表单
     */
    okhttp3.MediaType MEDIA_TYPE_MULTIPART_FORM = okhttp3.MediaType.parse("multipart/form-data");
    /**
     * 混合格式
     */
    okhttp3.MediaType MEDIA_TYPE_MULTIPART_MIXED = okhttp3.MediaType.parse("multipart/mixed");
    okhttp3.MediaType MEDIA_TYPE_MULTIPART_ALTERNATIVE = okhttp3.MediaType.parse("multipart/alternative");
    okhttp3.MediaType MEDIA_TYPE_MULTIPART_DIGEST = okhttp3.MediaType.parse("multipart/digest");
    okhttp3.MediaType MEDIA_TYPE_MULTIPART_PARALLEL = okhttp3.MediaType.parse("multipart/parallel");
}
