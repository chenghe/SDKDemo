package com.cgnb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cgnb.bean.RequestBean;

import org.json.JSONObject;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cn.cgnb.ses.client.util.AesUtil;
import cn.cgnb.ses.client.util.HandStringTool;
import cn.cgnb.ses.client.util.MD5Sum;
import cn.cgnb.ses.client.util.RSAUtil;

/**
 * Created by hechengbin on 2017/7/28.
 */

public class CgnbRequest {

    private static final String SHAREDPREFERENCES_NAME = "first_key";
    private static final String SHAREDPREFERENCES_NAME2 = "first_pref";

    private static HttpObject obj = new HttpObject();
    private static byte[] RHARDKEY = null;
    private static String HARDKEYINFO = null;// "myphone.wangsheng.1234" +
    public static boolean debug = false;
    public static boolean isr = true;
    public static String _TOKEN = "";
    public static String URL_PREFIX = null;// "http://172.32.11.96:8080/mobilebank/";
    public static String appversion = null;
    public static String THIRD_URL = null;// "ses/setKey/";
    public static String PUBLIC_URL = null;// "ses/public/";
    //    private String imei = null;
    private String systemversion;
    private String mac;
    private RequestBean requestBean;

    // 是否aes加密
    private boolean aes = true;
    //    private String url = null;
//    private String type = null;
//    private String j = null;
//    private String key = null;
//    private boolean strict = true;
    private String ip = "127.0.0.1";
    //    private String mac = null;
    private int k = 0;

    private Context mContext;

    public CgnbRequest(Context mContext, RequestBean requestBean) {
        this.mContext = mContext;
        this.requestBean = requestBean;
        initData();
    }


    public void initData() {

        saveIMEI(requestBean.getImei());
        if (appversion == null) {
            try {
                PackageManager manager = mContext.getPackageManager();
                PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
                appversion = info.versionName;
            } catch (Exception e) {
                // TODO: handle exception
                appversion = "0.0";
            }
        }
        if (systemversion == null) {
            systemversion = android.os.Build.MODEL + "_android_" + android.os.Build.VERSION.RELEASE;
        }

        ip = getIp();
        mac = getLocalMacAddress();

    }


    public void startRequest(CallbackContext callbackContext, RequestBean bean) {
        setInfo(URL_PREFIX, "URL_PREFIX");
        // setInfo(FIRST_URL, "FIRST_URL");
        // setInfo(SECOND_URL, "SECOND_URL");
        setInfo(THIRD_URL, "THIRD_URL");
        setInfo(PUBLIC_URL, "PUBLIC_URL");
        if (debug) {
            System.out.println("log:----------------------- " + bean.getUrl());
            System.out.println("log:----------------------- " + bean.getType());
            System.out.println("log:----------------------- " + bean.getJson());
            System.out.println("log:----------------------- " + bean.getKey());
        }
        long time = System.currentTimeMillis();
        functionRequest(callbackContext, bean);

    }

    public void startFirst(CallbackContext callbackContext, RequestBean bean) {
        setInfo(URL_PREFIX, "URL_PREFIX");
        // setInfo(FIRST_URL, "FIRST_URL");
        // setInfo(SECOND_URL, "SECOND_URL");
        setInfo(THIRD_URL, "THIRD_URL");
        setInfo(PUBLIC_URL, "PUBLIC_URL");
        if (debug) {
            System.out.println("log:----------------------- " + bean.getUrl());
            System.out.println("log:----------------------- " + bean.getType());
            System.out.println("log:----------------------- " + bean.getJson());
            System.out.println("log:----------------------- " + bean.getKey());
        }

        long time = System.currentTimeMillis();
        functionRequest(callbackContext, bean);


    }


