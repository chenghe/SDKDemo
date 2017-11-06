package com.zsmarter.hechengbin.sdkdemo.securityplugin.bean;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by hechengbin on 2017/11/6.
 */

public abstract class  SecurityBaseCallBack extends Callback<SecurityBean> {
    @Override
    public SecurityBean parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        SecurityBean bean = new Gson().fromJson(string, SecurityBean.class);
        return bean;
    }
}
