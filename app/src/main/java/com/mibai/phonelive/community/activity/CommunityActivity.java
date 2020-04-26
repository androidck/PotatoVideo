package com.mibai.phonelive.community.activity;

import com.google.android.material.tabs.TabLayout;

import android.content.Intent;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.AbsActivity;
import com.mibai.phonelive.community.adapter.MyViewPageAdapter;
import com.mibai.phonelive.community.fragment.CommunityFragment;
import com.mibai.phonelive.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CommunityActivity extends AbsActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.img_send_post)
    ImageView imgSendPost;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private List<String> title;
    private List<Fragment> fragmentList;

    private MyViewPageAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_community;
    }


    @Override
    protected void main() {
        super.main();
        initView();
        StatusBarUtil.setStatusBarMode(this, true, R.color.white);
    }

    protected void initView() {
        title = new ArrayList<>();
        fragmentList = new ArrayList<>();
        title.add("关注");
        title.add("最热");
        title.add("最新");
        title.add("精选");
        title.add("我的");
       /* if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }*/
        for (int i = 0; i < title.size(); i++) {

            fragmentList.add(CommunityFragment.getInstance(i));
        }
        adapter = new MyViewPageAdapter(getSupportFragmentManager(), this, fragmentList, title);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.img_send_post)
    public void onViewClicked() {
       startActivity(new Intent(mContext,SendPostActivity.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }*/
    }
}
