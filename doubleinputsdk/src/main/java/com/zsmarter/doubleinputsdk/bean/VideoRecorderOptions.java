package com.zsmarter.doubleinputsdk.bean;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zsmarter.doubleinputsdk.utils.FileUtil;

import java.io.File;
import java.io.Serializable;

/**
 * Created by wangxf on 2017/4/27.
 */

public class VideoRecorderOptions implements Parcelable {

    private int videoMaxTime = 60;//最大录制时间
    private int mWidth = 1080;//录制视频宽度
    private int mHeight = 720;//录制视频高度
//    private int frame = 18;
    private int videoEncodingBitRate = 1024;//生成的视频的视频码率
    private int audioEncodingBitRate = 128000;//生成的视频的音频码率
    private int audioSampleRate = 16000;//生成的视频的音频采样率(采样率的列表必须符合下列规则,不能随意更改:96000、88200、64000、48000、44100、32000、24000、22050、16000、12000、11025、8000、7350)
    private int waterPaddingWidth = 20;//水印宽度padding
    private int waterPaddingHeight = 20;//水印高度padding
    private int frameRate = 24;//视频帧率
    private int distinguishability = 720;//视频清晰度(例如:720,1080)
    private Bitmap waterMarkPicture;//设置水印图片
    private boolean isHaveWaterMark = false;//是否开启水印


    private String videoPath = "/mnt/sdcard/doubleinput/video";// 生成的视频在手机上的位置 (例如:doubleinput/123.mp4)
//    private String videoName = "test";

    public VideoRecorderOptions(String videoPath){
        if (!TextUtils.isEmpty(videoPath)){
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";//获取SD卡绝对路径
            this.videoPath = absolutePath+videoPath+".mp4";
        }

    }


    protected VideoRecorderOptions(Parcel in) {
        videoMaxTime = in.readInt();
        mWidth = in.readInt();
        mHeight = in.readInt();
//        frame = in.readInt();
        videoEncodingBitRate = in.readInt();
        audioEncodingBitRate = in.readInt();
        audioSampleRate = in.readInt();
        waterPaddingWidth = in.readInt();
        waterPaddingHeight = in.readInt();
        frameRate = in.readInt();
        distinguishability = in.readInt();
        waterMarkPicture = in.readParcelable(Bitmap.class.getClassLoader());
        isHaveWaterMark = in.readByte() != 0;
        videoPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(videoMaxTime);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
//        dest.writeInt(frame);
        dest.writeInt(videoEncodingBitRate);
        dest.writeInt(audioEncodingBitRate);
        dest.writeInt(audioSampleRate);
        dest.writeInt(waterPaddingWidth);
        dest.writeInt(waterPaddingHeight);
        dest.writeInt(frameRate);
        dest.writeInt(distinguishability);
        dest.writeParcelable(waterMarkPicture, flags);
        dest.writeByte((byte) (isHaveWaterMark ? 1 : 0));
        dest.writeString(videoPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoRecorderOptions> CREATOR = new Creator<VideoRecorderOptions>() {
        @Override
        public VideoRecorderOptions createFromParcel(Parcel in) {
            return new VideoRecorderOptions(in);
        }

        @Override
        public VideoRecorderOptions[] newArray(int size) {
            return new VideoRecorderOptions[size];
        }
    };

    public Bitmap getWaterMarkPicture() {
        return waterMarkPicture;
    }

    public void setWaterMarkPicture(Bitmap waterMarkPicture) {
        this.waterMarkPicture = waterMarkPicture;
    }

    public int getAudioSampleRate() {
        return audioSampleRate;
    }

    public void setAudioSampleRate(int audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }

    public int getDistinguishability() {
        return distinguishability;
    }

    public void setDistinguishability(int distinguishability) {
        this.distinguishability = distinguishability;
    }

    public boolean isHaveWaterMark() {
        return isHaveWaterMark;
    }

    public void setHaveWaterMark(boolean haveWaterMark) {
        isHaveWaterMark = haveWaterMark;
    }

//    private static VideoRecorderOptions videoRecorderOptions;

    private VideoRecorderOptions() {
    }

//    public static VideoRecorderOptions getInstance() {
//        if (videoRecorderOptions == null) {
//            videoRecorderOptions = new VideoRecorderOptions();
//        }
//        return videoRecorderOptions;
//    }

    public int getWaterPaddingWidth() {
        return waterPaddingWidth;
    }

    public void setWaterPaddingWidth(int waterPaddingWidth) {
        this.waterPaddingWidth = waterPaddingWidth;
    }

    public int getWaterPaddingHeight() {
        return waterPaddingHeight;
    }

    public void setWaterPaddingHeight(int waterPaddingHeight) {
        this.waterPaddingHeight = waterPaddingHeight;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getVideoMaxTime() {
        return videoMaxTime;
    }

    public void setVideoMaxTime(int videoMaxTime) {
        this.videoMaxTime = videoMaxTime;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

//    public int getFrame() {
//        return frame;
//    }

//    public void setFrame(int frame) {
//        this.frame = frame;
//    }

    public int getVideoEncodingBitRate() {
        return videoEncodingBitRate;
    }

    public void setVideoEncodingBitRate(int videoEncodingBitRate) {
        this.videoEncodingBitRate = videoEncodingBitRate;
    }

    public int getAudioEncodingBitRate() {
        return audioEncodingBitRate;
    }

    public void setAudioEncodingBitRate(int audioEncodingBitRate) {
        this.audioEncodingBitRate = audioEncodingBitRate;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}
