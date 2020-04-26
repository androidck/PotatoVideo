package com.mibai.phonelive.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mibai.phonelive.R;
import com.mibai.phonelive.utils.GifMovieView;
import com.mibai.phonelive.web.BrowserActivity;
import com.mibai.phonelive.web.SonicJavaScriptInterface;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by cxf on 2017/8/3.
 */

public abstract class AbsActivity extends AppCompatActivity {


    protected Context mContext;
    private Unbinder unbinder;

    public static final int MODE_DEFAULT = 0;

    public static final int MODE_SONIC = 1;

    public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(getLayoutId());
        mContext = this;
        unbinder = ButterKnife.bind(this);
        main(savedInstanceState);
    }

    protected abstract int getLayoutId();

    protected void main(Bundle savedInstanceState) {
        main();
    }

    protected void main() {
    }

    protected void setTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.titleView);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    public void backClick(View v) {
        if (v.getId() == R.id.btn_back) {
            onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    /**
     * 不带参数的activity跳转
     *
     * @param cls
     */
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
    }

    /**
     * 设置透明状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }

    private View mLoadingView;

    public void showLoading() {
        View contentView = findViewById(android.R.id.content);
        if (contentView != null && contentView instanceof FrameLayout) {
            if (mLoadingView == null) {
                mLoadingView = View.inflate(this, R.layout.common_loading_view, null);
            }
            mLoadingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            GifMovieView gifMovieView = (GifMovieView) mLoadingView.findViewById(R.id.iv_loading);
            gifMovieView.setOnClickListener(null);
            final TextView textView2 = (TextView) mLoadingView.findViewById(R.id.loading_msg);
            textView2.setText(getResources().getString(R.string.loading));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLoadingView.setLayoutParams(params);
            ViewParent viewParent = mLoadingView.getParent();
            if (viewParent != null) {
                ((FrameLayout) viewParent).removeView(mLoadingView);
            }
            ((FrameLayout) contentView).addView(mLoadingView);
        }
    }

    public void dismissLoading() {
        View contentView = findViewById(android.R.id.content);
        if (contentView != null && contentView instanceof FrameLayout) {
            if (mLoadingView != null) {
                ((FrameLayout) contentView).removeView(mLoadingView);
            }
        }
    }

    //跳转到浏览器
    public void startBrowserActivity(Context context, int mode, String url) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(BrowserActivity.PARAM_URL, url);
        intent.putExtra(BrowserActivity.PARAM_MODE, mode);
        intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
        startActivityForResult(intent, -1);
    }

}
