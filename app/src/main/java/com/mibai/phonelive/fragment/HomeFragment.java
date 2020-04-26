package com.mibai.phonelive.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.LoginActivity;
import com.mibai.phonelive.activity.MainActivity;
import com.mibai.phonelive.activity.PopularizeActivity;
import com.mibai.phonelive.bean.PromptBean;
import com.mibai.phonelive.custom.VideoPlayWrap;
import com.mibai.phonelive.dialog.FrequencyDialog;
import com.mibai.phonelive.dialog.LoginDialog;
import com.mibai.phonelive.dialog.PromptDialog;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.network.OkHttp;

import java.io.IOException;

import okhttp3.Request;

/**
 * Created by cxf on 2018/6/5.
 * 首页
 */

public class HomeFragment extends AbsFragment implements View.OnClickListener, VideoPlayWrap.OnCodeListener {

    private static final int RECOMMEND = 0;
    private static final int HOT = 1;
    private static final int NEAR = 2;
    private static final int FOLLOW = 3;
    private static final int GAME = 4;
    private HomeRecommendFragment mRecommendFragment;
    private HomeHotFragment mHotFragment;
    private HomeNearFragment mNearFragment;
    private FollowFragment mFollowFragment;
    private GameFragment gameFragment;
    private SparseArray<Fragment> mSparseArray;
    private int mCurKey;//当前选中的fragment的key
    private FragmentManager mFragmentManager;
    private RadioButton mBtnRecommend;
    private RadioButton mBtnHot;
    private RadioButton mBtnNear;
    private RadioButton mBtnFollow;
    private RadioButton mBtnGame;
    private SparseArray<RadioButton> mButtonSparseArray;
    private VideoPlayWrap.OnCodeListener codeListener;

    private PromptDialog dialog;
    private FrequencyDialog frequencyDialog;
    private LoginDialog loginDialog;

    private int codeIng;
    private String message;
    private String stat;
    private String uid;

