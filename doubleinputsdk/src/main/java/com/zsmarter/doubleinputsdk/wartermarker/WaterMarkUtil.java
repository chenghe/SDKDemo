package com.zsmarter.doubleinputsdk.wartermarker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.zsmarter.doubleinputsdk.bean.DoubleInput;
import com.zsmarter.doubleinputsdk.bean.WaterMarkOption;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by hechengbin on 2017/7/20.
 */

public class WaterMarkUtil {

    private Activity mContext;
    private String photoPath;
    private WaterMarkOption option;

    public WaterMarkUtil(WaterMarkOption option,Activity mContext){
        this.option = option;
        this.mContext = mContext;
    }

    public void setWaterMarkOption(WaterMarkOption option){
        this.option = option;
    }


    public void waterMark() throws IOException {
        switch (option.getFromType()) {
            case WaterMarkOption.FROMCAMERA:
                takeWaterPhoto(option.getImgPath(), option.getImgName());
                break;
            case WaterMarkOption.FROMPATH:
//                handler.sendEmptyMessage(WaterMarkConfig.FROMPATH);
                break;
        }
    }

    private void takeWaterPhoto(String path, String name) throws IOException {
        File appDir = new File(Environment.getExternalStorageDirectory(), path);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        File photoPath = new File(appDir+File.separator+name+".jpg");
        photoPath.createNewFile();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (photoPath != null) {
            Uri uri =  getPath(photoPath);
            this.photoPath = photoPath.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        }

        mContext.startActivityForResult(intent, DoubleInput.DOUBLEINPUT_WARTERMARKER_CAMERA_START);
    }

    private  Uri getPath(File path) {
        if(path==null){
            return null;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(mContext.getApplicationContext(),  mContext.getPackageName()+".provider", path);
        } else {
            return Uri.fromFile(path);
        }
    }

    public String createWaterMark() throws JSONException, FileNotFoundException {
        photoPath = Environment.getExternalStorageDirectory()+"/"+option.getImgPath()+"/"+option.getImgName()+".jpg";
        Log.i("hcb","photoPath"+ photoPath);
        option.setSourcePhotoPath(photoPath);
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = option.getInSampleSize();
        if (option.getSourcePhotoBase64() != null && option.getSourcePhotoBase64() != "") {
            bitmap = BitmapUtil.base64ToBitmap(option.getSourcePhotoBase64(), options);
            option.setSourceBitmap(bitmap);
            LogUtils.d(bitmap.getWidth() + "+" + bitmap.getHeight());
        }
        if (option.getSourcePhotoPath() != null && option.getSourcePhotoPath() != "") {
            bitmap = BitmapFactory.decodeFile(option.getSourcePhotoPath(), options);
            option.setSourceBitmap(bitmap);
        }

        if (bitmap == null) {
            throw new FileNotFoundException();
        }
        switch (option.getType()) {
            case WaterMarkOption.TYPE_MARK_STRING:
                bitmap = createStringWaterMark(option);
                break;
            case WaterMarkOption.TYPE_MARK_PHOTO:
                bitmap = createPhotoWaterMark(option);
                break;
            case WaterMarkOption.TYPE_MARK_ALL:
                bitmap = createStringWaterMark(option);
                option.setSourcePhotoBase64(BitmapUtil.bitmapToBase64(bitmap));
                bitmap = createPhotoWaterMark(option);
                break;
            default:
                option.setStringMarkSize(24);
                option.setRightStringPadding(bitmap.getWidth() / 35);
                option.setBottomStringPadding(bitmap.getWidth() / 35);
                bitmap = createStringWaterMark(option);
                break;
        }
        return BitmapUtil.saveImage(bitmap, option.getImgPath(), option.getImgName());

    }

    public String createWaterMark(String path) throws JSONException, FileNotFoundException {
        if (option == null){
            return null;
        }
        option.setSourcePhotoPath(path);
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = option.getInSampleSize();
        if (option.getSourcePhotoBase64() != null && option.getSourcePhotoBase64() != "") {
            bitmap = BitmapUtil.base64ToBitmap(option.getSourcePhotoBase64(), options);
            option.setSourceBitmap(bitmap);
            LogUtils.d(bitmap.getWidth() + "+" + bitmap.getHeight());
        }
        if (option.getSourcePhotoPath() != null && option.getSourcePhotoPath() != "") {
            bitmap = BitmapFactory.decodeFile(option.getSourcePhotoPath(), options);
            option.setSourceBitmap(bitmap);
        }

        if (bitmap == null) {
            throw new FileNotFoundException();
        }
        switch (option.getType()) {
            case WaterMarkOption.TYPE_MARK_STRING:
                bitmap = createStringWaterMark(option);
                break;
            case WaterMarkOption.TYPE_MARK_PHOTO:
                bitmap = createPhotoWaterMark(option);
                break;
            case WaterMarkOption.TYPE_MARK_ALL:
                bitmap = createStringWaterMark(option);
                option.setSourcePhotoBase64(BitmapUtil.bitmapToBase64(bitmap));
                bitmap = createPhotoWaterMark(option);
                break;
            default:
                option.setStringMarkSize(24);
                option.setRightStringPadding(bitmap.getWidth() / 35);
                option.setBottomStringPadding(bitmap.getWidth() / 35);
                bitmap = createStringWaterMark(option);
                break;
        }
        return BitmapUtil.saveImage(bitmap, option.getImgPath(), option.getImgName());
    }

