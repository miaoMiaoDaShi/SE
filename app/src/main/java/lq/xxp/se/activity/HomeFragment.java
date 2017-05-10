package lq.xxp.se.activity;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lq.xxp.se.Bean.HomeimgBean;
import lq.xxp.se.DAO.AllLink;
import lq.xxp.se.R;
import lq.xxp.se.adapter.HomeImgAdapter;
import lq.xxp.se.adapter.MyPageAdapter;
import lq.xxp.se.utils.Base64;
import lq.xxp.se.utils.GetDatas;
import lq.xxp.se.view.DepthPageTransformer;

import static lq.xxp.se.R.id.vp;



public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener {



    @ViewInject(R.id.tv_announcement)
    private TextView tv_announcement;
    private boolean loading = false;
    private String imgName[] = {
            "vp_img_1.jpg",
            "vp_img_2.jpg",
            "vp_img_3.jpg",
            "vp_img_4.jpg"};
    private String imgUrl = "http://www.xxpbox.top/app/img/";//vp_img_1.jpg
    private int pag = 1;
    private final int viewPagerCount = 4;
    private List<View> viewList;//图片资源的集合
    private List<Bitmap> bitmaps;
    private ViewGroup vg;//放置圆点
    //实例化原点View
    private ImageView iv_point;
    private ImageView[] ivPointArray;
    @ViewInject(R.id.vp)
    private ViewPager viewPager;
    @ViewInject(R.id.rv_home)
    private RecyclerView ry_home;
    private boolean isLooper;
    private ImageView imageView;
    private View view;
    private boolean isFirst = true;
    //支持下拉刷新的ViewGroup
    @ViewInject(R.id.rotate_header_list_view_frame)
    private PtrClassicFrameLayout mPtrFrame;
    //添加Header和Footer的封装类
    private RecyclerAdapterWithHF mAdapter;
    //图片列表
    private final int loadImg = 12;
    //List数据
    private List<HomeimgBean> mDatas = new ArrayList<>();


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x11) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else if (msg.what==INITGONGGAO){
                try {
                    JSONObject jb = new JSONObject((String) msg.obj);
                    Log.d("mmds","公告"+msg.obj.toString());
                    tv_announcement.setText(new String(Base64.decode(jb.getString("gongGao"))));
                } catch (JSONException e) {
                }
            }else if(msg.what==loadImg){
                if (msg.obj!=null){
                    mDatas.addAll((List<HomeimgBean>) msg.obj);
                    Log.d("mmds","公告"+mDatas.size()+"大小");
                    for (int i=0;i<mDatas.size();i++ ){
                        Log.d("mmds","公告"+mDatas.get(i).getTitle()+"大小");
                    }
                    mAdapter.notifyDataSetChanged();
                    mPtrFrame.refreshComplete();
                    mPtrFrame.setLoadMoreEnable(true);
                } else{
                    Toast.makeText(getActivity(),"服务器抽风了",Toast.LENGTH_SHORT).show();
                }

            }
        }
    };
    private final int INITGONGGAO = 14;
    private HomeImgAdapter mHomeImgAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment,container,false);
        ViewUtils.inject(this, view);
        showViewPager();
        initReclerView();
        return view;

    }

    private void initReclerView() {

        //ry_home.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        ry_home.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        ry_home.setItemAnimator( new DefaultItemAnimator());
        mHomeImgAdapter = new HomeImgAdapter(getActivity(),mDatas);
        mAdapter = new RecyclerAdapterWithHF(mHomeImgAdapter);
        ry_home.setAdapter(mAdapter);

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
                        List<HomeimgBean> imgs = new ArrayList<HomeimgBean>();
                        imgs = GetDatas.getImgJson(pag);
                        Message msg = mHandler.obtainMessage();
                        msg.obj = imgs;
                        msg.what = loadImg;
                        mHandler.sendMessage(msg);

                    }
                }).start();
//模拟联网 延迟更新列表

            }
        });
//上拉加载
        mPtrFrame.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                pag ++ ;
                Toast.makeText(getActivity(),"加载速度比较慢!不要急",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<HomeimgBean> imgs = new ArrayList<HomeimgBean>();
                        imgs = GetDatas.getImgJson(pag);
                        Message msg = mHandler.obtainMessage();
                        msg.obj = imgs;
                        msg.what = loadImg;
                        mHandler.sendMessage(msg);
                    }
                }).start();

                mAdapter.notifyDataSetChanged();
                mPtrFrame.loadMoreComplete(true);
            }
        });

        tv_announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VebActivity.class);
                intent.putExtra("blockLink",AllLink.soft_img_announcement);
                startActivity(intent);
            }
        });






    }