    @SuppressLint("DefaultLocale")
    private void functionRequest(final CallbackContext callbackContext, final RequestBean requestBean) {
        try {
            new Thread() {
                public void run() {
                    String s = null;
                    if (requestBean != null && requestBean.getAes()) {
                        long time = System.currentTimeMillis();
                        if (debug) {
                            Log.i("debug_info_functionRequest", "start");
                        }
                        RHARDKEY = Base64Util.decode(getInfo("key"));
                        HARDKEYINFO = getInfo("did") == null ? null : getInfo("did");
                        TelephonyManager tm = (TelephonyManager) mContext
                                .getSystemService(Context.TELEPHONY_SERVICE);
                        String iccid = tm.getSimSerialNumber(); // 取出ICCID
                        String imsi = tm.getSubscriberId();

                        if (iccid == null)
                            iccid = "";
                        if (imsi == null)
                            imsi = "";

                        String hardkeyinfo_ = "myphone" + "_iccid~" + iccid + "_imsi~" + imsi + "_imei~" + requestBean.getImei();// +
                        // "&"
                        // +
                        // System.currentTimeMillis();
                        obj.setRequest(HttpRequestAes.getR());
                        // app初次启动 没有主密钥或者硬件特征码没有，或者硬件特征码发生变动的情况下
                        if (RHARDKEY == null || HARDKEYINFO == null || !hardkeyinfo_.equals(HARDKEYINFO)) {
                            // 初次启动，生成主密钥,生成设备特征码，并保存
                            try {
                                if (debug) {
                                    System.out.println("app初次启动");
                                }
                                RHARDKEY = AesUtil.generateKey();

                                if (iccid == null)
                                    iccid = "";
                                if (imsi == null)
                                    imsi = "";

                                HARDKEYINFO = "myphone" + "_iccid~" + iccid + "_imsi~" + imsi + "_imei~" + requestBean.getImei();// +
                                if (debug) {
                                    time = System.currentTimeMillis();
                                    Log.i("debug_info_functionRequest", "1");
                                }
                                // init(obj, HARDKEYINFO, RHARDKEY);
                                if (debug) {
                                    Log.i("debug_info_functionRequest", "2 " + (System.currentTimeMillis() - time));
                                    time = System.currentTimeMillis();
                                }
                                boolean is = onStart(obj, HARDKEYINFO, RHARDKEY);
                                if (debug) {
                                    Log.i("debug_info_functionRequest", "3 " + (System.currentTimeMillis() - time));
                                }
                                if (!is) {
                                    System.out.println("shankhands failed 2");
                                    obj.setWorkKey(null);
                                    PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                            "shankhands failed");
                                    result.setKeepCallback(true);
                                    callbackContext.sendPluginResult(result);
                                    return;
                                } else {
                                }
                                setInfo(Base64Util.encode(RHARDKEY), "key");
                                setInfo(HARDKEYINFO, "did");
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                // e.printStackTrace();
                                String msg = errorMsg(obj.getRequest(), e.getMessage());
                                System.out.println("error:---" + msg);

                                if (obj.getRequest().ERROR_TYPE == HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT) {
                                    if (debug) {
                                        System.out.println("log:自动重连");
                                    }
                                    System.out.println("shankhands failed 3");
                                    obj.setWorkKey(null);
                                    PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                            "shankhands failed");
                                    result.setKeepCallback(true);
                                    callbackContext.sendPluginResult(result);
                                    return;
                                }

                                PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                        msg /** e.getMessage() **/
                                );
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                                RHARDKEY = null;
                                HARDKEYINFO = null;
                                setInfo(null, "key");
                                setInfo(null, "did");
                                return;
                            }
                        } else if (/* run || */obj == null || obj.getWorkKey() == null) {
                            // app每次启动
                            // run = false;
                            try {
                                if (debug) {
                                    Log.i("debug_info_functionRequest", "4 ");
                                    time = System.currentTimeMillis();
                                }
                                boolean is = onStart(obj, HARDKEYINFO, RHARDKEY);
                                if (debug) {
                                    Log.i("debug_info_functionRequest", "5 " + (System.currentTimeMillis() - time));
                                }
                                if (!is) {
                                    System.out.println("shankhands failed 4");
                                    obj.setWorkKey(null);
                                    PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                            "shankhands failed");
                                    result.setKeepCallback(true);
                                    callbackContext.sendPluginResult(result);
                                    return;
                                } else {
                                    // PluginResult result = new
                                    // PluginResult(PluginResult.Status.OK,HttpRequestAes.order);
                                    // result.setKeepCallback(true);
                                    // callbackContext.sendPluginResult(result);
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                // e.printStackTrace();
                                String msg = errorMsg(obj.getRequest(), e.getMessage());
                                if (debug) {
                                    System.out.println("error:---" + msg);
                                }

                                if (obj.getRequest().ERROR_TYPE == HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT) {
                                    if (debug) {
                                        System.out.println("log:自动重连");
                                    }
                                    System.out.println("shankhands failed 5");
                                    obj.setWorkKey(null);
                                    PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                            "shankhands failed");
                                    result.setKeepCallback(true);
                                    callbackContext.sendPluginResult(result);
                                    return;
                                }

                                PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                        msg /** e.getMessage() **/
                                );
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                                RHARDKEY = null;
                                HARDKEYINFO = null;
                                setInfo(null, "key");
                                setInfo(null, "did");
                                return;
                            }
                        }

