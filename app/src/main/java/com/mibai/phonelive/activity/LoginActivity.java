package com.mibai.phonelive.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.adapter.VideoShareAdapter;
import com.mibai.phonelive.bean.ConfigBean;
import com.mibai.phonelive.bean.ShareBean;
import com.mibai.phonelive.bean.UserBean;
import com.mibai.phonelive.event.LoginUserChangedEvent;
import com.mibai.phonelive.event.NeedRefreshEvent;
import com.mibai.phonelive.event.ShowInviteEvent;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.CommonCallback;
import com.mibai.phonelive.interfaces.OnItemClickListener;
import com.mibai.phonelive.utils.DialogUitl;
import com.mibai.phonelive.utils.HawkUtil;
import com.mibai.phonelive.utils.MD5Util;
import com.mibai.phonelive.utils.SharedPreferencesUtil;
import com.mibai.phonelive.utils.SharedSdkUitl;
import com.mibai.phonelive.utils.ToastUtil;
import com.mibai.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by cxf on 2018/6/8.
 */

public class LoginActivity extends AbsActivity implements OnItemClickListener<ShareBean> {

    private EditText mEditPhone;
    private EditText mEditCode;
    private RecyclerView mRecyclerView;
    private String mThirdLoginType;//三方登录的方式
    private SharedSdkUitl mSharedSdkUitl;
    private Handler mHandler;
    private TextView mBtnGetCode;
    private static final int TOTAL = 60;
    private int mCount = TOTAL;
    private String mDevice;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void main() {
        String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        mDevice = MD5Util.getMD5(ANDROID_ID + Build.SERIAL);
        mSharedSdkUitl = new SharedSdkUitl();
        mEditPhone = (EditText) findViewById(R.id.edit_phone);
        mEditCode = (EditText) findViewById(R.id.edit_code);
        mBtnGetCode = (TextView) findViewById(R.id.btn_get_code);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                List<ShareBean> list = mSharedSdkUitl.getShareList(configBean.getLogin_type());
                if (list != null && list.size() > 0) {
                    VideoShareAdapter adapter = new VideoShareAdapter(mContext, list, false, false);
                    adapter.setOnItemClickListener(LoginActivity.this);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    findViewById(R.id.other_login_group).setVisibility(View.INVISIBLE);
                }
            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mCount--;
                if (mCount > 0) {
                    mBtnGetCode.setText(mCount + "s");
                    mBtnGetCode.setTextColor(0xff646464);
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                } else {
                    mBtnGetCode.setTextColor(0xffb4b4b4);
                    mBtnGetCode.setText(WordUtil.getString(R.string.get_valid_code_2));
                    mCount = TOTAL;
                    if (mBtnGetCode != null) {
                        mBtnGetCode.setEnabled(true);
                    }
                }
            }
        };
    }

    @Override
    public void onItemClick(ShareBean bean, int position) {
        mThirdLoginType = bean.getType();
        thirdLogin();
    }

    public void loginClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_get_code:
                getLoginCode();
                break;
            case R.id.login_tip:
                loginTip();
                break;
        }
    }

    private void login() {
        String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        String mDeviceCode = MD5Util.getMD5(ANDROID_ID + Build.SERIAL);
        String mobile = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            mEditPhone.setError(WordUtil.getString(R.string.please_input_mobile));
            mEditPhone.requestFocus();
            return;
        }
        String code = mEditCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            mEditCode.setError(WordUtil.getString(R.string.please_input_code));
            mEditCode.requestFocus();
            return;
        }
        String s = MD5Util.getMD5(code) + Constants.SIGN_1 + mobile + Constants.SIGN_2 + AppConfig.getInstance().getConfig().getDecryptSign() + Constants.SIGN_3;
        s = MD5Util.getMD5(s);
        HttpUtil.login(mobile, code, s, mDeviceCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    private void getLoginCode() {
        String mobile = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            mEditPhone.setError(WordUtil.getString(R.string.please_input_mobile));
            mEditPhone.requestFocus();
            return;
        }
//        if (!ValidateUitl.validateMobileNumber(mobile)) {
//            mEditPhone.setError(getString(R.string.phone_num_error));
//            mEditPhone.requestFocus();
//            return;
//        }
        mEditCode.requestFocus();
        String s = MD5Util.getMD5(mobile) + Constants.SIGN_1 + mobile + Constants.SIGN_2 + AppConfig.getInstance().getConfig().getDecryptSign() + Constants.SIGN_3;
        s = MD5Util.getMD5(s);
        HttpUtil.getLoginCode(mobile, s, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
                mBtnGetCode.setEnabled(false);
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        });
    }


    /**
     * 隐私条款
     */
    private void loginTip() {
        String url = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=28";
        Intent intent = new Intent(mContext, WebActivity.class);
        intent.putExtra(Constants.URL, url);
        startActivity(intent);
    }

    /**
     * 三方登录
     */
    private void thirdLogin() {
        if (mSharedSdkUitl == null) {
            return;
        }
        final Dialog dialog = DialogUitl.loginAuthDialog(mContext);
        dialog.show();
        mSharedSdkUitl.login(mThirdLoginType, new SharedSdkUitl.ShareListener() {
            @Override
            public void onSuccess(Platform platform) {
                ToastUtil.show(getString(R.string.login_auth_success));
                onThirdAuthSuccess(platform);
            }

            @Override
            public void onError(Platform platform) {
                ToastUtil.show(getString(R.string.login_auth_failure));
            }

            @Override
            public void onCancel(Platform platform) {
                ToastUtil.show(getString(R.string.login_auth_cancle));
            }

            @Override
            public void onShareFinish() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 三方登录授权成功调用
     *
     * @param platform
     */
    private void onThirdAuthSuccess(Platform platform) {
        if (platform == null) {
            return;
        }
        PlatformDb platDB = platform.getDb();
        final String nickname = platDB.getUserName();
        final String icon = platDB.getUserIcon();
        String platformName = platDB.getPlatformNname();
        if (platformName.equals(Wechat.NAME)) {
            loginByThird(platDB.get("unionid"), nickname, icon);
        } else if (platDB.getPlatformNname().equals(QQ.NAME)) {
            loginByThird(platDB.getUserId(), nickname, icon);
        }

    }

    private void loginByThird(String openid, String nickname, String icon) {
        String s = MD5Util.getMD5(openid) + Constants.SIGN_1 + mThirdLoginType + Constants.SIGN_2 + AppConfig.getInstance().getConfig().getDecryptSign() + Constants.SIGN_3;
        s = MD5Util.getMD5(s);
        HttpUtil.loginByThird(openid, nickname, mThirdLoginType, icon, s, mDevice, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }


    //登录成功！
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            SharedPreferencesUtil.getInstance().saveUserBeanJson(info[0]);
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            boolean userChanged = !TextUtils.isEmpty(uid) && !uid.equals(AppConfig.getInstance().getUid());
            AppConfig.getInstance().login(uid, token);
            UserBean u = JSON.toJavaObject(obj, UserBean.class);
            AppConfig.getInstance().setLogin_type(obj.getString("login_type"));
            HawkUtil.getInstance().saveData(HawkUtil.LOGIN_TYPE, obj.getString("login_type"));
            AppConfig.getInstance().setUserBean(u);
            AppConfig.getInstance().loginJPush();
            AppConfig.getInstance().setIsVip(u.getIsvip());
            int isreg = obj.getIntValue("isreg");
            if (isreg == 1) {
                EventBus.getDefault().post(new ShowInviteEvent());
            }
            if (userChanged) {
                EventBus.getDefault().post(new LoginUserChangedEvent(uid));
            }
            EventBus.getDefault().post(new NeedRefreshEvent());
            finish();
        } else {
            ToastUtil.show(msg);
        }
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mSharedSdkUitl != null) {
            mSharedSdkUitl.cancelListener();
        }
        HttpUtil.cancel(HttpUtil.LOGIN);
        HttpUtil.cancel(HttpUtil.GET_LOGIN_CODE);
        HttpUtil.cancel(HttpUtil.GET_CONFIG);
        HttpUtil.cancel(HttpUtil.LOGIN_BY_THIRD);
        super.onDestroy();
    }

    public static void forwardLogin(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

}
