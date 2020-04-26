package com.mibai.phonelive.activity;

import android.content.Intent;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.adapter.VideoChooseAdapter;
import com.mibai.phonelive.bean.VideoChooseBean;
import com.mibai.phonelive.custom.ItemDecoration;
import com.mibai.phonelive.interfaces.CommonCallback;
import com.mibai.phonelive.interfaces.OnItemClickListener;
import com.mibai.phonelive.utils.VideoUtil;
import com.mibai.phonelive.utils.WordUtil;

import java.util.List;

/**
 * Created by cxf on 2018/6/20.
 */

public class VideoChooseActivity extends AbsActivity implements OnItemClickListener<VideoChooseBean> {

    private RecyclerView mRecyclerView;
    private VideoUtil mVideoUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_choose;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.local_video));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 1, 1);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mVideoUtil = new VideoUtil();
        mVideoUtil.getLocalVideoList(new CommonCallback<List<VideoChooseBean>>() {
            @Override
            public void callback(List<VideoChooseBean> videoList) {
                VideoChooseAdapter adapter = new VideoChooseAdapter(mContext, videoList);
                adapter.setOnItemClickListener(VideoChooseActivity.this);
                mRecyclerView.setAdapter(adapter);
            }
        });
    }


    @Override
    public void onItemClick(VideoChooseBean bean, int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.VIDEO_PATH, bean.getVideoPath());
        intent.putExtra(Constants.VIDEO_DURATION, bean.getDuration());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        mVideoUtil.release();
        super.onDestroy();
    }

}
