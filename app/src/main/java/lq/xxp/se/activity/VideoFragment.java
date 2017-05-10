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

import java.util.ArrayList;
import java.util.List;

import lq.xxp.se.Bean.MovieBean;
import lq.xxp.se.R;
import lq.xxp.se.adapter.VideoRyAdapter;
import lq.xxp.se.utils.GetDatas;


public class VideoFragment extends Fragment {

    @ViewInject(R.id.rv_video)
    private RecyclerView rv_video;
    private String url = "https://avso.pw/cn/page/";
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
    private int tiem = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment, container, false);
        ViewUtils.inject(this, view);
        initView();
        return view;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_video.setLayoutManager(layoutManager);
        mVideoRyAdapter = new VideoRyAdapter(getActivity(), mDatas);
        mAdapter = new RecyclerAdapterWithHF(mVideoRyAdapter);
        rv_video.setAdapter(mAdapter);

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
                        List<MovieBean> movie = GetDatas.getInfo(url + 1);
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

                tiem++;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<MovieBean> movie = GetDatas.getInfo(url + tiem);
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

}
