package com.mibai.phonelive.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.ShareUrlBean;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.utils.QRCodeUtils;
import com.mibai.phonelive.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

// 推广
public class PopularizeActivity extends AbsActivity {
    @BindView(R.id.titleView)
    TextView titleView;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_copy_line)
    Button btnCopyLine;
    @BindView(R.id.tv_qr_code)
    ImageView tvQrCode;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_invitation_record)
    TextView tvInvitationRecord;

    private String uid;
    private String shareUrl;
    private Bitmap bitmap;

    private ClipboardManager cm;
    private ClipData mClipData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_popularize;
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
    }

    public void initView() {
        uid = getIntent().getStringExtra("uid");
        getLink();
    }

    @OnClick({R.id.btn_back, R.id.btn_save, R.id.btn_copy_line, R.id.tv_invitation_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                PopularizeActivity.this.finish();
                break;
            case R.id.btn_save:
                saveQrCode();
                break;
            case R.id.btn_copy_line:
                //获取剪贴板管理器：
                cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                mClipData = ClipData.newPlainText("Label", shareUrl);
                cm.setPrimaryClip(mClipData);
                ToastUtil.show("链接已复制");
                break;
            case R.id.tv_invitation_record: // 邀请记录
                startActivity(new Intent(this, InvitationRecordActivity.class));
                break;
        }
    }


    // 生成链接
    public void getLink() {
        OkHttp.getAsync(HttpUtil.HTTP_URL + "service=Ad.getShareurl&uid=" + uid, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonParser = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonParser.getAsJsonObject("data");
                String json = object.get("shareurl").toString();
                ShareUrlBean shareUrlBean = new Gson().fromJson(json, ShareUrlBean.class);
                shareUrl = shareUrlBean.getUrl();
                bitmap = QRCodeUtils.createQRCodeBitmap(shareUrl, 400, 400, "UTF-8", "H", "1", Color.BLACK, Color.WHITE);
                tvQrCode.setImageBitmap(bitmap);
                tvCode.setText(shareUrlBean.getCode());
                tvAccount.setText(shareUrlBean.getDesc());
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 保存照片
    public void saveQrCode() {
        XXPermissions.with(PopularizeActivity.this)
                .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                .permission(Permission.Group.STORAGE) //不指定权限则自动获取清单中的危险权限
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            String fileName = "qr_" + System.currentTimeMillis() + ".jpg";
                            boolean isSaveSuccess = QRCodeUtils.saveImageToGallery(PopularizeActivity.this, bitmap, fileName);
                            if (isSaveSuccess) {
                                ToastUtil.show("图片已保存至本地");
                            } else {
                                ToastUtil.show("保存图片失败，请稍后重试");
                            }
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.gotoPermissionSettings(PopularizeActivity.this);
                        } else {
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.gotoPermissionSettings(PopularizeActivity.this);
                        }
                    }
                });
    }
}
