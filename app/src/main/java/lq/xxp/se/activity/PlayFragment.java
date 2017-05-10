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
import android.widget.ProgressBar;
import android.widget.Toast;

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
import lq.xxp.se.Bean.PlayBean;
import lq.xxp.se.R;
import lq.xxp.se.adapter.PlayRyAdapter;
import lq.xxp.se.utils.Base64;
import lq.xxp.se.utils.GetDatas;


public class PlayFragment extends Fragment {
    private int pag = 1;
    @ViewInject(R.id.loadMore)
    private ProgressBar loadMore;
    @ViewInject(R.id.rv_play)
    private RecyclerView rv_play;
    private String url = "http://www.zkzk11.com/a/shoujishipin/yazhouwuma/list_143_";
    @ViewInject(R.id.rotate_header_list_view_frame)
    private PtrClassicFrameLayout mPtrFrame;
    private RecyclerAdapterWithHF mAdapter;
    private List<PlayBean> mDatas = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mDatas.addAll((List<PlayBean>) msg.obj);
            mAdapter.notifyDataSetChanged();
            mPtrFrame.refreshComplete();
            mPtrFrame.setLoadMoreEnable(true);
            loadMore.setVisibility(View.INVISIBLE);
        }
    };
    private PlayRyAdapter mPlayRyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_fragment, container, false);
        ViewUtils.inject(this, view);

        initView();
        return view;

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_play.setLayoutManager(layoutManager);
        mPlayRyAdapter = new PlayRyAdapter(getActivity(), mDatas);
        mAdapter = new RecyclerAdapterWithHF(mPlayRyAdapter);
        rv_play.setAdapter(mAdapter);

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
        },100);


        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mDatas.clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<PlayBean> movie = new ArrayList<PlayBean>();
                        movie = GetDatas.getPlayVideo(url+1+".html");
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
                pag ++ ;
                loadMore.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(),"加载速度比较慢!不要急",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<PlayBean> movie = new ArrayList<PlayBean>();
                        movie = GetDatas.getPlayVideo(url+pag+".html");
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
            for (int i = 0; i < ja.length(); i++) {
                jb = ja.getJSONObject(i);
                String decodephotoUrl = new String(Base64.decode(jb.getString("photoUrl")));
                String decodeBlockLink = new String(Base64.decode(jb.getString("blockLink")));
                String decodeName = new String(Base64.decode(jb.getString("name")));
                String decodeNum = new String(Base64.decode(jb.getString("num")));
                String decodeTime = new String(Base64.decode(jb.getString("time")));
                movies.add(new MovieBean(decodeBlockLink, decodephotoUrl, decodeName, decodeNum, decodeTime));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

}