//    private List<ActorBean> loadJson(String json) {
//        List<ActorBean> actors = new ArrayList<ActorBean>();
//        try {
//            JSONObject jb = new JSONObject(json);
//            JSONArray ja = jb.getJSONArray("actors");
//            for(int i=0;i<ja.length();i++){
//                jb = ja.getJSONObject(i);
//                String decodephotoUrl = new String(Base64.decode(jb.getString("photoUrl")));
//                String decodeBlockLink = new String(Base64.decode(jb.getString("blockLink")));
//                String decodeName = new String(Base64.decode(jb.getString("name")));
//                actors.add(new ActorBean(decodephotoUrl,decodeBlockLink,decodeName));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return actors;
//    }
//
//    private String getJson() {
//        String encode = "";
//        try {
//            InputStreamReader isr = new InputStreamReader(getResources().openRawResource(R.raw.actors));
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


    private void showViewPager() {
        new myTask().execute(imgUrl);
        //加载ViewPager
        initViewPager();

        //加载底部圆点
        initPoint();

        //修改添加设置ViewPager的当前页，为了保证左右轮播
        viewPager.setCurrentItem(5000000);
        viewPager.setPageTransformer(true,new DepthPageTransformer());


        //开启一个线程，用于循环
        new Thread(new Runnable() {
            @Override
            public void run() {
                isLooper = true;
                Message msg = mHandler.obtainMessage();
                msg.what = INITGONGGAO;
                msg.obj = GetDatas.GetJson(AllLink.soft_json_main);
                mHandler.sendMessage(msg);
                while (isLooper) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    msg = new Message();
                    msg.what = 0x11;
                    mHandler.sendMessage(msg);
                }

            }
        }).start();
    }


    /**
     * 加载底部圆点
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void initPoint() {
        //这里实例化LinearLayout
        vg = (ViewGroup) view.findViewById(R.id.guide_ll_point);
        //根据ViewPager的item数量实例化数组
        ivPointArray = new ImageView[viewPagerCount];
        //循环新建底部圆点ImageView，将生成的ImageView保存到数组中
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            iv_point = new ImageView(getActivity());
            iv_point.setLayoutParams(new ViewGroup.LayoutParams(50,20));
            iv_point.setPadding(10, 0, 10, 0);//left,top,right,bottom
            ivPointArray[i] = iv_point;
            //第一个页面需要设置为选中状态，这里采用两张不同的图片
            if (i == 0) {
                iv_point.setBackgroundResource(R.drawable.full_holo);
            } else {
                iv_point.setBackgroundResource(R.drawable.empty_holo);
            }
            //将数组中的ImageView加入到ViewGroup
            vg.addView(ivPointArray[i]);
        }

    }

    /**
     * 加载图片ViewPager
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void initViewPager() {
        viewPager = (ViewPager) view.findViewById(vp);

        viewList = new ArrayList<>();


        //循环创建View并加入到集合中

        //new myTask().execute(imgUrl);

        bitmaps = new ArrayList<Bitmap>();
        for(int i=0;i<viewPagerCount;i++){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.noimg);
            bitmaps.add(bitmap);
        }

        for (int i = 0; i < viewPagerCount; i++) {
            //new ImageView并设置全屏和图片资源
            imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageBitmap(bitmaps.get(i));
            //将ImageView加入到集合中
            viewList.add(imageView);
        }

        //View集合初始化好后，设置Adapter
        viewPager.setAdapter(new MyPageAdapter(viewList));



        //设置滑动监听
        viewPager.setOnPageChangeListener(this);

    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 滑动后的监听
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {

        //修改全部的position长度
        int newPosition = position % viewPagerCount;

        //循环设置当前页的标记图
        int length = viewPagerCount;
        for (int i = 0; i < length; i++) {
            ivPointArray[newPosition].setBackgroundResource(R.drawable.full_holo);
            if (newPosition != i) {
                ivPointArray[i].setBackgroundResource(R.drawable.empty_holo);
            }
        }


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    private class myTask extends AsyncTask<String, Void, List<Bitmap>> {

        LruCache<String,Bitmap> mCache;
        public myTask() {
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int cacheSize = maxMemory / 4;
            mCache = new LruCache<String, Bitmap>(cacheSize){
                protected int sizeOf(String key, Bitmap value) {
                    //在每次存入缓存的时候调用
                    return value.getByteCount();
                }
            };
        }

       // 增加到缓存
       public void addBitmapToCache(String url, Bitmap bitmap)
       {
           if (getBitmapFromCache(url) == null)
           {
               mCache.put(url, bitmap);
           }
       }

       //从缓存中获取数据
       public Bitmap getBitmapFromCache(String url)
       {
           return mCache.get(url);
       }


        @Override
        protected List<Bitmap> doInBackground(String... params) {
            List<Bitmap> lists = new ArrayList<Bitmap>();
           for(int i=0;i<viewPagerCount;i++){
               //从缓存中取出对应的图片
               Bitmap bitmap = getBitmapFromCache(imgUrl+imgName[i]);
               //如果缓存中没有，那么必须下载
               if (bitmap == null)
               {
                   try {
                       bitmap = BitmapFactory.decodeStream(new URL(imgUrl+imgName[i]).openStream());
                       addBitmapToCache(imgUrl+imgName[i],bitmap);
                       lists.add(bitmap);
                   } catch (IOException e) {
                   }

               }else
               {
                   lists.add(bitmap);
               }
           }
            return lists;
        }



        @Override
        protected void onPostExecute(List<Bitmap> lists) {
            super.onPostExecute(bitmaps);
            if(!lists.isEmpty()){
                viewList.clear();
                for (int i = 0; i < viewPagerCount; i++) {
                    //new ImageView并设置全屏和图片资源
                    imageView = new ImageView(getActivity());
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setImageBitmap(lists.get(i));
                    //将ImageView加入到集合中
                    viewList.add(imageView);
                }

                //View集合初始化好后，设置Adapter
                viewPager.setAdapter(new MyPageAdapter(viewList));
            } else Toast.makeText(getActivity(),"网络连接情况不佳",Toast.LENGTH_LONG).show();
        }


    }







}
