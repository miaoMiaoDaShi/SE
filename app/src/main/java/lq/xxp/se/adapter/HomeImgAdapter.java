package lq.xxp.se.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import lq.xxp.se.Bean.HomeimgBean;
import lq.xxp.se.R;
import lq.xxp.se.activity.VebActivity;



public class HomeImgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HomeimgBean> mDatas;
    private Context mContext;
    private LayoutInflater inflater;
    private int mPosition;
    private List<Integer> mHeights;

    public HomeImgAdapter(Context context, List<HomeimgBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
        inflater = LayoutInflater.from(mContext);
        mHeights = new ArrayList<>();
    }

    @Override
    public int getItemCount() {

        return mDatas.size();
    }

    //填充onCreateViewHolder方法返回的holder中的控件
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        // 随机高度, 模拟瀑布效果.
        if (mHeights.size() <= position) {
            mHeights.add((int) (300 + Math.random() *200));
        }

        ViewGroup.LayoutParams lp = holder.iv_img.getLayoutParams();
        lp.height = mHeights.get(position);

        holder.tv_title.setText(mDatas.get(position).getTitle());
        holder.tv_time.setText(mDatas.get(position).getCtime());
        holder.tv_description.setText(mDatas.get(position).getDescription());

        BitmapUtils bit = new BitmapUtils(mContext);
        bit.display(holder.iv_img,mDatas.get(position).getPicUrl());
        holder.iv_img.setLayoutParams(lp);
//        holder.iv_img.setBackground(BlurImageview.BlurImages(BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.cs),mContext));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VebActivity.class);
                intent.putExtra("blockLink",mDatas.get(position).getUrl());
                mContext.startActivity(intent);
            }
        });


    }


    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.homeimg_item, parent, false);



        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        ImageView iv_img;
        TextView tv_time;
        TextView tv_description;
        View view;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            tv_title = (TextView) view.findViewById(R.id.homeimg_item_title);
            tv_description = (TextView) view.findViewById(R.id.homeimg_item_description);
            tv_time = (TextView) view.findViewById(R.id.homeimg_item_time);
            iv_img = (ImageView) view.findViewById(R.id.homeimg_item_img);


        }

        public TextView getName() {
            return tv_title;
        }

        public View getView() {
            return view;
        }


        }



    }



