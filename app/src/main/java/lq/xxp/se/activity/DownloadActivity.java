package lq.xxp.se.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import lq.xxp.se.R;
import lq.xxp.se.adapter.ViewPagerAdapter;


public class DownloadActivity extends AppCompatActivity {

    @ViewInject(R.id.download_toolbar)
    public Toolbar mToolbar;

    @ViewInject(R.id.download_tabs)
    public TabLayout mTabLayout;

    @ViewInject(R.id.download_view_pager)
    public ViewPager mViewPager;

    public String keyword;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ViewUtils.inject(this);

        Bundle bundle = this.getIntent().getExtras();
        this.keyword = this.getIntent().getExtras().getString("keyword");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(this.keyword);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment fragment;

        fragment = new DownloadFragment();
        bundle = (Bundle) bundle.clone();
        bundle.putString("provider", "btso");
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "通道一");

        fragment = new DownloadFragment();
        bundle = (Bundle) bundle.clone();
        bundle.putString("provider", "torrentkitty");
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "通道二");

        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
