//package com.zsmarter.doubleinputsdk.softencode;
//
//import android.app.Activity;
//import android.content.IntentFilter;
//import com.android.internal.telephony.PhoneCallReceiver;
//
//
///**
// * Created by wangxf on 2017/4/21.
// */
//
//public class PhoneDisableManager{
//
//
//  //定义一个广播监听器；
//  private static PhoneCallReceiver mPhoneCallReceiver;
//
//  public static void startPhoneCallListener(Activity mContext) throws Exception{
//    //定义一个过滤器；
//    IntentFilter intentFilter;
//    //实例化过滤器；
//    intentFilter = new IntentFilter();
//    //添加过滤的Action值；
//    intentFilter.addAction("android.intent.action.PHONE_STATE");
//
//    //实例化广播监听器；
//    mPhoneCallReceiver = new PhoneCallReceiver();
//
//    //将广播监听器和过滤器注册在一起；
//    mContext.registerReceiver(mPhoneCallReceiver, intentFilter);
//  }
//
//  public static void stopPhoneCallListener(Activity mContext) throws Exception{
//    if(mPhoneCallReceiver!=null){
//      //销毁Activity时取消注册广播监听器；
//      mContext.unregisterReceiver(mPhoneCallReceiver);
//    }
//  }
//}
