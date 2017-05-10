package lq.xxp.se.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 钟大爷 on 2016/10/29.
 */

public class MyPageAdapter extends PagerAdapter {
    private List<View> viewList;

    public MyPageAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    /**
     * @return 返回页面的个数
     */
    @Override
    public int getCount() {
        if (viewList != null){
//            return viewList.size();
            //第一处修改，设置轮播最大值，等于无限循环
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    /**
     * 判断对象是否生成界面
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 初始化position位置的界面
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        //第二处修改，当前要显示的数据索引为集合长度
        int newPosition = position % viewList.size();
        container.addView(viewList.get(newPosition));
        return viewList.get(newPosition);

//        container.addView(viewList.get(position));
//        return viewList.get(position);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        //第三处修改，移除的索引为集合的长度
        int newPosition = position % viewList.size();
        container.removeView(viewList.get(newPosition));

//        container.removeView(viewList.get(position));
    }
}