    // 是否弹出dialog
    private int isShowSystemDialog = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void main() {
        mCurKey = RECOMMEND;
        mRecommendFragment = new HomeRecommendFragment();
        mHotFragment = new HomeHotFragment();
        mNearFragment = new HomeNearFragment();
        mFollowFragment = new FollowFragment();
        gameFragment = new GameFragment();
        mSparseArray = new SparseArray<>();
        mSparseArray.put(RECOMMEND, mRecommendFragment);
        mSparseArray.put(HOT, mHotFragment);
        mSparseArray.put(NEAR, mNearFragment);
        mSparseArray.put(FOLLOW, mFollowFragment);
        mSparseArray.put(GAME, gameFragment);
        mFragmentManager = getChildFragmentManager();
        VideoPlayWrap.setOnCodeListener(this);
        FragmentTransaction tx = mFragmentManager.beginTransaction();
        for (int i = 0, size = mSparseArray.size(); i < size; i++) {
            Fragment fragment = mSparseArray.valueAt(i);
            tx.add(R.id.replaced, fragment);
            if (mSparseArray.keyAt(i) == mCurKey) {
                tx.show(fragment);
            } else {
                tx.hide(fragment);
            }
        }
        tx.commit();

        mBtnRecommend = (RadioButton) mRootView.findViewById(R.id.btn_recommend);
        mBtnHot = (RadioButton) mRootView.findViewById(R.id.btn_hot);
        mBtnNear = (RadioButton) mRootView.findViewById(R.id.btn_near);
        mBtnFollow = (RadioButton) mRootView.findViewById(R.id.btn_follow);
        mBtnGame = (RadioButton) mRootView.findViewById(R.id.btn_game);

        mButtonSparseArray = new SparseArray<>();
        mButtonSparseArray.put(RECOMMEND, mBtnRecommend);
        mButtonSparseArray.put(HOT, mBtnHot);
        mButtonSparseArray.put(NEAR, mBtnNear);
        mButtonSparseArray.put(FOLLOW, mBtnFollow);
        mButtonSparseArray.put(GAME, mBtnGame);
        for (int i = 0, size = mButtonSparseArray.size(); i < size; i++) {
            RadioButton button = mButtonSparseArray.valueAt(i);
            button.setOnClickListener(this);
            if (mButtonSparseArray.keyAt(i) == mCurKey) {
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            } else {
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                button.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            }
        }

        prompt();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppConfig.getInstance().isLogin()) {
            uid = AppConfig.getInstance().getUserBean().getId();
        } else {
            // 弹窗
            loginDialog = new LoginDialog(mContext, new LoginDialog.OnClickListener() {
                @Override
                public void onClick() {
                    LoginActivity.forwardLogin(mContext);
                }
            });
            Context context = loginDialog.getContext();
            int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = loginDialog.findViewById(divierId);
            if (divider != null) {
                divider.setBackgroundColor(Color.TRANSPARENT);
            }
            // loginDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recommend:
                toggleRecommend();
                break;
            case R.id.btn_hot:
                toggleHot();
                break;
            case R.id.btn_near:
                toggleNear();
                break;
            case R.id.btn_follow:
                toggleFollow();
                break;
            case R.id.btn_game:
                toggleGame();
                break;
        }
    }


    private void toggleRecommend() {
        toggle(RECOMMEND);
        if (mRecommendFragment != null) {
            mRecommendFragment.hiddenChanged(false);
        }
        ((MainActivity) mContext).setCanScroll(true);
    }

    private void toggleHot() {
        toggle(HOT);
        if (mRecommendFragment != null) {
            mRecommendFragment.hiddenChanged(true);
        }
        ((MainActivity) mContext).setCanScroll(false);
    }

    private void toggleNear() {
        toggle(NEAR);
        if (mRecommendFragment != null) {
            mRecommendFragment.hiddenChanged(true);
        }
        ((MainActivity) mContext).setCanScroll(false);
    }

    private void toggleFollow() {
        toggle(FOLLOW);
        if (mRecommendFragment != null) {
            mRecommendFragment.hiddenChanged(true);
        }
        ((MainActivity) mContext).setCanScroll(false);
    }

    private void toggleGame() {
        toggle(GAME);
        ((MainActivity) mContext).setCanScroll(false);
    }


    private void toggle(int key) {
        if (key == mCurKey) {
            return;
        }
        mCurKey = key;
        FragmentTransaction tx = mFragmentManager.beginTransaction();
        for (int i = 0, size = mSparseArray.size(); i < size; i++) {
            Fragment fragment = mSparseArray.valueAt(i);
            if (mSparseArray.keyAt(i) == mCurKey) {
                tx.show(fragment);
            } else {
                tx.hide(fragment);
            }
        }
        tx.commit();
        for (int i = 0, size = mButtonSparseArray.size(); i < size; i++) {
            RadioButton button = mButtonSparseArray.valueAt(i);
            if (mButtonSparseArray.keyAt(i) == mCurKey) {
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            } else {
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                button.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        hiddenChanged(hidden);
    }

    public void hiddenChanged(boolean hidden) {
        if (mCurKey == RECOMMEND && mRecommendFragment != null) {
            mRecommendFragment.hiddenChanged(hidden);
        }
    }

    public boolean isRecommend() {
        return mCurKey == RECOMMEND;
    }


    @Override
    public void onCode(int code, String msg) {
        codeIng = code;
        message = msg;
    }


    // 弹窗数据接口请求
    public void prompt() {
        OkHttp.getAsync(HttpUtil.HTTP_URL + "service=Ad.getPopup", new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.d("dasda", result);
                JsonObject jsonParser = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonParser.getAsJsonObject("data");
                JsonObject jsonElements = object.getAsJsonObject("info");//获取JsonArray对象
                PromptBean bean = new Gson().fromJson(jsonElements.toString(), PromptBean.class);
                stat = bean.getStat();
                if ("1".equals(bean.getStat())) {
                    dialog = new PromptDialog(getContext(), bean.getContent(), new PromptDialog.OnItemClickListener() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                            if (codeIng == 10030) {
                                shareDialog(message);
                                isShowSystemDialog = 1;
                            }
                        }
                    });
                    Context context = dialog.getContext();
                    int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
                    View divider = dialog.findViewById(divierId);
                    if (divider != null) {
                        divider.setBackgroundColor(Color.TRANSPARENT);
                    }
                    dialog.show();
                } else if (!"1".equals(bean.getStat()) && codeIng == 10030) {
                    shareDialog(message);
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void shareDialog(String content) {
        frequencyDialog = new FrequencyDialog(getContext(), content, new FrequencyDialog.OnItemClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(getActivity(), PopularizeActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                frequencyDialog.dismiss();
            }
        });
        Context context = frequencyDialog.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = frequencyDialog.findViewById(divierId);
        if (divider != null) {
            divider.setBackgroundColor(Color.TRANSPARENT);
        }
        frequencyDialog.show();
    }


}
