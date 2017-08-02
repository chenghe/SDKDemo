package com.cgnb.bean;

/**
 * Created by hechengbin on 2017/7/31.
 */

public class RequestBean {

    private String url = "";
    private String type = "";
    private String json = "";
    private String imei = "";
    private String tokenId = "";
    private String key = "";
    private String URL_PREFIX = "";
    private String FIRST_URL = "";
    private String SECOND_URL = "";
    private String THIRD_URL = "";
    private String PUBLIC_URL = "";
    private boolean strict = true;
    private boolean aes = true;

    public String getFIRST_URL() {
        return FIRST_URL;
    }

    public void setFIRST_URL(String FIRST_URL) {
        this.FIRST_URL = FIRST_URL;
    }

    public String getSECOND_URL() {
        return SECOND_URL;
    }

    public void setSECOND_URL(String SECOND_URL) {
        this.SECOND_URL = SECOND_URL;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public boolean isAes() {
        return aes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public boolean getAes() {
        return aes;
    }

    public void setAes(boolean aes) {
        this.aes = aes;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getURL_PREFIX() {
        return URL_PREFIX;
    }

    public void setURL_PREFIX(String URL_PREFIX) {
        this.URL_PREFIX = URL_PREFIX;
    }

    public String getTHIRD_URL() {
        return THIRD_URL;
    }

    public void setTHIRD_URL(String THIRD_URL) {
        this.THIRD_URL = THIRD_URL;
    }

    public String getPUBLIC_URL() {
        return PUBLIC_URL;
    }

    public void setPUBLIC_URL(String PUBLIC_URL) {
        this.PUBLIC_URL = PUBLIC_URL;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
