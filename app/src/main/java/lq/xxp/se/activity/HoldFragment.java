package lq.xxp.se.activity;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lq.xxp.se.Bean.MovieBean;
import lq.xxp.se.R;
import lq.xxp.se.adapter.VideoRyAdapter;
import lq.xxp.se.utils.Base64;
import lq.xxp.se.utils.GetDatas;


public class HoldFragment extends Fragment{

    @ViewInject(R.id.rv_hold)
    private RecyclerView rv_hold;
    private String url = "https://avso.pw/cn/popular/page/";
    //支持下拉刷新的ViewGroup
    @ViewInject(R.id.rotate_header_list_view_frame)
    private PtrClassicFrameLayout mPtrFrame;
    //添加Header和Footer的封装类
    private RecyclerAdapterWithHF mAdapter;
    //List数据
    private List<MovieBean> mDatas = new ArrayList<>();
    private Handler mHandler = new Handler(){
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hold_fragment,container,false);
        ViewUtils.inject(this, view);

        initView();
        return view;

    }

    private int tiem = 1;
    @TargetApi(Build.VERSION_CODES.M)
    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_hold.setLayoutManager(layoutManager);
        mVideoRyAdapter = new VideoRyAdapter(getActivity(),mDatas);
        mAdapter = new RecyclerAdapterWithHF(mVideoRyAdapter);
        rv_hold.setAdapter(mAdapter);

        //下拉刷新支持时间
        mPtrFrame.setLastUpdateTimeRelateObject(this);
//下拉刷新一些设置 详情参考文档
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
// default is false
        mPtrFrame.setPullToRefresh(false);
// default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
//进入Activity就进行自动下拉刷新
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);


        //下拉刷新
        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mDatas.clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<MovieBean> movie = GetDatas.getInfo("https://avso.pw/cn/popular/page/1");
                        Message msg = mHandler.obtainMessage();
                        msg.obj = movie;
                        mHandler.sendMessage(msg);

                    }
                }).start();


            }
        });
//上拉加载
        mPtrFrame.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {

                    tiem ++;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<MovieBean> movie = GetDatas.getInfo("https://avso.pw/cn/popular/page/"+tiem);
                        Message msg = mHandler.obtainMessage();
                        msg.obj = movie;
                        mHandler.sendMessage(msg);

                    }
                }).start();

                mAdapter.notifyDataSetChanged();
                mPtrFrame.loadMoreComplete(true);
            }
        });

        }







    private List<MovieBean> loadJson(String json) {
        List<MovieBean> movies = new ArrayList<MovieBean>();
        try {
            JSONObject jb = new JSONObject(json);
            JSONArray ja = jb.getJSONArray("movies");
            for(int i=0;i<ja.length();i++){
                jb = ja.getJSONObject(i);
                String decodephotoUrl = new String(Base64.decode(jb.getString("photoUrl")));
                String decodeBlockLink = new String(Base64.decode(jb.getString("blockLink")));
                String decodeName = new String(Base64.decode(jb.getString("name")));
                String decodeNum = new String(Base64.decode(jb.getString("num")));
                String decodeTime = new String(Base64.decode(jb.getString("time")));
                movies.add(new MovieBean(decodeBlockLink,decodephotoUrl,decodeName,decodeNum,decodeTime));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

//    private String getJson() {
//        String encode = "";
//        try {
//            InputStreamReader isr = new InputStreamReader(getResources().openRawResource(R.raw.hold_video));
//            BufferedReader br = new BufferedReader(isr);
//            String readLine = "";
//            while ((readLine=br.readLine())!=null){
//                encode += readLine;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return encode;
//    }
}
