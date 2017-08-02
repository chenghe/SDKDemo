package com.zsmarter.doubleinputsdk.audio;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.SeekBar;

import com.zsmarter.doubleinoutsdk.R;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by hechengbin on 2017/3/31.
 */

public class AudioPlayActivity extends Activity {
    
    public static final String PLAY_PATH = "palyPath";
    private SeekBar mSeekBar;
    private String path;
    private MediaPlayer mediaPlayer;
    private boolean isCellPlay;/*在挂断电话的时候，用于判断是否为是来电时中断*/
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private int currentPosition;//当前音乐播放的进度
    private SeekBar seekBar;
    private Timer timer;
    private Activity activity = AudioPlayActivity.this;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_audioplay);
        
        
        Intent intent = getIntent();
        path = intent.getStringExtra(PLAY_PATH);
        
        //实例化媒体播放器
        mediaPlayer = new MediaPlayer();
        mSeekBar = (SeekBar) findViewById(R.id.playSeekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
            }
            
            /*滚动时,应当暂停后台定时器*/
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }
            /*滑动结束后，重新设置值*/
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = false;
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        
        if (!TextUtils.isEmpty(path)){
            mediaPlayer.reset();
            currentPosition = 0;
            play();
        }
    }


    /**
     * 播放
     */
    private void play() {
        //        File media = new File("/mnt/sdcard/files/","bewithyou.mp3");//由于是练习，就把mp3名称固定了
        //        Log.i(TAG, media.getAbsolutePath());
        //        if(media.exists())
        //        {
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音频类型
            mediaPlayer.setDataSource(path);//设置mp3数据源
            mediaPlayer.prepareAsync();//数据缓冲
            /*监听缓存 事件，在缓冲完毕后，开始播放*/
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.seekTo(currentPosition);
                    //                        playButton.setText(R.string.play);
                    mSeekBar.setMax(mediaPlayer.getDuration());
                }
            });
            //结束播放
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    activity.finish();
                }
            });
            //监听播放时回调函数
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!isSeekBarChanging){
                        mSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            },0,50);
        } catch (Exception e) {
            //                Toast.makeText(getApplicationContext(), R.string.playError, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            System.out.println(e);
        }
        
        //        else{
        //            Toast.makeText(getApplicationContext(), R.string.fileError, Toast.LENGTH_LONG).show();
        //        }
        
    }
    
    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        timer.cancel();
        timer = null;
        mediaPlayer = null;
        super.onDestroy();
    }
    
    
}
