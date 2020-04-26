package com.mibai.phonelive.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.ChatMessageBean;
import com.mibai.phonelive.bean.ConfigBean;
import com.mibai.phonelive.bean.OffLineMsgEvent;
import com.mibai.phonelive.bean.UserBean;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.community.activity.CommunityActivity;
import com.mibai.phonelive.custom.MyViewPager;
import com.mibai.phonelive.event.ChatRoomCloseEvent;
import com.mibai.phonelive.event.JMessageLoginEvent;
import com.mibai.phonelive.event.LoginUserChangedEvent;
import com.mibai.phonelive.event.LogoutEvent;
import com.mibai.phonelive.event.NeedRefreshEvent;
import com.mibai.phonelive.event.ShowInviteEvent;
import com.mibai.phonelive.fragment.InviteFragment;
import com.mibai.phonelive.fragment.MainFragment;
import com.mibai.phonelive.fragment.UserFragment;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.CommonCallback;
import com.mibai.phonelive.interfaces.GlobalLayoutChangedListener;
import com.mibai.phonelive.interfaces.VideoChangeListener;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.presenter.GlobalLayoutPresenter;
import com.mibai.phonelive.utils.DialogUitl;
import com.mibai.phonelive.utils.HawkUtil;
import com.mibai.phonelive.utils.LocationUtil;
import com.mibai.phonelive.utils.MD5Util;
import com.mibai.phonelive.utils.SharedPreferencesUtil;
import com.mibai.phonelive.utils.ToastUtil;
import com.mibai.phonelive.utils.VideoStorge;
import com.mibai.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * Created by cxf on 2018/6/11.
 */

public class MainActivity extends AudioAbsActivity implements ViewPager.OnPageChangeListener, GlobalLayoutChangedListener, VideoChangeListener {

    private MyViewPager mViewPager;
    private MainFragment mMainFragment;
    private UserFragment mUserFragment;
    private GlobalLayoutPresenter mPresenter;
    private boolean mShowInvite;

    private String mDevice;
    private UserBean u;
    private String mCurVersion;

