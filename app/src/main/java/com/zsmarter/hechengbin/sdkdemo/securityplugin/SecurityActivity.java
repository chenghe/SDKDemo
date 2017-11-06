package com.zsmarter.hechengbin.sdkdemo.securityplugin;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zsmarter.hechengbin.sdkdemo.R;
import com.zsmarter.hechengbin.sdkdemo.securityplugin.bean.InitSecurityBean;
import com.zsmarter.hechengbin.sdkdemo.securityplugin.bean.InitSecurityCallBack;
import com.zsmarter.hechengbin.sdkdemo.securityplugin.bean.SecurityBaseCallBack;
import com.zsmarter.hechengbin.sdkdemo.securityplugin.bean.SecurityBean;
import com.zsmarter.hechengbin.sdkdemo.securityplugin.bean.SecurityPostBean;
import com.zsmarter.hechengbin.sdkdemo.securityplugin.util.AesEncodeUtil;
import com.zsmarter.hechengbin.sdkdemo.securityplugin.util.RSA;

import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by hechengbin on 2017/11/1.
 */

public class SecurityActivity extends Activity implements View.OnClickListener {

    public static final String SUCCESS = "200";
    private Button btInit;
    private Button btFirst;
    private Button btSecond;
    private boolean TIMEOUT = false;
    private CountDownTimer countDownTimer;//计时器

    private String baseUrl = "http://192.168.6.136:8080/security/";
    private String webData = "test1234567890";
    private String webRasPubKey;//网络获取 后台 rsa public key
    private String webRasPriKey;//网络获取 后台 rsa public key
    private String rsaPubKey;//本地生成rsa public key
    private String rsaPriKey;//本地生成rsa private key
    private String aesKey;//本地生成 aes key
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        btInit = (Button) findViewById(R.id.bt_init);
        btInit.setOnClickListener(this);
        btFirst = (Button) findViewById(R.id.bt_first);
        btFirst.setOnClickListener(this);
        btSecond = (Button) findViewById(R.id.bt_sec);
        btSecond.setOnClickListener(this);


    }

    private void initTimeCount() {
        countDownTimer = new CountDownTimer(20 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                                        Log.i("hcb","onTick" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                TIMEOUT = true;
            }
        }.start();
    }

    private void showError(String error){
        Toast.makeText(SecurityActivity.this,error,
                Toast.LENGTH_SHORT).show();
    }

    private void initKey(){
        RSA rsa = new RSA();
        try {
            Map<String,Object> rsaMap = rsa.initKey();
            rsaPubKey = rsa.getPublicKey(rsaMap);
            rsaPriKey = rsa.getPrivateKey(rsaMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

       aesKey =  AesEncodeUtil.getRandomString(16);
    }

    private SecurityPostBean encodeData(SecurityPostBean securityPostBean) throws Exception {

        String signData = RSA.sign(webData.getBytes(),rsaPriKey);
//        String rsaAes = new String(RSA.encryptByPublicKey(aesKey.getBytes(),webRasPubKey),"UTF-8");

        String enRsaAes = RSA.bytesToString(RSA.encryptByPublicKey(aesKey.getBytes("UTF-8"),webRasPubKey));
        String deRsaAes = new String(RSA.decryptByPrivateKey(RSA.StringyoByte(enRsaAes),webRasPriKey));
        Log.i("hcb","enRsaAes "+enRsaAes);
        Log.i("hcb","deRsaAes "+deRsaAes);
        securityPostBean.setAesKey(enRsaAes);
//        String  aes = new String(RSA.decryptByPrivateKey(rsaAes.getBytes(),rsaPriKey),"UTF-8");
//        Log.i("hcb","aes "+aes);
//        securityPostBean.setAesKey(new String(aesKey.getBytes()));
        securityPostBean.setSignData(AesEncodeUtil.encrypt(signData,aesKey));
        securityPostBean.setPubKey(AesEncodeUtil.encrypt(rsaPubKey,aesKey));
        securityPostBean.setData(AesEncodeUtil.encrypt(webData,aesKey));
        securityPostBean.setToken(token);
//        Log.i("hcb","encodedata "+data);
        return securityPostBean;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_init:

                if (TextUtils.isEmpty(token)){
                    initKey();
                }
                OkHttpUtils.get()
                        .url(baseUrl + "init")
                        .addParams("imei", "654321")
                        .addParams("token", "")
                        .build()
                        .execute(new InitSecurityCallBack() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(InitSecurityBean response, int id) {
                                if (response.code.equals("200")) {
                                    //开始计时
//                                    initTimeCount();
                                    webRasPubKey = response.getContext().getPublicKey();
                                    webRasPriKey = response.getContext().getPrivateKey();
                                    token = response.getContext().getToken();
                                    Log.i("hcb","webRasPubKey   " + webRasPubKey);
                                    Log.i("hcb","webRasPriKey   " + webRasPriKey);
                                } else {
                                    showError(response.message);
                                }
                            }
                        });
                break;
            case R.id.bt_first:
//                String postData = "";
//
//                try {
//                     postData = encodeData();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                SecurityPostBean securityPostBean = new SecurityPostBean();
                try {
                    securityPostBean = encodeData(securityPostBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (TIMEOUT) {
                    //超时请求
                } else {
                    OkHttpUtils.postString()
                            .url(baseUrl + "postPlugin")
                            .content(new Gson().toJson(securityPostBean))
                            .mediaType(MediaType.parse("application/json; charset=utf-8"))
                            .build()
                            .execute(new SecurityBaseCallBack() {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public void onResponse(SecurityBean response, int id) {
                                    Log.i("hcb","code   "+ response.getCode());
                                    Log.i("hcb","message   "+ response.getMessage());
                                }
                            });
                }
                break;
            case R.id.bt_sec:
                    OkHttpUtils.get()
                            .url(baseUrl+"getDataList")
                            .addParams("token",token)
                            .build()
                            .execute(new SecurityBaseCallBack() {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public void onResponse(SecurityBean response, int id) {

                                    if (response.getCode().equals(SUCCESS)){

                                    }else {
                                        Log.i("hcb","code   "+ response.getCode());
                                        Log.i("hcb","message   "+ response.getMessage());
                                        showToast(response.getMessage());
                                    }
                                }
                            });
        }
    }

    private String toJson(Object object){
        return new Gson().toJson(object);
    }

    private void showToast (String content){
        Toast.makeText(SecurityActivity.this,content,Toast.LENGTH_SHORT).show();
    }

}
