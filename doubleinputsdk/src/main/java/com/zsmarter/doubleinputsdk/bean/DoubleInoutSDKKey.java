package com.zsmarter.doubleinputsdk.bean;

/**
 * Created by hechengbin on 2017/7/21.
 */

public class DoubleInoutSDKKey {
    private String appid;
    private String appkey;
    private String accessKey;


    public DoubleInoutSDKKey(String appid, String appkey, String accessKey) {
        this.appid = appid;
        this.appkey = appkey;
        this.accessKey = accessKey;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
}
