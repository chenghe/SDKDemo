//package com.zsmarter.doubleinputsdk.audio;
//
//import android.Manifest;
//import android.os.Environment;
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.Toast;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaInterface;
//import org.apache.cordova.CordovaPlugin;
//import org.apache.cordova.CordovaWebView;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Created by hechengbin on 17/3/14.
// */
//
//public class AudioPlugin extends CordovaPlugin {
//
//    public static String AUDIOPLUGINSTART = "AudioPluginStart";
//    public static String AUDIOPLUGINSTARTPARAM = "AudioPluginStartParam";
//    public static String AUDIOPLUGINSTOP = "AudioPluginStop";
//    public static String AUDIOPLUGINPLAY = "AudioPluginPlay";
//    private AudioUtil audioUtil;
//    private String aduioName;//音频名称PARAM
//    private String fileName;//音频所在文件夹
//    private String playPath;//音频播放路径
//    private int encodingBitRate;//比特率
//    private int samplingRate;//采样率
//    private String format;//文件格式
//    private String path;
//    private String[] permissions = {
//            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
//    };
//
//    @Override
//    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//        super.initialize(cordova, webView);
//
//        Log.e("test", "initialize");
////        PermissionHelper.requestPermissions(this, 0, permissions);
////        XFResourcesIDFinder.init(cordova.getActivity());
//    }
//
//    @Override
//    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
//
//        if (action.equals(AUDIOPLUGINSTART)) {
//            //开始录制音频
//            audioUtil = new AudioUtil(cordova.getActivity());
//            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();//获取SD卡绝对路径
//            JSONObject obj = args.getJSONObject(0);
//
//            aduioName = obj.optString("aduioName");
//            fileName = obj.optString("path");
//            if (!TextUtils.isEmpty(aduioName) && !TextUtils.isEmpty(fileName)) {
//                path = absolutePath + fileName;//文件最终路径
//                audioUtil.startAudio(path, aduioName);
//            } else {
//                if (TextUtils.isEmpty(aduioName)) {
//                    Toast.makeText(cordova.getActivity(), "请输入音频名称", Toast.LENGTH_SHORT).show();
//                }
//                if (TextUtils.isEmpty(fileName)) {
//                    Toast.makeText(cordova.getActivity(), "请输入目录名称", Toast.LENGTH_SHORT).show();
//                }
//            }
//            callbackContext.success();
//        } else if (action.equals(AUDIOPLUGINSTARTPARAM)) {
//            //开始录制音频，并带有视频相关参数
//            audioUtil = new AudioUtil(cordova.getActivity());
//            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();//获取SD卡绝对路径
//            JSONObject obj = args.getJSONObject(0);
//
//            aduioName = obj.optString("aduioName");
//            fileName = obj.optString("path");
//            encodingBitRate = obj.optInt("encodingBitRate");//比特率
//            samplingRate = obj.optInt("samplingRate");//采样率
//            format = obj.optString("format");//文件格式
//
//            if (!TextUtils.isEmpty(aduioName) && !TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(format)
//                    && encodingBitRate>0 && samplingRate>0 ) {
//                path = absolutePath + fileName;//文件最终路径
//                audioUtil.starAudioParam(path, aduioName,encodingBitRate,samplingRate,format);
//            } else {
//                if (TextUtils.isEmpty(aduioName)) {
//                    Toast.makeText(cordova.getActivity(), "请输入音频名称", Toast.LENGTH_SHORT).show();
//                }
//                if (TextUtils.isEmpty(fileName)) {
//                    Toast.makeText(cordova.getActivity(), "请输入目录名称", Toast.LENGTH_SHORT).show();
//                }
//                if (TextUtils.isEmpty(format)){
//                    Toast.makeText(cordova.getActivity(), "格式错误", Toast.LENGTH_SHORT).show();
//                }
//                if(!(encodingBitRate>0)){
//                    Toast.makeText(cordova.getActivity(), "比特率必须大于0", Toast.LENGTH_SHORT).show();
//                }
//                if(!(samplingRate>0)){
//                    Toast.makeText(cordova.getActivity(), "采样率必须大于0", Toast.LENGTH_SHORT).show();
//                }
//            }
//             callbackContext.success();
//        } else if (action.equals(AUDIOPLUGINSTOP)) {
//            //结束录制音频
//            if (!TextUtils.isEmpty(aduioName) && !TextUtils.isEmpty(fileName)) {
//                audioUtil.stopAudio();
//                callbackContext.success(new JSONObject().put("audioPath", path + aduioName + ".mp3"));
//
//            }
//
//        } else if (action.equals(AUDIOPLUGINPLAY)) {
//            //播放音频
//            audioUtil = new AudioUtil(cordova.getActivity());
//            JSONObject obj = args.getJSONObject(0);
//            playPath = obj.optString("playPath");
//            audioUtil.playAudio(playPath);
//        }
//
//        return true;
//    }
//
////    @Override
////    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
////                switch (requestCode) {
////            case PermissionChecker.PERMISSION_REQUEST_CODE:
////                if (permissionChecker.hasAllPermissionsGranted(grantResults)) {
////                    audioUtil = new AudioUtil(cordova.getActivity());
////                } else {
////                    permissionChecker.showDialog();
////                }
////                break;
////        }
////    }
//
//
//    //    @Override
////    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        switch (requestCode) {
////            case PermissionChecker.PERMISSION_REQUEST_CODE:
////                if (permissionChecker.hasAllPermissionsGranted(grantResults)) {
////                    startAudio();
////                } else {
////                    permissionChecker.showDialog();
////                }
////                break;
////        }
////    }
//}
