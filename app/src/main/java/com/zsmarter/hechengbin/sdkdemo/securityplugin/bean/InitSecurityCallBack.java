package com.zsmarter.hechengbin.sdkdemo.securityplugin.bean;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by hechengbin on 2017/11/2.
 */

public abstract class InitSecurityCallBack extends Callback<InitSecurityBean> {

    @Override
    public InitSecurityBean parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        InitSecurityBean bean = new Gson().fromJson(string, InitSecurityBean.class);
        return bean;
    }
}
