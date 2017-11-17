package com.zsmarter.hechengbin.sdkdemo;

import android.app.Application;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

/**
 * Created by hechengbin on 2017/6/23.
 */

public class MyApplication extends Application{

    private OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        InputStream[] sslStream = new InputStream[1];
        try {
            sslStream[0] =  getAssets().open("server.cer");
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(sslStream, null, null);
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("hcb"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //https 配置 通用全部证书(https://github.com/hongyangAndroid/okhttputils)
				.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        Log.i("hcb","hostname==="+hostname);
                        Log.i("hcb","session==="+session.toString());
                        return true;
                    }
                })
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
