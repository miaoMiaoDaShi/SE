package lq.xxp.se.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import java.util.List;

import lq.xxp.se.Bean.MovieBean;
import lq.xxp.se.R;
import lq.xxp.se.activity.MovieInfoActivity;


public class VideoRyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MovieBean> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    public VideoRyAdapter(Context context, List<MovieBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {

        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        BitmapUtils bitmapUtils = new BitmapUtils(mContext);
        bitmapUtils.display(holder.iv_img, mDatas.get(position).getImgUrl());
        holder.tv_name.setText(mDatas.get(position).getName());
        holder.tv_time.setText(mDatas.get(position).getTime());
        holder.tv_num.setText(mDatas.get(position).getNum());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieInfoActivity.class);
                Bundle bundle = new Bundle();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bundle.putString("Link", mDatas.get(position).getBlockLink());
                bundle.putString("num",mDatas.get(position).getNum());
                Log.d("mmds","连接"+mDatas.get(position).getBlockLink());
                bundle.putString("name", mDatas.get(position).getName());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.movie_item, parent, false);

        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_num;
        TextView tv_time;
        ImageView iv_img;
        View view;


        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            tv_name = (TextView) view.findViewById(R.id.tv_movie_title);
            tv_time = (TextView) view.findViewById(R.id.tv_movie_time);
            tv_num = (TextView) view.findViewById(R.id.tv_movie_num);
            iv_img = (ImageView) view.findViewById(R.id.iv_movie_img);
        }


    }
}