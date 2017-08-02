//package com.zsmarter.doubleinputsdk.video;
//
//import android.Manifest;
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.hardware.Camera;
//import android.media.ThumbnailUtils;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.content.FileProvider;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.zsmarter.doubleinput.utils.XFResourcesIDFinder;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaInterface;
//import org.apache.cordova.CordovaPlugin;
//import org.apache.cordova.CordovaWebView;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import static android.content.Context.MODE_PRIVATE;
//import static android.content.Intent.ACTION_VIEW;
//import static android.provider.MediaStore.Video.Thumbnails.MICRO_KIND;
//
//public class VideoRecord extends CordovaPlugin {
//  private String[] permissions = {
//    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
//  };
//  private static final String TAG = "Tag";
//  private Activity mContext;
//  private CallbackContext mCallBack;
//  private EncryptionOrDecryptionTask mTask;
//  private String nowVideo;
//  private String headerPath;
//  String importVideoName;
//  String importVideoPath;
//  String importVideoHeaderPath;
//  public Handler handler = new Handler(new Handler.Callback() {
//    @Override
//    public boolean handleMessage(Message message) {
//      switch (message.what) {
//        case 01:
//
//          break;
//        case 02:
//          Log.v("T", "back");
//          String videoPath = (String) message.obj;
//          try {
//            playVideo(videoPath);
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }
//          break;
//      }
//      return false;
//    }
//  });
//
//  @Override
//  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//    super.initialize(cordova, webView);
//    Log.e(cordova.getClass().getName(), "init..");
//    this.mContext = cordova.getActivity();
//    XFResourcesIDFinder.init(this.mContext);
//    PermissionHelper.requestPermissions(this, 0, permissions);
//  }
//
//  @Override
//  public void onStart() {
//    super.onStart();
//    Log.e(cordova.getClass().getName(), "onStart..");
//
//  }
//
//  @Override
//  public void onResume(boolean multitasking) {
//    super.onResume(multitasking);
//    Log.e(cordova.getClass().getName(), "onResume..");
//  }
//
//  @Override
//  public void onPause(boolean multitasking) {
//    super.onPause(multitasking);
//    Log.e(cordova.getClass().getName(), "onPause..");
//
//  }
//
//
//  @TargetApi(Build.VERSION_CODES.KITKAT)
//  @Override
//  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
//    Log.e(cordova.getClass().getName(), "onPlugining..");
//    this.mCallBack = callbackContext;
//    switch (action) {
//      case "recordVideo":
//        JSONObject jsonObject = args.getJSONObject(0);
//        int videoInput = jsonObject.optInt("videoInput");
//        int maxTime = jsonObject.optInt("videoMaxTime");
//        int videoOutput = jsonObject.optInt("videoOutput");
//        int audioInput = jsonObject.optInt("audioInput");
//        int audioOutput = jsonObject.optInt("audioOutput");
//        int videoEncodingBitRate = jsonObject.optInt("videoEncodingBitRate");
//        int audioEncodingBitRate = jsonObject.optInt("audioEncodingBitRate");
//        int cameraPos = jsonObject.optInt("cameraPos");
//        int cameraOri = jsonObject.optInt("cameraOri");
//        int mHeight = jsonObject.optInt("mHeight");
//        int mWidth = jsonObject.optInt("mWidth");
//        String videoName = jsonObject.optString("videoName");
//        String videoPath = jsonObject.optString("videoPath");
//        SharedPreferences sp = cordova.getActivity().getSharedPreferences("video", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putInt("videoMaxTime", maxTime);
//        editor.commit();
//        this.headerPath = videoPath + "/header";
//        Log.e(TAG, jsonObject.toString());
//        recordVideo(videoName, videoPath,cameraOri, videoInput, videoOutput, videoEncodingBitRate, audioInput, audioOutput, audioEncodingBitRate, cameraPos, mHeight, mWidth, maxTime);
//        break;
//      case "playVideo":
//        JSONObject jsonObject1 = args.getJSONObject(0);
//        String _videoPath = jsonObject1.optString("videoPath");
////                videoDecode(videoPath);
//        try {
//          playVideo(_videoPath);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//        break;
//      case "importVideo":
//        JSONObject importJsonObject = args.getJSONObject(0);
//        importVideoName = importJsonObject.optString("videoName");
//        importVideoPath = importJsonObject.optString("videoPath");
//        importVideoHeaderPath = importVideoPath + "/header";
//        importVideo();
//        break;
//      case "getVideoParams":
//        JSONObject ss = args.getJSONObject(0);
//        int camera = ss.optInt("cameraPs");
//        getVideoParams(camera);
//        break;
//      case "delVideo()":
//        JSONObject video_pa = args.getJSONObject(0);
//        String video_path = video_pa.optString("videoPath");
//        delvideo(video_path);
//        break;
//    }
//
//    return true;
//  }
//
//  private void delvideo(String video_path) {
//    FileUtil.deleteFile(video_path);
//  }
//
//  /**
//   * 获取视频参数
//   *
//   * @param pos
//   * @throws JSONException
//   */
//  private void getVideoParams(int pos) throws JSONException {
//    Camera mCamera = null;
//    try {
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
//          Camera.CameraInfo info = new Camera.CameraInfo();
//          Camera.getCameraInfo(i, info);
//          if (info.facing == pos) {
//            mCamera = Camera.open(i);
//          }
//        }
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//      freeCameraResource(mCamera);
//    }
//    if (mCamera == null)
//      return;
//    Camera.Parameters parameters = mCamera.getParameters();
//    StringBuffer sb = new StringBuffer();
//    JSONArray jsonArray = new JSONArray();
//    for (int i = 0; i < parameters.getSupportedVideoSizes().size(); i++) {
//      Camera.Size size = parameters.getSupportedVideoSizes().get(i);
//      jsonArray.put(size.width + "x" + size.height);
//    }
//    freeCameraResource(mCamera);
//    Log.e(TAG, jsonArray.toString());
//    this.mCallBack.success(jsonArray);
//  }
//
//  /**
//   * 导入视频
//   */
//  private void importVideo() {
//    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//    intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
////        intent.setType("doubleinput/*");
//    intent.addCategory(Intent.CATEGORY_OPENABLE);
//    cordova.startActivityForResult(this, intent, 300);
//  }
//
//  /**
//   * 录制视频
//   *
//   * @param videoName
//   * @param videoPath
//   * @param videoInput
//   * @param videoOutput
//   * @param videoEncodingBitRate
//   * @param audioInput
//   * @param audioOutput
//   * @param audioEncodingBitRate
//   * @param cameraPos
//   * @param mHeight
//   * @param mWidth
//   */
//  private void recordVideo(String videoName, String videoPath,int cameraOri, int videoInput, int videoOutput, int videoEncodingBitRate, int audioInput, int audioOutput, int audioEncodingBitRate, int cameraPos, int mHeight, int mWidth, int maxTime) {
//    Intent recordIntent = new Intent(mContext, VideoRecordActivity.class);
//    recordIntent.putExtra("videoName", videoName);
//    recordIntent.putExtra("videoPath", videoPath);
//    recordIntent.putExtra("maxTime", maxTime);
//    recordIntent.putExtra("mHeight", mHeight);
//    recordIntent.putExtra("mWidth", mWidth);
//    recordIntent.putExtra("videoInput", videoInput);
//    recordIntent.putExtra("videoOutput", videoOutput);
//    recordIntent.putExtra("videoEncodingBitRate", videoEncodingBitRate);
//    recordIntent.putExtra("audioInput", audioInput);
//    recordIntent.putExtra("audioOutput", audioOutput);
//    recordIntent.putExtra("audioEncodingBitRate", audioEncodingBitRate);
//    recordIntent.putExtra("cameraPos", cameraPos);
//    recordIntent.putExtra("cameraOri", cameraOri);
//    cordova.startActivityForResult(this, recordIntent, 100);
//  }
//
//  /**
//   * 播放视频
//   *
//   * @param videoPath
//   */
////  private void playVideo(String videoPath) {
////    try {
////      File path = new File(videoPath);
////      Log.e("1111", videoPath);
////      Intent intent = new Intent(ACTION_VIEW);
////      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//////      intent.putExtra("oneshot", 0);
//////      intent.putExtra("configchange", 0);
//////    Uri uri = Uri.fromFile(path)
////      // 授予目录临时共享权限
////      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
////        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
////      Uri photoURI = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", path);
////      Log.e("videoPath", photoURI.toString());
////      intent.setDataAndType(photoURI, "doubleinput/*");
////      mContext.startActivity(intent);
////    } catch (Exception e) {
////      e.printStackTrace();
////    }
////  }
//  private void playVideo(String videoPath) throws InterruptedException {
//    nowVideo = videoPath;
//    File path = new File(videoPath);
//    Intent intent = new Intent(ACTION_VIEW);
//    Uri uri;
//    if (Build.VERSION.SDK_INT >= 24) {
//      uri = FileProvider.getUriForFile(mContext.getApplicationContext(), cordova.getActivity().getPackageName() + ".provider", path);
//      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
//        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//    } else {
//      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//      intent.putExtra("oneshot", 0);
//      intent.putExtra("configchange", 0);
//      uri = Uri.fromFile(path);
//    }
//
//    Log.e("videoPah", uri.toString());
//    intent.setDataAndType(uri, "video/*");
//    cordova.startActivityForResult(this, intent, 400);
//  }
//
//
//  @Override
//  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//    Log.e("requestCode", requestCode + "/");
//    if (resultCode == 200) {
//      String url = intent.getStringExtra("videoPath");
//      Bitmap bitmap = getVideoThumbnail(url, 60, 60, MICRO_KIND);
//      if(bitmap!=null){
//        JSONObject nson = new JSONObject();
//        try {
//          nson.put("videoPath", url);
//          nson.put("videoHeader", saveImage(bitmap, this.headerPath, getFileName(url)));
//          this.mCallBack.success(nson);
//        } catch (JSONException e) {
//          e.printStackTrace();
//        }
//      }else {
//        try {
//          this.mCallBack.error(new JSONObject().put("message","IllegalStateException").put("code","4001"));
//        } catch (JSONException e) {
//          e.printStackTrace();
//        }
//        if (resultCode == 500) {
//          try {
//             mCallBack.error(new JSONObject().put("error","back from video"));
//              } catch (JSONException e) {
//               e.printStackTrace();
//         }
//        }
//    }
//      }
//    if (requestCode == 300) {
//      if (intent != null) {
//        Uri uri = intent.getData();
//        String sourceVideoPath = UriUtil.getFileAbsolutePath(mContext, uri);
//        Bitmap bitmap = getVideoThumbnail(sourceVideoPath, 60, 60, MICRO_KIND);
////                videoEncode(importVideoPath);
//        if (bitmap == null) {
//          Toast.makeText(mContext, "请选择视频文件!", Toast.LENGTH_LONG).show();
//          return;
//        }
//        String targetPATH = Environment.getExternalStorageDirectory() + File.separator + importVideoPath + importVideoName + ".mp4";
//        FileUtil.copyFile(sourceVideoPath, targetPATH);
//        if(bitmap!=null){
//          JSONObject nson = new JSONObject();
//          try {
//            nson.put("videoPath", Environment.getExternalStorageDirectory() + File.separator + importVideoPath + importVideoName + ".mp4");
//            nson.put("videoHeader", saveImage(bitmap, importVideoHeaderPath, importVideoName));
//            this.mCallBack.success(nson);
//          } catch (JSONException e) {
//            e.printStackTrace();
//          }
//        }
//
//      } else {
//        try {
//          this.mCallBack.error(new JSONObject().put("message","IllegalStateException").put("code","4001"));
//        } catch (JSONException e) {
//          e.printStackTrace();
//        }
//      }
//    }
//    if (requestCode == 400) {
////            videoEncode(nowVideo);
//    }
//    if (resultCode == 500) {
//      try {
//        this.mCallBack.error(new JSONObject().put("message","IllegalStateException").put("code","4001"));
//      } catch (JSONException e) {
//        e.printStackTrace();
//      }
//    }
//  }
//
//
//  private Bitmap getVideoThumbnail(String videoPath, int width, int height,
//                                   int kind) {
//    Bitmap bitmap = null;
//    // 获取视频的缩略图
//    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
//    bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
//      ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//    return bitmap;
//  }
//
//  /**
//   * 获取图片的名字
//   *
//   * @param pathandname
//   * @return
//   */
//  public String getFileName(String pathandname) {
//    String name;
//    int start = pathandname.lastIndexOf("/");
//    int end = pathandname.lastIndexOf(".");
//    if (start != -1 && end != -1) {
//      name = pathandname.substring(start + 1, end);
//    } else {
//      return null;
//    }
//    return name;
//  }
//
//  /**
//   * 保存图片
//   */
//  public static String saveImage(Bitmap bmp, String path, String name) {
//    File appDir = new File(Environment.getExternalStorageDirectory(), path);
//    if (!appDir.exists()) {
//      appDir.mkdir();
//    }
//    String fileName = name + ".jpg";
//    File file = new File(appDir, fileName);
//    try {
//      FileOutputStream fos = new FileOutputStream(file);
//      bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//      fos.flush();
//      fos.close();
//      return file.getPath();
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
//
//  /**
//   * 释放摄像头资源
//   */
//  private void freeCameraResource(Camera mCamera) {
//    if (mCamera != null) {
//      mCamera.setPreviewCallback(null);
//      mCamera.stopPreview();
//      mCamera.lock();
//      mCamera.release();
//      mCamera = null;
//    }
//  }
//
////  private void updateVideo(String videoFile) {
////
////    Configuration config = new Configuration.Builder().zone(Zone.httpAutoZone).build();
////    UploadManager uploadManager = new UploadManager(config);
////    Log.w("asdf", videoFile);
////    String key = null;
////    String token = "8NYFEa1hysXXA5i14zdhKYWLoQ0NjeH45ascFmYW:1gIVe4qYIU_Qs2HDj8_ot284rtk=:eyJzY29wZSI6IiB6c21hcnRlci12aWRlbyIsImRlYWRsaW5lIjoxNDgyMTg3NDIzfQ==";
////    uploadManager.put(videoFile, key, token,
////      new UpCompletionHandler() {
////        @Override
////        public void complete(String key, ResponseInfo info, JSONObject res) {
////          //res包含hash、key等信息，具体字段取决于上传策略的设置
////          Log.w("qiniu", key + ",\r\n " + info + ",\r\n " + res);
////        }
////      }, null);
////  }
//
//  private void videoEncode(String mFile) {
//    if (mTask != null) {
//      mTask.cancel(true);
//    }
//    try {
//      mTask = new EncryptionOrDecryptionTask(true, mFile, mFile, "1234567812345678", handler);
//      mTask.execute();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
//
//  private void videoDecode(String mFile) {
//    if (mTask != null) {
//      mTask.cancel(true);
//    }
//
//    mTask = new EncryptionOrDecryptionTask(false, mFile, mFile, "1234567812345678", handler);
//    mTask.execute();
//  }
//}
