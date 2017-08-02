package com.zsmarter.doubleinputsdk.softencode;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by wangxf on 2017/5/4.
 */

public class SoftCameraManager {
    private static Camera mCamera = null;

    public static Camera getSoftEncodeCamera() {
        if (mCamera == null) {
            mCamera = Camera.open();
        }
        return mCamera;
    }

    public static boolean setCameraParameters(int width, int height, int frameRate) throws
            Exception {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            params.setPreviewSize(width, height);
            params.setPreviewFrameRate(frameRate);
            mCamera.setParameters(params);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取视频参数
     *
     * @param pos
     * @throws JSONException
     */
    public static JSONObject getVideoParams(int pos) throws JSONException {
        Camera mCamera = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    Camera.getCameraInfo(i, info);
                    if (info.facing == pos) {
                        mCamera = Camera.open(i);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource(mCamera);
        }
        if (mCamera == null) return null;
        Camera.Parameters parameters = mCamera.getParameters();
        StringBuffer sb = new StringBuffer();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < parameters.getSupportedVideoSizes().size(); i++) {
            Camera.Size size = parameters.getSupportedVideoSizes().get(i);
            jsonObject.put(String.valueOf(size.height), size.width);
        }
        freeCameraResource(mCamera);
        return jsonObject;
    }

    /**
     * 获取视频参数
     *
     * @throws JSONException
     */
    public static JSONObject getOpenCameraParams(Camera mCamera) throws JSONException {

        if (mCamera == null) return null;
        Camera.Parameters parameters = mCamera.getParameters();
        StringBuffer sb = new StringBuffer();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < parameters.getSupportedVideoSizes().size(); i++) {
            Camera.Size size = parameters.getSupportedVideoSizes().get(i);
            jsonObject.put(String.valueOf(size.height), size.width);
        }
        return jsonObject;
    }


    public static void releaseCamera() {
        mCamera = null;
    }

    /**
     * 释放摄像头资源
     */

    public static void freeCameraResource(Camera mCamera) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 取最近的分辨率
     *
     * @param array
     * @param targetNum
     * @return
     */
    public static int binarysearchKey(Object[] array, int targetNum) {
        Arrays.sort(array);
        int left = 0, right = 0;
        for (right = array.length - 1; left != right; ) {
            int midIndex = (right + left) / 2;
            int mid = (right - left);
            int midValue = (Integer) array[midIndex];
            if (targetNum == midValue) {
                return midValue;
            }
            if (targetNum > midValue) {
                left = midIndex;
            } else {
                right = midIndex;
            }
            if (mid <= 1) {
                break;
            }
        }
        int rightnum = ((Integer) array[right]).intValue();
        int leftnum = ((Integer) array[left]).intValue();
        int ret = Math.abs((rightnum - leftnum) / 2) > Math.abs(rightnum - targetNum) ? rightnum
                : leftnum;
        return ret;
    }


    /**
     * 获取首帧图片
     *
     * @param videoPath
     * @param width
     * @param height
     * @param kind
     * @return
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils
                .OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 保存bitmap到本地
     *
     * @param bmp
     * @param path
     * @param name
     * @return
     */
    public static String saveImage(Bitmap bmp, String path, String name) {
        File appDir = new File(path);
        if (bmp == null) {
            return null;
        }
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取图片的名字
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        String name;
        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if (start != -1 && end != -1) {
            name = path.substring(start + 1, end);
        } else {
            return null;
        }
        return name;
    }

    /**
     * 获取图片的路径
     *
     * @param path
     * @return
     */
    public static String getFilePath(String path) {
        String realPath;
        int end = path.lastIndexOf("/");
        if (end != -1) {
            realPath = path.substring(0, end);
        } else {
            return null;
        }
        return realPath;
    }

    /**
     * 生成文件夹
     *
     * @param filePath
     */
    public static void makeDirectory(String filePath) throws Exception {
        File file = new File(filePath.substring(0, filePath.lastIndexOf("/")));
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void setPerformance(Context context, String name, String value0, String value1) {
        SharedPreferences preferences = context.getSharedPreferences(name, 0);
        if (preferences.getString(name, null) != null) {
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(String.valueOf(0), value0);
        editor.putString(String.valueOf(1), value1);
        editor.commit();
    }

    public static String getPerformance(Context context, String name, String code) {
        SharedPreferences preferences = context.getSharedPreferences(name, 0);
        return preferences.getString(code, null);
    }

    public static void clearPerformance(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(name, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
