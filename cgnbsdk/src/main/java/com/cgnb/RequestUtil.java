package com.cgnb;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class RequestUtil {

	private DefaultHttpClient client = null;
	private static RequestUtil util = null;
	
	private RequestUtil(){
		
	}
	
	public static synchronized RequestUtil getRequestUtil(){
		if(util == null){
			util = new RequestUtil();
		}
		
		return util;
	}

	private synchronized DefaultHttpClient getHttpclient() {
		if (client == null) {
			client = new DefaultHttpClient();
		}
		return client;
	}

	public void httpRequest(RequestListener listener, String type, String json, String url,int code,String key) {
//		DefaultHttpClient httpClient = getHttpclient();
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if ("post".equals(type)) {
			post(httpClient, listener, json, url,code,key);
		} else {
			get(httpClient, listener, json, url,code,key);
		}

	}

	private void post(DefaultHttpClient httpClient, RequestListener listener, String json, String url,int code,String key) {
		try {
			HttpPost method = new HttpPost(url);
//			StringEntity entity = new StringEntity(json, "utf-8");
//			entity.setContentEncoding("UTF-8");
//			entity.setContentType("application/json");
//			method.setEntity(entity);
			method.setHeader("tokenId", key);
			List <NameValuePair> params = new ArrayList<NameValuePair>();  
	        params.add(new BasicNameValuePair("json", json));  
	        params.add(new BasicNameValuePair("tokenId", key)); 
	        method.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse result = httpClient.execute(method);
			String resData = EntityUtils.toString(result.getEntity());
			if (resData != null) {
				listener.success(code,resData);
			}
		} catch (Exception e) {
			// TODO: handle exception
			listener.error(code,null);
		}
	}

	private void get(DefaultHttpClient httpClient, RequestListener listener, String json, String url,int code,String key) {
		try {
			HttpGet method = new HttpGet(url);
//			StringEntity entity = new StringEntity(json, "utf-8");
//			entity.setContentEncoding("UTF-8");
//			entity.setContentType("application/json");
			method.setHeader("tokenId", key);
			HttpResponse result = httpClient.execute(method);
			String resData = EntityUtils.toString(result.getEntity());
			if (resData != null) {
				listener.success(code,resData);
			}
		} catch (Exception e) {
			// TODO: handle exception
			listener.error(code,null);
		}
	}

}
