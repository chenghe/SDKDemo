package com.zsmarter.doubleinputsdk.softencode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thundersoft.swencodesdk.CameraPreview;
import com.thundersoft.swencodesdk.PicToYuv;
import com.thundersoft.swencodesdk.SoftEncoder;
import com.zsmarter.doubleinoutsdk.R;
import com.zsmarter.doubleinputsdk.bean.DoubleInput;
import com.zsmarter.doubleinputsdk.bean.VideoRecorderOptions;
//import com.zsmarter.doubleinputsdk.utils.XFResourcesIDFinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by wangxf on 2017/4/26.
 */

public class VideoRecordActivity extends Activity {
    private static String TAG = VideoRecordActivity.class.getSimpleName();
    private Message message = new Message();
    private SoftEncoder softEncode = null;
    private ProgressBar progressBar;
    private TextView time_video;
    private ImageButton imageButton_video;
    private ImageButton imageButton_camera_change;
    private ImageView video_record_img;
    private CameraPreview preview;
    private Camera mCamera;
    private FrameLayout video_record_view;
    private int video_time;
    private int maxVideoTime;
    private int audioEncodingBitRate;
    private int audioSampleRate;
    private int videoEncodingBitRate;
    private int frameRate;
    private int mWidth;
    private int mHeight;
    private int waterPaddingWidth;
    private int waterPaddingHeight;
    private int video_time_minute;
    private int video_time_second;
    private int distinguishability;
    private int cameraPos = 0;
    private boolean isHaveWaterMark = false;
    private String videoPath;
    private Timer timer;
    private Bitmap btp;
    private VideoRecorderOptions mRecorderOptions;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    time_video.setText(setMinute(video_time_minute) + " : " + setSecond(video_time_second));
                    break;
                case 2:
                    String msg = (String) message.obj;
                    Intent mIntent = new Intent();
                    mIntent.putExtra("message", msg);
                    // 设置结果，并进行传送
//          VideoRecordActivity.this.setResult(DoubileInput.DOUBLEINPUT_VIDEO_SUCCESS, mIntent);
                    VideoRecordActivity.this.finish();
                    break;
                case 3:
                    try {
                        stopVideo();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent sIntent = new Intent();
                        sIntent.putExtra("message", e.getMessage());
                        // 设置结果，并进行传送
//            VideoRecordActivity.this.setResult(DoubileInput.DOUBLEINPUT_VIDEO_SUCCESS, sIntent);
                        VideoRecordActivity.this.finish();
                    }
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    XFResourcesIDFinder.init(this);
        setContentView(R.layout.video_record_view);
        try {
            //生成softEncode实例
            softEncode = SoftEncoder.getInstance(this);
            initView();
            initListener();
            initData();
            initStartCamera();
        } catch (Exception e) {
            e.printStackTrace();
            message.obj = e.getMessage();
            message.what = 2;
            handler.sendMessage(message);
        }
    }

    /**
     * 初始化参数
     */
    private void initData() throws Exception {
        Intent intent = getIntent();
        VideoRecorderOptions recorderOptions = (VideoRecorderOptions) intent
                .getParcelableExtra(DoubleInput.DOUBLEINPUT_VIDEO_OPTION);
        mRecorderOptions = recorderOptions;
        maxVideoTime = recorderOptions.getVideoMaxTime();
        frameRate = recorderOptions.getFrameRate();
        mWidth = recorderOptions.getmWidth();
        mHeight = recorderOptions.getmHeight();
        waterPaddingWidth = recorderOptions.getWaterPaddingWidth();
        waterPaddingHeight = recorderOptions.getWaterPaddingHeight();
        int videoInput = recorderOptions.getVideoEncodingBitRate();
        int videoOutput = recorderOptions.getVideoEncodingBitRate();
        int audioOutput = recorderOptions.getAudioEncodingBitRate();
        videoEncodingBitRate = recorderOptions.getVideoEncodingBitRate();
        audioEncodingBitRate = recorderOptions.getAudioEncodingBitRate();
        audioSampleRate = recorderOptions.getAudioSampleRate();
        videoPath = recorderOptions.getVideoPath();
        isHaveWaterMark = recorderOptions.isHaveWaterMark();
        distinguishability = recorderOptions.getDistinguishability();
    }


    /**
     * 初始化录制按钮
     *
     * @throws Exception
     */
    private void initListener() throws Exception {
        imageButton_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (softEncode.isStarted()) {
                    try {
                        if (video_time <= 5 && video_time > 0) {
                            Toast.makeText(VideoRecordActivity.this, "录制时间过短", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        handler.sendEmptyMessage(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    imageButton_video.setImageResource(R.drawable.softencode_stop);
                    imageButton_camera_change.setVisibility(View.INVISIBLE);
                    try {
                        initAnimation();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        softEncode.Record(mWidth, mHeight, frameRate, videoEncodingBitRate, videoPath);
                        softEncode.startAudioRecord();
                        initVideoStartData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.obj = e.getMessage();
                        message.what = 2;
                        handler.sendMessage(message);
                    }

                }
            }
        });

        imageButton_camera_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraPos = cameraPos == 0 ? 1 : 0;
                try {
                    switchCamera();
                } catch (Exception e) {
                    if (preview != null) {
                        preview.releaseCamera();
                        softEncode.releaseCamera();
                        finish();
                    }
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 初始化即时操作
     */
    private void initVideoStartData() throws Exception {
        video_time = 0;
        video_time_second = 0;
        video_time_minute = 0;
        progressBar.setMax(maxVideoTime);
        progressBar.setProgress(0);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progressBar.incrementProgressBy(1);
                video_time++;
                video_time_minute = getMinute(video_time);
                video_time_second = getSecond(video_time);
                handler.sendEmptyMessage(1);
                if (video_time == maxVideoTime) {
                    handler.sendEmptyMessage(3);
                }
            }
        }, 0, 1000);
    }


    /**
     * 初始化相机
     */
    private void initStartCamera() throws Exception {
        switchCamera();
    }

    /**
     * 初始化本地yuv图片
     */
    private void initYUV() throws Exception {
        //将一张水印图片转为test.yuv
        PicToYuv yuv = new PicToYuv();
//		需要放一张图片手机sd卡目录（具体放哪儿可自定义）
        if (mRecorderOptions!=null&&mRecorderOptions.getWaterMarkPicture()!=null){
            btp = mRecorderOptions.getWaterMarkPicture();
        }else{
            btp = BitmapFactory.decodeResource(getResources(), R.drawable.softencode_watermark);
        }
        btp = yuv.scaleBitmap(mWidth, mHeight, btp.getWidth(), btp.getHeight(), btp, 0.99f, 1f);
        byte[] b = yuv.getYUV420sp(btp);
//		生成的yuv文件必须在/mnt/sdcard/test.yuv（该版本底层暂时写死）
        yuv.getFile(b);
    }


    /**
     * 初始化view
     *
     * @throws Exception
     */
    private void initView() throws Exception {
        progressBar = (ProgressBar) findViewById(R.id.pb_video);
        time_video = (TextView) findViewById(R.id.time_video);
        video_record_img = (ImageView) findViewById(R.id.video_record_img);
        video_record_view = (FrameLayout) findViewById(R.id.video_record);
        imageButton_video = (ImageButton) findViewById(R.id.video_record_btn);
        imageButton_camera_change = (ImageButton) findViewById(R.id.imageButton_camera_change);
    }


    /**
     * 切换前后摄像头
     */
    private void switchCamera() throws Exception {
//        initCameraParams();
        Log.e("fenbialv", mWidth + "+" + mHeight);
        initYUV();
        video_record_view.removeAllViews();
        preview = null;
        mCamera = null;
        //第一步init参数（视频分辨率的宽、高、是否添加水印、水印宽、高、水印起始坐标X、Y(0-100百分比)）
        softEncode.init(mWidth, mHeight, isHaveWaterMark, btp.getWidth(), btp.getHeight(),
                waterPaddingWidth, waterPaddingHeight);
        //设置音频比特率（默认16000）
        softEncode.setAudioBitRate(audioEncodingBitRate);
        //设置音频采样率（默认16000），可选参数（96000、88200、64000、48000
        //	、44100、32000、24000、22050、16000、12000、11025、8000、7350）
        softEncode.setAudioSampleRate(audioSampleRate);
        switchCamera(cameraPos, mWidth, mHeight, frameRate);
        if (preview == null) {
            preview = new CameraPreview(getApplicationContext(), mCamera, softEncode);
        }
        video_record_view.addView(preview);

    }

    /**
     * 获取不同摄像头的分辨率
     *
     * @throws JSONException
     */
    private void initCameraParams() throws JSONException {
        String params = SoftCameraManager.getPerformance(VideoRecordActivity.this, "display", String
                .valueOf(cameraPos));
        JSONObject videoParams = new JSONObject(params);
        List heights = new ArrayList();
        Iterator it = videoParams.keys();
        while (it.hasNext()) {
            String key = ((String) it.next());
            heights.add(Integer.parseInt(key));
        }
        mHeight = SoftCameraManager.binarysearchKey(heights.toArray(), distinguishability);
        mWidth = videoParams.getInt(String.valueOf(mHeight));

        Log.e("fenbialv", mWidth + "+" + mHeight);
    }

    /**
     * 设置camera参数
     *
     * @param width
     * @param height
     * @param frameRate
     */
    public void setCameraParameters(int width, int height, int frameRate) {
        if (mCamera != null) {
            Log.e(TAG, "setCameraParameters");
            Camera.Parameters params = mCamera.getParameters();
            params.setPreviewSize(width, height);
            params.setPreviewFrameRate(frameRate);
            mCamera.setParameters(params);
        } else {
            Log.d(TAG, "camera is null !!");
        }
    }

    /**
     * 切换摄像头
     *
     * @param cameraPos
     * @param width
     * @param height
     * @param frameRate
     */
    public void switchCamera(int cameraPos, int width, int height, int frameRate) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数
        Log.v(TAG, "camera number is : " + cameraCount + "");
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && cameraPos == 1) {
                releaseCamera();
                openCamera(cameraPos);
                setCameraParameters(width, height, frameRate);
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK && cameraPos == 0) {
                releaseCamera();
                openCamera(cameraPos);
                setCameraParameters(width, height, frameRate);
            }
        }
    }

    /**
     * 打开camera
     *
     * @param i
     */
    public void openCamera(int i) {
        if (mCamera == null) {
            Log.e(TAG, "openCamera");
            mCamera = getCameraInstance(i);
            Log.e(TAG, "get camera:" + mCamera == null ? "fail" : "success");
        } else {
            Log.e(TAG, "mCamera == null");
        }
    }

    /**
     * 获取camera对象
     *
     * @param i
     * @return
     */
    public static Camera getCameraInstance(int i) {
        Camera c = null;
        try {
            c = Camera.open(i); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "get mCamera fail:" + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * 释放摄像头资源
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }


    /**
     * 停止视频录制
     */
    public void stopVideo() throws Exception {
//    Plugin_ZS_Video.handler.sendEmptyMessage(1001);
        if (softEncode != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    softEncode.stopRecord();
                    softEncode.stopAudioRecord();
                    if (timer != null) {
                        timer.cancel();
                    }
                    preview = null;
                    mCamera = null;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//          Plugin_ZS_Video.handler.sendEmptyMessage(1002);
                }
            }).start();
            VideoRecordActivity.this.setResult(DoubleInput.DOUBLEINPUT_VIDEO_SUCCESS);
            VideoRecordActivity.this.finish();
        }
    }

    /**
     * 闪烁动画
     */
    private void initAnimation() throws Exception {
        Animation animation = new AlphaAnimation(1.0f, 0);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(1000);
        animation.setRepeatCount(maxVideoTime);
        video_record_img.setAnimation(animation);
    }

    /**
     * 获取计时器的秒值
     *
     * @param video_time
     * @return
     */
    private int getSecond(int video_time) {
        return video_time % 60;
    }

    /**
     * 获取计时器的分值
     *
     * @param video_time_second
     * @return
     */
    private int getMinute(int video_time_second) {
        return video_time_second / 60;
    }

    /**
     * 秒值小于10的补0
     *
     * @param video_time_second
     * @return
     */
    private String setSecond(int video_time_second) {
        return video_time_second < 10 ? "0" + video_time_second : String.valueOf(video_time_second);
    }

    /**
     * 分值小于10的补0
     *
     * @param video_time_minute
     * @return
     */
    private String setMinute(int video_time_minute) {
        return video_time_minute < 10 ? "0" + video_time_minute : String.valueOf(video_time_minute);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("where", "video-stop");
//    Plugin_ZS_Video.isIn = false;
        try {
//      PhoneDisableManager.stopPhoneCallListener(this);
            if (timer != null) {
                timer.cancel();
            }
            cancelRecord();
//      this.setResult(DoubleInput.DOUBLEINPUT_VIDEO_SUCCESS);
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (softEncode != null) {
            softEncode.releaseCamera();
        }
    }

    /**
     * 取消录制不保存
     */
    public void cancelRecord() {
        if (softEncode != null) {
            if (softEncode.isStarted()) {
                softEncode.cancelRecord();
                softEncode.stopAudioRecord();
                softEncode.releaseCamera();
                preview = null;
                mCamera = null;
            }
        }
    }

}
