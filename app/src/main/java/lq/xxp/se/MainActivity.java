package lq.xxp.se;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lq.xxp.se.DAO.AllLink;
import lq.xxp.se.activity.AboutFragment;
import lq.xxp.se.activity.ActorFragment;
import lq.xxp.se.activity.CollectFragment;
import lq.xxp.se.activity.HoldFragment;
import lq.xxp.se.activity.HomeFragment;
import lq.xxp.se.activity.PlayFragment;
import lq.xxp.se.activity.VideoFragment;
import lq.xxp.se.service.MyService;
import lq.xxp.se.utils.Base64;
import lq.xxp.se.utils.GetDatas;
import lq.xxp.se.utils.TorrentKittyLinkProvider;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @ViewInject(R.id.btn_open_menu)
    Button btn_open_menu;
    @ViewInject(R.id.drawer_layout)
    private DrawerLayout mDrawer;
    @ViewInject(R.id.nav_view)
    private NavigationView navigationView;
    @ViewInject(R.id.tv_title)


    private TextView tv_title;

    private HomeFragment mHomeFragment;
    private CollectFragment mCollectFragment;
    private HoldFragment mHoldFragment;
    private VideoFragment mVideoFragment;
    private ActorFragment mActorFragment;
    private AboutFragment mAboutFragment;
    private PlayFragment mPlayFragment;
    private String newAppUrl = null;
    private String mNewAppVersionDescribe = null;
    private String mNewAppVersionName = null;
    private final int UPDATE = 0X02;
    private final int NOUPDATE = 0X03;
    private final int UPDATEFAIL = 0X01;
    private final int open_0 = 1100;
    private final int open_1 = 1101;
    private final int open_2 = 1102;
    private final int open_3 = 1103;
    private final int open_4 = 1104;
    private final int open_5 = 1105;
    private final int YES_SUCCEED = 0x1996;
    private final int NO_SUCCEED = 0x1997;

    private boolean xxp = false;
    private FragmentManager mFragmentManager;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    dialogShow();
                    break;
                case UPDATEFAIL:
                    showToast("检查更新失败!");
                    break;
                case NOUPDATE:
                    showToast("软件已是最新版本!");
                    break;
                case YES_SUCCEED:
                    showToast("欢迎你喵喵撸VIP");
                    break;
                case NO_SUCCEED:
                    dialogRootShow((String) msg.obj);
                    break;
                case 1234:
                    showToast("网络连接有问题!");
                    break;
//                case open_0:
//                    setCollectView();
//                    break;
//                case open_1:
//                    setHoldView();
//                    break;
//                case open_2:
//                    setVideoView();
//                    break;
//                case open_3:
//                    setActorView();
//                    break;
//                case open_4:
//                    setPlayView();
//                    break;
//                case open_5:
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putBoolean("imgRoot",5);
//                    break;
            }
        }

    };
    private String QQKEY = "L-r5-2yR3XcSKanlCwXvr5QTNmsu3t2G";
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewUtils.inject(this);

        sp = getPreferences(MODE_PRIVATE);
        btn_open_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getFragmentManager();
        // 设置默认的Fragment
        setDefaultFragment();
        //checkRoot();
        test();
    }


    private void showToast(String content) {
        Toast.makeText(getApplicationContext(), content, Toast.LENGTH_LONG).show();
    }

    private Fragment mContent;

    /**
     * 修改显示的内容 不会重新加载
     **/
    public void switchContent(Fragment to) {
        if (mContent != to) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (!to.isAdded()) { // 先判断是否被add过
                // 隐藏当前的fragment，add下一个到Activity中
                transaction.hide(mContent).add(R.id.fl_content, to).commit();
            } else {
                transaction.hide(mContent).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            mContent = to;
        }
    }

    private void setDefaultFragment() {
        tv_title.setText("主页");
        FragmentTransaction ft_home = mFragmentManager.beginTransaction();
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        ft_home.add(R.id.fl_content, mHomeFragment).commit();
        mContent = mHomeFragment;


    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setHomeView();
        } else if (id == R.id.nav_collect) {
//            if (!xxp) {
//                checkRoot(0);
//            } else
            setCollectView();
        } else if (id == R.id.nav_hold) {
//            if (!xxp) {
//                checkRoot(1);
//            } else
            setHoldView();
        } else if (id == R.id.nav_video) {
//            if (!xxp) {
//                checkRoot(2);
//            } else
            setVideoView();
        } else if (id == R.id.nav_image) {
//            if (!xxp) {
//                checkRoot(3);
//            } else
            setActorView();
        } else if (id == R.id.nav_clear) {
            Toast.makeText(getApplicationContext(), "正在检查更新!", Toast.LENGTH_SHORT).show();
            checkUpdate();
        } else if (id == R.id.nav_player) {
//            if (!xxp) {
//                checkRoot(4);
//            } else
            Toast.makeText(getApplicationContext(), "喵喵大大已将其关闭!", Toast.LENGTH_SHORT).show();
            //setPlayView();
        }
