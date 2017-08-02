package com.zsmarter.doubleinputsdk.bean;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by hechengbin on 2017/5/26.
 */

public abstract class DoubleInputCallBack extends Callback<DoubleInputBean> {
    @Override
    public DoubleInputBean parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        DoubleInputBean user = new Gson().fromJson(string, DoubleInputBean.class);
        return user;
    }


}
