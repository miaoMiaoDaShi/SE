package lq.xxp.se.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import lq.xxp.se.Bean.DownloadLink;
import lq.xxp.se.R;
import lq.xxp.se.adapter.DownloadLinkAdapter;
import lq.xxp.se.utils.DownloadLinkProvider;
import lq.xxp.se.utils.ViewUtil;
import lq.xxp.se.view.BasicOnScrollListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
/**
 * Created by 钟大爷 on 2016/11/10.
 */

public class DownloadFragment<LM extends RecyclerView.LayoutManager> extends Fragment{

    public List<DownloadLink> links = new ArrayList<>();

    public DownloadLinkProvider provider;

    public String keyword;
    @ViewInject(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private LM mLayoutManager;

    @ViewInject(R.id.refresh_layout)
    public SwipeRefreshLayout mRefreshLayout;

    public SwipeRefreshLayout.OnRefreshListener mRefreshListener;

    public BasicOnScrollListener mScrollListener;

    protected void setRecyclerViewPadding(int dp) {
        this.mRecyclerView.setPadding(
                ViewUtil.dpToPx(dp),
                ViewUtil.dpToPx(dp),
                ViewUtil.dpToPx(dp),
                ViewUtil.dpToPx(dp)
        );
    }


    public DownloadFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        ViewUtils.inject(this, view);
        return view;
    }

    public void setLayoutManager(LM mLayoutManager) {
        this.mRecyclerView.setLayoutManager(this.mLayoutManager = mLayoutManager);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        this.provider = DownloadLinkProvider.getProvider(bundle.getString("provider"));
        this.keyword = bundle.getString("keyword");
    }

    public LM getLayoutManager() {
        return mLayoutManager;
    }

    public void setAdapter(RecyclerView.Adapter mAdapter) {
        this.mRecyclerView.setAdapter(this.mAdapter = mAdapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setRecyclerViewPadding(4);

        mRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this.getContext(), R.color.googleBlue),
                ContextCompat.getColor(this.getContext(), R.color.googleGreen),
                ContextCompat.getColor(this.getContext(), R.color.googleRed),
                ContextCompat.getColor(this.getContext(), R.color.googleYellow)
        );
        this.setLayoutManager((LM) new LinearLayoutManager(this.getContext()));
        this.setAdapter(new ScaleInAnimationAdapter(new DownloadLinkAdapter(links, this.getActivity(), this.provider)));

        RecyclerView.ItemAnimator animator = new SlideInUpAnimator();
        animator.setAddDuration(300);
        mRecyclerView.setItemAnimator(animator);

        this.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollListener.refresh();
            }
        });

        this.addOnScrollListener(new BasicOnScrollListener<DownloadLink>() {
            @Override
            public Call<ResponseBody> newCall(int page) {
                return DownloadFragment.this.newCall(page);
            }

            @Override
            public RecyclerView.LayoutManager getLayoutManager() {
                return DownloadFragment.this.getLayoutManager();
            }

            @Override
            public SwipeRefreshLayout getRefreshLayout() {
                return DownloadFragment.this.mRefreshLayout;
            }

            @Override
            public List<DownloadLink> getItems() {
                return DownloadFragment.this.links;
            }

            @Override
            public void onResult(ResponseBody response) throws Exception {
                super.onResult(response);
                List<DownloadLink> downloads = provider.parseDownloadLinks(response.string());

                int pos = links.size();

                if (pos > 0) {
                    pos--;
                }

                if (downloads.isEmpty()) {
                    setEnd(true);
                } else {
                    links.addAll(downloads);
                    getAdapter().notifyItemChanged(pos, downloads.size());
                }
            }
        });

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mRefreshListener.onRefresh();
            }
        });
    }

    public Call<ResponseBody> newCall(int page) {
        return this.provider.search(this.keyword, page);
    }

    public void addOnScrollListener(BasicOnScrollListener listener) {
        mRecyclerView.addOnScrollListener(mScrollListener = listener);
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mRefreshLayout.setOnRefreshListener(mRefreshListener = listener);
    }
}
