package lq.xxp.se.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Timer;
import java.util.TimerTask;

import lq.xxp.se.R;
import lq.xxp.se.utils.DownloadService;
import lq.xxp.se.utils.Player;

public class PlayActivity extends AppCompatActivity {

    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;
    @ViewInject(R.id.surfaceView1)
    private SurfaceView surfaceView;
    @ViewInject(R.id.btnPause)
    private ImageButton ibtn_pause;

    @ViewInject(R.id.frame_play)
    private FrameLayout fragm_play;
    @ViewInject(R.id.bottom)
    private LinearLayout bottom;
    @ViewInject(R.id.tv_self)
    private TextView tv_self;
    @ViewInject(R.id.tv_sd)
    private TextView tv_sd;
    @ViewInject(R.id.iv_download)
    private ImageButton iv_download;
    @ViewInject(R.id.skbProgress)
    private SeekBar skbProgress;
    private Player player;
    private boolean isPlaying = true;
    private boolean isFirst = true;
    @ViewInject(R.id.start)
    private LinearLayout start;
    private final int hideBottom = 11;
    private int loadTime;
    private Boolean isFirs = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                long kb = (long) msg.obj;
                if(kb<50){
                    loadTime ++;
                    if(loadTime>10&&isFirs){
                        Toast.makeText(getApplicationContext(),"当前影片解码状态不佳,返回看看其他的吧!",Toast.LENGTH_LONG).show();
                        isFirs = false;
                    }
                }
                tv_sd.setText(kb+"/kb");
            } else if(msg.what ==hideBottom ){
                bottom.setVisibility(View.INVISIBLE);
            }
        }
    };
    private String url;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Log.e("mmds","0");
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ViewUtils.inject(this);
        Log.e("mmds","1");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initPlay();
//                progressBar.setVisibility(View.VISIBLE);
//                start.setVisibility(View.GONE);
//            }
//        });
        ibtn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    player.pause();
                    ibtn_pause.setBackgroundResource(R.drawable.play);
                    isPlaying = false;
                } else if (!isPlaying) {
                    player.play();
                    isPlaying = true;
                    ibtn_pause.setBackgroundResource(R.drawable.pause);
                }
            }
        });

        iv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this, DownloadService.class);
                intent.putExtra("url", url);
                startService(intent);
            }
        });

        fragm_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottom();
            }
        });
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        player = new Player(surfaceView, skbProgress, tv_self, start, bottom);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initPlay();
    }

    private void initPlay() {
        url = getIntent().getExtras().getString("Link");
        player.setUrl(url);
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeStamp = System.currentTimeMillis();
        new Timer().schedule(task, 1000, 2000); // 1s后启动任务，每2s执行一次

    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
            //tv_self.setText(player.mediaPlayer.getDuration());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);

        }
    }


    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            showNetSpeed();
        }
    };

    private void showNetSpeed() {

        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        Message msg = mHandler.obtainMessage();
        msg.what = 100;
        msg.obj = speed;

        mHandler.sendMessage(msg);//更新界面
    }

    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出播放", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                player.stop();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void hideBottom() {
        bottom.setVisibility(View.VISIBLE);
        if (bottom.getVisibility()==View.VISIBLE){
            Message msg = mHandler.obtainMessage();
            msg.what = hideBottom;
            mHandler.sendMessageDelayed(msg,5000);
        }
    }

}
