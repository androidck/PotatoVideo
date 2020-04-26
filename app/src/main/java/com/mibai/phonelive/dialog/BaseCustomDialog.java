package com.mibai.phonelive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.mibai.phonelive.web.BrowserActivity;
import com.mibai.phonelive.web.SonicJavaScriptInterface;


// 自定义dialog
public abstract class BaseCustomDialog extends Dialog {

    public BaseCustomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉白色背景
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(getLayoutId());//这行一定要写在前面
        setCancelable(isCancelable());//点击外部不可dismiss
        Window window = this.getWindow();
        window.setGravity(showGravity());
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        initView();
        initData();
    }

    // 绑定布局
    public abstract int getLayoutId();

    // 绑定视图
    public abstract void initView();

    // 初始化数据
    public abstract void initData();

    // 是否可以点击
    public abstract boolean isCancelable();

    // 显示位置
    public abstract int showGravity();

    //跳转到浏览器
    public void startBrowserActivity(Context context, String title, int mode, String url) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(BrowserActivity.PARAM_URL, url);
        intent.putExtra(BrowserActivity.PARAM_TITLE, title);
        intent.putExtra(BrowserActivity.PARAM_MODE, mode);
        intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
        context.startActivity(intent);
    }
}
