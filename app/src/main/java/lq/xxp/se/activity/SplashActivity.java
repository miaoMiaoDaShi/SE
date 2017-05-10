package lq.xxp.se.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import lq.xxp.se.MainActivity;
import lq.xxp.se.R;
import lq.xxp.se.utils.Check;


public class SplashActivity extends AppCompatActivity {

    @ViewInject(R.id.activity_splash)
    private RelativeLayout mSplashlayout;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        ViewUtils.inject(this);
        initData();

        AlphaAnimation alpha = new AlphaAnimation(0,1);
        alpha.setDuration(1000);
        mSplashlayout.setAnimation(alpha);
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                checkInternet();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private void initData() {
        sp = getPreferences(MODE_PRIVATE);
    }

    /*
    检查网络
     */
    private void checkInternet() {
        if (!Check.isConnect(getApplicationContext())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
            dialog.setTitle("软件提示");
            dialog.setMessage("检测到网络不可用进入也没用!");
            dialog.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   System.exit(0);
                }
            });

            dialog.setPositiveButton("进入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goHome();
                }
            });
            dialog.show();
        } else if(sp.getBoolean("isOnceUse",true)) {
            wranDialog();
        } else {
            goHome();
        }



    }

    private void wranDialog() {

        Dialog dl = new Dialog(SplashActivity.this, R.style.dialog);
        dl.setCancelable(false);
        View view = LayoutInflater.from(SplashActivity.this).inflate(R.layout.warn_dialog, null);
        Button btn_agree = (Button) view.findViewById(R.id.btn_come);
        final CheckBox cb_agree = (CheckBox) view.findViewById(R.id.cb_agree);
        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_agree.isChecked()){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isOnceUse",false);
                    editor.commit();
                    goHome();
                } else{
                    Toast.makeText(getApplicationContext(),"你还没勾选同意使用",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Window window = dl.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        dl.show();
        wl.gravity = Gravity.CENTER;
        wl.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.9);
        wl.height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.5);
        dl.getWindow().setAttributes(wl);
        dl.setContentView(view);
    }

    private void goHome() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }


}
