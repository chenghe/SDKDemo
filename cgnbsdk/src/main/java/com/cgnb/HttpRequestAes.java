package com.cgnb;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import cn.cgnb.ses.client.util.HandStringTool;
import cn.cgnb.ses.client.util.RSAUtil;
import cn.cgnb.ses.client.util.TrustManagerEbankCgnb;

public class HttpRequestAes {
	// private HttpURLConnection conn;
	private boolean https = false;
	private String cookie;
	private String uri = "/";
	private int connectTimeout = 30000;// 30 seconds
	private int soTimeout = connectTimeout;// 30 seconds
	private boolean setHttps = false;
	private String sslProtocol = "TLSv1.2";// TLS// SSL

	public static String order = null;
	
	boolean strict = true;

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/** 请求数据不完整 */
	public static final int RETURN_STATUS_REQ_DATA_INVALIDATE = 428;
	/** 拒绝未知设备访问 */
	public static final int RETURN_STATUS_UNKNOWN_DEVICE = 429;
	/** 设备信息缺失，请卸载APP后重新安装 */
	public static final int RETURN_STATUS_DEVICE_KEY_NOT_FOUND = 430;
	/** 握手失败 */
	public static final int RETURN_STATUS_SHAKEHANDS_FAILED = 431;
	/** 会话已超时请重新登录 */
	public static final int RETURN_STATUS_SESSION_TIMEOUT = 432;
	/** 会话异常 */
	public static final int RETURN_STATUS_INVALIDATE_SESSION = 433;
	/** 会话报文验证不通过 */
	public static final int RETURN_STATUS_CHECK_DATA_FAILED = 434;
	/** 返回报文加密错误 */
	public static final int RETURN_STATUS_ENCODE_MSG_FAILED = 435;
	/** 解密请求数据错误 */
	public static final int RETURN_STATUS_DECODE_REQ_FAILED = 436;
	/** 绑定设备主秘钥失败 */
	public static final int RETURN_STATUS_BIND_DEVICE_KEY_FAILED = 437;
	/** 请求公钥失败 */
	public static final int RETURN_STATUS_GET_PUBLIC_KEY_FAILED = 438;
	public int ERROR_TYPE = 0;

	{
		if (System.getProperty("java.version").startsWith("1.0") || System.getProperty("java.version").startsWith("1.1")
				|| System.getProperty("java.version").startsWith("1.2")
				|| System.getProperty("java.version").startsWith("1.3")) {
			sslProtocol = "SSL";
		} else if (System.getProperty("java.version").startsWith("1.4")
				|| System.getProperty("java.version").startsWith("1.5")) {
			sslProtocol = "TLSv1";
		}
	}

	private static HttpRequestAes r = null;
	public static String _TOKEN = "";
	public static String CONTENT_TYPE = "application/octet-stream";// "application/x-www-form-urlencoded;
	public static boolean timeout = false;
	// charset=utf-8";

	public byte[] sendHttpStream(HttpObject obj, String uri, Map<String, String> headers, final byte[] body,
			final String bod) throws Exception {
		if (bod == null) {
			return sendHttpStream(obj, uri, this.soTimeout, headers, body);
		} else {
			return sendHttpStream(obj, uri, this.soTimeout, headers, body, bod);
		}
	}

	public byte[] sendHttpStream(HttpObject obj, String uri, final int timeout, Map<String, String> headers,
			final byte[] body) throws Exception {
		// System.out.println("================uri:" + uri);
		byte[] result = null;
		if (uri == null) {
			uri = this.uri;
		}
		if (uri == null) {
			uri = "/";
		}
		this.uri = uri;
		if (uri.toLowerCase().startsWith("https:")) {
			https = true;
		}
		URL requestUrl = new URL(uri);
		if (https && (!setHttps)) {
			setHttpsEnv();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) requestUrl.openConnection();
			if (timeout > 0 && timeout < 300000) {// 5分钟
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
			} else {
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(soTimeout);
			}
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", CONTENT_TYPE);
			headersRequest = headers;
			if (null != headers) {
				// System.out.println("url :" + ", request-headers:" + headers);
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
					// System.out.println("set request-header:" + entry.getKey()
					// + ":" + entry.getValue());
				}
			}

