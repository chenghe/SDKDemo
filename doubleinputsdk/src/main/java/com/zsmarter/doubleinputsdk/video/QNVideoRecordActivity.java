//package com.zsmarter.doubleinputsdk.video;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.media.ThumbnailUtils;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.zsmarter.doubleinoutsdk.R;
//import com.zsmarter.doubleinputsdk.utils.XFResourcesIDFinder;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class QNVideoRecordActivity extends Activity {
//  private MovieRecorderView movieRV;
//  private ImageButton btn_video;
//  private ImageButton btn_changeCamera;
//  private TextView videoTime;
//  private boolean isStart = false;
//  private int mRecordMaxTime;// 一次拍摄最长时间
//  private int mTimeCount;// 时间计数
//  private Timer mTimer;// 计时器
//  public String videoName;
//  public String videoPath;
//  public int mWidth;// 视频分辨率宽度
//  public int mHeight;// 视频分辨率高度
//  public int videoInput;
//  public int videoOutput;
//  public int audioOutput;
//  public int videoEncodingBitRate;
//  public int audioEncodingBitRate;
//  public int cameraPos;
//  public int cameraOri;
//  private int maxTime;
//  private Handler handler = new Handler(new Handler.Callback() {
//    @Override
//    public boolean handleMessage(Message message) {
//      switch (message.what) {
//        case 1:
//          videoTime.setText("录制时间:" + mTimeCount + "s");
//          break;
//        case 2:
//          btn_video.setImageResource(XFResourcesIDFinder.getResDrawableID("start"));
//
//          Intent mIntent = new Intent();
////                    Uri uri = Uri.fromFile(movieRV.getmRecordFile().getAbsolutePath());
//          mIntent.putExtra("videoPath", movieRV.getmRecordFile().getAbsolutePath());
//          // 设置结果，并进行传送
//          QNVideoRecordActivity.this.setResult(200, mIntent);
//          QNVideoRecordActivity.this.finish();
//          break;
//      }
//      return true;
//    }
//  });
//
//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_qn_video_record);
//    videoName = getIntent().getStringExtra("videoName");
//    if (videoName.equals("")) {
//      videoName = "record";
//    }
//    videoPath = getIntent().getStringExtra("videoPath");
//    if (videoPath.equals("")) {
//      videoPath = "doubleinput/video/";
//    }
//    maxTime = getIntent().getIntExtra("maxTime", 60);
//    mWidth = getIntent().getIntExtra("mWidth", 640);
//    mHeight = getIntent().getIntExtra("mHeight", 960);
//    videoInput = getIntent().getIntExtra("videoInput", 0);
//    videoOutput = getIntent().getIntExtra("videoOutput", 0);
//    audioOutput = getIntent().getIntExtra("audioOutput", 0);
//    videoEncodingBitRate = getIntent().getIntExtra("videoEncodingBitRate", 1536);
//    audioEncodingBitRate = getIntent().getIntExtra("audioEncodingBitRate", 1536);
//    cameraPos = getIntent().getIntExtra("cameraPos", 1);
//    cameraOri = getIntent().getIntExtra("cameraOri", 0);
//    mRecordMaxTime = maxTime;
//    initViews();
//    initData();
//    initEvents();
//  }
//
//  private void initData() {
//    movieRV.videoName = videoName;
//    movieRV.videoPath = videoPath;
//    movieRV.mHeight = mHeight;
//    movieRV.mWidth = mWidth;
//    movieRV.videoEncodingBitRate = videoEncodingBitRate;
////        movieRV.videoOutput = videoOutput;
////        movieRV.videoInput = videoInput;
////        movieRV.audioOutput = audioOutput;
//    movieRV.audioEncodingBitRate = audioEncodingBitRate;
//    movieRV.cameraPos = cameraPos;
//    movieRV.cameraOri = cameraOri;
//    movieRV.mRecordMaxTime = maxTime;
//  }
//
//
//  private void initViews() {
//    movieRV = (MovieRecorderView) findViewById(XFResourcesIDFinder.getResIdID("moive_rv"));
//    btn_video = (ImageButton) findViewById(XFResourcesIDFinder.getResIdID("btn_video"));
//    btn_changeCamera = (ImageButton) findViewById(XFResourcesIDFinder.getResIdID("btn_changeCamera"));
//    videoTime = (TextView) findViewById(XFResourcesIDFinder.getResIdID("videoTime"));
//  }
//
//  private void initEvents() {
//    btn_video.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//
//        if (!isStart) {
//          isStart = true;
//          btn_video.setImageResource(XFResourcesIDFinder.getResDrawableID("stop"));
//          btn_changeCamera.setVisibility(View.INVISIBLE);
//          mTimeCount = 0;// 时间计数器重新赋值
//          mTimer = new Timer();
//          mTimer.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//              // TODO Auto-generated method stub
//              mTimeCount++;
//              handler.sendEmptyMessage(1);
//              if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
//                stopVideo();
//              }
//            }
//          }, 0, 1000);
//
//          try {
//            movieRV.record(new MovieRecorderView.OnRecordFinishListener() {
//              @Override
//              public void onRecordFinish() {
//              }
//            });
//          } catch (Exception e) {
//            isStart = false;
//            btn_video.setImageResource(XFResourcesIDFinder.getResDrawableID("start"));
//            videoTime.setVisibility(View.INVISIBLE);
//            btn_changeCamera.setVisibility(View.VISIBLE);
//            mTimeCount = 0;
//            mTimer.cancel();
//
//            Intent mIntent = new Intent();
//            // 设置结果，并进行传送
//            QNVideoRecordActivity.this.setResult(500, mIntent);
//            QNVideoRecordActivity.this.finish();
//          }
//
//        } else {
//          stopVideo();
//        }
//
//
//      }
//    });
//
//
//    btn_changeCamera.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        movieRV.freeCameraResource();
//        movieRV.cameraPos = movieRV.cameraPos == 1 ? 0 : 1;
//        try {
//          movieRV.initCamera();
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
//    });
//  }
//
//  @Override
//  protected void onPause() {
//    super.onPause();
//  }
//
//  private void stopVideo() {
//    if (mTimeCount <= 5 && mTimeCount > 0) {
//      Toast.makeText(this, "录制时间过短", Toast.LENGTH_SHORT).show();
//      return;
//    }
//    isStart = false;
//    mTimer.cancel();
//    movieRV.stop();
//    handler.sendEmptyMessage(2);
//  }
//
//  private void playV() {
////        File path = movieRV.getmVecordFile();
////        Intent intent = new Intent(ACTION_VIEW);
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        intent.putExtra("oneshot", 0);
////        intent.putExtra("configchange", 0);
////        Uri uri = Uri.fromFile(path);
////        Log.e("sadf", uri.toString());
////        intent.setDataAndType(uri, "doubleinput/*");
////        startActivity(intent);
//  }
//
//  /**
//   * 获取视频缩略图
//   *
//   * @param videoPath
//   * @param width
//   * @param height
//   * @param kind
//   * @return
//   */
//  private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
//    Bitmap bitmap = null;
//    // 获取视频的缩略图
//    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
//    bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//    return bitmap;
//  }
//
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
//    return  null;
//  }
//}
