package com.zsmarter.hechengbin.sdkdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.werb.permissionschecker.PermissionChecker;
import com.zsmarter.doubleinputsdk.bean.DoubleInput;
import com.zsmarter.doubleinputsdk.bean.VideoRecorderOptions;
import com.zsmarter.doubleinputsdk.bean.DoubleInoutSDKKey;
import com.zsmarter.doubleinputsdk.bean.WaterMarkOption;
import com.zsmarter.hechengbin.sdkdemo.securityplugin.SecurityActivity;
import com.zsmarter.sdklib.SDKMainActivity;
import com.zsmarter.sdklib.SDKWebActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private Button sdkbutton;
    private Button startAudio;
    private Button stopAudio;
    private Button playAudio;
    private Button startVideo;
    private Button startWarterMarker;
    private Button startCgnb;
    private Button btsec;
    private String videoPath;
    private DoubleInput doubileInput;
    private PermissionChecker permissionChecker;
    private Activity activity = MainActivity.this;
    private Uri mMediaUrl;
    private DoubleInoutSDKKey sdkKey;
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    private String appid = "REQ0RTQyOTAtRDEzRC00NzYyLTlDMjctOTU3NkNBOTU1OEU2";//测试用appid
    private String appkey = "ODAzMEUzQzAtRDkwMS00RTg0LThCRUMtRkU3OTdENUZCNzdF";//测试用appkey
    private String accessKey = "RUU2RURFNjQtNEQwQi00RkQ0LUIwMUUtM0UxNTc1NjJGQzVG";//测试用accessKey


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sdkKey = new DoubleInoutSDKKey(appid,appkey,accessKey);
        permissionChecker = new PermissionChecker(this); // initialize，must need

        doubileInput = new DoubleInput(activity);
        if (permissionChecker.isLackPermissions(PERMISSIONS)) {
            permissionChecker.requestPermissions();
        }

        button = (Button) findViewById(R.id.button);
        sdkbutton = (Button) findViewById(R.id.sdkbutton);
        button.setOnClickListener(this);
        sdkbutton.setOnClickListener(this);
        startAudio = (Button) findViewById(R.id.start_audio);
        startAudio.setOnClickListener(this);
        stopAudio = (Button) findViewById(R.id.stop_audio);
        stopAudio.setOnClickListener(this);
        playAudio = (Button) findViewById(R.id.play_audio);
        playAudio.setOnClickListener(this);
        startVideo = (Button) findViewById(R.id.start_video);
        startVideo.setOnClickListener(this);
        startWarterMarker = (Button) findViewById(R.id.start_warterMarker);
        startWarterMarker.setOnClickListener(this);
        startCgnb = (Button) findViewById(R.id.start_cgnb);
        startCgnb.setOnClickListener(this);
        btsec = (Button) findViewById(R.id.start_security);
        btsec.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
//        if (permissionChecker.isLackPermissions(PERMISSIONS)) {
//            permissionChecker.requestPermissions();
//        }
        switch (v.getId()) {
            case R.id.start_cgnb:
                intent = new Intent(MainActivity.this,CgnbActivity.class);
                startActivity(intent);
                break;
            case R.id.button:
                intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra(WebActivity.WEBURL, "https://www.baidu.com");
                startActivity(intent);
                break;
            case R.id.sdkbutton:
                intent = new Intent(MainActivity.this, SDKMainActivity.class);
                intent.putExtra(SDKWebActivity.SDKWEBURL, "https://www.baidu.com");
                startActivity(intent);
                break;
            case R.id.start_audio:
                if (permissionChecker.isLackPermissions(PERMISSIONS)) {
                    permissionChecker.requestPermissions();
                } else {
                    doubileInput.startAudio("aaa/audio", "text",sdkKey);
                }

                break;
            case R.id.stop_audio:
                doubileInput.stopAudio();
                break;
            case R.id.play_audio:
                break;
            case R.id.start_video:
                videoPath = "aaa/video/test";
                VideoRecorderOptions options = new VideoRecorderOptions(videoPath);
                options.setHaveWaterMark(true);
                options.setWaterMarkPicture(BitmapFactory.decodeResource(getResources(), R.drawable.watermarker));
                doubileInput.recordVideo(options,sdkKey);
                break;
            case R.id.start_warterMarker:
                WaterMarkOption option = new WaterMarkOption("aaa/picture", "text");
                option.setAlpha(255);
                option.setType(WaterMarkOption.TYPE_MARK_STRING);
                option.setPosition(WaterMarkOption.POSITION_RIGHT_BOTTOM);
                option.setStringMarkSize(40);
//                option.setPenColor("#4cd964");
                doubileInput.stakeWarterMarkPicture(option,sdkKey);
                break;
            case R.id.start_security:
                Intent intent1 = new Intent(MainActivity.this, SecurityActivity.class);
                startActivity(intent1);

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionChecker.PERMISSION_REQUEST_CODE:
                if (permissionChecker.hasAllPermissionsGranted(grantResults)) {

                } else {
                    permissionChecker.showDialog();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case DoubleInput.DOUBLEINPUT_VIDEO_SUCCESS:
//                Bitmap bitmap = SoftCameraManager.getVideoThumbnail(videoPath, 80, 80, MICRO_KIND);
//                String headerPath = SoftCameraManager.saveImage(bitmap, SoftCameraManager
//                        .getFilePath(videoPath) + "/header", SoftCameraManager.getFileName(videoPath));
//              nson.put("videoPath", videoPath);
//              nson.put("videoHeader", headerPath);
//              mCallBack.success(nson);
                break;

        }
        if (requestCode == DoubleInput.DOUBLEINPUT_WARTERMARKER_CAMERA_START) {
            doubileInput.waterMarkActivityForResult();

//            if (data == null) {
//                Bundle bundle = data.getExtras();
//                if (bundle != null) {
//                    Bitmap photo = (Bitmap) bundle.get("data");
//                    doubileInput.waterMarkActivityForResult(photo);
//                } else {
//                    Toast.makeText(MainActivity.this, "data error",
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            } else {
//                String path = data.getData().getPath();
//                doubileInput.waterMarkActivityForResult(path);
//            }
//            String path = null;
//            Uri uri = data.getData();
//            if (uri != null) {
//                path = data.getData().getPath();
//                    doubileInput.waterMarkActivityForResult(path);
//            }
//            if (path == null) {
//                Bundle bundle = data.getExtras();
//                if (bundle != null) {
//                    Bitmap photo = (Bitmap) bundle.get("data");
//                    doubileInput.waterMarkActivityForResult(photo);
//                } else {
//                    Toast.makeText(MainActivity.this, "data error",
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }

//            String path = data.getData().getPath();
//            try {
//                doubileInput.waterMarkActivityForResult(path);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
        }
    }
}
