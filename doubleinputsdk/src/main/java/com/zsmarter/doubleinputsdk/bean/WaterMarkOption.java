package com.zsmarter.doubleinputsdk.bean;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @project android
 * Created by wangxf on 2016/10/31
 */

public class WaterMarkOption {

    public static final int POSITION_LEFT_TOP = 01;
    public static final int POSITION_RIGHT_TOP = 02;
    public static final int POSITION_LEFT_BOTTOM = 03;
    public static final int POSITION_RIGHT_BOTTOM = 04;
    public static final int POSITION_MIDDLE = 05;
    public static final int TYPE_MARK_STRING = 11;
    public static final int TYPE_MARK_PHOTO = 12;
    public static final int TYPE_MARK_ALL = 13;
    public final static int FROMCAMERA =  0;
    public final static int FROMPATH =  1;

    private int type;
    private int position;
    private String imgPath;//目标图片路径（相对路径）
    private String imgName;//目标图片名称
    private String stringMarkText;
    private String sourcePhotoPath;//源图片路径
    private String markPhotoPath;
    private String sourcePhotoBase64;
    private String markPhotoBase64;
    private  int fromType = FROMCAMERA;
    private int penColor ;
    private int inSampleSize;
    private int stringMarkSize = 40;
    private int alpha = 255;//透明度
    private int leftStringPadding = 15;
    private int rightStringPadding = 15;
    private int bottomStringPadding = 15;
    private int topStringPadding = 15;
    private int leftPhotoPadding = 15;
    private int rightPhotoPadding = 15;
    private int bottomPhotoPadding = 15;
    private int topPhotoPadding = 15;
    private  Bitmap sourceBitmap;
    private Bitmap markBitmap;

    public WaterMarkOption(String imgPath,String imgName){
        this.imgPath = imgPath;
        this.imgName = imgName;
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
        stringMarkText = dateFormat.format(now).toString();
        Log.i("hcb","stringMarkText" + stringMarkText);
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public void setPenColor(int penColor) {
        this.penColor = penColor;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public Bitmap getSourceBitmap() {
        return sourceBitmap;
    }

    public void setSourceBitmap(Bitmap sourceBitmap) {
        this.sourceBitmap = sourceBitmap;
    }

    public Bitmap getMarkBitmap() {
        return markBitmap;
    }

    public void setMarkBitmap(Bitmap markBitmap) {
        this.markBitmap = markBitmap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPenColor() {
        return penColor;
    }

    public void setPenColor(String penColor) {
        if (penColor == null || penColor == "") {
            this.penColor = Color.BLACK;
        } else {
            this.penColor = Color.parseColor(penColor);
        }
    }

    public String getStringMarkText() {
        return stringMarkText;
    }

    public void setStringMarkText(String stringMarkText) {
        this.stringMarkText = stringMarkText;
    }

    public String getSourcePhotoPath() {
        return sourcePhotoPath;
    }

    public void setSourcePhotoPath(String sourcePhotoPath) {
        this.sourcePhotoPath = sourcePhotoPath;
    }

    public String getMarkPhotoPath() {
        return markPhotoPath;
    }

    public void setMarkPhotoPath(String markPhotoPath) {
        this.markPhotoPath = markPhotoPath;
    }

    public int getInSampleSize() {
        return inSampleSize;
    }

    public void setInSampleSize(int inSampleSize) {
        this.inSampleSize = inSampleSize;
    }

    public int getStringMarkSize() {
        return stringMarkSize;
    }

    public void setStringMarkSize(int stringMarkSize) {
        this.stringMarkSize = stringMarkSize;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        if (alpha == 0) {
            alpha = 255;
        }
        this.alpha = alpha;
    }

    public int getLeftStringPadding() {
        return leftStringPadding;
    }

    public void setLeftStringPadding(int leftPadding) {
        this.leftStringPadding = leftPadding;
    }

    public int getRightStringPadding() {
        return rightStringPadding;
    }

    public void setRightStringPadding(int rightPadding) {
        this.rightStringPadding = rightPadding;
    }

    public int getBottomStringPadding() {
        return bottomStringPadding;
    }

    public void setBottomStringPadding(int bottomPadding) {
        this.bottomStringPadding = bottomPadding;
    }

    public int getTopStringPadding() {
        return topStringPadding;
    }

    public void setTopStringPadding(int topPadding) {
        this.topStringPadding = topPadding;
    }

    public int getLeftPhotoPadding() {
        return leftPhotoPadding;
    }

    public void setLeftPhotoPadding(int leftPhotoPadding) {
        this.leftPhotoPadding = leftPhotoPadding;
    }

    public int getRightPhotoPadding() {
        return rightPhotoPadding;
    }

    public void setRightPhotoPadding(int rightPhotoPadding) {
        this.rightPhotoPadding = rightPhotoPadding;
    }

    public int getBottomPhotoPadding() {
        return bottomPhotoPadding;
    }

    public void setBottomPhotoPadding(int bottomPhotoPadding) {
        this.bottomPhotoPadding = bottomPhotoPadding;
    }

    public int getTopPhotoPadding() {
        return topPhotoPadding;
    }

    public void setTopPhotoPadding(int topPhotoPadding) {
        this.topPhotoPadding = topPhotoPadding;
    }

    public String getSourcePhotoBase64() {
        return sourcePhotoBase64;
    }

    public void setSourcePhotoBase64(String sourcePhotoBase64) {
        this.sourcePhotoBase64 = sourcePhotoBase64;
    }

    public String getMarkPhotoBase64() {
        return markPhotoBase64;
    }

    public void setMarkPhotoBase64(String markPhotoBase64) {
        this.markPhotoBase64 = markPhotoBase64;
    }

    @Override
    public String toString() {
        return "WaterMarkOption{" +
                "type='" + type + '\'' +
                "imgName='" + imgName + '\'' +
                "imgPath='" + imgPath + '\'' +
                ", postion='" + position + '\'' +
                ", stringMarkText='" + stringMarkText + '\'' +
                ", sourcePhotoPath='" + sourcePhotoPath + '\'' +
                ", markPhotoPath='" + markPhotoPath + '\'' +
                ", penColor=" + penColor +
                ", inSampleSize=" + inSampleSize +
                ", fromType=" + fromType +
                ", stringMarkSize=" + stringMarkSize +
                ", alpha=" + alpha +
                ", leftStringPadding=" + leftStringPadding +
                ", rightStringPadding=" + rightStringPadding +
                ", bottomStringPadding=" + bottomStringPadding +
                ", topStringPadding=" + topStringPadding +
                ", leftPhotoPadding=" + leftPhotoPadding +
                ", rightPhotoPadding=" + rightPhotoPadding +
                ", bottomPhotoPadding=" + bottomPhotoPadding +
                ", topPhotoPadding=" + topPhotoPadding +
                ", sourcePhotoBase64=" + sourcePhotoBase64 +
                ", markPhotoBase64=" + markPhotoBase64 +
                '}';
    }
}
