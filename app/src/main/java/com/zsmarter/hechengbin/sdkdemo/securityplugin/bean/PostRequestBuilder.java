package com.zsmarter.hechengbin.sdkdemo.securityplugin.bean;

import android.text.TextUtils;

import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.request.PostStringRequest;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;

/**
 * Created by hechengbin on 2017/11/7.
 */

public class PostRequestBuilder extends OkHttpRequestBuilder<PostRequestBuilder>{

    private String content;
    private MediaType mediaType;

    public static PostRequestBuilder createInstance(){
       return new PostRequestBuilder();
    }


    public PostRequestBuilder content(String content)
    {
        this.content = content;
        return this;
    }

    public PostRequestBuilder mediaType(MediaType mediaType)
    {
        this.mediaType = mediaType;
        return this;
    }

    public PostRequestBuilder params(Map<String, String> params){
        this.params = params;
        return this;
    }

    public PostRequestBuilder addParams(String name, String value){
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)){
            if (params == null){
                Map<String, String> params = new HashMap<String, String>();
            }
            params.put(name,value);
        }
        return this;
    }

    @Override
    public RequestCall build()
    {
        return new PostStringRequest(url, tag, params, headers, content, mediaType,id).build();
    }
}
