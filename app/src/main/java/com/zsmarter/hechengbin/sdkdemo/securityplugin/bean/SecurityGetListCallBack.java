package com.zsmarter.hechengbin.sdkdemo.securityplugin.bean;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by hechengbin on 2017/11/6.
 */

public abstract class SecurityGetListCallBack extends Callback<GetListBean> {
    @Override
    public GetListBean parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        GetListBean bean = new Gson().fromJson(string, GetListBean.class);
        return bean;
    }
}
