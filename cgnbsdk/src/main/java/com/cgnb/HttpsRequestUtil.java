package com.cgnb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;

public class HttpsRequestUtil {

	private static HttpsRequestUtil util = null;

	private static HttpClient httpsclient = null;

	private static HttpClient httpclient = null;

	private HttpsRequestUtil() {
	}

	public static HttpsRequestUtil getUtil(Context context) {
		if (util == null) {
			util = new HttpsRequestUtil();
		}

		return util;
	}

	public void httpRequest(RequestListener listener, String type, String json, String url, int code, String key,
			String imei, String appversion, String systemversion) throws Exception {
//		System.out.println("log:1-------------httpRequest-----" + json);
		if ("post".equals(type)) {
			doHttpPost(listener, json, url, code, key, imei, appversion, systemversion, false);
		} else {
			doHttpGet(listener, json, url, code, key, imei, appversion, systemversion, false);
		}
	}

	public void httpsRequest(RequestListener listener, String type, String json, String url, int code, String key,
			String imei, String appversion, String systemversion) throws Exception {
//		System.out.println("log:1-------------httpsRequest-----" + json);
		if ("post".equals(type)) {
			doHttpPost(listener, json, url, code, key, imei, appversion, systemversion, true);
		} else {
			doHttpGet(listener, json, url, code, key, imei, appversion, systemversion, true);
		}
	}

	public static synchronized void doHttpGet(RequestListener listener, String json, String url, int code, String key,
			String imei, String appversion, String systemversion, boolean ifhttps) throws Exception {
		// 参数
		// url = "https://www.baidu.com";
		HttpParams httpParameters = new BasicHttpParams();
		// 设置连接超时
		HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
		// 设置socket超时
		HttpConnectionParams.setSoTimeout(httpParameters, 30000);
		HttpClient hc = null;
		if (ifhttps) {
			// 获取HttpClient对象 （认证）
			hc = getHttpsClient();
		} else {
			// 获取HttpClient对象 （普通）
			hc = getHttpClient();
		}
		HttpGet get = new HttpGet(url);
		// 发送数据类型
		get.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		// 接受数据类型
		get.addHeader("Accept", "application/json");
		// -------------------------------------------------------
		get.addHeader("imei", imei);
		get.addHeader("appversion", appversion);
		get.addHeader("systemversion", systemversion);
		// -------------------------------------------------------
		// 请求报文
		// StringEntity entity = new StringEntity(jsonStr, "UTF-8");
		// post.setEntity(entity);

		get.setHeader("tokenId", key);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// String t = java.net.URLEncoder.encode(json);
		// System.out.println("log:2------------------" + t);
		params.add(new BasicNameValuePair("json", json));
		params.add(new BasicNameValuePair("tokenId", key));
		// post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
		get.setParams(httpParameters);

		HttpResponse response = null;
		try {
			response = hc.execute(get);
		} catch (Exception e) {
			try {
				get.abort();
				// hc.getConnectionManager().shutdown();
			} catch (Exception e2) {
				// TODO: handle exception
//				e2.printStackTrace();
			}
//			e.printStackTrace();
			listener.error(-1, e.getLocalizedMessage());
			return;
		}
		int sCode = response.getStatusLine().getStatusCode();
		if (sCode == HttpStatus.SC_OK) {
			// return EntityUtils.toString(response.getEntity());
			String s = getContent(response);
			listener.success(sCode, s);
		} else {
			listener.error(sCode, null);
		}
		try {
			// hc.getConnectionManager().shutdown();
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
		}
		// throw new Exception("StatusCode is " + sCode);
	}