//        } else if (id == R.id.nav_clear) {
//            if(!xxp){
//                checkRoot();
//            } else {
//
//            }
//        }
//        else if (id == R.id.nav_register) {
//            Intent intent = new Intent(getApplicationContext(), VebActivity.class);
//            intent.putExtra("blockLink", AllLink.soft_img_sq);
//            startActivity(intent);
//        }
        else if (id == R.id.nav_info) {


            setAboutView();

        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setHomeView() {
        tv_title.setText("主页");
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        switchContent(mHomeFragment);

    }

    private void setCollectView() {
        tv_title.setText("收藏");
        if (mCollectFragment == null) {
            mCollectFragment = new CollectFragment();
        }
        switchContent(mCollectFragment);

    }

    private void setHoldView() {
        tv_title.setText("最热");
        if (mHoldFragment == null) {
            mHoldFragment = new HoldFragment();
        }
        switchContent(mHoldFragment);

    }

    private void setVideoView() {
        tv_title.setText("视频");
        if (mVideoFragment == null) {
            mVideoFragment = new VideoFragment();
        }
        switchContent(mVideoFragment);


    }

    private void setActorView() {
        tv_title.setText("女优");
        if (mActorFragment == null) {
            mActorFragment = new ActorFragment();
        }
        switchContent(mActorFragment);
    }

    private void setPlayView() {
        tv_title.setText("极速在线播放");
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
        }
        switchContent(mPlayFragment);
    }

    private void setAboutView() {
        tv_title.setText("免责申明");
        if (mAboutFragment == null) {
            mAboutFragment = new AboutFragment();
        }
        switchContent(mAboutFragment);

    }

    //检查用户权限
    private void checkRoot() {
        new Thread() {
            private String resultData = "";
            private String imei;
            private InputStream is;
            String str1 = "";
            Message msg = mHandler.obtainMessage();

            public void run() {
                try {
                    imei = getIMEI();
                    str1 = imei.substring(3, 8);
                    Log.d("mmds", str1);
                    HttpURLConnection hcc = (HttpURLConnection) new URL(AllLink.soft_json_main).openConnection();
                    if(hcc.getConnectTimeout()>5){
                        msg.what=1234;
                        mHandler.sendMessage(msg);
                    }
                    resultData = GetDatas.GetJson(AllLink.soft_json_main);


                    if (resultData.contains(Base64.encode("Everyone".getBytes()))) {
                        msg.what = YES_SUCCEED;
                    } else if (resultData.indexOf(Base64.encode(str1.getBytes())) != -1) {
                        msg.what = YES_SUCCEED;
                    } else  if(resultData.contains(Base64.encode("171****6450".getBytes()))){
                        msg.what = YES_SUCCEED;
                    }
                    else {
                        msg.what = NO_SUCCEED;
                    }
                } catch (Exception e) {
                    if (resultData.contains(Base64.encode("Everyone".getBytes()))) {
                        msg.what = YES_SUCCEED;
                    } else {
                        msg.what = NO_SUCCEED;
                    }

                } finally {
                    msg.obj = str1;
                    mHandler.sendMessage(msg);
                }

            }
        }.start();


    }

    private void succeed() {
        xxp = true;
//        Message msg = mHandler.obtainMessage();
//        switch (operation) {
//            case 0:
//                msg.what = open_0;
//                mHandler.sendMessage(msg);
//                break;
//            case 1:
//                msg.what = open_1;
//                mHandler.sendMessage(msg);
//                break;
//            case 2:
//                msg.what = open_2;
//                mHandler.sendMessage(msg);
//                break;
//            case 3:
//                msg.what = open_3;
//                mHandler.sendMessage(msg);
//                break;
//            case 4:
//                msg.what = open_4;
//                mHandler.sendMessage(msg);
//                break;
//            case 5:
//                msg.what = open_5;
//                mHandler.sendMessage(msg);
//                break;
//        }
    }

    private String getIMEI() {
        return ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
    }

    private void rootCheckFalse(final String imei) {
        Dialog dl = new Dialog(MainActivity.this, R.style.dialog);
        //dl.setCancelable(false);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_check, null);

        final EditText et = (EditText) view.findViewById(R.id.et_xxp);
        Button bt = (Button) view.findViewById(R.id.bt_sendmass);
        et.setText(imei);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("seedLink", et.getText().toString()));
                Toast.makeText(getApplicationContext(), "机器码已复制到剪切板!请加其联系方式直接粘贴",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Window window = dl.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        dl.show();
        wl.gravity = Gravity.CENTER;
        wl.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        wl.height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.24);
        dl.getWindow().setAttributes(wl);
        dl.setContentView(view);
    }

    private void startExit() {
        showToast("软件将在5分钟后退出!");
        startService(new Intent(getApplicationContext(), MyService.class));
    }

    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /****************
     * 发起添加群流程。群号：ＱＣＭＭ(600484665) 的 key 为： L-r5-2yR3XcSKanlCwXvr5QTNmsu3t2G
     * 调用 joinQQGroup(L-r5-2yR3XcSKanlCwXvr5QTNmsu3t2G) 即可发起手Q客户端申请加群 ＱＣＭＭ(600484665)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    private boolean joinQQGroup(String key, String im) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            rootCheckFalse(im);
            return false;
        }
    }


    //显示更新提示的对话框
    private void dialogShow() {
        final Dialog updateDialog = new Dialog(MainActivity.this, R.style.dialog);
        updateDialog.setCancelable(false);
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog, null);
        ImageView iv_icon = (ImageView) dialogView.findViewById(R.id.iv_icon);
        TextView tv_title = (TextView) dialogView.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) dialogView.findViewById(R.id.tv_content);
        final Button btn_negative = (Button) dialogView.findViewById(R.id.btn_negative);
        Button btn_positive = (Button) dialogView.findViewById(R.id.btn_positive);


        updateDialog.setContentView(dialogView);

        tv_title.setText("新版本" + mNewAppVersionName + "发布");
        tv_content.setText(mNewAppVersionDescribe);
        tv_content.setTextSize(23);

        View.OnClickListener myListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_negative:
                        updateDialog.dismiss();
                        break;
                    case R.id.btn_positive:
                        updateDialog.dismiss();
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText("", newAppUrl));
                        Toast.makeText(getApplicationContext(), "下载链接已复制到剪切板", Toast.LENGTH_SHORT).show();
                        updateStart();
                        break;
                }
            }
        };

        btn_negative.setOnClickListener(myListener);
        btn_positive.setOnClickListener(myListener);

        updateDialog.show();
        Window win = updateDialog.getWindow();
        WindowManager.LayoutParams wl = win.getAttributes();
        wl.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.9);
        wl.height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.4);
        win.setAttributes(wl);

    }


    //开始更新方案
    private void updateStart() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "下载正在启动中.....", Toast.LENGTH_SHORT).show();
            String savePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "xxp.apk";
            final HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(newAppUrl, savePath, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Toast.makeText(getApplicationContext(), "下载成功了", Toast.LENGTH_SHORT).show();
                    File newApp = responseInfo.result;
                    pd.dismiss();
                    setupApp(newApp);

                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(getApplicationContext(), "下载失败!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(newAppUrl);
                    intent.setData(content_url);
                    startActivity(intent);

                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    downloadDialogShow(total, current);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "获取SD卡路径失败!", Toast.LENGTH_SHORT).show();
        }
    }


    //安装新的App
    private void setupApp(File newApp) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.fromFile(newApp), "application/vnd.android.package-archive");
        startActivity(i);
    }

    //弹出下载的进度框
    ProgressDialog pd = null;

    private void downloadDialogShow(long total, long current) {
        if (pd == null) {
            pd = new ProgressDialog(MainActivity.this);
        }
        pd.setCancelable(false);
        pd.setTitle("正在玩命下载中....");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress((int) (current / (double) total * 100));
        pd.show();
    }


    //获取版本号
    private String getVersionName() {
        PackageInfo pi = null;
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi.versionName;
    }

    //获取版本号
    private int getVersionCode() {
        PackageInfo pi = null;
        PackageManager pm = getPackageManager();
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi.versionCode;
    }


    //检查是否要更新
    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime;
                long endTime;
                startTime = System.currentTimeMillis();
                String json = GetDatas.GetJson(AllLink.soft_json_main);
                Log.d("mmds", "检查是否要更新" + json);

                Message msg = Message.obtain();
                msg.what = NOUPDATE;

                JSONObject jb = null;

                try {
                    jb = new JSONObject(json);
                    newAppUrl = new String(Base64.decode(jb.getString("updateUrl")));
                    mNewAppVersionDescribe = new String(Base64.decode(jb.getString("versionDescribe")));
                    mNewAppVersionName = jb.getString("versionName");
                    //如果服务器端的版本号大于本地的就进行更新
                    if (getVersionCode() < Integer.parseInt(jb.getString("versionCode"))) {
                        msg.what = UPDATE;

                    }
                } catch (JSONException e) {
                    msg.what = UPDATEFAIL;
                }

                endTime = System.currentTimeMillis();
                mHandler.sendMessage(msg);


            }
        }).start();
    }

    //显示需要权限提示的对话框
    private void dialogRootShow(final String imei) {
        final Dialog updateDialog = new Dialog(MainActivity.this, R.style.dialog);
        updateDialog.setCancelable(false);
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.forroot, null);
        ImageView iv_icon = (ImageView) dialogView.findViewById(R.id.iv_icon);
        TextView tv_title = (TextView) dialogView.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) dialogView.findViewById(R.id.tv_content);
        final Button btn_negative = (Button) dialogView.findViewById(R.id.btn_negative);
        Button btn_positive = (Button) dialogView.findViewById(R.id.btn_positive);


        updateDialog.setContentView(dialogView);

        tv_title.setText("你还不是正式用户");
        tv_content.setText("正式用户特权大大!\n如你是非正式用户,软件将为你提供5分钟的试用时间!\n开发人员平时较忙," +
                "请加群后耐心等待!");
        tv_content.setTextSize(23);

        View.OnClickListener myListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_negative:
                        updateDialog.dismiss();
                        startExit();
                        break;
                    case R.id.btn_positive:
                        startExit();
                        updateDialog.dismiss();
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText("seedLink", imei));
                        Toast.makeText(getApplicationContext(), "机器码已复制到剪切板!请加群直接粘贴",
                                Toast.LENGTH_SHORT).show();
                        joinQQGroup(QQKEY, imei);
                        break;
                }
            }
        };

        btn_negative.setOnClickListener(myListener);
        btn_positive.setOnClickListener(myListener);

        updateDialog.show();
        Window win = updateDialog.getWindow();
        WindowManager.LayoutParams wl = win.getAttributes();
        wl.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.9);
        wl.height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.4);
        win.setAttributes(wl);

    }
    private void test() {
        TorrentKittyLinkProvider to = new TorrentKittyLinkProvider();
        Call<ResponseBody> s004 = to.search("s004", 1);
        //ResponseBody re = s004;
    }


}
