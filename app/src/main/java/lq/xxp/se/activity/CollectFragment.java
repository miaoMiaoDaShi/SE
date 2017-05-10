package lq.xxp.se.activity;

import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import lq.xxp.se.Bean.ActorBean;
import lq.xxp.se.Bean.DbBean;
import lq.xxp.se.Bean.MovieBean;
import lq.xxp.se.Db.DbInit;
import lq.xxp.se.R;
import lq.xxp.se.adapter.ActorRyAdapter;
import lq.xxp.se.adapter.VideoRyAdapter;

/**
 * Created by 钟大爷 on 2016/10/30.
 */

public class CollectFragment extends Fragment implements View.OnClickListener {

    private DbInit mDbInit;
    private List<DbBean> mDbBeens;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private ArrayList<View> pageview;
    @ViewInject(R.id.actorLayout)
    private TextView actorLayout;
    @ViewInject(R.id.videoLayout)
    private TextView videoLayout;
    // 滚动条图片
    @ViewInject(R.id.scrollbar)
    private ImageView scrollbar;
    // 滚动条初始偏移量
    private int offset = 0;
    // 当前页编号
    private int currIndex = 0;
    // 滚动条宽度
    private int bmpW;
    //一倍滚动量
    private int one;
    private RecyclerView rv_video_collect;

    private RecyclerView rv_actor_collect;
    private List<ActorBean> actors = new ArrayList<ActorBean>();
    private List<MovieBean> movies = new ArrayList<MovieBean>();
    private ActorRyAdapter mActorRyAdapter;
    private VideoRyAdapter mVideoRyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.collect_fragment, container, false);
        ViewUtils.inject(this, view);
        initData();
        initView();

        return view;

    }

    private void initView() {
        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view1 = inflater.inflate(R.layout.actor_collect, null);
        View view2 = inflater.inflate(R.layout.movie_collect, null);
        rv_actor_collect = (RecyclerView) view1.findViewById(R.id.rv_actor_collect);
        rv_video_collect = (RecyclerView) view2.findViewById(R.id.rv_video_collect);

        rv_actor_collect.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_video_collect.setLayoutManager(layoutManager);


        mActorRyAdapter = new ActorRyAdapter(getActivity(), actors);
        mVideoRyAdapter = new VideoRyAdapter(getActivity(), movies);

        rv_actor_collect.setAdapter(mActorRyAdapter);
        rv_video_collect.setAdapter(mVideoRyAdapter);

        actorLayout.setOnClickListener(this);
        videoLayout.setOnClickListener(this);
        pageview = new ArrayList<View>();
        //添加想要切换的界面
        pageview.add(view1);
        pageview.add(view2);

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            //使从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1) {
                ((ViewPager) arg0).addView(pageview.get(arg1));
                return pageview.get(arg1);
            }
        };


        //绑定适配器
        viewPager.setAdapter(pagerAdapter);
        //设置viewPager的初始界面为第一个界面
        viewPager.setCurrentItem(0);
        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        // 获取滚动条的宽度
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.dialog_bg).getWidth();
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //得到屏幕的宽度
        int screenW = displayMetrics.widthPixels;
        //计算出滚动条初始的偏移量
        offset = (screenW / 2 - bmpW) / 2;
        //计算出切换一个界面时，滚动条的位移量
        one = offset * 2 + bmpW;
        Matrix matrix = new Matrix();
        matrix.postTranslate(0, 0);
        //将滚动条的初始位置设置成与左边界间隔一个offset
        scrollbar.setImageMatrix(matrix);
    }

    private final String TBNAME = "tb_collect";

    private void initData() {
        mDbInit = DbInit.DbInitInstance(getActivity());
        mDbBeens = mDbInit.giveData(TBNAME);

        if (mDbBeens != null) {
            for (int i = 0; i < mDbBeens.size(); i++) {
                if (mDbBeens.get(i).getType().equals("actors")) {
                    actors.add(new ActorBean(mDbBeens.get(i).getImgUrl(),
                            mDbBeens.get(i).getLink()
                            ,mDbBeens.get(i).getTitle()));
                } else if (mDbBeens.get(i).getType().equals("videos")) {
                    movies.add(new MovieBean(mDbBeens.get(i).getLink(),
                            mDbBeens.get(i).getImgUrl(),
                            mDbBeens.get(i).getTitle(),
                            mDbBeens.get(i).getNum(),
                            mDbBeens.get(i).getTime()));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actorLayout:
                viewPager.setCurrentItem(0);
                break;
            case R.id.videoLayout:
                viewPager.setCurrentItem(1);
                break;
        }
    }


    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    /**
                     * TranslateAnimation的四个属性分别为
                     * float fromXDelta 动画开始的点离当前View X坐标上的差值
                     * float toXDelta 动画结束的点离当前View X坐标上的差值
                     * float fromYDelta 动画开始的点离当前View Y坐标上的差值
                     * float toYDelta 动画开始的点离当前View Y坐标上的差值
                     **/
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    break;
            }
            //arg0为切换到的页的编码
            currIndex = arg0;
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
            //滚动条开始动画
            scrollbar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mActorRyAdapter.notifyDataSetChanged();
            mVideoRyAdapter.notifyDataSetChanged();
        }
    }
}