    public String createWaterMark(Bitmap bitmap) throws JSONException, FileNotFoundException {
        if (option == null){
            return null;
        }
//        option.setSourcePhotoPath(path);
//        Bitmap bitmap = null;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = false;
//        options.inSampleSize = option.getInSampleSize();
//        if (option.getSourcePhotoBase64() != null && option.getSourcePhotoBase64() != "") {
//            bitmap = BitmapUtil.base64ToBitmap(option.getSourcePhotoBase64(), options);
//            option.setSourceBitmap(bitmap);
//            LogUtils.d(bitmap.getWidth() + "+" + bitmap.getHeight());
//        }
//        if (option.getSourcePhotoPath() != null && option.getSourcePhotoPath() != "") {
//            bitmap = BitmapFactory.decodeFile(option.getSourcePhotoPath(), options);
//            option.setSourceBitmap(bitmap);
//        }

        if (bitmap == null) {
            throw new FileNotFoundException();
        }
        switch (option.getType()) {
            case WaterMarkOption.TYPE_MARK_STRING:
                bitmap = createStringWaterMark(option);
                break;
            case WaterMarkOption.TYPE_MARK_PHOTO:
                bitmap = createPhotoWaterMark(option);
                break;
            case WaterMarkOption.TYPE_MARK_ALL:
                bitmap = createStringWaterMark(option);
                option.setSourcePhotoBase64(BitmapUtil.bitmapToBase64(bitmap));
                bitmap = createPhotoWaterMark(option);
                break;
            default:
                option.setStringMarkSize(24);
                option.setRightStringPadding(bitmap.getWidth() / 35);
                option.setBottomStringPadding(bitmap.getWidth() / 35);
                bitmap = createStringWaterMark(option);
                break;
        }
        return BitmapUtil.saveImage(bitmap, option.getImgPath(), option.getImgName());
    }

    private Bitmap createStringWaterMark(WaterMarkOption option) throws JSONException {
        Bitmap bitmap = option.getSourceBitmap();
        switch (option.getPosition()) {
            case WaterMarkOption.POSITION_LEFT_TOP:
                bitmap = ImageUtil.drawTextToLeftTop(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getLeftStringPadding(), option.getTopStringPadding(), option.getAlpha());
                break;
            case WaterMarkOption.POSITION_RIGHT_TOP:
                bitmap = ImageUtil.drawTextToRightTop(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getRightStringPadding(), option.getTopStringPadding(), option.getAlpha());
                break;
            case WaterMarkOption.POSITION_LEFT_BOTTOM:
                bitmap = ImageUtil.drawTextToLeftBottom(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getLeftStringPadding(), option.getBottomStringPadding(), option.getAlpha());
                break;
            case WaterMarkOption.POSITION_RIGHT_BOTTOM:
                bitmap = ImageUtil.drawTextToRightBottom(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getRightStringPadding(), option.getBottomStringPadding(), option.getAlpha());
                break;
            case WaterMarkOption.POSITION_MIDDLE:
                bitmap = ImageUtil.drawTextToCenter(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getAlpha());
                break;
            default:
                bitmap = ImageUtil.drawTextToRightBottom(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getRightStringPadding(), option.getBottomStringPadding(), option.getAlpha());
                break;
        }
        return bitmap;
    }

    private Bitmap createPhotoWaterMark(WaterMarkOption option) throws JSONException {
        Bitmap bitmap = null;
        Bitmap markPhoto = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = option.getInSampleSize();
        if (option.getSourcePhotoBase64() != null && option.getSourcePhotoBase64() != "") {
            bitmap = BitmapUtil.base64ToBitmap(option.getSourcePhotoBase64(), options);
        }
        if (option.getSourcePhotoPath() != null && option.getSourcePhotoPath() != "") {
            bitmap = BitmapFactory.decodeFile(option.getSourcePhotoPath(), options);
        }
        if (option.getMarkPhotoBase64() != null && option.getMarkPhotoBase64() != "") {
            markPhoto = BitmapUtil.base64ToBitmap(option.getMarkPhotoBase64(), options);
        }
        if (option.getMarkPhotoPath() != null && option.getMarkPhotoPath() != "") {
            markPhoto = BitmapFactory.decodeFile(option.getMarkPhotoPath(), options);
//            markPhoto = BitmapUtil.getImageFromAssetsFile(mContext,"www/img/logo.png");
        }
        switch (option.getPosition()) {
            case WaterMarkOption.POSITION_LEFT_TOP:
                bitmap = ImageUtil.createWaterMaskLeftTop(mContext, bitmap, markPhoto, option.getLeftPhotoPadding(), option.getTopPhotoPadding(), option.getAlpha());
                break;
            case WaterMarkOption.POSITION_RIGHT_TOP:
                bitmap = ImageUtil.createWaterMaskRightTop(mContext, bitmap, markPhoto, option.getRightPhotoPadding(), option.getTopPhotoPadding(), option.getAlpha());
                break;
            case WaterMarkOption.POSITION_LEFT_BOTTOM:
                bitmap = ImageUtil.createWaterMaskLeftBottom(mContext, bitmap, markPhoto, option.getLeftPhotoPadding(), option.getBottomPhotoPadding(), option.getAlpha());
                break;
            case WaterMarkOption.POSITION_RIGHT_BOTTOM:
                bitmap = ImageUtil.createWaterMaskRightBottom(mContext, bitmap, markPhoto, option.getRightPhotoPadding(), option.getBottomPhotoPadding(), option.getAlpha());
                break;
            case WaterMarkOption.POSITION_MIDDLE:
                bitmap = ImageUtil.createWaterMaskCenter(bitmap, markPhoto, option.getAlpha());
                break;
            default:
                bitmap = ImageUtil.createWaterMaskRightBottom(mContext, bitmap, markPhoto, option.getRightPhotoPadding(), option.getBottomPhotoPadding(), option.getAlpha());
                break;
        }
        return bitmap;
    }
}
