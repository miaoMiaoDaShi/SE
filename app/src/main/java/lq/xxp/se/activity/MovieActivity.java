package lq.xxp.se.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import lq.xxp.se.Bean.DbBean;
import lq.xxp.se.Bean.MovieBean;
import lq.xxp.se.Db.DbInit;
import lq.xxp.se.R;
import lq.xxp.se.adapter.VideoRyAdapter;
import lq.xxp.se.utils.GetDatas;

public class MovieActivity extends AppCompatActivity {

    @ViewInject(R.id.iv_ma_collect)
    private ImageView iv_ma_collect;
    @ViewInject(R.id.btn_back)
    private Button btn_back;
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.rv_movie)
    private RecyclerView rv_movie;
    private String url;
    private Bundle mBundle;
    private final int INITDATA  = 1;
    private final int UPDATE = 2;

    @ViewInject(R.id.rotate_header_list_view_frame)
    private PtrClassicFrameLayout mPtrFrame;
    private RecyclerAdapterWithHF mAdapter;
    private List<MovieBean> mDatas = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mDatas.addAll((List<MovieBean>) msg.obj);
            mAdapter.notifyDataSetChanged();
            mPtrFrame.refreshComplete();
            mPtrFrame.setLoadMoreEnable(true);
        }
    };

    private VideoRyAdapter mVideoRyAdapter;
    private int time = 1;
    private DbInit mDbInit;
    private List<DbBean> mDbBeens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        ViewUtils.inject(this);

        initView();
        initData();
        initCollect();
    }

    private void initCollect() {
        if(mDbBeens!=null){
            for(int i=0;i<mDbBeens.size();i++){
                if(mDbBeens.get(i).getTitle().equals(mBundle.getString("name"))){
                    iv_ma_collect.setVisibility(View.GONE);
                }
            }
        }
    }

    private final String TBNAME = "tb_collect";
    private void initData() {
        mDbInit = DbInit.DbInitInstance(getApplicationContext());
        mDbBeens = mDbInit.giveData(TBNAME);
    }

    private void initView() {
        mBundle = getIntent().getExtras();
        url = mBundle.getString("Link")+"/page/";
        tv_title.setText(mBundle.getString("name"));
        rv_movie.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mVideoRyAdapter = new VideoRyAdapter(getApplicationContext(),mDatas);
        mAdapter = new RecyclerAdapterWithHF(mVideoRyAdapter);
        rv_movie.setAdapter(mAdapter);

        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        mPtrFrame.setPullToRefresh(false);
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);


        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mDatas.clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<MovieBean> movie = GetDatas.getInfo(url+time);
                        Message msg = mHandler.obtainMessage();
                        msg.obj = movie;
                        mHandler.sendMessage(msg);

                    }
                }).start();


            }
        });
        mPtrFrame.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {

                time ++;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<MovieBean> movie = GetDatas.getInfo(url+time);
                        Message msg = mHandler.obtainMessage();
                        msg.obj = movie;
                        mHandler.sendMessage(msg);

                    }
                }).start();

                mAdapter.notifyDataSetChanged();
                mPtrFrame.loadMoreComplete(true);
            }
        });




        iv_ma_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("title",mBundle.getString("name"));
                contentValues.put("blockLink",mBundle.getString("Link"));
                contentValues.put("imgUrl",mBundle.getString("imgUrl"));
                contentValues.put("type","actors");
                mDbInit.addData(TBNAME,contentValues);
                Toast.makeText(getApplicationContext(),"收藏成功!",Toast.LENGTH_SHORT).show();
                iv_ma_collect.setVisibility(View.INVISIBLE);
            }
        });

    }

}
