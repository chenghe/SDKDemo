package com.zsmarter.doubleinputsdk.bean;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;
import com.zsmarter.doubleinputsdk.audio.AudioUtil;
import com.zsmarter.doubleinputsdk.softencode.SoftCameraManager;
import com.zsmarter.doubleinputsdk.softencode.VideoRecordActivity;
import com.zsmarter.doubleinputsdk.utils.Keys;
import com.zsmarter.doubleinputsdk.utils.RSASign;
import com.zsmarter.doubleinputsdk.wartermarker.WaterMarkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by hechengbin on 2017/7/21.
 */

public class DoubleInput {

    public static final int DOUBLEINPUT_VIDEO_SUCCESS = 100;
    public static final int DOUBLEINPUT_VIDEO_START = 101;
    public static final int DOUBLEINPUT_WARTERMARKER_CAMERA_START = 200;
    public static final String TAG = "DoubleInput";


    public static final String DOUBLEINPUT_VIDEO_OPTION = "DOUBLEINPUT_VIDEO_OPTION";

    private Activity activity;
    private AudioUtil audio;
    private WaterMarkUtil waterMarkUtil;
    private String  httpurl = "http://di.zsmarter.com/di/ws/merchant/checkapp";
    private WaterMarkOption option = new WaterMarkOption("aaa/test","test");
    private String abPath =  Environment.getExternalStorageDirectory() + "/";
    private String key = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJ1WMYGv4N4i5/XmDl6bnEeNAEQhgXtyIL+qbEttA3IRjyJ7lY+g7Mi0Eezk+30G2wfSRT0eMDs2VsZRwjUTe1BD8yFVH2uSqnBTSZUcYsITKpxeMWj5TPa4+E2bdWGgw7oh5rc4RcZk07XKZzJZMG291zfMMvIFlnb3aVad7x63AgMBAAECgYEAhOZPvxoPOdQGV6FSNfv/kDi8JtCejOWkYAHWpAebR3kpjJkThlUKbaYdFht9iuiFjx/AOJYNa5bEyZQ6FZc1Kw0/d9dxzzAutWpX7lf4/BQYtDAi9lGllNd6GLX0S7maVXgKobsuWowouy+5A1sFD0qUPw4Zfl9/Oi6J+tMpguECQQDbFo6PkdFjkyzusWk+YrWiO0PeX/MaRo9R0rAIIa0tjBCUfVkyChmIVHmN2LxzwchyXexNb0YjTnjGOCt0c/svAkEAt9g/wjmjUAkwNLTp6e8yZyCkFimu+JSFJHekn3dS5wp+unGnxEBKrDidIpNPq5/ue6KJknZBi3t0isHvZlGS+QJBAJr+TkTLiDLFwBftcuMrugvXmaAMiVOhb2sXfmmCSZ3bZS9nrl50PrPv7z6mgkvyX/ho8e+mNRxtO4wf2L49/wkCQANV3ApFC253TDWYlqnU2iA+2ltAlFkUMLoxpX3zJ0Dj0trFFxsbY39uY4NuEsL1WtlJYJ+Un6nNKpcd358GmdkCQQCyef4lhOXy+OiNDdMyDthm3CGwApBMvoagHveUntaf8XBVZz2fVE5o6J2HhWe4n16aUj6BaGjN8dHRou1cKflQ";

    public DoubleInput(Activity activity) {
        this.activity = activity;
        audio = new AudioUtil(activity);
        waterMarkUtil = new WaterMarkUtil(option,activity);

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(TAG))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //https 配置 通用全部证书(https://github.com/hongyangAndroid/okhttputils)
//				.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }


    public void startAudio(final String path, final String name, DoubleInoutSDKKey SDKkey) {
        try {
            String sign = RSASign.sign(Keys.decryptBASE64(key),(SDKkey.getAppid()+"_"+SDKkey.getAppkey()).getBytes());
            OkHttpUtils.get()
                    .url(httpurl)
                    .addParams("signData", sign)
                    .addParams("accessKey", SDKkey.getAccessKey())
                    .build()
                    .execute(new DoubleInputCallBack() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(activity,"网络连接异常",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(DoubleInputBean response, int id) {
                            if (response.getCode().equals("00")){
                                audio.startAudio(abPath+path,name);
                            }else {
                                Toast.makeText(activity,response.getMessage(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAudio() {
        audio.stopAudio();
    }

    public void recordVideo(final VideoRecorderOptions options, DoubleInoutSDKKey SDKkey){

        try {
            Log.i(TAG,"options.getVideoPath()" + options.getVideoPath());
            SoftCameraManager.makeDirectory(options.getVideoPath());
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            String sign = RSASign.sign(Keys.decryptBASE64(key),(SDKkey.getAppid()+"_"+SDKkey.getAppkey()).getBytes());
            OkHttpUtils.get()
                    .url(httpurl)
                    .addParams("signData", sign)
                    .addParams("accessKey", SDKkey.getAccessKey())
                    .build()
                    .execute(new DoubleInputCallBack() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(activity,"网络连接异常",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(DoubleInputBean response, int id) {
                            if (response.getCode().equals("00")){

                                JSONObject videoParams  = null;
                                try {
                                    videoParams = SoftCameraManager.getVideoParams(0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JSONObject videoParams1 = null;
                                try {
                                    videoParams1 = SoftCameraManager.getVideoParams(1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SoftCameraManager.setPerformance(activity, "display", String.valueOf
                                        (videoParams), String.valueOf(videoParams1));
                                List heights = new ArrayList();
                                Iterator it = videoParams.keys();
                                while (it.hasNext()) {
                                    String key = ((String) it.next());
                                    heights.add(Integer.parseInt(key));
                                }

                                Intent intent = new Intent(activity,VideoRecordActivity.class);
                                intent.putExtra(DOUBLEINPUT_VIDEO_OPTION,options);
                                activity.startActivityForResult(intent,DOUBLEINPUT_VIDEO_START);
                            }else {
                                Toast.makeText(activity,response.getMessage(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stakeWarterMarkPicture(final WaterMarkOption option, DoubleInoutSDKKey SDKkey){
        try {
            String sign = RSASign.sign(Keys.decryptBASE64(key),(SDKkey.getAppid()+"_"+SDKkey.getAppkey()).getBytes());
            OkHttpUtils.get()
                    .url(httpurl)
                    .addParams("signData", sign)
                    .addParams("accessKey", SDKkey.getAccessKey())
                    .build()
                    .execute(new DoubleInputCallBack() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(activity,"网络连接异常",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(DoubleInputBean response, int id) {
                            if (response.getCode().equals("00")){
                                waterMarkUtil.setWaterMarkOption(option);
                                try {
                                    waterMarkUtil.waterMark();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(activity,response.getMessage(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void waterMarkActivityForResult() {
        try {
            waterMarkUtil.createWaterMark();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
