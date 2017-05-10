package lq.xxp.se.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 钟大爷 on 2016/11/5.
 */

public class Player implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback {
    private int videoWidth;
    private int videoHeight;
    private TextView tv_self;
    public MediaPlayer mediaPlayer;
    private SeekBar skbProgress;
    private int currentProgress;
    private Timer mTimer = new Timer();
    private String url;
    private LinearLayout mStart;
    private int CurrentPosition;
    private SurfaceView mSurfaceView;
    private LinearLayout bottom;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Player(SurfaceView surfaceView, SeekBar skbProgress, TextView tv_self, LinearLayout start,
                  LinearLayout bottom) {

        this.skbProgress = skbProgress;
        this.tv_self = tv_self;
        this.mStart = start;
        this.mSurfaceView = surfaceView;
        this.bottom = bottom;
        //surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().addCallback(this);
        mTimer.schedule(mTimerTask, 0, 1000);

    }

    /*******************************************************
     * 通过定时器和Handler来更新进度条
     ******************************************************/
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
                handleProgress.sendEmptyMessage(0);
            } else if (!mediaPlayer.isPlaying()) {
                Message msg = handleProgress.obtainMessage();
                msg.what = 1;
                handleProgress.sendMessage(msg);
            }
        }
    };
    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
            } else {

                int position = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                if (duration > 0) {
                    long pos = skbProgress.getMax() * position / duration;
                    skbProgress.setProgress((int) pos);
                    Calendar calendar_1 = Calendar.getInstance();
                    Calendar calendar_2 = Calendar.getInstance();
                    calendar_1.setTimeInMillis(mediaPlayer.getCurrentPosition());
                    calendar_2.setTimeInMillis(mediaPlayer.getDuration());
                    calendar_1.set(Calendar.HOUR, calendar_1.get(Calendar.HOUR) - 8);
                    calendar_2.set(Calendar.HOUR, calendar_2.get(Calendar.HOUR) - 8);
                    Date date_1 = calendar_1.getTime();
                    Date date_2 = calendar_2.getTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    tv_self.setText(simpleDateFormat.format(date_1).toString() + "/" + simpleDateFormat.format(date_2).toString());
                }
            }

        }
    };

    //*****************************************************
    public void play() {
        mediaPlayer.start();
    }


    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.e("mediaPlayer", "surface changed");
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        if(CurrentPosition>0){
            mediaPlayer.start();

        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();//prepare之后自动播放
            mediaPlayer.setDisplay(mSurfaceView.getHolder());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);

        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }
        Log.e("mediaPlayer", "surface created");
        System.out.println("创建了");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            CurrentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
            mStart.setVisibility(View.VISIBLE);
        }

        Log.e("mediaPlayer", "surface destroyed");
        System.out.println("销毁了");
    }

    @Override
    /**
     * 通过onPrepared播放
     */
    public void onPrepared(MediaPlayer arg0) {
        bottom.setVisibility(View.INVISIBLE);
        videoWidth = mediaPlayer.getVideoWidth();
        videoHeight = mediaPlayer.getVideoHeight();
        if (videoHeight != 0 && videoWidth != 0) {
            mStart.setVisibility(View.INVISIBLE);
            if(mediaPlayer.isPlaying()){
                return;
            } else {
                mediaPlayer.start();
                mediaPlayer.seekTo(CurrentPosition);
            }

        }
        Log.e("mediaPlayer", "onPrepared");
        System.out.println(" 通过onPrepared播放");
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        skbProgress.setSecondaryProgress(bufferingProgress);
        Message msg = handleProgress.obtainMessage();
        msg.what = 1;
        handleProgress.sendMessage(msg);
        int currentProgress = skbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        Log.e(currentProgress + "% play", bufferingProgress + "% buffer");
    }
}