			String method = "POST";
			// 加入数据
			conn.setRequestMethod(method);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			if (cookie != null && cookie.length() > 0) {
				conn.setRequestProperty("CGNBSSID", cookie);
				// System.out.println("set CGNBSSID:" + cookie);
				// FilterMessage.info("conn Cookie:", cookie);
			}
			conn.connect();
			if (body != null && body.length > 0) {
				OutputStream out = conn.getOutputStream();
				out.write(body);
				out.flush();
			}
			// 获取输入流
			int code = conn.getResponseCode();
			ERROR_TYPE = code;

			InputStream ins = conn.getInputStream();

			// if(ERROR_TYPE != 200){
			// ERROR_TYPE = 1;
			// }else{
			// ERROR_TYPE = 0;
			// }
			Map<String, List<String>> fields = conn.getHeaderFields();
			headersResponse = fields;
			// System.out.println("url :" + uri + ", code:" + code + ",
			// response-headers:" + fields);

			if (fields.get("SVR_ORDER") != null) {
				List<String> values = fields.get("SVR_ORDER");
				if (values != null && values.size() > 0) {
					String msg = values.get(values.size() - 1);
					// System.out.println("SVR_ORDER :" + msg);

					order = msg;
				}
			}

			if (fields.get("SET-CGNBSSID") != null) {
				List<String> values = fields.get("SET-CGNBSSID");
				if (values != null && values.size() > 0) {
					cookie = values.get(values.size() - 1);
					String sessionId = null;
					if (cookie.indexOf("=") != -1) {
						sessionId = cookie.substring(cookie.indexOf("=") + 1, cookie.indexOf(";"));
					} else {
						sessionId = cookie;
					}

					// System.out.println("sessionId :" + sessionId);

					obj.setSessionId(sessionId);
				}
				HttpRequestAes.timeout = true;
			} else {
				HttpRequestAes.timeout = false;
			}
			try {
				result = HandStringTool.read2ByteArray(ins);
			} catch (Exception e) {
				// TODO: handle exception
				result = null;
			}
		} finally {
			if (null != conn)
				conn.disconnect();
		}
		return result;
	}

	public byte[] sendHttpStream(HttpObject obj, String uri, final int timeout, Map<String, String> headers,
			final byte[] body, final String bod) throws Exception {
		// System.out.println("================uri:" + uri);
		byte[] result = null;
		if (uri == null) {
			uri = this.uri;
		}
		if (uri == null) {
			uri = "/";
		}
		this.uri = uri;
		if (uri.toLowerCase().startsWith("https:")) {
			https = true;
		}
		URL requestUrl = new URL(uri);
		if (https && (!setHttps)) {
			setHttpsEnv();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) requestUrl.openConnection();
			if (timeout > 0 && timeout < 300000) {// 5分钟
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
			} else {
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(soTimeout);
			}
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", CONTENT_TYPE);
			headersRequest = headers;
			if (null != headers) {
				// System.out.println("url :" + ", request-headers:" + headers);
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
					// System.out.println("set request-header:" + entry.getKey()
					// + ":" + entry.getValue());
				}
			}

			conn.setRequestProperty("ATOKEN", _TOKEN);
			System.out.println("log:---1" + _TOKEN + ":" + _TOKEN);
			// String checkCode = Encoder.encoding(bod, _TOKEN);
			// System.out.println("=====>>>Send CHECK_CODE:" + checkCode);
			// conn.setRequestProperty("CHECK_CODE", checkCode);

			String method = "POST";
			// 加入数据
			conn.setRequestMethod(method);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			if (cookie != null && cookie.length() > 0) {
				conn.setRequestProperty("CGNBSSID", cookie);
				// System.out.println("set CGNBSSID:" + cookie);
				// FilterMessage.info("conn Cookie:", cookie);
			}
			conn.connect();
			if (body != null && body.length > 0) {
				OutputStream out = conn.getOutputStream();
				out.write(body);
				out.flush();
			}

			// 获取输入流
			int code = conn.getResponseCode();
			ERROR_TYPE = code;

			
			InputStream ins = null;
			
			if(code == HttpURLConnection.HTTP_OK|| code == HttpURLConnection.HTTP_CREATED|| code ==HttpURLConnection.HTTP_ACCEPTED){
				 ins = conn.getInputStream();
			 }else{
				 ins = conn.getErrorStream();
			 }
			
			System.out.println("ERROR_TYPE========" + ERROR_TYPE);
			
			// 从header获取tokenid
			try {
				printResponseHeader(conn);
			} catch (Exception e) {
				// TODO: handle exception
				// e.printStackTrace();
			}
			Map<String, List<String>> fields = conn.getHeaderFields();
			headersResponse = fields;
			// System.out.println("url :" + uri + ", code:" + code + ",
			// response-headers:" + fields);
			if (fields.get("SET-CGNBSSID") != null) {
				List<String> values = fields.get("SET-CGNBSSID");
				if (values != null && values.size() > 0) {
					cookie = values.get(values.size() - 1);
					String sessionId = cookie.substring(cookie.indexOf("=") + 1, cookie.indexOf(";"));
					obj.setSessionId(sessionId);

				}
				HttpRequestAes.timeout = true;
			} else {
				HttpRequestAes.timeout = false;
			}
			try {
				result = HandStringTool.read2ByteArray(ins);
			} catch (Exception e) {
				// TODO: handle exception
				result = null;
			}
		} finally {
			if (null != conn)
				conn.disconnect();
		}
		return result;
	}

	public Map<String, String> headersRequest;
	public Map<String, List<String>> headersResponse;

	public String getCookie() {
		return cookie;
	}

	public synchronized static HttpRequestAes getR() {
		if (r == null) {
			r = new HttpRequestAes();
		}
		return r;
	}

	private static void printResponseHeader(HttpURLConnection http) throws UnsupportedEncodingException {
		Map<String, String> header = getHttpResponseHeader(http);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() : "";
			if ("ATOKEN".equals(key)) {
				_TOKEN = entry.getValue();
				System.out.println("log:---2" + key + ":" + entry.getValue());
				break;
			}
			// System.out.println("log:---" + key + ":" + entry.getValue());
		}
	}

	private static Map<String, String> getHttpResponseHeader(HttpURLConnection http)
			throws UnsupportedEncodingException {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null)
				break;
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
	}

	public void setHttpsEnv() throws NoSuchAlgorithmException, KeyManagementException {
		// 设置SSLContext
		SSLContext sslContext = null;
		System.setProperty("https.protocols", sslProtocol);
		sslContext = SSLContext.getInstance(sslProtocol);
		try {
			// sslContext.init(null, new TrustManager[] { new TrustManagerCheck(
			// keyStore, "changeit") }, new java.security.SecureRandom());
			sslContext.init(null, new TrustManager[] { new TrustManagerEbankCgnb(strict) }, RSAUtil.RANDOM);
			// System.out.println("https.protocols: " +
			// System.getProperty("https.protocols")
			// + ", sslContext init ok ! 缺省安全套接字使用的协议: " +
			// sslContext.getProtocol());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 设置套接工厂
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(myHostnameVerifier);
		setHttps = true;
	}

	private static HostnameVerifier myHostnameVerifier = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return "ebank.cgnb.cn".equalsIgnoreCase(hostname) && hostname.equals(session.getPeerHost());
		}
	};

}
