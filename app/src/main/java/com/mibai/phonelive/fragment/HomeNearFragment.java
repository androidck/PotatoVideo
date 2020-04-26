package com.mibai.phonelive.fragment;


import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.VideoPlayActivity;
import com.mibai.phonelive.adapter.NearVideoAdapter;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.custom.ItemDecoration;
import com.mibai.phonelive.custom.RefreshAdapter;
import com.mibai.phonelive.custom.RefreshView;
import com.mibai.phonelive.gaode.LocationUtils;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.OnItemClickListener;
import com.mibai.phonelive.utils.VideoStorge;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/6/9.
 */

public class HomeNearFragment extends AbsFragment implements OnItemClickListener<VideoBean>, LocationUtils.OnLocationListener {

    private RefreshView mRefreshView;
    private NearVideoAdapter mNearAdapter;
    private boolean mFirst = true;

    private String latStr, lngStr;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_hot;
    }

    @Override
    protected void main() {
        LocationUtils.getInstance(mContext).init();
        LocationUtils.getInstance(mContext).startLocation();
        LocationUtils.setListener(this);
        mRefreshView = (RefreshView) mRootView.findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_default);
        mRefreshView.setDataHelper(new RefreshView.DataHelper<VideoBean>() {

            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                if (mNearAdapter == null) {
                    mNearAdapter = new NearVideoAdapter(mContext);
                    mNearAdapter.setOnItemClickListener(HomeNearFragment.this);
                }
                return mNearAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getNearbyVideos(lngStr,latStr,p, callback);
            }

            @Override
            public List<VideoBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), VideoBean.class);
            }

            @Override
            public void onRefresh(List<VideoBean> list) {
                VideoStorge.getInstance().put(Constants.VIDEO_NEAR, list);
            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

            }

        });
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 2, 2);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
    }

    @Override
    public void onItemClick(VideoBean bean, int position) {
        if (mRefreshView != null && bean != null && bean.getUserinfo() != null) {
            VideoPlayActivity.forwardVideoPlay(mContext, Constants.VIDEO_NEAR, position, mRefreshView.getPage(), bean.getUserinfo(), bean.getIsattent());
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mFirst) {
                mFirst = false;
                mRefreshView.initData();
            }
        }
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_VIDEO_LIST);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mRefreshView != null) {
            mRefreshView.setDataHelper(null);
        }
        LocationUtils.getInstance(mContext).destroyLocation();
        super.onDestroy();
    }

    @Override
    public void onLocation(AMapLocation location) {
        latStr = String.valueOf(location.getLatitude());
        lngStr = String.valueOf(location.getLongitude());
    }
}