                        if (requestBean.getUrl() == null) {
                            PluginResult result = new PluginResult(PluginResult.Status.OK, HttpRequestAes.order);
                            result.setKeepCallback(true);
                            callbackContext.sendPluginResult(result);
                            return;
                        }

                        // 业务逻辑
                        try {

                            if (debug) {
                                Log.i("debug_info_functionRequest", "6 ");
                                time = System.currentTimeMillis();
                            }

                            s = business(obj.getRequest(), requestBean.getUrl(), requestBean.getJson(),
                                    obj.getSessionId(), obj.getWorkKey(), HARDKEYINFO);

                            if (debug) {
                                Log.i("debug_info_functionRequest", "7 " + (System.currentTimeMillis() - time));
                            }

                            if ("shankhands failed".equals(s)) {
                                System.out.println("shankhands failed 6");
                                obj.setWorkKey(null);
                                PluginResult result = new PluginResult(PluginResult.Status.ERROR, "shankhands failed");
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                                return;
                            }
                            if (HttpRequestAes.timeout) {
                                s = null;
                                obj.setWorkKey(null);
                                // obj.setSessionId(null);
                            }
                        } catch (Exception e) {
                            e.getMessage();

                            String msg = errorMsg(obj.getRequest(), e.getMessage());

                            if (debug) {
                                System.out.println("error:---" + msg);
                            }

                            obj.setWorkKey(null);
                            // obj.setSessionId(null);

                            if (debug) {
                                System.out.println("log:error-------------------e=" + e.getMessage());
                            }

                            if (obj.getRequest().ERROR_TYPE == HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT) {
                                if (debug) {
                                    System.out.println("log:自动重连");
                                }
                                System.out.println("shankhands failed 7");
                                obj.setWorkKey(null);
                                PluginResult result = new PluginResult(PluginResult.Status.ERROR, "shankhands failed");
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                                return;
                            }

                            if (HttpRequestAes.timeout) {
                                PluginResult result = new PluginResult(PluginResult.Status.ERROR, "连接超时");
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                            } else {
                                PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                        msg/** e.getMessage() **/
                                );
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                            }
                            return;
                        }

                    } else {
                        HttpRequest r = HttpRequest.getR(mContext);
                        r.setStrict(requestBean.isStrict());
                        r.setURL(requestBean.getUrl());
                        try {
                            String net = "未知";

                            try {
                                net = NetUtil.getCurrentNetworkType();
                            } catch (Exception e2) {
                                // TODO: handle exception
                                //e2.printStackTrace();
                            }

                            String only_post_once_transcation_cache_id = requestBean.getImei() + "_" + System.currentTimeMillis() + "_"
                                    + Math.random() + "";
                            /*String BodyString = json.substring(0, json.length() - 1) + ",\"deviceid\":\"" + HARDKEYINFO
                                    + "\",\"deviceinfo\":\"" + "deviceIp=" + ip + "&mac=" + mac + "&net=" + net
									+ "&appversion=" + appversion + "&systemversion=" + systemversion
									+ "&only_post_once_transcation_cache_id=" + only_post_once_transcation_cache_id
									+ "\"}";*/

                            String BodyString = requestBean.getJson().substring(0, requestBean.getJson().length() - 1) + ",\"deviceid\":\"" + HARDKEYINFO
                                    + "\",\"deviceinfo\":{\"deviceIp\":\"" + ip + "\",\"mac\":\"" + mac + "\",\"net\":\"" + net + "\",\"appversion\":\""
                                    + appversion + "\",\"systemversion\":\"" + systemversion + "\",\"only_post_once_transcation_cache_id\":\"" + r + "\"}}";

                            if (debug) {
                                System.out.println("log:-------------------BodyString=" + BodyString);
                            }

                            s = r.sendHttp(requestBean.getUrl(), requestBean.getType().toUpperCase(), requestBean.getImei(),
                                    appversion, systemversion, BodyString, ip);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (debug) {
                                System.out.println("log:error-------------------e=" + e.getMessage());
                            }
                            PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                            result.setKeepCallback(true);
                            callbackContext.sendPluginResult(result);
                            return;
                        }
                    }
                    if (s == null) {
                        s = "time out";
                        PluginResult result = new PluginResult(PluginResult.Status.ERROR, s);
                        result.setKeepCallback(true);
                        callbackContext.getCallbackId();
                        callbackContext.sendPluginResult(result);
                        if (debug) {
                            System.out.println("log:error-------------------json=" + s);
                            System.out.println("log:error----------------------- id=" + callbackContext.getCallbackId()
                                    + " url=" + requestBean.getUrl());
                        }
                        return;
                    }
                    PluginResult result = new PluginResult(PluginResult.Status.OK, s);
                    result.setKeepCallback(true);
                    callbackContext.getCallbackId();
                    callbackContext.sendPluginResult(result);
                    if (debug) {
                        System.out.println("log:success-------------------json=" + s);
                        System.out.println("log:success----------------------- id=" + callbackContext.getCallbackId()
                                + " url=" + requestBean.getUrl());
                    }
                }
            }.start();
        } catch (Exception e) {
            // isRun = false;
            // TODO Auto-generated catch block
            if (debug) {
                System.out.println("log:6-------------------json=" + e.getMessage());
            }
            e.printStackTrace();
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "error");
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            if (debug) {
                System.out.println(
                        "log:error----------------------- id=" + callbackContext.getCallbackId() + " url=" + requestBean.getUrl());
            }
        }
    }


    private synchronized void startClean(final CallbackContext callbackContext) {
        try {
            new Thread() {
                public void run() {
                    obj.setWorkKey(null);

                    if (debug) {
                        System.out.println("log:自动重连 1");
                    }
                    try {
                        boolean is = onStart(obj, HARDKEYINFO, RHARDKEY);
                        if (!is) {
                            System.out.println("shankhands failed 1");
                            obj.setWorkKey(null);
                            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "shankhands failed");
                            result.setKeepCallback(true);
                            callbackContext.sendPluginResult(result);
                            return;
                        } else {
                            // callbackContext.success(HttpRequestAes.order);
                        }
                    } catch (Exception ex) {
                        RHARDKEY = null;
                        HARDKEYINFO = null;
                        setInfo(null, "key");
                        setInfo(null, "did");
                    }

                    PluginResult result = new PluginResult(PluginResult.Status.OK, HttpRequestAes.order);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                }
            }.start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    public synchronized boolean onStart(HttpObject obj, String hardKeyInfo, byte[] rHardKey) throws Exception {
        if (obj.getWorkKey() != null) {
            // 当多次请求并发，都在握手的时候，如果判断上次握手成功，退出握手流程
            return true;
        }

        long time = System.currentTimeMillis();

        if (debug) {
            Log.i("debug_info_onStart", "1 ");
        }

        final Map<String, String> headers = new LinkedHashMap<String, String>();
        // headers.put("only_post_once_transcation_cache_id", r);
        headers.put("imei", requestBean.getImei());
        // headers.put("deviceIp", ip);
        headers.put("appversion", appversion);
        headers.put("systemversion", systemversion);
        headers.put("system", "2");

        byte[] res = obj.getRequest().sendHttpStream(obj, URL_PREFIX + PUBLIC_URL, headers, "{}".getBytes(), null);

        if (debug) {
            Log.i("debug_info_onStart", "2 " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }

        byte[] publiC = AesUtil.getPublic(res);

        if (debug) {
            Log.i("debug_info_onStart", "3 " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }

        if (debug) {
            Log.i("debug_info_onStart", "4 " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }

        if (debug) {
            Log.i("debug_info_onStart", "5 " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }

        // 工作密钥加密后
        byte[] workData = RSAUtil.encrypt(publiC, RHARDKEY);

        HandStringTool hanString = HandStringTool.makeHand(obj.getSessionId(), RHARDKEY);

        if (debug) {
            Log.i("debug_info_onStart", "6 " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }

        // String randomString = hanString.getRandomString();
        byte[] hash = hanString.getHash();
        // System.out.println(
        // "randomString:" + randomString + ", sessionId:" + sessionId + ",
        // cookie:" + cookie + ", hash:" + hash);

        // 2===========================

        byte[] response = obj.getRequest().sendHttpStream(obj, URL_PREFIX + THIRD_URL, headers,
                HandStringTool.arrayMerge(hash, HandStringTool.arrayMerge(workData, hanString.getRandomEncode())),
                null);

        if (debug) {
            Log.i("debug_info_onStart", "7 " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }

        byte[] hashBytes = HandStringTool.arraySub(response, 0, HandStringTool.LENGTH_HAND_HASH);
        // String hashString = new String(hashBytes);
        boolean check = HandStringTool.checkHandMac(obj.getSessionId(), RHARDKEY, hashBytes,
                HandStringTool.arraySub(response, HandStringTool.LENGTH_HAND_HASH)).isCheck();

        if (debug) {
            Log.i("debug_info_onStart", "8 " + (System.currentTimeMillis() - time));
        }

        // System.out.println("setKey 成功 : " + check + ", 握手信息完成 !");
        obj.setWorkKey(RHARDKEY);
        // obj.setSessionId(sessionId);
        return check;
    }

    private String errorMsg(HttpRequestAes re, String emsg) {
        String msg = null;
        // emsg = emsg.replaceAll("[^0-9]", "");
        int e = re.ERROR_TYPE;
        // if (e == 200) {
        // try {
        // emsg = emsg.substring(2, emsg.length());
        // e = Integer.parseInt(emsg);
        // } catch (Exception ex) {
        // // TODO: handle exception
        // }
        // }
        switch (e) {
            case HttpRequestAes.RETURN_STATUS_REQ_DATA_INVALIDATE:
                msg = "请求数据不完整";
                break;
            case HttpRequestAes.RETURN_STATUS_UNKNOWN_DEVICE:
                msg = "拒绝未知设备访问";
                break;
            case HttpRequestAes.RETURN_STATUS_DEVICE_KEY_NOT_FOUND:
                msg = "设备信息缺失，请卸载APP后重新安装";
                break;
            case HttpRequestAes.RETURN_STATUS_SHAKEHANDS_FAILED:
                msg = "握手失败";
                break;
            case HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT:
                msg = "会话已超时请重新登录";
                break;
            case HttpRequestAes.RETURN_STATUS_INVALIDATE_SESSION:
                msg = "会话异常";
                break;
            case HttpRequestAes.RETURN_STATUS_CHECK_DATA_FAILED:
                msg = "会话报文验证不通过";
                break;
            case HttpRequestAes.RETURN_STATUS_ENCODE_MSG_FAILED:
                msg = "返回报文加密错误";
                break;
            case HttpRequestAes.RETURN_STATUS_DECODE_REQ_FAILED:
                msg = "解密请求数据错误";
                break;
            case HttpRequestAes.RETURN_STATUS_BIND_DEVICE_KEY_FAILED:
                msg = "绑定设备主秘钥失败";
                break;
            case HttpRequestAes.RETURN_STATUS_GET_PUBLIC_KEY_FAILED:
                msg = "请求公钥失败";
                break;

            default:
                msg = "网络连接失败，" + emsg;
        }

        if (debug) {
            msg = msg + " code:" + e;
        } else {
            msg = "网络不给力，请稍后再试(" + e + ")";
        }

        return msg;
    }


    @SuppressLint("DefaultLocale")
    private void functionRequest(final String url, final String type, final String json, final String key,
                                 final CallbackContext callbackContext, final String imei, final String appversion,
                                 final String systemversion, final String ip, final boolean aes, final String mac) {
        try {
            new Thread() {
                public void run() {
                    String s = null;
                    if (aes) {
                        long time = System.currentTimeMillis();
                        if (debug) {
                            Log.i("debug_info_functionRequest", "start");
                        }
                        RHARDKEY = Base64Util.decode(getInfo("key"));
                        HARDKEYINFO = getInfo("did") == null ? null : getInfo("did");
                        TelephonyManager tm = (TelephonyManager) mContext
                                .getSystemService(Context.TELEPHONY_SERVICE);
                        String iccid = tm.getSimSerialNumber(); // 取出ICCID
                        String imsi = tm.getSubscriberId();

                        if (iccid == null)
                            iccid = "";
                        if (imsi == null)
                            imsi = "";

                        String hardkeyinfo_ = "myphone" + "_iccid~" + iccid + "_imsi~" + imsi + "_imei~" + imei;// +
                        // "&"
                        // +
                        // System.currentTimeMillis();
                        obj.setRequest(HttpRequestAes.getR());
                        // app初次启动 没有主密钥或者硬件特征码没有，或者硬件特征码发生变动的情况下
                        if (RHARDKEY == null || HARDKEYINFO == null || !hardkeyinfo_.equals(HARDKEYINFO)) {
                            // 初次启动，生成主密钥,生成设备特征码，并保存
                            try {
                                if (debug) {
                                    System.out.println("app初次启动");
                                }
                                RHARDKEY = AesUtil.generateKey();

                                if (iccid == null)
                                    iccid = "";
                                if (imsi == null)
                                    imsi = "";

                                HARDKEYINFO = "myphone" + "_iccid~" + iccid + "_imsi~" + imsi + "_imei~" + imei;// +
                                if (debug) {
                                    time = System.currentTimeMillis();
                                    Log.i("debug_info_functionRequest", "1");
                                }
                                // init(obj, HARDKEYINFO, RHARDKEY);
                                if (debug) {
                                    Log.i("debug_info_functionRequest", "2 " + (System.currentTimeMillis() - time));
                                    time = System.currentTimeMillis();
                                }
                                boolean is = onStart(obj, HARDKEYINFO, RHARDKEY);
                                if (debug) {
                                    Log.i("debug_info_functionRequest", "3 " + (System.currentTimeMillis() - time));
                                }
                                if (!is) {
                                    System.out.println("shankhands failed 2");
                                    obj.setWorkKey(null);
                                    PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                            "shankhands failed");
                                    result.setKeepCallback(true);
                                    callbackContext.sendPluginResult(result);
                                    return;
                                } else {
                                    // callbackContext.success(HttpRequestAes.order);
                                    // PluginResult result = new
                                    // PluginResult(PluginResult.Status.OK,HttpRequestAes.order);
                                    // result.setKeepCallback(true);
                                    // callbackContext.sendPluginResult(result);
                                }
                                setInfo(Base64Util.encode(RHARDKEY), "key");
                                setInfo(HARDKEYINFO, "did");
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                // e.printStackTrace();
                                String msg = errorMsg(obj.getRequest(), e.getMessage());
                                System.out.println("error:---" + msg);

                                if (obj.getRequest().ERROR_TYPE == HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT) {
                                    if (debug) {
                                        System.out.println("log:自动重连");
                                    }
                                    System.out.println("shankhands failed 3");
                                    obj.setWorkKey(null);
                                    PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                            "shankhands failed");
                                    result.setKeepCallback(true);
                                    callbackContext.sendPluginResult(result);
                                    return;
                                }

                                PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                        msg /** e.getMessage() **/
                                );
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                                RHARDKEY = null;
                                HARDKEYINFO = null;
                                setInfo(null, "key");
                                setInfo(null, "did");
                                return;
                            }
                        } else if (/* run || */obj == null || obj.getWorkKey() == null) {
                            // app每次启动
                            // run = false;
                            try {
                                if (debug) {
                                    Log.i("debug_info_functionRequest", "4 ");
                                    time = System.currentTimeMillis();
                                }
                                boolean is = onStart(obj, HARDKEYINFO, RHARDKEY);
                                if (debug) {
                                    Log.i("debug_info_functionRequest", "5 " + (System.currentTimeMillis() - time));
                                }
                                if (!is) {
                                    System.out.println("shankhands failed 4");
                                    obj.setWorkKey(null);
                                    PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                            "shankhands failed");
                                    result.setKeepCallback(true);
                                    callbackContext.sendPluginResult(result);
                                    return;
                                } else {
                                    // PluginResult result = new
                                    // PluginResult(PluginResult.Status.OK,HttpRequestAes.order);
                                    // result.setKeepCallback(true);
                                    // callbackContext.sendPluginResult(result);
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                // e.printStackTrace();
                                String msg = errorMsg(obj.getRequest(), e.getMessage());
                                if (debug) {
                                    System.out.println("error:---" + msg);
                                }

                                if (obj.getRequest().ERROR_TYPE == HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT) {
                                    if (debug) {
                                        System.out.println("log:自动重连");
                                    }
                                    System.out.println("shankhands failed 5");
                                    obj.setWorkKey(null);
                                    PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                            "shankhands failed");
                                    result.setKeepCallback(true);
                                    callbackContext.sendPluginResult(result);
                                    return;
                                }

                                PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                        msg /** e.getMessage() **/
                                );
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                                RHARDKEY = null;
                                HARDKEYINFO = null;
                                setInfo(null, "key");
                                setInfo(null, "did");
                                return;
                            }
                        }

                        if (url == null) {
                            PluginResult result = new PluginResult(PluginResult.Status.OK, HttpRequestAes.order);
                            result.setKeepCallback(true);
                            callbackContext.sendPluginResult(result);
                            return;
                        }

                        // 业务逻辑
                        try {

                            if (debug) {
                                Log.i("debug_info_functionRequest", "6 ");
                                time = System.currentTimeMillis();
                            }

                            s = business(obj.getRequest(), url, json, obj.getSessionId(), obj.getWorkKey(),
                                    HARDKEYINFO);

                            if (debug) {
                                Log.i("debug_info_functionRequest", "7 " + (System.currentTimeMillis() - time));
                            }

                            if ("shankhands failed".equals(s)) {
                                System.out.println("shankhands failed 6");
                                obj.setWorkKey(null);
                                PluginResult result = new PluginResult(PluginResult.Status.ERROR, "shankhands failed");
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                                return;
                            }
                            if (HttpRequestAes.timeout) {
                                s = null;
                                obj.setWorkKey(null);
                                // obj.setSessionId(null);
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            // e.printStackTrace();

                            e.getMessage();

                            String msg = errorMsg(obj.getRequest(), e.getMessage());

                            if (debug) {
                                System.out.println("error:---" + msg);
                            }

                            obj.setWorkKey(null);
                            // obj.setSessionId(null);

                            if (debug) {
                                System.out.println("log:error-------------------e=" + e.getMessage());
                            }

                            if (obj.getRequest().ERROR_TYPE == HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT) {
                                if (debug) {
                                    System.out.println("log:自动重连");
                                }
                                System.out.println("shankhands failed 7");
                                obj.setWorkKey(null);
                                PluginResult result = new PluginResult(PluginResult.Status.ERROR, "shankhands failed");
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                                return;
                            }

                            if (HttpRequestAes.timeout) {
                                PluginResult result = new PluginResult(PluginResult.Status.ERROR, "连接超时");
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                            } else {
                                PluginResult result = new PluginResult(PluginResult.Status.ERROR,
                                        msg/** e.getMessage() **/
                                );
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                            }
                            return;
                        }

                    } else {
                        HttpRequest r = HttpRequest.getR(mContext);
                        r.setStrict(requestBean.isStrict());
                        r.setURL(url);
                        try {
                            String net = "未知";

                            try {
                                net = NetUtil.getCurrentNetworkType();
                            } catch (Exception e2) {
                                // TODO: handle exception
                                //e2.printStackTrace();
                            }

                            String only_post_once_transcation_cache_id = imei + "_" + System.currentTimeMillis() + "_"
                                    + Math.random() + "";
                            /*String BodyString = json.substring(0, json.length() - 1) + ",\"deviceid\":\"" + HARDKEYINFO
                                    + "\",\"deviceinfo\":\"" + "deviceIp=" + ip + "&mac=" + mac + "&net=" + net
									+ "&appversion=" + appversion + "&systemversion=" + systemversion
									+ "&only_post_once_transcation_cache_id=" + only_post_once_transcation_cache_id
									+ "\"}";*/

                            String BodyString = json.substring(0, json.length() - 1) + ",\"deviceid\":\"" + HARDKEYINFO
                                    + "\",\"deviceinfo\":{\"deviceIp\":\"" + ip + "\",\"mac\":\"" + mac + "\",\"net\":\"" + net + "\",\"appversion\":\""
                                    + appversion + "\",\"systemversion\":\"" + systemversion + "\",\"only_post_once_transcation_cache_id\":\"" + r + "\"}}";

                            if (debug) {
                                System.out.println("log:-------------------BodyString=" + BodyString);
                            }

                            s = r.sendHttp(url, type.toUpperCase(), imei, appversion, systemversion, BodyString, ip);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (debug) {
                                System.out.println("log:error-------------------e=" + e.getMessage());
                            }
                            PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                            result.setKeepCallback(true);
                            callbackContext.sendPluginResult(result);
                            return;
                        }
                    }
                    if (s == null) {
                        s = "time out";
                        PluginResult result = new PluginResult(PluginResult.Status.ERROR, s);
                        result.setKeepCallback(true);
                        callbackContext.getCallbackId();
                        callbackContext.sendPluginResult(result);
                        if (debug) {
                            System.out.println("log:error-------------------json=" + s);
                            System.out.println("log:error----------------------- id=" + callbackContext.getCallbackId()
                                    + " url=" + url);
                        }
                        return;
                    }
                    PluginResult result = new PluginResult(PluginResult.Status.OK, s);
                    result.setKeepCallback(true);
                    callbackContext.getCallbackId();
                    callbackContext.sendPluginResult(result);
                    if (debug) {
                        System.out.println("log:success-------------------json=" + s);
                        System.out.println("log:success----------------------- id=" + callbackContext.getCallbackId()
                                + " url=" + url);
                    }
                }

                ;
            }.start();
        } catch (Exception e) {
            // isRun = false;
            // TODO Auto-generated catch block
            if (debug) {
                System.out.println("log:6-------------------json=" + e.getMessage());
            }
            e.printStackTrace();
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "error");
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            if (debug) {
                System.out.println(
                        "log:error----------------------- id=" + callbackContext.getCallbackId() + " url=" + url);
            }
        }
    }

    private String getIp() {
        try {
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            if (i != 0) {
                return int2ip(i);
            } else {
                return getLocalIpAddress();
            }
        } catch (Exception ex) {
            return "127.0.0.1";
        }
        // return null;
    }

    public String getLocalMacAddress() {
        String r = "";
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null)
                r = info.getMacAddress();
        }

        if (!"".equals(r)) {
            Pattern pattern = Pattern.compile("([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}");
            r = r.replaceAll("-", ":");
            if (!pattern.matcher(r).find()) {
                r = "";
            }
        }

        if ("02:00:00:00:00:00".equals(r)) {
            try {
                r = CgnbUtil.getMac();
                if (!"".equals(r)) {
                    Pattern pattern = Pattern.compile("([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}");
                    r = r.replaceAll("-", ":");
                    if (!pattern.matcher(r).find()) {
                        r = "";
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        return r;
    }

    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    private void setInfo(String v, String k) {
        SharedPreferences preferences = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // 存入数据
        editor.putString(k, v);
        // 提交修改
        editor.commit();
    }

    private String getInfo(String k) {
        SharedPreferences preferences = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME,
                Context.MODE_PRIVATE);
        String key = preferences.getString(k, null);
        if (key != null) {
            return key;
        } else {
            return null;
        }
    }

    public String business(final HttpRequestAes request, final String uri, final String requestBodyString,
                           final String sessionId, final byte[] workKey, String device) throws Exception {

        String r = requestBean.getImei() + "_" + System.currentTimeMillis() + "_" + Math.random() + "";
        String net = "未知";
        try {
            net = NetUtil.getCurrentNetworkType();
        } catch (Exception e) {
            // TODO: handle exception
        }

        String BodyString = requestBodyString.substring(0, requestBodyString.length() - 1) + ",\"deviceid\":\"" + device
                + "\",\"deviceinfo\":{\"deviceIp\":\"" + ip + "\",\"mac\":\"" + mac + "\",\"net\":\"" + net + "\",\"appversion\":\""
                + appversion + "\",\"systemversion\":\"" + systemversion + "\",\"only_post_once_transcation_cache_id\":\"" + r + "\"}}";

        byte[] requestBody = BodyString.getBytes(MD5Sum.ENCODE);
        HandStringTool hanString = HandStringTool.makeValues(sessionId, workKey, requestBody);

        long time = System.currentTimeMillis();

        if (debug) {
            Log.i("debug_info_business", "1 ");
        }

        final Map<String, String> headers = new LinkedHashMap<String, String>();
        // headers.put("only_post_once_transcation_cache_id", r);
        headers.put("imei", requestBean.getImei());
        // headers.put("deviceIp", ip);
        headers.put("appversion", appversion);
        headers.put("systemversion", systemversion);
        headers.put("system", "2");
        request.setStrict(request.isStrict());
        byte[] response = request.sendHttpStream(obj, uri, headers, hanString.getRandomEncode(), BodyString);

        if (debug) {
            Log.i("debug_info_business", "2 " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }

        if (!sessionId.equals(obj.getSessionId())) {
            // 需要重新握手

            if (debug) {
                Log.i("debug_info_business", "out 1 ");
            }

            HttpRequestAes.timeout = true;
            return "shankhands failed";
        }

        hanString = HandStringTool.checkBusinessMac(sessionId, workKey, response);

        if (debug) {
            Log.i("debug_info_business", "3 " + (System.currentTimeMillis() - time));
        }

        String msg = null;
        if (hanString.isCheck() && hanString.getPlain() != null) {
            msg = new String(hanString.getPlain(), MD5Sum.ENCODE);
            if (debug) {
                System.out.println(uri + " business 成功　: " + msg);
                Log.i("debug_info_business", "out 2 ");
            }
        } else {
            msg = new String(response, MD5Sum.ENCODE);
            if (debug) {
                System.out.println(uri + " business 失败 ! response: " + msg);
                Log.i("debug_info_business", "out 3 ");
            }
        }

        return msg;

    }


    private String getTmpIMEI() {
        String t = "android-tmp-" + CgnbUtil.createRandom(false, 3) + System.currentTimeMillis() +
                CgnbUtil.createRandom(false, 5);
        return t;
    }

    private void saveIMEI(String imei) {
        SharedPreferences preferences = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME2,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // 存入数据
        editor.putString("tmpimei", imei);
        // 提交修改
        editor.commit();
    }

}
