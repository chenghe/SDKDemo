//package com.zsmarter.doubleinputsdk.softencode;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaInterface;
//import org.apache.cordova.CordovaPlugin;
//import org.apache.cordova.CordovaWebView;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import static android.provider.MediaStore.Video.Thumbnails.MICRO_KIND;
//import static android.provider.MediaStore.Video.Thumbnails.cancelThumbnailRequest;
//
///**
// * Created by wangxf on 2017/4/21.
// */
//
//public class Plugin_ZS_Video extends CordovaPlugin {
//
//  private static CallbackContext mCallBack;
//  private JSONObject nson = new JSONObject();
//  JSONObject videoParams = null;
//  JSONObject videoParams1 = null;
//  private static String videoPath;
//  private static ProgressDialog proDialog;
//  private static Activity mActivity = cordova.getActivity();
//  public static boolean isIn = false;
//  public static Handler handler = new Handler(new Handler.Callback() {
//    @Override
//    public boolean handleMessage(Message message) {
//      switch (message.what) {
//        case 1001:
//          proDialog = ProgressDialog.show(mActivity, null, "正在保存视频,请稍候......");
//          break;
//        case 1002:
//          try {
//            releasePlugin();
//            if (videoPath != null) {
//              JSONObject nson = new JSONObject();
//              Bitmap bitmap = SoftCameraManager.getVideoThumbnail(videoPath, 80, 80, MICRO_KIND);
//              String headerPath = SoftCameraManager.saveImage(bitmap, SoftCameraManager
//                .getFilePath(videoPath) + "/header", SoftCameraManager.getFileName(videoPath));
//              nson.put("videoPath", videoPath);
//              nson.put("videoHeader", headerPath);
//              mCallBack.success(nson);
//            }
//          } catch (Exception e) {
//            e.printStackTrace();
//          }
//          break;
//      }
//      return true;
//    }
//  });
//
//
//  @Override
//  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws
//    JSONException {
//    if (!isIn) {
//      isIn = true;
//      this.mCallBack = callbackContext;
//      try {
//        PhoneDisableManager.startPhoneCallListener(mActivity);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//      switch (action) {
//        case "recordVideo":
//          JSONObject jsonObject = args.getJSONObject(0);
//          int videoInput = jsonObject.optInt("videoInput");
//          int maxTime = jsonObject.optInt("videoMaxTime", 60);
//          int videoOutput = jsonObject.optInt("videoOutput");
//          int audioInput = jsonObject.optInt("audioInput");
//          int audioOutput = jsonObject.optInt("audioOutput");
//          int videoEncodingBitRate = jsonObject.optInt("videoEncodingBitRate", 1000);
//          int audioEncodingBitRate = jsonObject.optInt("audioEncodingBitRate", 16000);
//          int audioSampleRate = jsonObject.optInt("audioSampleRate", 16000);
//          int mHeight = 720;
//          int distinguishability = jsonObject.optInt("distinguishability", 360);
//          int waterPaddingWidth = jsonObject.optInt("waterPaddingWidth", 5);
//          int waterPaddingHeight = jsonObject.optInt("waterPaddingHeight", 5);
//          int mWidth = 1080;
//          int frameRate = jsonObject.optInt("frameRate", 18);
//          boolean isHaveWaterMark = jsonObject.optBoolean("isHaveWaterMark", false);
//          String videoPath = jsonObject.optString("videoPath", "doubleinput/TS.mp4");
//          videoPath = "/mnt/sdcard/" + videoPath;
//          this.videoPath = videoPath;
//          try {
//            videoParams = SoftCameraManager.getVideoParams(0);
//            videoParams1 = SoftCameraManager.getVideoParams(1);
//            SoftCameraManager.setPerformance(cordova.getActivity(), "display", String.valueOf
//              (videoParams), String.valueOf(videoParams1));
//            SoftCameraManager.makeDirectory(videoPath);
//            List heights = new ArrayList();
//            Iterator it = videoParams.keys();
//            while (it.hasNext()) {
//              String key = ((String) it.next());
//              heights.add(Integer.parseInt(key));
//            }
//            mHeight = SoftCameraManager.binarysearchKey(heights.toArray(), distinguishability);
//            mWidth = videoParams.getInt(String.valueOf(mHeight));
//            VideoRecorderOptions options = VideoRecorderOptions.getInstance();
//            options.setFrameRate(frameRate);
//            options.setWaterPaddingHeight(waterPaddingHeight);
//            options.setWaterPaddingWidth(waterPaddingWidth);
//            options.setVideoEncodingBitRate(videoEncodingBitRate);
//            options.setAudioEncodingBitRate(audioEncodingBitRate);
//            options.setAudioSampleRate(audioSampleRate);
//            options.setmWidth(mWidth);
//            options.setmHeight(mHeight);
//            options.setVideoPath(videoPath);
//            options.setVideoMaxTime(maxTime);
//            options.setHaveWaterMark(isHaveWaterMark);
//            options.setDistinguishability(distinguishability);
//            Intent recordIntent = new Intent(mActivity, VideoRecordActivity.class);
//            cordova.startActivityForResult(Plugin_ZS_Video.this, recordIntent, 1001);
//          } catch (Exception e) {
//            e.printStackTrace();
//            releasePlugin();
//            nson.put("code", "4001");
//            nson.put("message", "get camera params erro:" + e.getMessage());
//            mCallBack.error(nson);
//          }
//
//          break;
//      }
//    }
//
//    return true;
//  }
//
//
//  @Override
//  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//    super.onActivityResult(requestCode, resultCode, intent);
//    try {
//      switch (resultCode) {
//        case 4001:
//          releasePlugin();
//          if (intent != null) {
//            String message = intent.getStringExtra("message");
//            nson.put("code", "4001");
//            nson.put("message", message);
//            mCallBack.error(nson);
//          }
//          break;
//      }
//    } catch (JSONException e) {
//      e.printStackTrace();
//    }
//  }
//
//  public static void releasePlugin() {
//    isIn = false;
//    try {
//      PhoneDisableManager.stopPhoneCallListener(mActivity);
//      if (proDialog != null) {
//        proDialog.dismiss();
//      }
//      proDialog = null;
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
//}
