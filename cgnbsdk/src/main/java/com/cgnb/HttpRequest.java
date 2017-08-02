package com.cgnb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.Context;
import cn.cgnb.ses.client.util.RSAUtil;
import cn.cgnb.ses.client.util.TrustManagerEbankCgnb;

public class HttpRequest {
	// private HttpURLConnection conn;
	private boolean https = false;
	private String url;
	private String server;
	private String cookie;
	private int port;
	private String uri = "/";
	private String encode = HttpUtil.ENCODE;
	private int connectTimeout = 15000;// 30 seconds
	private int soTimeout = 30000;// 30 seconds
	private boolean setHttps = false;
	private String sslProtocol = "TLS";
	private static HttpRequest r = null;
	public static String _TOKEN = "";

	private static Context c = null;
	boolean strict = true;

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		System.out.println("checkServerTrusted --------" + strict);
		this.strict = strict;
	}

	public synchronized static HttpRequest getR(Context c) {
		if (r == null) {
			r = new HttpRequest();
		}
		if (HttpRequest.c == null) {
			HttpRequest.c = c;
		}
		return r;
	}

	public void setURL(String url) {
		int protocolIdx = url.indexOf("://");
		if (-1 == protocolIdx) {
			url = "http://" + url;
			protocolIdx = url.indexOf("://");
		}
		this.url = url;
		String protocol = url.substring(0, protocolIdx);
		this.https = "https".equalsIgnoreCase(protocol);
		int uriEndIdx = url.indexOf("/", 3 + protocolIdx);
		int port_endInx = url.length();
		if (-1 == uriEndIdx) {
			this.uri = "/";
		} else {
			port_endInx = uriEndIdx;
			this.uri = url.substring(uriEndIdx);
		}
		String server_port = url.substring(3 + protocolIdx, port_endInx);
		int portInx = server_port.indexOf(":");
		if (-1 == portInx) {
			this.port = this.https ? 443 : 80;
			this.server = server_port;
		} else {
			this.server = server_port.substring(0, portInx);
			this.port = Integer.parseInt(server_port.substring(1 + portInx));
		}
	}

	public URL getURL(final String uri) throws MalformedURLException {
		// String
		url = HttpUtil.getURL(https, server, port, uri);
		return new URL(url);
	}

	public void setHttpsEnv() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sslContext = null;
		if ("TLS".equalsIgnoreCase(sslProtocol)) {
			System.setProperty("https.protocols", "TLSv1");
			sslContext = SSLContext.getInstance("TLSv1");
		} else {
			sslContext = SSLContext.getInstance("SSL");
		}

		try {
			sslContext.init(null, new TrustManager[] { new TrustManagerCheck(strict) }, RSAUtil.RANDOM);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		// 设置套接工厂
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(myHostnameVerifier);
		setHttps = true;
	}

	public String sendHttp() throws KeyManagementException, NoSuchAlgorithmException, IOException {
		return sendHttp(null);
	}

	public String sendHttp(String uri) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		return sendHttp(uri, "GET", null, null, null, null, null);
	}

	public String sendHttp(String uri, final String method, String imei, String appversion, String systemversion,
			String json, String ip) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		System.out.println("log:error-------------------uri=" + uri + ",json=" + json);
		String uid = System.currentTimeMillis() + "_" + Math.random() + "";
		String result = null;
		if (uri == null) {
			uri = this.uri;
		}
		if (uri == null) {
			uri = "/";
		}
		this.uri = uri;
		String requestParam = json;
		boolean isGet = "GET".equalsIgnoreCase(method);
		if (!https && requestParam.length() > 0 && isGet) {
			uri = uri + "?" + requestParam;
		}
		URL requestUrl = new URL(uri);
		if (https && (!setHttps)) {
			setHttpsEnv();
		}

		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) requestUrl.openConnection();
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/octet-stream;charset=utf-8");
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			// 接受数据类型
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("imei", imei);
			// conn.setRequestProperty("ClientIp", ip);
			conn.setRequestProperty("appversion", appversion);
			conn.setRequestProperty("systemversion", systemversion + "_" + ip);
			conn.setRequestProperty("system", "2");
			conn.setRequestProperty("ATOKEN", _TOKEN);
			String checkCode = Encoder.encoding(json, _TOKEN);
			conn.setRequestProperty("CHECK_CODE", checkCode);
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(soTimeout);
			// 加入数据
			conn.setRequestMethod(method);
			// conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			if (cookie != null && cookie.length() > 0) {
				conn.setRequestProperty("Cookie", cookie);
				// FilterMessage.info("conn Cookie:", cookie);
			}

			OutputStream out = null;
			if (!isGet && requestParam.length() > 0) {
				out = conn.getOutputStream();
				out.write(requestParam.getBytes(encode));
				out.flush();
			}
			conn.connect();

			int code = conn.getResponseCode();
			System.out.println("log:--------------code=" + code);
			InputStream ins = null;
			if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED
					|| code == HttpURLConnection.HTTP_ACCEPTED) {
				ins = conn.getInputStream();
			} else {
				ins = conn.getErrorStream();
			}
			// 获取输入流
			String p = HttpUtil.getFirstHeadStringIgnoreCase(conn.getHeaderFields(), "Set-Cookie");

			if (p != null) {
				cookie = p;
			}
			if (HttpURLConnection.HTTP_OK == code) {
				// byte[] data = IoTool.read2ByteArray(ins);
				result = convertStreamToString(ins);
				try {
					printResponseHeader(conn);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else {
				result = convertStreamToString(ins);
			}
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
			// FilterMessage.error(e);
			return e.toString();
		} finally {
			if (null != conn)
				conn.disconnect();
		}
		return result;
	}

	private static HostnameVerifier myHostnameVerifier = new HostnameVerifier() {

		public boolean verify(String hostname, SSLSession session) {
			return hostname.equals(session.getPeerHost());
		}
	};

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}

		return sb.toString();
	}

	private static void printResponseHeader(HttpURLConnection http) throws UnsupportedEncodingException {
		Map<String, String> header = getHttpResponseHeader(http);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() : "";
			if ("ATOKEN".equals(key)) {
				_TOKEN = entry.getValue();
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
}

class TrustManagerCheck implements X509TrustManager {

	X509TrustManager sunJSSEX509TrustManager;
	boolean strict = true;

	public TrustManagerCheck() {
		this.strict = true;
	}

	public TrustManagerCheck(boolean strict) {
		this.strict = strict;
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// 不需要校验证书,比如更新的url
		return;
	}

	public X509Certificate[] getAcceptedIssuers() {
		// System.out.println("======================>>>>getAcceptedIssuers");
		return sunJSSEX509TrustManager.getAcceptedIssuers();
	}
}