    @Override
    protected int getLayoutId() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return R.layout.activity_main;
    }

    @Override
    protected void main() {
        super.main();
        mViewPager = (MyViewPager) findViewById(R.id.viewPager);
        mViewPager.addOnPageChangeListener(this);
        mMainFragment = new MainFragment();
        mUserFragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_MAIN_USER_CENTER, false);
        mUserFragment.setArguments(bundle);
        mUserFragment.setOnBackClickListener(new UserFragment.OnBackClickListener() {
            @Override
            public void onBackClick() {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(0, true);
                }
            }
        });
        final List<Fragment> list = new ArrayList<>();
        list.add(mMainFragment);
        list.add(mUserFragment);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });
        mCurVersion = AppConfig.getInstance().getVersion();
        mPresenter = new GlobalLayoutPresenter(this, mViewPager);
        EventBus.getDefault().register(this);
        //HttpUtil.getConfig(null);
        startLocation();
        AppConfig.getInstance().loginJPush();
        notLogin();
      /*  if (!AppConfig.getInstance().isUser()) {

        }else {
            AppConfig.getInstance().loginJPush();
        }*/
        checkUpdate();

    }


    /**
     * 检查更新
     */
    private void checkUpdate() {
        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(final ConfigBean bean) {
                if (mCurVersion.equals(bean.getApk_ver())) {
                    ToastUtil.show(WordUtil.getString(R.string.latest_version));
                } else {
                    DialogUitl.showSimpleDialog(mContext, bean.getApk_des(), new DialogUitl.SimpleDialogCallback() {
                        @Override
                        public void onComfirmClick() {
                            String apkUrl = AppConfig.getInstance().getConfig().getApk_url();
                            if (TextUtils.isEmpty(apkUrl)) {
                                ToastUtil.show(WordUtil.getString(R.string.apk_url_not_exist));
                            } else {
                                try {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    intent.setData(Uri.parse(apkUrl));
                                    startActivity(intent);
                                } catch (Exception e) {
                                    ToastUtil.show(WordUtil.getString(R.string.apk_url_not_exist));
                                }
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        L.e("MainActivity", "onPageScrolled--------position------>" + position);
//        L.e("MainActivity", "onPageScrolled--------positionOffset------>" + positionOffset);
//        L.e("MainActivity", "onPageScrolled--------positionOffsetPixels------>" + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            if (mMainFragment != null) {
                mMainFragment.hiddenChanged(false);
            }
        } else if (position == 1) {
            if (mMainFragment != null) {
                mMainFragment.hiddenChanged(true);
            }
            if (mUserFragment != null) {
                mUserFragment.loadData();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void changeVideo(VideoBean videoBean) {
        if (videoBean != null && mUserFragment != null) {
            mUserFragment.setUserInfo(videoBean.getUserinfo(), videoBean.getIsattent());
        }
    }

    public void setCanScroll(boolean canScroll) {
        if (mViewPager != null) {
            mViewPager.setCanScroll(canScroll);
        }
    }

    @Override
    public void addLayoutListener() {
        if (mPresenter != null) {
            mPresenter.addLayoutListener();
        }
    }

    @Override
    public void removeLayoutListener() {
        if (mPresenter != null) {
            mPresenter.removeLayoutListener();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mViewPager != null) {
            mViewPager.clearOnPageChangeListeners();
        }
        removeLayoutListener();
        if (mPresenter != null) {
            mPresenter.release();
        }
        VideoStorge.getInstance().clear();
        LocationUtil.getInstance().stopLocation();
        super.onDestroy();
    }


    public void showUserInfo() {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(1, true);
        }
    }

    public void mainClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record:
                if (AppConfig.getInstance().isLogin()) {
                    checkVideoPermission();
                } else {
                    LoginActivity.forwardLogin(mContext);
                }
                break;
            case R.id.btn_search:
                forwardSearch();

                break;
            case R.id.tv_community:
                startActivity( new Intent(mContext, CommunityActivity.class));
                break;
        }
    }

    private void forwardSearch() {
        startActivity(new Intent(mContext, SearchActivity2.class));
    }

    /**
     * 开启定位
     */
    private void startLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);
            } else {
                LocationUtil.getInstance().startLocation();
            }
        } else {
            LocationUtil.getInstance().startLocation();
        }
    }


    /**
     * 检查并申请录制视频的权限
     */
    public void checkVideoPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        Constants.REQUEST_VIDEO_PERMISSION);
            } else {
                startVideoRecord();
            }
        } else {
            startVideoRecord();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (isAllGranted(permissions, grantResults)) {
            switch (requestCode) {
                case Constants.REQUEST_LOCATION_PERMISSION:
                    LocationUtil.getInstance().startLocation();
                    break;
                case Constants.REQUEST_VIDEO_PERMISSION:
                    startVideoRecord();
                    break;
            }
        }


    }

    //判断申请的权限有没有被允许
    private boolean isAllGranted(String[] permissions, int[] grantResults) {
        boolean isAllGranted = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                showTip(permissions[i]);
                break;
            }
        }
        return isAllGranted;
    }

    //拒绝某项权限时候的提示
    private void showTip(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                ToastUtil.show(getString(R.string.storage_permission_refused));
                break;
            case Manifest.permission.CAMERA:
                ToastUtil.show(getString(R.string.camera_permission_refused));
                break;
            case Manifest.permission.RECORD_AUDIO:
                ToastUtil.show(getString(R.string.record_audio_permission_refused));
                break;
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                ToastUtil.show(getString(R.string.location_permission_refused));
                break;
        }
    }

    /**
     * 开启短视频录制
     */
    private void startVideoRecord() {
        startActivity(new Intent(mContext, VideoMusicActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJMessageLoginEvent(JMessageLoginEvent e) {
        refreshUnReadCount();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEvent(LogoutEvent e) {
        if (mMainFragment != null) {
            mMainFragment.onLogout();
        }
        refreshUnReadCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatRoomCloseEvent(ChatRoomCloseEvent e) {
        refreshUnReadCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessageBean(ChatMessageBean e) {
        refreshUnReadCount();
    }

    /**
     * 接收到了离线消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOffLineMsgEvent(OffLineMsgEvent e) {
        refreshUnReadCount();
    }

    public void refreshUnReadCount() {
        if (mMainFragment != null) {
            mMainFragment.refreshUnReadCount();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowInviteEvent(ShowInviteEvent e) {
        mShowInvite = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mShowInvite) {
            mShowInvite = false;
            InviteFragment fragment = new InviteFragment();
            fragment.show(getSupportFragmentManager(), "InviteFragment");
        }

    }

    @Override
    public void onBackPressed() {
        if (mViewPager != null && mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0, true);
            return;
        }
        super.onBackPressed();
    }

    public void notLogin() {
        String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        mDevice = MD5Util.getMD5(ANDROID_ID + Build.SERIAL);
        OkHttp.getAsync(HttpUtil.HTTP_URL + "service=Login.touristLogin&device=" + mDevice, new OkHttp.DataCallBack() {
            @Override
            // tourist
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject jsonObject1 = jsonObject.getAsJsonObject("data");
                JsonArray info = jsonObject1.getAsJsonArray("info");
                for (JsonElement json : info) {
                    u = new Gson().fromJson(json, UserBean.class);
                }
                boolean userChanged = !TextUtils.isEmpty(u.getId()) && !u.getId().equals(AppConfig.getInstance().getUid());
                HawkUtil.getInstance().saveData(HawkUtil.LOGIN_TYPE, u.getLogin_type());
                AppConfig.getInstance().setLogin_type(u.getLogin_type());
                AppConfig.getInstance().login(u.getId(), u.getToken());
                AppConfig.getInstance().setIsVip(u.getIsvip());
                AppConfig.getInstance().loginJPush();
                SharedPreferencesUtil.getInstance().saveUserBeanJson(new Gson().toJson(u));
                AppConfig.getInstance().setUserBean(u);
                if (userChanged) {
                    EventBus.getDefault().post(new LoginUserChangedEvent(u.getId()));
                }
                EventBus.getDefault().post(new NeedRefreshEvent());
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

}
