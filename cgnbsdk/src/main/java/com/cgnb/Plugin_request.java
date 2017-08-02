//package com.cgnb;
//
//import java.io.FileReader;
//import java.io.InputStreamReader;
//import java.io.LineNumberReader;
//import java.io.Reader;
//import java.io.UnsupportedEncodingException;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.util.Enumeration;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.regex.Pattern;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaPlugin;
//import org.apache.cordova.PluginResult;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import com.cgbank.http.Base64Util;
//import com.cgbank.http.HttpObject;
//import com.cgbank.http.HttpRequest;
//import com.cgbank.http.HttpRequestAes;
//import com.cgbank.http.NetUtil;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import cn.cgnb.ses.client.util.AesUtil;
//import cn.cgnb.ses.client.util.HandStringTool;
//import cn.cgnb.ses.client.util.MD5Sum;
//import cn.cgnb.ses.client.util.RSAUtil;
//
///**
// * @author liukai
// *
// */
//public class Plugin_request extends CordovaPlugin {
//
//	// private static List<RequestBean> beans = new ArrayList<RequestBean>();
//	// private static boolean isRun = false;
//
//	private String imei = null;
//	public static String appversion = null;
//	private String systemversion = null;
//	public static boolean debug = false;
//	public static boolean isr = true;
//	// 是否aes加密
//	private boolean aes = true;
//	private static final String SHAREDPREFERENCES_NAME = "first_key";
//	private static final String SHAREDPREFERENCES_NAME2 = "first_pref";
//	// AesUtil.generateKey();
//	private static byte[] RHARDKEY = null;
//	private static String HARDKEYINFO = null;// "myphone.wangsheng.1234" +
//												// System.currentTimeMillis();
//	// private static boolean run = false;
//
//	public static String _TOKEN = "";
//
//	public static String URL_PREFIX = null;// "http://172.32.11.96:8080/mobilebank/";
//	// public static String FIRST_URL = null;// "ses/mykey/";
//	// public static String SECOND_URL = null;// "ses/getKey/";
//	public static String THIRD_URL = null;// "ses/setKey/";
//	public static String PUBLIC_URL = null;// "ses/public/";
//
//	private static HttpObject obj = new HttpObject();
//
//	// private static long time = 0;
//	// private static String lip = null;
//
//	// private String infos;
//	/**
//	 *
//	 */
//	public Plugin_request() {
//
//	}
//
//	// CallbackContext callbackContext;
//	String url = null;
//	String type = null;
//	String j = null;
//	String key = null;
//	boolean strict = true;
//	public String ip = "127.0.0.1";
//	public String mac = null;
//	int k = 0;
//
//	@SuppressWarnings("static-access")
//	@Override
//	public boolean execute(String action, JSONArray json, final CallbackContext callbackContext)
//			throws org.json.JSONException {
//		if (isr) {
//			debug = isApkDebugable(cordova.getActivity(), cordova.getActivity().getPackageName());
//			isr = false;
//		}
//
//		NetUtil.setContext(cordova.getActivity());
//		//debug = true;
//
//		// System.getProperties().put("sun.net.http.retryPost", "false");
//		System.setProperty("sun.net.http.retryPost", "false");
//
//		/*
//		 * 先获取保存的imei
//		 */
//		if (imei == null) {
//			SharedPreferences preferences = cordova.getActivity().getSharedPreferences(SHAREDPREFERENCES_NAME2,
//					Context.MODE_PRIVATE);
//			imei = preferences.getString("tmpimei", null);
//		}
//
//		if (imei == null) {
//			imei = ((TelephonyManager) cordova.getActivity().getSystemService(cordova.getActivity().TELEPHONY_SERVICE))
//					.getDeviceId();
//			// 如果imei为空获取sim卡编号
//			if (imei == null || "".equals(imei)) {
//				TelephonyManager tm = (TelephonyManager) cordova.getActivity()
//						.getSystemService(cordova.getActivity().TELEPHONY_SERVICE);
//				imei = tm.getSimSerialNumber(); // 取出ICCID
//				if (imei == null || "".equals(imei)) {
//					// sim卡编号为空,返回错误
//					// callbackContext.error("imei 获取失败");
//					// return true;
//					imei = getTmpIMEI();
//				}
//			} else {
//				try {
//					// 判断imei是否为00000
//					long t = Long.parseLong(imei);
//					if (t == 0) {
//						TelephonyManager tm = (TelephonyManager) cordova.getActivity()
//								.getSystemService(cordova.getActivity().TELEPHONY_SERVICE);
//						imei = tm.getSimSerialNumber(); // 取出ICCID
//						if (imei == null || "".equals(imei)) {
//							// sim卡编号为空,返回错误
//							// callbackContext.error("imei 获取失败");
//							// return true;
//							imei = getTmpIMEI();
//						}
//					}
//					// imei为纯数字
//				} catch (Exception e) {
//					// TODO: handle exception
//					// imei 不为纯数字
//				}
//			}
//
//			saveIMEI(imei);
//		}
//
//		// imei = "867527023159183";
//
//		if (appversion == null) {
//			/*SharedPreferences preferences = cordova.getActivity().getSharedPreferences(SHAREDPREFERENCES_NAME2,
//					cordova.getActivity().MODE_PRIVATE);
//			appversion = preferences.getString("dversion", null);
//			if (appversion == null) {
//				appversion = preferences.getString("version", null);
//			}*/
//
////			if (appversion == null) {
//				try {
//					PackageManager manager = cordova.getActivity().getPackageManager();
//					PackageInfo info = manager.getPackageInfo(cordova.getActivity().getPackageName(), 0);
//					appversion = info.versionName;
//				} catch (Exception e) {
//					// TODO: handle exception
//					appversion = "0.0";
//				}
////			}
//
//		}
//
//		if (systemversion == null) {
//			systemversion = android.os.Build.MODEL + "_android_" + android.os.Build.VERSION.RELEASE;
//		}
//
//		ip = getIp();
//
//		mac = getLocalMacAddress();
//
//		// this.callbackContext = callbackContext;
//		if (action.equals("first")) {
//			try {
//				String s = java.net.URLDecoder.decode(json.getString(0), "UTF-8");
//				if (debug) {
//					System.out.println("log:----------------------- " + s);
//				}
//				JSONObject a = new JSONObject(s);
//				url = null;
//				try {
//					url = a.getString("url");
//				} catch (Exception e) {
//					// TODO: handle exception
//					url = "";
//				}
//
//				type = null;
//
//				try {
//					type = a.getString("type");
//				} catch (Exception e) {
//					// TODO: handle exception
//					type = "";
//				}
//
//				j = null;
//				try {
//					j = a.getString("json");
//				} catch (Exception e) {
//					// TODO: handle exception
//					j = "";
//				}
//
//				aes = true;
//				try {
//					aes = a.getBoolean("aes");
//				} catch (Exception e) {
//					// TODO: handle exception
//					aes = true;
//				}
//
//				key = null;
//
//				try {
//					key = a.getString("tokenId");
//				} catch (Exception e) {
//					// TODO: handle exception
//					key = "";
//				}
//
//				strict = true;
//				try {
//					strict = a.getBoolean("strict");
//				} catch (Exception e) {
//					// TODO: handle exception
//					strict = true;
//				}
//
//				URL_PREFIX = null;
//
//				try {
//					URL_PREFIX = a.getString("URL_PREFIX");
//				} catch (Exception e) {
//					// TODO: handle exception
//					URL_PREFIX = "";
//				}
//
//				// FIRST_URL = null;
//				//
//				// try {
//				// FIRST_URL = a.getString("FIRST_URL");
//				// } catch (Exception e) {
//				// // TODO: handle exception
//				// FIRST_URL = "";
//				// }
//				//
//				// SECOND_URL = null;
//				//
//				// try {
//				// SECOND_URL = a.getString("SECOND_URL");
//				// } catch (Exception e) {
//				// // TODO: handle exception
//				// SECOND_URL = "";
//				// }
//
//				THIRD_URL = null;
//
//				try {
//					THIRD_URL = a.getString("THIRD_URL");
//				} catch (Exception e) {
//					// TODO: handle exception
//					THIRD_URL = "";
//				}
//
//				PUBLIC_URL = null;
//
//				try {
//					PUBLIC_URL = a.getString("PUBLIC_URL");
//				} catch (Exception e) {
//					// TODO: handle exception
//					PUBLIC_URL = "";
//				}
//
//				setInfo(URL_PREFIX, "URL_PREFIX");
//				// setInfo(FIRST_URL, "FIRST_URL");
//				// setInfo(SECOND_URL, "SECOND_URL");
//				setInfo(THIRD_URL, "THIRD_URL");
//				setInfo(PUBLIC_URL, "PUBLIC_URL");
//
//				if (debug) {
//					System.out.println("log:----------------------- " + url);
//					System.out.println("log:----------------------- " + type);
//					System.out.println("log:----------------------- " + j);
//					System.out.println("log:----------------------- " + key);
//					System.out.println("log:-- id=" + callbackContext.getCallbackId() + " url=" + url);
//				}
//				long time = System.currentTimeMillis();
//				if (debug) {
//					Log.i("debug_info_Plugin_request", "start 1");
//				}
//				Plugin_request.this.functionRequest(null, type, j, key, callbackContext, imei, appversion,
//						systemversion, ip, aes, mac);
//				if (debug) {
//					Log.i("debug_info_Plugin_request", "end" + (System.currentTimeMillis() - time));
//				}
//				return true;
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (action.equals("request")) {
//
//			try {
//				String s = java.net.URLDecoder.decode(json.getString(0), "UTF-8");
//				if (debug) {
//					System.out.println("log:----------------------- " + s);
//				}
//				JSONObject a = new JSONObject(s);
//				url = null;
//				try {
//					url = a.getString("url");
//				} catch (Exception e) {
//					// TODO: handle exception
//					url = "";
//				}
//
//				type = null;
//
//				try {
//					type = a.getString("type");
//				} catch (Exception e) {
//					// TODO: handle exception
//					type = "";
//				}
//
//				j = null;
//				try {
//					j = a.getString("json");
//				} catch (Exception e) {
//					// TODO: handle exception
//					j = "";
//				}
//
//				aes = true;
//				try {
//					aes = a.getBoolean("aes");
//				} catch (Exception e) {
//					// TODO: handle exception
//					aes = true;
//				}
//
//				key = null;
//
//				try {
//					key = a.getString("tokenId");
//				} catch (Exception e) {
//					// TODO: handle exception
//					key = "";
//				}
//				if (debug) {
//					System.out.println("log:----------------------- " + url);
//					System.out.println("log:----------------------- " + type);
//					System.out.println("log:----------------------- " + j);
//					System.out.println("log:----------------------- " + key);
//					System.out.println("log:-- id=" + callbackContext.getCallbackId() + " url=" + url);
//				}
//
//				if (URL_PREFIX == null
//						/* || FIRST_URL == null || SECOND_URL == null */ || THIRD_URL == null || PUBLIC_URL == null) {
//					if (aes) {
//						URL_PREFIX = getInfo("URL_PREFIX");
//						// FIRST_URL = getInfo("FIRST_URL");
//						// SECOND_URL = getInfo("SECOND_URL");
//						THIRD_URL = getInfo("THIRD_URL");
//						PUBLIC_URL = getInfo("PUBLIC_URL");
//
//						if (URL_PREFIX == null
//								/* || FIRST_URL == null || SECOND_URL == null */ || THIRD_URL == null
//								|| PUBLIC_URL == null) {
//
//							PluginResult result = new PluginResult(PluginResult.Status.ERROR,
//									"没有初始化" /** e.getMessage() **/
//							);
//							result.setKeepCallback(true);
//							callbackContext.sendPluginResult(result);
//							return true;
//						}
//					}
//				}
//
//				long time = System.currentTimeMillis();
//				if (debug) {
//					Log.i("debug_info_Plugin_request", "start 2");
//				}
//
//				Plugin_request.this.functionRequest(url, type, j, key, callbackContext, imei, appversion, systemversion,
//						ip, aes, mac);
//
//				if (debug) {
//					Log.i("debug_info_Plugin_request", "end " + (System.currentTimeMillis() - time));
//				}
//
//				return true;
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (action.equals("clean")) {
//			functionClean(callbackContext);
//			return true;
//		}
//		return false;
//
//	}
//
//	private synchronized void functionClean(final CallbackContext callbackContext) {
//		try {
//			new Thread() {
//				public void run() {
//					obj.setWorkKey(null);
//
//					if (debug) {
//						System.out.println("log:自动重连 1");
//					}
//					try {
//						boolean is = onStart(obj, HARDKEYINFO, RHARDKEY);
//						if (!is) {
//							System.out.println("shankhands failed 1");
//							obj.setWorkKey(null);
//							PluginResult result = new PluginResult(PluginResult.Status.ERROR, "shankhands failed");
//							result.setKeepCallback(true);
//							callbackContext.sendPluginResult(result);
//							return;
//						} else {
//							// callbackContext.success(HttpRequestAes.order);
//						}
//					} catch (Exception ex) {
//						RHARDKEY = null;
//						HARDKEYINFO = null;
//						setInfo(null, "key");
//						setInfo(null, "did");
//					}
//
//					PluginResult result = new PluginResult(PluginResult.Status.OK, HttpRequestAes.order);
//					result.setKeepCallback(true);
//					callbackContext.sendPluginResult(result);
//				}
//			}.start();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}
//
//	@SuppressLint("DefaultLocale")
//	private void functionRequest(final String url, final String type, final String json, final String key,
//			final CallbackContext callbackContext, final String imei, final String appversion,
//			final String systemversion, final String ip, final boolean aes, final String mac) {
//		try {
//			new Thread() {
//				public void run() {
//					String s = null;
//					if (aes) {
//						long time = System.currentTimeMillis();
//						if (debug) {
//							Log.i("debug_info_functionRequest", "start");
//						}
//						RHARDKEY = Base64Util.decode(getInfo("key"));
//						HARDKEYINFO = getInfo("did") == null ? null : getInfo("did");
//						TelephonyManager tm = (TelephonyManager) cordova.getActivity()
//								.getSystemService(Context.TELEPHONY_SERVICE);
//						String iccid = tm.getSimSerialNumber(); // 取出ICCID
//						String imsi = tm.getSubscriberId();
//
//						if (iccid == null)
//							iccid = "";
//						if (imsi == null)
//							imsi = "";
//
//						String hardkeyinfo_ = "myphone" + "_iccid~" + iccid + "_imsi~" + imsi + "_imei~" + imei;// +
//						// "&"
//						// +
//						// System.currentTimeMillis();
//						obj.setRequest(HttpRequestAes.getR());
//						// app初次启动 没有主密钥或者硬件特征码没有，或者硬件特征码发生变动的情况下
//						if (RHARDKEY == null || HARDKEYINFO == null || !hardkeyinfo_.equals(HARDKEYINFO)) {
//							// 初次启动，生成主密钥,生成设备特征码，并保存
//							try {
//								if (debug) {
//									System.out.println("app初次启动");
//								}
//								RHARDKEY = AesUtil.generateKey();
//
//								if (iccid == null)
//									iccid = "";
//								if (imsi == null)
//									imsi = "";
//
//								HARDKEYINFO = "myphone" + "_iccid~" + iccid + "_imsi~" + imsi + "_imei~" + imei;// +
//								if (debug) {
//									time = System.currentTimeMillis();
//									Log.i("debug_info_functionRequest", "1");
//								}
//								// init(obj, HARDKEYINFO, RHARDKEY);
//								if (debug) {
//									Log.i("debug_info_functionRequest", "2 " + (System.currentTimeMillis() - time));
//									time = System.currentTimeMillis();
//								}
//								boolean is = onStart(obj, HARDKEYINFO, RHARDKEY);
//								if (debug) {
//									Log.i("debug_info_functionRequest", "3 " + (System.currentTimeMillis() - time));
//								}
//								if (!is) {
//									System.out.println("shankhands failed 2");
//									obj.setWorkKey(null);
//									PluginResult result = new PluginResult(PluginResult.Status.ERROR,
//											"shankhands failed");
//									result.setKeepCallback(true);
//									callbackContext.sendPluginResult(result);
//									return;
//								} else {
//									// callbackContext.success(HttpRequestAes.order);
//									// PluginResult result = new
//									// PluginResult(PluginResult.Status.OK,HttpRequestAes.order);
//									// result.setKeepCallback(true);
//									// callbackContext.sendPluginResult(result);
//								}
//								setInfo(Base64Util.encode(RHARDKEY), "key");
//								setInfo(HARDKEYINFO, "did");
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								// e.printStackTrace();
//								String msg = errorMsg(obj.getRequest(), e.getMessage());
//								System.out.println("error:---" + msg);
//
//								if (obj.getRequest().ERROR_TYPE == HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT) {
//									if (debug) {
//										System.out.println("log:自动重连");
//									}
//									System.out.println("shankhands failed 3");
//									obj.setWorkKey(null);
//									PluginResult result = new PluginResult(PluginResult.Status.ERROR,
//											"shankhands failed");
//									result.setKeepCallback(true);
//									callbackContext.sendPluginResult(result);
//									return;
//								}
//
//								PluginResult result = new PluginResult(PluginResult.Status.ERROR,
//										msg /** e.getMessage() **/
//								);
//								result.setKeepCallback(true);
//								callbackContext.sendPluginResult(result);
//								RHARDKEY = null;
//								HARDKEYINFO = null;
//								setInfo(null, "key");
//								setInfo(null, "did");
//								return;
//							}
//						} else if (/* run || */obj == null || obj.getWorkKey() == null) {
//							// app每次启动
//							// run = false;
//							try {
//								if (debug) {
//									Log.i("debug_info_functionRequest", "4 ");
//									time = System.currentTimeMillis();
//								}
//								boolean is = onStart(obj, HARDKEYINFO, RHARDKEY);
//								if (debug) {
//									Log.i("debug_info_functionRequest", "5 " + (System.currentTimeMillis() - time));
//								}
//								if (!is) {
//									System.out.println("shankhands failed 4");
//									obj.setWorkKey(null);
//									PluginResult result = new PluginResult(PluginResult.Status.ERROR,
//											"shankhands failed");
//									result.setKeepCallback(true);
//									callbackContext.sendPluginResult(result);
//									return;
//								} else {
//									// PluginResult result = new
//									// PluginResult(PluginResult.Status.OK,HttpRequestAes.order);
//									// result.setKeepCallback(true);
//									// callbackContext.sendPluginResult(result);
//								}
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								// e.printStackTrace();
//								String msg = errorMsg(obj.getRequest(), e.getMessage());
//								if (debug) {
//									System.out.println("error:---" + msg);
//								}
//
//								if (obj.getRequest().ERROR_TYPE == HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT) {
//									if (debug) {
//										System.out.println("log:自动重连");
//									}
//									System.out.println("shankhands failed 5");
//									obj.setWorkKey(null);
//									PluginResult result = new PluginResult(PluginResult.Status.ERROR,
//											"shankhands failed");
//									result.setKeepCallback(true);
//									callbackContext.sendPluginResult(result);
//									return;
//								}
//
//								PluginResult result = new PluginResult(PluginResult.Status.ERROR,
//										msg /** e.getMessage() **/
//								);
//								result.setKeepCallback(true);
//								callbackContext.sendPluginResult(result);
//								RHARDKEY = null;
//								HARDKEYINFO = null;
//								setInfo(null, "key");
//								setInfo(null, "did");
//								return;
//							}
//						}
//
//						if (url == null) {
//							PluginResult result = new PluginResult(PluginResult.Status.OK, HttpRequestAes.order);
//							result.setKeepCallback(true);
//							callbackContext.sendPluginResult(result);
//							return;
//						}
//
//						// 业务逻辑
//						try {
//
//							if (debug) {
//								Log.i("debug_info_functionRequest", "6 ");
//								time = System.currentTimeMillis();
//							}
//
//							s = business(obj.getRequest(), url, json, obj.getSessionId(), obj.getWorkKey(),
//									HARDKEYINFO);
//
//							if (debug) {
//								Log.i("debug_info_functionRequest", "7 " + (System.currentTimeMillis() - time));
//							}
//
//							if ("shankhands failed".equals(s)) {
//								System.out.println("shankhands failed 6");
//								obj.setWorkKey(null);
//								PluginResult result = new PluginResult(PluginResult.Status.ERROR, "shankhands failed");
//								result.setKeepCallback(true);
//								callbackContext.sendPluginResult(result);
//								return;
//							}
//							if (HttpRequestAes.timeout) {
//								s = null;
//								obj.setWorkKey(null);
//								// obj.setSessionId(null);
//							}
//						} catch (Exception e) {
//							// TODO: handle exception
//							// e.printStackTrace();
//
//							e.getMessage();
//
//							String msg = errorMsg(obj.getRequest(), e.getMessage());
//
//							if (debug) {
//								System.out.println("error:---" + msg);
//							}
//
//							obj.setWorkKey(null);
//							// obj.setSessionId(null);
//
//							if (debug) {
//								System.out.println("log:error-------------------e=" + e.getMessage());
//							}
//
//							if (obj.getRequest().ERROR_TYPE == HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT) {
//								if (debug) {
//									System.out.println("log:自动重连");
//								}
//								System.out.println("shankhands failed 7");
//								obj.setWorkKey(null);
//								PluginResult result = new PluginResult(PluginResult.Status.ERROR, "shankhands failed");
//								result.setKeepCallback(true);
//								callbackContext.sendPluginResult(result);
//								return;
//							}
//
//							if (HttpRequestAes.timeout) {
//								PluginResult result = new PluginResult(PluginResult.Status.ERROR, "连接超时");
//								result.setKeepCallback(true);
//								callbackContext.sendPluginResult(result);
//							} else {
//								PluginResult result = new PluginResult(PluginResult.Status.ERROR,
//										msg/** e.getMessage() **/
//								);
//								result.setKeepCallback(true);
//								callbackContext.sendPluginResult(result);
//							}
//							return;
//						}
//
//					} else {
//						HttpRequest r = HttpRequest.getR(cordova.getActivity());
//						r.setStrict(strict);
//						r.setURL(url);
//						try {
//							String net = "未知";
//
//							try {
//								net = NetUtil.getCurrentNetworkType();
//							} catch (Exception e2) {
//								// TODO: handle exception
//								//e2.printStackTrace();
//							}
//
//							String only_post_once_transcation_cache_id = imei + "_" + System.currentTimeMillis() + "_"
//									+ Math.random() + "";
//							/*String BodyString = json.substring(0, json.length() - 1) + ",\"deviceid\":\"" + HARDKEYINFO
//									+ "\",\"deviceinfo\":\"" + "deviceIp=" + ip + "&mac=" + mac + "&net=" + net
//									+ "&appversion=" + appversion + "&systemversion=" + systemversion
//									+ "&only_post_once_transcation_cache_id=" + only_post_once_transcation_cache_id
//									+ "\"}";*/
//
//							String BodyString = json.substring(0, json.length() - 1) + ",\"deviceid\":\"" + HARDKEYINFO
//							+ "\",\"deviceinfo\":{\"deviceIp\":\"" + ip + "\",\"mac\":\"" + mac + "\",\"net\":\"" + net + "\",\"appversion\":\""
//							+ appversion + "\",\"systemversion\":\"" + systemversion + "\",\"only_post_once_transcation_cache_id\":\"" + r + "\"}}";
//
//							if (debug) {
//								System.out.println("log:-------------------BodyString=" + BodyString);
//							}
//
//							s = r.sendHttp(url, type.toUpperCase(), imei, appversion, systemversion, BodyString, ip);
//						} catch (Exception e) {
//							e.printStackTrace();
//							if (debug) {
//								System.out.println("log:error-------------------e=" + e.getMessage());
//							}
//							PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
//							result.setKeepCallback(true);
//							callbackContext.sendPluginResult(result);
//							return;
//						}
//					}
//					if (s == null) {
//						s = "time out";
//						PluginResult result = new PluginResult(PluginResult.Status.ERROR, s);
//						result.setKeepCallback(true);
//						callbackContext.getCallbackId();
//						callbackContext.sendPluginResult(result);
//						if (debug) {
//							System.out.println("log:error-------------------json=" + s);
//							System.out.println("log:error----------------------- id=" + callbackContext.getCallbackId()
//									+ " url=" + url);
//						}
//						return;
//					}
//					PluginResult result = new PluginResult(PluginResult.Status.OK, s);
//					result.setKeepCallback(true);
//					callbackContext.getCallbackId();
//					callbackContext.sendPluginResult(result);
//					if (debug) {
//						System.out.println("log:success-------------------json=" + s);
//						System.out.println("log:success----------------------- id=" + callbackContext.getCallbackId()
//								+ " url=" + url);
//					}
//				};
//			}.start();
//		} catch (Exception e) {
//			// isRun = false;
//			// TODO Auto-generated catch block
//			if (debug) {
//				System.out.println("log:6-------------------json=" + e.getMessage());
//			}
//			e.printStackTrace();
//			PluginResult result = new PluginResult(PluginResult.Status.ERROR, "error");
//			result.setKeepCallback(true);
//			callbackContext.sendPluginResult(result);
//			if (debug) {
//				System.out.println(
//						"log:error----------------------- id=" + callbackContext.getCallbackId() + " url=" + url);
//			}
//		}
//	}
//
//	private String getIp() {
//		try {
//			WifiManager wifiManager = (WifiManager) cordova.getActivity().getSystemService(Context.WIFI_SERVICE);
//			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//			int i = wifiInfo.getIpAddress();
//			if (i != 0) {
//				return int2ip(i);
//			} else {
//				return getLocalIpAddress();
//			}
//		} catch (Exception ex) {
//			return "127.0.0.1";
//		}
//		// return null;
//	}
//
//	public String getLocalMacAddress() {
//		String r = "";
//		WifiManager wifi = (WifiManager) cordova.getActivity().getSystemService(Context.WIFI_SERVICE);
//		if (wifi != null) {
//			WifiInfo info = wifi.getConnectionInfo();
//			if (info != null)
//				r = info.getMacAddress();
//		}
//
//		if (!"".equals(r)) {
//			Pattern pattern = Pattern.compile("([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}");
//			r = r.replaceAll("-", ":");
//			if (!pattern.matcher(r).find()) {
//				r = "";
//			}
//		}
//
//		if ("02:00:00:00:00:00".equals(r)) {
//			try {
//				r = getMac();
//				if (!"".equals(r)) {
//					Pattern pattern = Pattern.compile("([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}");
//					r = r.replaceAll("-", ":");
//					if (!pattern.matcher(r).find()) {
//						r = "";
//					}
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//
//		return r;
//	}
//
//	public static String int2ip(int ipInt) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(ipInt & 0xFF).append(".");
//		sb.append((ipInt >> 8) & 0xFF).append(".");
//		sb.append((ipInt >> 16) & 0xFF).append(".");
//		sb.append((ipInt >> 24) & 0xFF);
//		return sb.toString();
//	}
//
//	public String getLocalIpAddress() {
//		try {
//			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//				NetworkInterface intf = en.nextElement();
//				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//					InetAddress inetAddress = enumIpAddr.nextElement();
//					if (!inetAddress.isLoopbackAddress()) {
//						return inetAddress.getHostAddress().toString();
//					}
//				}
//			}
//		} catch (SocketException ex) {
//			Log.e("WifiPreference IpAddress", ex.toString());
//		}
//		return null;
//	}
//
//	private void setInfo(String v, String k) {
//		SharedPreferences preferences = cordova.getActivity().getSharedPreferences(SHAREDPREFERENCES_NAME,
//				Context.MODE_PRIVATE);
//		Editor editor = preferences.edit();
//		// 存入数据
//		editor.putString(k, v);
//		// 提交修改
//		editor.commit();
//	}
//
//	private String getInfo(String k) {
//		SharedPreferences preferences = cordova.getActivity().getSharedPreferences(SHAREDPREFERENCES_NAME,
//				Context.MODE_PRIVATE);
//		String key = preferences.getString(k, null);
//		if (key != null) {
//			return key;
//		} else {
//			return null;
//		}
//	}
//
//	/*
//	 * public synchronized void init(HttpObject obj, String hardKeyInfo, byte[]
//	 * rHardKey) throws Exception { // HttpObject obj = new HttpObject(); //
//	 * 0===========================
//	 *
//	 * // System.out.println("rHardKey.length:" + rHardKey.length + ", //
//	 * hardKeyInfo:" + hardKeyInfo); // HttpRequestAes request =
//	 * HttpRequestAes.getR(); // obj.setRequest(request); // byte[] pub =
//	 * HandStringTool.read2ByteArray(RSAUtil.class //
//	 * .getResourceAsStream("public"));
//	 *
//	 * long time = System.currentTimeMillis();
//	 *
//	 * if (debug) { Log.i("debug_info_init", "1 "); }
//	 *
//	 * final Map<String, String> headers = new LinkedHashMap<String, String>();
//	 * // headers.put("only_post_once_transcation_cache_id", r);
//	 * headers.put("imei", imei); headers.put("deviceIp", ip);
//	 * headers.put("appversion", appversion); headers.put("systemversion",
//	 * systemversion); headers.put("system", "2");
//	 *
//	 * byte[] res = obj.getRequest().sendHttpStream(obj, URL_PREFIX +
//	 * PUBLIC_URL, headers, null, null);
//	 *
//	 * if (debug) { Log.i("debug_info_init", "2 " + (System.currentTimeMillis()
//	 * - time)); time = System.currentTimeMillis(); }
//	 *
//	 * byte[] publiC = AesUtil.getPublic(res);
//	 *
//	 * if (debug) { Log.i("debug_info_init", "3 " + (System.currentTimeMillis()
//	 * - time)); time = System.currentTimeMillis(); }
//	 *
//	 * byte[] aesKey_phoneInfo = HandStringTool.arrayMerge(rHardKey,
//	 * hardKeyInfo.getBytes(MD5Sum.ENCODE));
//	 *
//	 * if (debug) { Log.i("debug_info_init", "4 " + (System.currentTimeMillis()
//	 * - time)); time = System.currentTimeMillis(); } //
//	 * System.out.println("上传的原文长度:" + aesKey_phoneInfo.length + " 原文数据为:");
//	 * byte[] pub = RSAUtil.encrypt(publiC, aesKey_phoneInfo);
//	 *
//	 * if (debug) { Log.i("debug_info_init", "5 " + (System.currentTimeMillis()
//	 * - time)); time = System.currentTimeMillis(); }
//	 *
//	 * // System.out.println("设备主密钥的长度:" + rHardKey.length + " 数据为:");
//	 *
//	 * // System.out.println("上传的报文体长度:" + pub.length + " 数据为:");
//	 *
//	 * res = obj.getRequest().sendHttpStream(obj, URL_PREFIX + FIRST_URL,
//	 * headers, pub, null);
//	 *
//	 * if (debug) { Log.i("debug_info_init", "6 " + (System.currentTimeMillis()
//	 * - time)); time = System.currentTimeMillis(); }
//	 *
//	 * byte[] resData = AesUtil.decrypt(rHardKey, res);
//	 *
//	 * if (debug) { Log.i("debug_info_init", "7 " + (System.currentTimeMillis()
//	 * - time)); time = System.currentTimeMillis(); }
//	 *
//	 * String resString = new String(resData, MD5Sum.ENCODE); if (debug) {
//	 * System.out.println("/ses/mykey/ ===> " + resString); } }
//	 */
//
//	public synchronized boolean onStart(HttpObject obj, String hardKeyInfo, byte[] rHardKey) throws Exception {
//		if (obj.getWorkKey() != null) {
//			// 当多次请求并发，都在握手的时候，如果判断上次握手成功，退出握手流程
//			return true;
//		}
//
//		long time = System.currentTimeMillis();
//
//		if (debug) {
//			Log.i("debug_info_onStart", "1 ");
//		}
//
//		final Map<String, String> headers = new LinkedHashMap<String, String>();
//		// headers.put("only_post_once_transcation_cache_id", r);
//		headers.put("imei", imei);
//		// headers.put("deviceIp", ip);
//		headers.put("appversion", appversion);
//		headers.put("systemversion", systemversion);
//		headers.put("system", "2");
//
//		// final Map<String, String> headers = new LinkedHashMap<String,
//		// String>();
//		// headers.put(HandStringTool.REQUEST_HARD_KEY, hardKeyInfo);
//		byte[] res = obj.getRequest().sendHttpStream(obj, URL_PREFIX + PUBLIC_URL, headers, "{}".getBytes(), null);
//
//		if (debug) {
//			Log.i("debug_info_onStart", "2 " + (System.currentTimeMillis() - time));
//			time = System.currentTimeMillis();
//		}
//
//		byte[] publiC = AesUtil.getPublic(res);
//		// byte[] pub = RSAUtil.encrypt(publiC,
//		// hardKeyInfo.getBytes(MD5Sum.ENCODE));
//
//		if (debug) {
//			Log.i("debug_info_onStart", "3 " + (System.currentTimeMillis() - time));
//			time = System.currentTimeMillis();
//		}
//
//		// 1===========================
//		// 公钥密文
//		// headers.put("only_post_once_transcation_cache_id", r);
//		// headers.put("imei", imei);
//		// headers.put("deviceIp", ip);
//		// headers.put("appversion", appversion);
//		// headers.put("systemversion", systemversion);
//		// headers.put("system", "2");
//		// byte[] rsa = obj.getRequest().sendHttpStream(obj, URL_PREFIX +
//		// SECOND_URL, headers, pub, null);
//
//		if (debug) {
//			Log.i("debug_info_onStart", "4 " + (System.currentTimeMillis() - time));
//			time = System.currentTimeMillis();
//		}
//
//		// rsa 公钥明文
//		// final byte[] rsaValue = AesUtil.decrypt(rHardKey, rsa);
//		// 生成工作密钥
//		// final byte[] workKey = AesUtil.generateKey();
//		// System.out.println("workKey.length():" + workKey.length);
//
//		// String cookie = obj.getRequest().getCookie();
//
//		if (debug) {
//			Log.i("debug_info_onStart", "5 " + (System.currentTimeMillis() - time));
//			time = System.currentTimeMillis();
//		}
//
//		// 工作密钥加密后
//		byte[] workData = RSAUtil.encrypt(publiC, RHARDKEY);
//
//		// System.out.println("workData.length():" + workData.length + ",
//		// workKey:" + workKey.length);
//		// cookie:JSESSIONID=17E551744FC67350BB6AAD5E8F257F76; Path=/ses
//		// String sessionId = cookie.substring(cookie.indexOf("=") + 1,
//		// cookie.indexOf(";"));
//
//		HandStringTool hanString = HandStringTool.makeHand(obj.getSessionId(), RHARDKEY);
//
//		if (debug) {
//			Log.i("debug_info_onStart", "6 " + (System.currentTimeMillis() - time));
//			time = System.currentTimeMillis();
//		}
//
//		// String randomString = hanString.getRandomString();
//		byte[] hash = hanString.getHash();
//		// System.out.println(
//		// "randomString:" + randomString + ", sessionId:" + sessionId + ",
//		// cookie:" + cookie + ", hash:" + hash);
//
//		// 2===========================
//
//		byte[] response = obj.getRequest().sendHttpStream(obj, URL_PREFIX + THIRD_URL, headers,
//				HandStringTool.arrayMerge(hash, HandStringTool.arrayMerge(workData, hanString.getRandomEncode())),
//				null);
//
//		if (debug) {
//			Log.i("debug_info_onStart", "7 " + (System.currentTimeMillis() - time));
//			time = System.currentTimeMillis();
//		}
//
//		byte[] hashBytes = HandStringTool.arraySub(response, 0, HandStringTool.LENGTH_HAND_HASH);
//		// String hashString = new String(hashBytes);
//		boolean check = HandStringTool.checkHandMac(obj.getSessionId(), RHARDKEY, hashBytes,
//				HandStringTool.arraySub(response, HandStringTool.LENGTH_HAND_HASH)).isCheck();
//
//		if (debug) {
//			Log.i("debug_info_onStart", "8 " + (System.currentTimeMillis() - time));
//		}
//
//		// System.out.println("setKey 成功 : " + check + ", 握手信息完成 !");
//		obj.setWorkKey(RHARDKEY);
//		// obj.setSessionId(sessionId);
//		return check;
//	}
//
//	private String errorMsg(HttpRequestAes re, String emsg) {
//		String msg = null;
//		// emsg = emsg.replaceAll("[^0-9]", "");
//		int e = re.ERROR_TYPE;
//		// if (e == 200) {
//		// try {
//		// emsg = emsg.substring(2, emsg.length());
//		// e = Integer.parseInt(emsg);
//		// } catch (Exception ex) {
//		// // TODO: handle exception
//		// }
//		// }
//		switch (e) {
//		case HttpRequestAes.RETURN_STATUS_REQ_DATA_INVALIDATE:
//			msg = "请求数据不完整";
//			break;
//		case HttpRequestAes.RETURN_STATUS_UNKNOWN_DEVICE:
//			msg = "拒绝未知设备访问";
//			break;
//		case HttpRequestAes.RETURN_STATUS_DEVICE_KEY_NOT_FOUND:
//			msg = "设备信息缺失，请卸载APP后重新安装";
//			break;
//		case HttpRequestAes.RETURN_STATUS_SHAKEHANDS_FAILED:
//			msg = "握手失败";
//			break;
//		case HttpRequestAes.RETURN_STATUS_SESSION_TIMEOUT:
//			msg = "会话已超时请重新登录";
//			break;
//		case HttpRequestAes.RETURN_STATUS_INVALIDATE_SESSION:
//			msg = "会话异常";
//			break;
//		case HttpRequestAes.RETURN_STATUS_CHECK_DATA_FAILED:
//			msg = "会话报文验证不通过";
//			break;
//		case HttpRequestAes.RETURN_STATUS_ENCODE_MSG_FAILED:
//			msg = "返回报文加密错误";
//			break;
//		case HttpRequestAes.RETURN_STATUS_DECODE_REQ_FAILED:
//			msg = "解密请求数据错误";
//			break;
//		case HttpRequestAes.RETURN_STATUS_BIND_DEVICE_KEY_FAILED:
//			msg = "绑定设备主秘钥失败";
//			break;
//		case HttpRequestAes.RETURN_STATUS_GET_PUBLIC_KEY_FAILED:
//			msg = "请求公钥失败";
//			break;
//
//		default:
//			msg = "网络连接失败，" + emsg;
//		}
//
//		if (debug) {
//			msg = msg + " code:" + e;
//		} else {
//			msg = "网络不给力，请稍后再试(" + e + ")";
//		}
//
//		return msg;
//	}
//
//	// public static String getHash(final HttpRequestAes request) {
//	// List<String> ss = request.headersResponse.get(HandStringTool.HASH);
//	// System.out.println("headersResponse : " + ss);
//	// String hash_response = ss != null && ss.size() > 0 ? ss.get(0) : null;
//	// return hash_response;
//	// }
//
//	public String business(final HttpRequestAes request, final String uri, final String requestBodyString,
//			final String sessionId, final byte[] workKey, String device) throws Exception {
//
//		String r = imei + "_" + System.currentTimeMillis() + "_" + Math.random() + "";
//		String net = "未知";
//		try {
//			net = NetUtil.getCurrentNetworkType();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		String BodyString = requestBodyString.substring(0, requestBodyString.length() - 1) + ",\"deviceid\":\"" + device
//				+ "\",\"deviceinfo\":{\"deviceIp\":\"" + ip + "\",\"mac\":\"" + mac + "\",\"net\":\"" + net + "\",\"appversion\":\""
//				+ appversion + "\",\"systemversion\":\"" + systemversion + "\",\"only_post_once_transcation_cache_id\":\"" + r + "\"}}";
//
//		byte[] requestBody = BodyString.getBytes(MD5Sum.ENCODE);
//		HandStringTool hanString = HandStringTool.makeValues(sessionId, workKey, requestBody);
//
//		long time = System.currentTimeMillis();
//
//		if (debug) {
//			Log.i("debug_info_business", "1 ");
//		}
//
//		final Map<String, String> headers = new LinkedHashMap<String, String>();
//		// headers.put("only_post_once_transcation_cache_id", r);
//		headers.put("imei", imei);
//		// headers.put("deviceIp", ip);
//		headers.put("appversion", appversion);
//		headers.put("systemversion", systemversion);
//		headers.put("system", "2");
//		request.setStrict(strict);
//		byte[] response = request.sendHttpStream(obj, uri, headers, hanString.getRandomEncode(), BodyString);
//
//		if (debug) {
//			Log.i("debug_info_business", "2 " + (System.currentTimeMillis() - time));
//			time = System.currentTimeMillis();
//		}
//
//		if (!sessionId.equals(obj.getSessionId())) {
//			// 需要重新握手
//
//			if (debug) {
//				Log.i("debug_info_business", "out 1 ");
//			}
//
//			HttpRequestAes.timeout = true;
//			return "shankhands failed";
//		}
//
//		hanString = HandStringTool.checkBusinessMac(sessionId, workKey, response);
//
//		if (debug) {
//			Log.i("debug_info_business", "3 " + (System.currentTimeMillis() - time));
//		}
//
//		String msg = null;
//		if (hanString.isCheck() && hanString.getPlain() != null) {
//			msg = new String(hanString.getPlain(), MD5Sum.ENCODE);
//			if (debug) {
//				System.out.println(uri + " business 成功　: " + msg);
//				Log.i("debug_info_business", "out 2 ");
//			}
//		} else {
//			msg = new String(response, MD5Sum.ENCODE);
//			if (debug) {
//				System.out.println(uri + " business 失败 ! response: " + msg);
//				Log.i("debug_info_business", "out 3 ");
//			}
//		}
//
//		return msg;
//
//	}
//
//	public static boolean isApkDebugable(Context context, String packageName) {
//		try {
//			PackageInfo pkginfo = context.getPackageManager().getPackageInfo(packageName, 1);
//			if (pkginfo != null) {
//				ApplicationInfo info = pkginfo.applicationInfo;
//				return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
//			}
//
//		} catch (Exception e) {
//
//		}
//		return false;
//	}
//
//	private String getTmpIMEI() {
//		String t = "android-tmp-" + createRandom(false, 3) + System.currentTimeMillis() + createRandom(false, 5);
//		return t;
//	}
//
//	/**
//	 * 创建指定数量的随机字符串
//	 *
//	 * @param numberFlag
//	 *            是否是数字
//	 * @param length
//	 * @return
//	 */
//	public static String createRandom(boolean numberFlag, int length) {
//		String retStr = "";
//		String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
//		int len = strTable.length();
//		boolean bDone = true;
//		do {
//			retStr = "";
//			int count = 0;
//			for (int i = 0; i < length; i++) {
//				double dblR = Math.random() * len;
//				int intR = (int) Math.floor(dblR);
//				char c = strTable.charAt(intR);
//				if (('0' <= c) && (c <= '9')) {
//					count++;
//				}
//				retStr += strTable.charAt(intR);
//			}
//			if (count >= 2) {
//				bDone = false;
//			}
//		} while (bDone);
//
//		return retStr;
//	}
//
//	private void saveIMEI(String imei) {
//		SharedPreferences preferences = cordova.getActivity().getSharedPreferences(SHAREDPREFERENCES_NAME2,
//				Context.MODE_PRIVATE);
//		Editor editor = preferences.edit();
//		// 存入数据
//		editor.putString("tmpimei", imei);
//		// 提交修改
//		editor.commit();
//	}
//
//	/**
//	 * 获取手机的MAC地址
//	 *
//	 * @return
//	 */
//	public static String getMac() {
//		String str = "";
//		String macSerial = "";
//		try {
//			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
//			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
//			LineNumberReader input = new LineNumberReader(ir);
//
//			for (; null != str;) {
//				str = input.readLine();
//				if (str != null) {
//					macSerial = str.trim();// 去空格
//					break;
//				}
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		if (macSerial == null || "".equals(macSerial)) {
//			try {
//				return loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
//			} catch (Exception e) {
//				e.printStackTrace();
//
//			}
//
//		}
//		return macSerial;
//	}
//
//	public static String loadFileAsString(String fileName) throws Exception {
//		FileReader reader = new FileReader(fileName);
//		String text = loadReaderAsString(reader);
//		reader.close();
//		return text;
//	}
//
//	public static String loadReaderAsString(Reader reader) throws Exception {
//		StringBuilder builder = new StringBuilder();
//		char[] buffer = new char[4096];
//		int readLength = reader.read(buffer);
//		while (readLength >= 0) {
//			builder.append(buffer, 0, readLength);
//			readLength = reader.read(buffer);
//		}
//		return builder.toString();
//	}
//
//}
