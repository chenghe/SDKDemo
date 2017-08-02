//package com.zsmarter.doubleinputsdk.wartermarker;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.MediaStore;
//import android.support.v4.content.FileProvider;
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
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import static com.zsmarter.watermark.WaterMarkConfig.LEFT_BOTTOM;
//import static com.zsmarter.watermark.WaterMarkConfig.LEFT_TOP;
//import static com.zsmarter.watermark.WaterMarkConfig.MARK_ALL;
//import static com.zsmarter.watermark.WaterMarkConfig.MARK_PHOTO;
//import static com.zsmarter.watermark.WaterMarkConfig.MARK_STRING;
//import static com.zsmarter.watermark.WaterMarkConfig.MIDDLE;
//import static com.zsmarter.watermark.WaterMarkConfig.RIGHT_BOTTOM;
//import static com.zsmarter.watermark.WaterMarkConfig.RIGHT_TOP;
//import static com.zsmarter.watermark.WaterMarkConfig.TAKE_WATER_PHOTO;
//
//
///**
// * Created by wangxf on 2016/10/28.
// */
//
//public class WaterMark extends CordovaPlugin {
//
//    private Activity mContext;
//    private CallbackContext mCallBack;
//    private WaterMarkOption option;
//    private String photoPath;
//    protected final static String[] permissions = { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE };
//    private Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message message) {
//            JSONObject res = new JSONObject();
//            switch (message.what){
//                case TAKE_WATER_PHOTO:
//                try {
//                    String photoPath = createWaterMark(option);
//                    res.put("photoPath", photoPath);
//                    res.put("code", "00");
//                    mCallBack.success(res);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//
//
//            return false;
//        }
//    });
//
//    @Override
//    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//        super.initialize(cordova, webView);
//        Log.d(cordova.getClass().getName(), "initialize..");
//        mContext = cordova.getActivity();
//        PermissionHelper.requestPermissions(this, 0, permissions);
//    }
//
//    @Override
//    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
//        this.mCallBack = callbackContext;
//        JSONObject jsonObject = args.getJSONObject(0);
//        option = new WaterMarkOption();
//        if(jsonObject.optString("imgName")==""||jsonObject.optString("imgPath")==""){
//            callbackContext.error("incomplete parameters");
//        }
//        option.setType(jsonObject.optString("type"));
//        option.setPostion(jsonObject.optString("postion"));
//        option.setPenColor(jsonObject.optString("penColor"));
//        option.setMarkPhotoBase64(jsonObject.optString("markPhotoBase64"));
//        option.setSourcePhotoBase64(jsonObject.optString("sourcePhotoBase64"));
//        option.setInSampleSize(jsonObject.optInt("inSampleSize"));
//        option.setStringMarkSize(jsonObject.optInt("stringMarkSize"));
//        option.setAlpha(jsonObject.optInt("alpha"));
//        option.setLeftStringPadding(jsonObject.optInt("leftStringPadding"));
//        option.setRightStringPadding(jsonObject.optInt("rightStringPadding"));
//        option.setBottomStringPadding(jsonObject.optInt("bottomStringPadding"));
//        option.setTopStringPadding(jsonObject.optInt("topStringPadding"));
//        option.setLeftPhotoPadding(jsonObject.optInt("leftPhotoPadding"));
//        option.setRightPhotoPadding(jsonObject.optInt("rightPhotoPadding"));
//        option.setBottomPhotoPadding(jsonObject.optInt("bottomPhotoPadding"));
//        option.setTopPhotoPadding(jsonObject.optInt("topPhotoPadding"));
//        option.setImgPath(jsonObject.optString("imgPath"));
//        option.setImgName(jsonObject.optString("imgName"));
//        option.setSourcePhotoPath(jsonObject.optString("sourcePhotoPath"));
//        option.setMarkPhotoPath(jsonObject.optString("markPhotoPath"));
//        option.setStringMarkText(jsonObject.optString("stringMarkText")==""?new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()):jsonObject.optString("stringMarkText"));
//        option.setFromType(jsonObject.optInt("fromType"));
//        try {
//            waterMark(option);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return true;
//    }
//
//    private void waterMark(WaterMarkOption option) throws IOException {
//        switch (option.getFromType()) {
//            case WaterMarkOption.FROMCAMERA:
//                takeWaterPhoto(option.getImgPath(), option.getImgName());
//                break;
//            case WaterMarkOption.FROMPATH:
//                handler.sendEmptyMessage(WaterMarkConfig.FROMPATH);
//                break;
//        }
//    }
//
//    private void takeWaterPhoto(String path, String name) throws IOException {
//        File appDir = new File(Environment.getExternalStorageDirectory(), path);
//         if (!appDir.exists()) {
//            appDir.mkdirs();
//        }
//        File photoPath = new File(appDir+File.separator+name+".jpg");
//        photoPath.createNewFile();
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
//                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        if (photoPath != null) {
//            Uri uri =  getPath(photoPath);
//            this.photoPath = photoPath.getAbsolutePath();
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//        }
//
//        cordova.startActivityForResult(this,intent, TAKE_WATER_PHOTO);
//    }
//
//    private  Uri getPath(File path) {
//        if(path==null){
//            return null;
//        }
//        if (Build.VERSION.SDK_INT >= 24) {
//            return FileProvider.getUriForFile(mContext.getApplicationContext(),  mContext.getPackageName()+".provider", path);
//        } else {
//            return Uri.fromFile(path);
//        }
//    }
//
//    private String createWaterMark(WaterMarkOption option) throws JSONException, FileNotFoundException {
//        Bitmap bitmap = null;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = false;
//        options.inSampleSize = option.getInSampleSize();
//            if (option.getSourcePhotoBase64() != null && option.getSourcePhotoBase64() != "") {
//                bitmap = BitmapUtil.base64ToBitmap(option.getSourcePhotoBase64(), options);
//                option.setSourceBitmap(bitmap);
//                LogUtils.d(bitmap.getWidth() + "+" + bitmap.getHeight());
//            }
//            if (option.getSourcePhotoPath() != null && option.getSourcePhotoPath() != "") {
//                bitmap = BitmapFactory.decodeFile(option.getSourcePhotoPath(), options);
//                option.setSourceBitmap(bitmap);
//            }
//
//        if (bitmap == null) {
//            throw new FileNotFoundException();
//        }
//        switch (option.getType()) {
//            case WaterMarkOption.MARK_STRING:
//                bitmap = createStringWaterMark(option);
//                break;
//            case WaterMarkOption.MARK_PHOTO:
//                bitmap = createPhotoWaterMark(option);
//                break;
//            case WaterMarkOption.MARK_ALL:
//                bitmap = createStringWaterMark(option);
//                option.setSourcePhotoBase64(BitmapUtil.bitmapToBase64(bitmap));
//                bitmap = createPhotoWaterMark(option);
//                break;
//            default:
//                option.setStringMarkSize(24);
//                option.setRightStringPadding(bitmap.getWidth() / 35);
//                option.setBottomStringPadding(bitmap.getWidth() / 35);
//                bitmap = createStringWaterMark(option);
//                break;
//        }
//        return BitmapUtil.saveImage(bitmap, option.getImgPath(), option.getImgName());
//    }
//
//    private Bitmap createStringWaterMark(WaterMarkOption option) throws JSONException {
//        Bitmap bitmap = option.getSourceBitmap();
//        switch (option.getPostion()) {
//            case WaterMarkOption.LEFT_TOP:
//                bitmap = ImageUtil.drawTextToLeftTop(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getLeftStringPadding(), option.getTopStringPadding(), option.getAlpha());
//                break;
//            case WaterMarkOption.RIGHT_TOP:
//                bitmap = ImageUtil.drawTextToRightTop(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getRightStringPadding(), option.getTopStringPadding(), option.getAlpha());
//                break;
//            case WaterMarkOption.LEFT_BOTTOM:
//                bitmap = ImageUtil.drawTextToLeftBottom(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getLeftStringPadding(), option.getBottomStringPadding(), option.getAlpha());
//                break;
//            case WaterMarkOption.RIGHT_BOTTOM:
//                bitmap = ImageUtil.drawTextToRightBottom(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getRightStringPadding(), option.getBottomStringPadding(), option.getAlpha());
//                break;
//            case WaterMarkOption.MIDDLE:
//                bitmap = ImageUtil.drawTextToCenter(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getAlpha());
//                break;
//            default:
//                bitmap = ImageUtil.drawTextToRightBottom(mContext, bitmap, option.getStringMarkText(), option.getStringMarkSize(), option.getPenColor(), option.getRightStringPadding(), option.getBottomStringPadding(), option.getAlpha());
//                break;
//        }
//        return bitmap;
//    }
//
//    private Bitmap createPhotoWaterMark(WaterMarkOption option) throws JSONException {
//        Bitmap bitmap = null;
//        Bitmap markPhoto = null;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = false;
//        options.inSampleSize = option.getInSampleSize();
//        if (option.getSourcePhotoBase64() != null && option.getSourcePhotoBase64() != "") {
//            bitmap = BitmapUtil.base64ToBitmap(option.getSourcePhotoBase64(), options);
//        }
//        if (option.getSourcePhotoPath() != null && option.getSourcePhotoPath() != "") {
//            bitmap = BitmapFactory.decodeFile(option.getSourcePhotoPath(), options);
//        }
//        if (option.getMarkPhotoBase64() != null && option.getMarkPhotoBase64() != "") {
//            markPhoto = BitmapUtil.base64ToBitmap(option.getMarkPhotoBase64(), options);
//        }
//        if (option.getMarkPhotoPath() != null && option.getMarkPhotoPath() != "") {
//            markPhoto = BitmapFactory.decodeFile(option.getMarkPhotoPath(), options);
////            markPhoto = BitmapUtil.getImageFromAssetsFile(mContext,"www/img/logo.png");
//        }
//        switch (option.getPostion()) {
//            case WaterMarkOption.LEFT_TOP:
//                bitmap = ImageUtil.createWaterMaskLeftTop(mContext, bitmap, markPhoto, option.getLeftPhotoPadding(), option.getTopPhotoPadding(), option.getAlpha());
//                break;
//            case WaterMarkOption.RIGHT_TOP:
//                bitmap = ImageUtil.createWaterMaskRightTop(mContext, bitmap, markPhoto, option.getRightPhotoPadding(), option.getTopPhotoPadding(), option.getAlpha());
//                break;
//            case WaterMarkOption.LEFT_BOTTOM:
//                bitmap = ImageUtil.createWaterMaskLeftBottom(mContext, bitmap, markPhoto, option.getLeftPhotoPadding(), option.getBottomPhotoPadding(), option.getAlpha());
//                break;
//            case WaterMarkOption.RIGHT_BOTTOM:
//                bitmap = ImageUtil.createWaterMaskRightBottom(mContext, bitmap, markPhoto, option.getRightPhotoPadding(), option.getBottomPhotoPadding(), option.getAlpha());
//                break;
//            case WaterMarkOption.MIDDLE:
//                bitmap = ImageUtil.createWaterMaskCenter(bitmap, markPhoto, option.getAlpha());
//                break;
//            default:
//                bitmap = ImageUtil.createWaterMaskRightBottom(mContext, bitmap, markPhoto, option.getRightPhotoPadding(), option.getBottomPhotoPadding(), option.getAlpha());
//                break;
//        }
//        return bitmap;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//
//            switch (requestCode) {
//                case TAKE_WATER_PHOTO: //拍摄图片并选择
//                            option.setSourcePhotoPath(this.photoPath);
//                            handler.sendEmptyMessage(TAKE_WATER_PHOTO);
//                    break;
//        }
//    }
//}