	public static synchronized void doHttpPost(RequestListener listener, String json, String url, int code, String key,
			String imei, String appversion, String systemversion, boolean ifhttps) throws Exception {
		// url = "http://etest.cgnb.cn:8091/";
		// 参数
		HttpParams httpParameters = new BasicHttpParams();
		// 设置连接超时
		HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
		// 设置socket超时
		HttpConnectionParams.setSoTimeout(httpParameters, 30000);

		HttpClient hc = null;
		if (ifhttps) {
			// 获取HttpClient对象 （认证）
			hc = getHttpsClient();
		} else {
			// 获取HttpClient对象 （普通）
			hc = getHttpClient();
		}
		HttpPost post = new HttpPost(url);
		// 发送数据类型
		post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		post.addHeader("X-Requested-With", "XMLHttpRequest");
		// 接受数据类型
		post.addHeader("Accept", "application/json");
		// -------------------------------------------------------
		post.addHeader("imei", imei);
		post.addHeader("appversion", appversion);
		post.addHeader("systemversion", systemversion);
		// -------------------------------------------------------

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// String t = java.net.URLEncoder.encode(json);
		// System.out.println("log:2------------------" + t);
		params.add(new BasicNameValuePair("json", json));
		params.add(new BasicNameValuePair("tokenId", key));
		post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
		post.setParams(httpParameters);

		HttpResponse response = null;
		try {
			response = hc.execute(post);
		} catch (Exception e) {
			try {
				post.abort();
				// hc.getConnectionManager().shutdown();
			} catch (Exception e2) {
				// TODO: handle exception
//				e2.printStackTrace();
			}
//			e.printStackTrace();
			listener.error(-1, e.getLocalizedMessage());
			return;
		}
		int sCode = response.getStatusLine().getStatusCode();
//		System.out.println("log:11------------------" + sCode);
		if (sCode == HttpStatus.SC_OK) {
//			String s = EntityUtils.toString(response.getEntity());
			String s = getContent(response);
			// return EntityUtils.toString(response.getEntity());
//			System.out.println("log:3------------------" + s);
			listener.success(sCode, s);
		} else {
//			System.out.println("log:3------------------error" + sCode);
			listener.error(sCode, null);
		}
		try {
			// hc.getConnectionManager().shutdown();
			// post.
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
		}
		// throw new Exception("StatusCode is " + sCode);
	}

	public static synchronized HttpClient getHttpClient() {
		if (null == httpclient) {
			httpclient = new DefaultHttpClient();
		}
		return httpclient;
	}

	public static synchronized HttpClient getHttpsClient() {
		// if ( null == httpsclient) {
		// 初始化工作
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); // 允许所有主机的验证
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);
			// 设置连接管理器的超时
			ConnManagerParams.setTimeout(params, 10000);
			// 设置连接超时
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			// 设置socket超时
			HttpConnectionParams.setSoTimeout(params, 10000);
			// 设置http https支持
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", sf, 443));
			ClientConnectionManager conManager = new ThreadSafeClientConnManager(params, schReg);
			return new DefaultHttpClient(conManager, params);
		} catch (Exception e) {
//			e.printStackTrace();
			return new DefaultHttpClient();
		}
		// }
	}

	/**
	 * 初始化HttpClient对象
	 * 
	 * @param params
	 * @return
	 */
	public static synchronized HttpClient initHttpClient(HttpParams params) {
		if (httpsclient == null) {
			try {
				// InputStream ins = null;
				// ins = context.getAssets().open("test.cer");
				// //下载的证书放到项目中的assets目录中
				// CertificateFactory cerFactory =
				// CertificateFactory.getInstance("X.509");
				// Certificate cer = cerFactory.generateCertificate(ins);
				// KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
				// keyStore.load(null, null);
				// keyStore.setCertificateEntry("trust", cer);
				// SSLSocketFactory sf = new SSLSocketFactoryImp(keyStore);
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);
				// 允许所有主机的验证
				SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
				// 设置http和https支持

				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				registry.register(new Scheme("https", sf, 443));

				ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

				return new DefaultHttpClient(ccm, params);
			} catch (Exception e) {
//				e.printStackTrace();
				return new DefaultHttpClient(params);
			}
		}
		return httpsclient;
	}

	public static class SSLSocketFactoryImp extends SSLSocketFactory {
		final SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryImp(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
				throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public void test(String imei, String appversion, String systemversion)
			throws ClientProtocolException, IOException, URISyntaxException {
		HttpPost post = new HttpPost("https://tianfu.cgnb.cn:8091/mobilebank/index.jsp");
		// post.setURI(new URI("https://tianfu.cgnb.cn:8091"));
		// Header header = new BasicHeader("imei", "352562073372875");
		// post.addHeader(header);
		post.addHeader("imei", "352562073372875");
		post.addHeader("appversion", appversion);
		post.addHeader("systemversion", systemversion);
		HttpClient hc = getHttpClient();
		// HttpPost post = new HttpPost(url);
		HttpResponse res = hc.execute(post);
//		System.out.println(res.getStatusLine());
		if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = res.getEntity();
//			System.out.println(entity);
		}
	}

	public static synchronized String getContent(HttpResponse response) {

		BufferedReader in = null;
		String page = "";
		try {
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			page = sb.toString();
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
//					e.printStackTrace();
				}
		}
		return page;
	}

}
