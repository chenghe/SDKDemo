package com.cgnb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetWork extends BroadcastReceiver {
	
	private String ssid = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		State wifiState = null;
		State mobileState = null;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			// 手机网络连接成功
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			// 手机没有任何的网络
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			// 无线网络连接成功
		}
		
		try {
			setSsid(context);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	private void setSsid(Context context){
		   WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
		   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		   Log.d("wifiInfo", wifiInfo.toString());
		   Log.d("SSID",wifiInfo.getSSID());
		   ssid = wifiInfo.getSSID();
	}

}
