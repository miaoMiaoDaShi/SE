package lq.xxp.se.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lq.xxp.se.Bean.DbBean;
import lq.xxp.se.Bean.MovieInfoBean;
import lq.xxp.se.Db.DbInit;
import lq.xxp.se.R;
import lq.xxp.se.utils.GetDatas;

public class MovieInfoActivity extends AppCompatActivity {

    @ViewInject(R.id.iv_mi_collect)
    private ImageView iv_mi_collect;
    @ViewInject(R.id.tv_seed_list_wait)
    private TextView tv_waitwarn;
    @ViewInject(R.id.seed_list_wait)
    private LinearLayout seed_list_wait;
    //@ViewInject(R.id.lv_seed)
    private ListView lv_seed;
    @ViewInject(R.id.rl_movieinfo)
    private RelativeLayout rl_movieinfo;
    @ViewInject(R.id.btn_gett)
    private Button btn_get;
    @ViewInject(R.id.iv_movieinfo_img)
    private ImageView iv_img;
    @ViewInject(R.id.tv_movieinfo_num)
    private TextView tv_num;
    @ViewInject(R.id.tv_movieinfo_name)
    private TextView tv_name;
    @ViewInject(R.id.tv_movieinfo_time)
    private TextView tv_time;
    @ViewInject(R.id.tv_movieinfo_len)
    private TextView tv_len;
    @ViewInject(R.id.tv_movieinfo_make)
    private TextView tv_make;
    @ViewInject(R.id.tv_movieinfo_series)
    private TextView tv_series;
    @ViewInject(R.id.tv_movieinfo_category)
    private TextView tv_category;
    @ViewInject(R.id.btn_back)
    private Button btn_back;
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    private Bundle mBundle;
    private final int INITDATA = 1;
    private final int INITSEEDLIST = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == INITDATA) {

                MovieInfoBean movieInfoBean = (MovieInfoBean) msg.obj;
                BitmapUtils bitmapUtils = new BitmapUtils(getApplicationContext());
                imgUrl = movieInfoBean.getImgUrl();
                bitmapUtils.display(iv_img, movieInfoBean.getImgUrl());
                String name = movieInfoBean.getName();
                num = movieInfoBean.getNum();
                time = movieInfoBean.getTime();
                String len = movieInfoBean.getLenght();
                String producers = movieInfoBean.getProducers();
                String series = movieInfoBean.getSeries();
                String category = movieInfoBean.getCategory();

                tv_name.setText("片名: " + name);
                tv_num.setText("识别号: " + num);
                tv_time.setText("发行时间: " + time);
                tv_len.setText("时长: " + len);
                tv_make.setText("制作商: " + producers);
                tv_series.setText("系列: " + series);
                tv_category.setText("种类: " + category);

                //initSeedList();

            } else if (msg.what == INITSEEDLIST) {
                if (msg.obj != null) {
                    mList = (List<Map<String, Object>>) msg.obj;
                    seed_list_wait.setVisibility(View.INVISIBLE);
                    initSeedLIstView(mList);
                } else {
                    tv_waitwarn.setText("没有资源啦!");
                }

            }
        }
    };
    private DbInit mDbInit;
    private List<DbBean> mDbBeens;
    private String num;
    private String time;
    private String imgUrl;
    private List<Map<String, Object>> mList;

    private void initSeedLIstView(final List<Map<String, Object>> list) {
        lv_seed.setVisibility(View.VISIBLE);
        SimpleAdapter adapter = new SimpleAdapter(
                getApplicationContext(),
                list,
                R.layout.seedlist_item,
                new String[]{"title", "otherInfo"},
                new int[]{R.id.tv_seedinfo_title, R.id.tv_seedinfo_otherinfo}
        );
        lv_seed.setAdapter(adapter);
        lv_seed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("seedLink", list.get(position).get("seedLink").toString()));
                Toast.makeText(getApplicationContext(), "种子链接已复制到剪切板!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int startWidth;
    private int startHeight;
    private boolean isOpend = false;
    private ViewGroup.LayoutParams layoutParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        ViewUtils.inject(this);
        mList = new ArrayList<>();
        initView();
        initData();
        initCollect();
    }

    private void initCollect() {
        if (mDbBeens != null) {
            for (int i = 0; i < mDbBeens.size(); i++) {
                if (mDbBeens.get(i).getTitle().equals(mBundle.getString("name"))) {
                    iv_mi_collect.setVisibility(View.GONE);
                }
            }
        }
    }

    private final String TBNAME = "tb_collect";

    private void initData() {
        mDbInit = DbInit.DbInitInstance(getApplicationContext());
        mDbBeens = mDbInit.giveData(TBNAME);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MovieInfoBean movieINfos = GetDatas.getMovieInfo(mBundle.getString("Link"));
                Message msg = mHandler.obtainMessage();
                msg.what = INITDATA;
                msg.obj = movieINfos;
                mHandler.sendMessage(msg);
            }
        }).start();

    }

    private boolean isFirft = true;

    private void initView() {


        startWidth = rl_movieinfo.getWidth();
        startHeight = rl_movieinfo.getHeight();

        mBundle = getIntent().getExtras();
        tv_title.setText(mBundle.getString("name"));
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!isOpend) {
//                    isOpend = true;
//                    layoutParams = rl_movieinfo.getLayoutParams();
//                    lv_seed.setVisibility(View.VISIBLE);
//                    if (mList.size() == 0) {
//                        seed_list_wait.setVisibility(View.VISIBLE);
//                    }
//                    rl_movieinfo.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
//                    btn_get.setText("展开电影详情");
//                } else {
//                    isOpend = false;
//                    rl_movieinfo.setLayoutParams(layoutParams);
//                    lv_seed.setVisibility(View.INVISIBLE);
//                    btn_get.setText("展开种子列表");
//                }
//                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                cm.setPrimaryClip(ClipData.newPlainText("Link",num));
//                Toast.makeText(getApplicationContext(), "种子号码已复制到剪切板!",
//                        Toast.LENGTH_SHORT).show();
//                Intent intent  = new Intent(getApplicationContext(),VebActivity.class);
//                intent.putExtra("blockLink","https://btso.pw/tags");
//                startActivity(intent);
                Intent intent = new Intent(MovieInfoActivity.this, DownloadActivity.class);
                Bundle arguments = new Bundle();
                arguments.putString("keyword",num);
                intent.putExtras(arguments);
                startActivity(intent);
            }
        });

        iv_mi_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", mBundle.getString("name"));
                contentValues.put("blockLink", mBundle.getString("Link"));
                contentValues.put("num", num);
                contentValues.put("imgUrl", imgUrl);
                contentValues.put("time", time);
                contentValues.put("type", "videos");
                mDbInit.addData(TBNAME, contentValues);
                Toast.makeText(getApplicationContext(), "收藏成功!", Toast.LENGTH_SHORT).show();
                iv_mi_collect.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initSeedList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Map<String, Object>> list = GetDatas.getSeedInfo(getIntent().getExtras().getString("num"));
                Message msg = mHandler.obtainMessage();
                msg.what = INITSEEDLIST;
                msg.obj = list;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}
