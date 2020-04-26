package com.mibai.phonelive.community.adapter;

import android.content.Context;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * viewpage 适配器
 */
public class MyViewPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragment;
    private List<String> mListTitle;
    private Context mContext;


    public MyViewPageAdapter(FragmentManager fm, Context context, List<Fragment> mFragment, List<String> mListTitle) {
        super(fm);
        this.mContext = context;
        this.mFragment = mFragment;
        this.mListTitle = mListTitle;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragment.get(i);
    }

    @Override
    public int getCount() {
        return mListTitle == null ? 0 : mListTitle.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mListTitle.get(position);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}
