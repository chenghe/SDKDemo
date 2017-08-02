package com.zsmarter.doubleinputsdk.video;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.security.NoSuchProviderException;

import javax.crypto.Cipher;

/**
 * Created by wangxf on 16-12-22.
 */

public class EncryptionOrDecryptionTask extends AsyncTask<Void, Void, Boolean> {

    private String mSourceFile = "";
    private String mNewFilePath = "";
    private String mSeed = "";
    private boolean mIsEncrypt = false;
    private AESHelper mAESHelper;
    private Handler handler;

    public EncryptionOrDecryptionTask(boolean isEncrypt, String sourceFile,
                                      String newFilePath, String seed, Handler handler) {
        this.mSourceFile = sourceFile;
        this.mNewFilePath = newFilePath;
        this.mSeed = seed;
        this.mIsEncrypt = isEncrypt;
        this.mAESHelper = new AESHelper();
        this.handler = handler;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean result = false;
        try {

            if (mIsEncrypt) {
                result = mAESHelper.AESCipher(Cipher.ENCRYPT_MODE, mSourceFile,
                        mNewFilePath , mSeed);

            } else {
                result = mAESHelper.AESCipher(Cipher.DECRYPT_MODE, mSourceFile,
                        mNewFilePath , mSeed);
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        String showMessage = "";
        if (mIsEncrypt) {
            showMessage = result ? "加密已完成" : "加密失败!";
            handler.sendEmptyMessage(01);
        } else {
            showMessage = result ? "解密完成" : "解密失败!";
            Message message = new Message();
            message.obj = this.mSourceFile;
            message.what = 02;
            handler.sendMessage(message);
        }
        Log.w("T", showMessage);
    }
}
