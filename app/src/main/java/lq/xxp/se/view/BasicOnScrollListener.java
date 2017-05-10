package lq.xxp.se.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Project: ＳＥzhong
 */

public abstract class BasicOnScrollListener<I> extends RecyclerView.OnScrollListener {

    //是否加载中.
    private boolean loading = false;

    private int loadThreshold = 5;
    //当前加载的是第几页
    private int currentPage = 0;

    //标记
    private long token;
    //数据到是否底了...
    private boolean end = false;


    //重置状态
    public void reset() {
        loading = false;
        loadThreshold = 5;
        currentPage = 0;
        getItems().clear();
    }

    //获取布局管理
    public abstract RecyclerView.LayoutManager getLayoutManager();

    //获取谷歌的刷新控件
    public abstract SwipeRefreshLayout getRefreshLayout();

    //
    public abstract List<I> getItems();

    public abstract Call<ResponseBody> newCall(int page);

    //执行刷新
    public void refresh() {
        setLoading(true);
        reset();
        getItems().clear();
        onLoad(token = System.currentTimeMillis());
    }

    private void onLoad(final long token) {
        final int page = currentPage;
        Call<ResponseBody> call = newCall(page + 1);

        if (call == null) {
            setLoading(false);
            getRefreshLayout().setRefreshing(false);
            return;
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (token == BasicOnScrollListener.this.token && page == currentPage) {
                    try {
                        onResult(response.body());
                        currentPage++;
                    } catch (Throwable e) {
                        onFailure(call, e);
                    }
                }

                setLoading(false);
                getRefreshLayout().setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                setLoading(false);
                getRefreshLayout().setRefreshing(false);
                onExceptionCaught(t);
            }
        });
    }

    public void onExceptionCaught(Throwable t) {

    }

    //请求的到的结果
    public void onResult(ResponseBody response) throws Exception {

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        //当前状态并不是正在加载,且RecylerView满足加载的条件
        if (!isLoading() && canLoadMore(recyclerView)) {
            onLoad(token = System.currentTimeMillis());
            loading = true;
        }
    }

    //判断是否达到加载的条件
    /*
    假设一共有 10个Item,我们能看见的9个,当前第一个可见的item的position为4
    visibleItemCount = 10
    totalItemCount = 9
    firstVisibleItem = 4
     */
    public boolean canLoadMore(RecyclerView recyclerView) {
        RecyclerView.LayoutManager mLayoutManager = getLayoutManager();
        //得到可见的item数量
        int visibleItemCount = recyclerView.getChildCount();
        //得到全部的item的数量
        int totalItemCount = mLayoutManager.getItemCount();
        //第一个可见的item
        int firstVisibleItem = 0;
        //如果是瀑布流布局/格子布局/条目布局
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            firstVisibleItem = ((StaggeredGridLayoutManager) mLayoutManager).findFirstVisibleItemPositions(null)[0];
        } else if (mLayoutManager instanceof GridLayoutManager) {
            firstVisibleItem = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }

        //
        return (totalItemCount - visibleItemCount) <= (firstVisibleItem + this.loadThreshold);
    }


    //判断数据是否到底了
    public boolean isEnd() {
        return end;
    }

    //判断是否正在加载
    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

}
