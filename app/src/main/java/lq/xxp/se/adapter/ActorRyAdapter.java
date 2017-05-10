package lq.xxp.se.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import lq.xxp.se.Bean.ActorBean;
import lq.xxp.se.R;
import lq.xxp.se.activity.MovieActivity;


public class ActorRyAdapter extends RecyclerView.Adapter<ActorRyAdapter.MyViewHolder> {

    private List<ActorBean> mDatas;
    private List<Integer> mHeights;
    private Context mContext;
    private LayoutInflater inflater;
    private String itemColor[] = {"#9067C0E5","#909AD262","#90EA9EA0","#9075C0AC","#90F1C873"};

    public ActorRyAdapter(Context context, List<ActorBean> datas) {
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
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

            // 随机高度, 模拟瀑布效果.
            if (mHeights.size() <= position) {
                mHeights.add((int) (80 + Math.random() *100));
                holder.ll_view.setBackgroundColor(Color.parseColor(itemColor[(int) (Math.random()*4)]));
            }

            ViewGroup.LayoutParams lp = holder.getName().getLayoutParams();
            lp.height = mHeights.get(position);
            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
            bitmapUtils = new BitmapUtils(mContext);
            bitmapUtils.display(holder.iv_img,mDatas.get(position).getImgUrl());
            holder.getName().setLayoutParams(lp);
            holder.tv_name.setText(mDatas.get(position).getName());



    }


    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.home_item, parent, false);



        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name;
        ImageView iv_img;
        View view;
        LinearLayout ll_view;


        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            tv_name = (TextView) view.findViewById(R.id.home_item_name);
            iv_img = (ImageView) view.findViewById(R.id.home_item_img);
            ll_view = (LinearLayout) view.findViewById(R.id.ll_view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MovieActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Link",mDatas.get(getLayoutPosition()).getLink());
                    bundle.putString("name",mDatas.get(getLayoutPosition()).getName());
                    bundle.putString("imgUrl",mDatas.get(getLayoutPosition()).getImgUrl());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }

        public TextView getName() {
            return tv_name;
        }

        public View getView() {
            return view;
        }


        }



    }



