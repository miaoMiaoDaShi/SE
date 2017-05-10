package lq.xxp.se.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

import lq.xxp.se.MainActivity;
import lq.xxp.se.R;

public class DownloadService extends Service {

    private long currentTime;
    private long startTime;

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadStart(intent.getStringExtra("url"));
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadStart(final String url) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "下载正在启动中.....", Toast.LENGTH_SHORT).show();
            String savePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "movie.mp4";
            final HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(url, savePath, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Toast.makeText(getApplicationContext(), "下载成功了", Toast.LENGTH_SHORT).show();
                    File video = responseInfo.result;
                    //pd.dismiss();

                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(getApplicationContext(), "下载失败!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);

                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    //downloadDialogShow(total, current);
                    currentTime = System.currentTimeMillis();
//                    if((currentTime-startTime)>60000){
                        showNotification("喵喵撸","下载中"+(int) (current / (double) total * 100)+"%文件位于sd卡根目录","来自喵喵撸");
//                        startTime = currentTime;
//                    }

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "获取SD卡路径失败!", Toast.LENGTH_SHORT).show();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification(String title, String content, String ticker ){
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent go_main = PendingIntent.getActivity(getApplicationContext(),0,new Intent(getApplicationContext(), MainActivity.class),0);
        Notification myNotification = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.icon)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(go_main)
                //.setVibrate(new long[]{1000,1000})
                .setLights(Color.RED, 3000, 3000)
                .build();
        myNotification.flags = Notification.FLAG_AUTO_CANCEL;

        manager.notify(1, myNotification);
    }
}
