package com.zsmarter.hechengbin.sdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by hechengbin on 2017/6/22.
 */

public class WebActivity extends Activity{

    public static final String WEBURL="weburl";

    private WebView webView;
    private String webUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        webUrl = intent.getStringExtra(WEBURL);

        initWebView();
        if (!TextUtils.isEmpty(webUrl)){
          webView.loadUrl(webUrl);
        }


    }

    public void initWebView() {

        //下边的这句代码加上后会特别卡，并且有可能 显示的内容超出屏幕
        //mWebView.setInitialScale(100);//这里一定要设置，数值可以根据各人的需求而定，我这里设置的是50%的缩放
        webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(false);// support zoom
        webSettings.setUseWideViewPort(true);// 这个很关键
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        //webSettings.setTextZoom(300);
        //webSettings.setTextSize(WebSettings.TextSize.LARGER);

    }
}
