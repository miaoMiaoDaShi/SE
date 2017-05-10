package lq.xxp.se.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import lq.xxp.se.R;

/**
 * Created by 钟大爷 on 2016/10/30.
 */

public class AboutFragment extends Fragment {
    @ViewInject(R.id.wv_warn)
    private WebView wv_warn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment,container,false);
        ViewUtils.inject(this, view);
        initView();
        return view;

    }

    private void initView() {
        wv_warn.loadUrl("file:///android_asset/warn.html");
    }

}
