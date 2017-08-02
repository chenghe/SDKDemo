package com.zsmarter.doubleinputsdk.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import com.zsmarter.doubleinputsdk.utils.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by hechengbin on 17/3/14.
 */

public class AudioUtil {
    public static String TAG = "AudioUtil";
    private MediaUtils mediaUtils;
    private Activity activity;
    private String mp3Path;
    private String format = "mp3";
    private PowerManager.WakeLock m_wklk;
    private int encodingBitRate = 256000;
    private int samplingRate = 44100;

    public AudioUtil(Activity activity) {
        this.activity = activity;
        mediaUtils = new MediaUtils(activity);
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cn");

        //        mediaUtils.setTargetDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
    }

    /**
     * 开始录制音频文件
     *
     * @param path 需要录制文件的相对路径
     * @param name 录制音频名称
     */
    public void startAudio(String path, String name) {

        if (TextUtils.isEmpty(path)||TextUtils.isEmpty(name)){
            Log.e(TAG,"name"+name);
            Log.e(TAG,"path"+path);
            return;
        }

        FileUtil fileUtil = new FileUtil();
        File file = fileUtil.makeRootDirectoryReturn(path);
        Log.i("test", file.getAbsolutePath());
        mediaUtils.setRecorderType(MediaUtils.MEDIA_AUDIO);
        //        mediaUtils.setTargetDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
        mediaUtils.setTargetDir(file);
        mediaUtils.setTargetName(name + ".mp3");
        mediaUtils.record();
        mp3Path = path + name + ".mp3";

        m_wklk.acquire(); //设置保持唤醒
        Log.i("test", "startAudrio");
        Log.i("test", "mp3Path" + mp3Path);
    }

    /**
     * 开始自定义录制参数录制
     *
     * @param path            需要录制文件的相对路径
     * @param name            录制音频名称
     * @param encodingBitRate 音频比特率（默认256000,无需修改请传0）
     * @param samplingRate    音频采样率（默认44100,无需修改请传0）
     * @param format          音频格式设置 （默认MP3）
     */
    public void starAudioParam(String path, String name, int encodingBitRate, int samplingRate, String format) {

        if (samplingRate > 0) {
            this.samplingRate = samplingRate;
        }

        if (encodingBitRate > 0) {
            this.encodingBitRate = encodingBitRate;

        }

        FileUtil fileUtil = new FileUtil();
        File file = fileUtil.makeRootDirectoryReturn(path);
        Log.i("test", file.getAbsolutePath());
        mediaUtils.setRecorderType(MediaUtils.MEDIA_AUDIO);
        mediaUtils.setTargetDir(file);
        mediaUtils.setTargetName(name + "." + format);
        mediaUtils.setAudioSamplingRate(samplingRate);
        mediaUtils.setAudioEncodingBitRate(encodingBitRate);
        mediaUtils.record();
        mp3Path = path + name + "." + format;

        m_wklk.acquire(); //设置保持唤醒
        Log.i("test", "starAudioParam");
        Log.i("test", "mp3Path" + mp3Path);
    }

    /**
     * 停止录制
     */
    public void stopAudio() {
        //        mediaUtils.stopNow();
        mediaUtils.setRecording(true);
        mediaUtils.stopRecordSave();
        m_wklk.release(); //设置保持唤醒
        Log.i("test", "stopAudrio");
        //        Toast.makeText(activity, "文件以保存至：" + path, Toast.LENGTH_SHORT).show();

    }


    /**
     * 播放音频文件
     * @param path
     */
    public void playAudio(String path) {
        Log.i("test", "playAudio");
        Log.i("test", "path" + path);

        if(TextUtils.isEmpty(path)){
            return;
        }

        File file = new File(path);
        Intent it = new Intent(Intent.ACTION_VIEW);
        if (android.os.Build.VERSION.SDK_INT < 24) {

            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it.setDataAndType(Uri.parse("file://" + path), "audio/MP3");


        } else {
            //7.0以上版本自定义播放器播放
            Uri uri = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName() + ".provider", file);
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            it.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Log.i("test", "uri " + uri);
            it.setDataAndType(uri, "audio/*");

            List<PackageInfo> resInfoList = activity.getApplicationContext().getPackageManager().getInstalledPackages(0);
            for (PackageInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.packageName;
                activity.getApplicationContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            //            Intent intent = new Intent(activity,AudioPlayActivity.class);
            //            intent.putExtra(AudioPlayActivity.PLAY_PATH,path);
            //            activity.startActivity(intent);
        }
        activity.startActivity(it);
    }

    public String getPath() {
        return mp3Path;
    }


}
