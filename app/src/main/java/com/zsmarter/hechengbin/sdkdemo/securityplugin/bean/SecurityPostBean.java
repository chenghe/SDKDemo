package com.zsmarter.hechengbin.sdkdemo.securityplugin.bean;

/**
 * Created by hechengbin on 2017/11/2.
 */

public class SecurityPostBean {

    private String data;//原始数据
    private String pubKey;//rsakey
//    private String aesKey;//aeskey
    private String signData;//签名后数据
    private String aesKey;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }


}
