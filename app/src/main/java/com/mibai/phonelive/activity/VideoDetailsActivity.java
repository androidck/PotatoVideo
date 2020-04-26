package com.mibai.phonelive.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mibai.phonelive.R;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.VideoView;
import org.salient.artplayer.ui.ControlPanel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// 视频详情
public class VideoDetailsActivity extends AbsActivity {
    @BindView(R.id.titleView)
    TextView titleView;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.video_view)
    VideoView videoView;

    private String title;
    private String videoUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void main() {
        super.main();
        initView();
        initData();
    }

    private void initView() {
        title = this.getIntent().getStringExtra("title");
        videoUrl = this.getIntent().getStringExtra("video_url");
        titleView.setText(title);
        ControlPanel controlPanel = new ControlPanel(this);
        videoView.setControlPanel(controlPanel);
        videoView.setUp(videoUrl);
        videoView.start();
    }

    public void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (MediaPlayerManager.instance().backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.instance().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerManager.instance().releasePlayerAndView(this);
    }




    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        if (videoView != null) {
            finish();
        }
    }
}
