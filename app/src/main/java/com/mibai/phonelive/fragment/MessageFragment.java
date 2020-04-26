package com.mibai.phonelive.fragment;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.AdminMsgActivity;
import com.mibai.phonelive.activity.AtMsgActivity;
import com.mibai.phonelive.activity.ChatActivity;
import com.mibai.phonelive.activity.CommentMsgActivity;
import com.mibai.phonelive.activity.ContactsActivity;
import com.mibai.phonelive.activity.FansMsgActivity;
import com.mibai.phonelive.activity.MainActivity;
import com.mibai.phonelive.activity.SystemMsgActivity;
import com.mibai.phonelive.activity.ZanMsgActivity;
import com.mibai.phonelive.adapter.MessageAdapter;
import com.mibai.phonelive.adapter.TypeHeadAdapter;
import com.mibai.phonelive.bean.ChatMessageBean;
import com.mibai.phonelive.bean.ChatUserBean;
import com.mibai.phonelive.bean.OffLineMsgEvent;
import com.mibai.phonelive.bean.TypeHeadBean;
import com.mibai.phonelive.event.ChatRoomCloseEvent;
import com.mibai.phonelive.event.ChatRoomOpenEvent;
import com.mibai.phonelive.event.JMessageLoginEvent;
import com.mibai.phonelive.event.LoginUserChangedEvent;
import com.mibai.phonelive.event.RoamMsgEvent;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.OnItemClickListener;
import com.mibai.phonelive.jpush.JMessageUtil;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.model.Message;
import okhttp3.Request;

import static com.mibai.phonelive.http.HttpUtil.HTTP_URL;

/**
 * Created by cxf on 2018/6/5.
 */

public class MessageFragment extends AbsFragment implements OnItemClickListener<ChatUserBean>, View.OnClickListener {

    private RecyclerView mRecyclerView,headRecyclerView;
    private MessageAdapter mAdapter;
    private Map<String, ChatUserBean> mMap;
    private String mNotUpdateUid;
    private ChatMessageBean mNewMessageBean;
    private List<ChatUserBean> mAdminList;

    private TypeHeadAdapter headAdapter;

    private List<TypeHeadBean> headBeanList = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void main() {
        mRootView.findViewById(R.id.btn_add_user).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_fans).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_zan).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_at).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_comment).setOnClickListener(this);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        headRecyclerView= (RecyclerView) mRootView.findViewById(R.id.head_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MessageAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mMap = new HashMap<>();
        mAdminList = mAdapter.getAdminChatBeanList();
        for (ChatUserBean bean : mAdminList) {
            mMap.put(bean.getId(), bean);
        }
        EventBus.getDefault().register(this);
        getConversationInfo();
        getLastMessage();
        getHead();
        getTypeAdvert();
    }

    @Override
    public void onItemClick(ChatUserBean bean, int position) {
        if (bean != null) {
            if (bean.getFromType() == ChatUserBean.TYPE_SYSTEM) {
                if (bean.getUnReadCount() > 0) {
                    bean.setUnReadCount(0);
                    mAdapter.updateItem(bean.getId());
                    JMessageUtil.getInstance().markAllMessagesAsRead(bean.getId());
                    ((MainActivity) mContext).refreshUnReadCount();
                }
                if (Constants.YB_ID_1.equals(bean.getId())) {
                    startActivity(new Intent(mContext, AdminMsgActivity.class));
                } else {
                    startActivity(new Intent(mContext, SystemMsgActivity.class));
                }
            } else {
                ChatActivity.forwardChatRoom(mContext, bean);
            }
        }
    }


    private void getConversationInfo() {
        if (AppConfig.getInstance().isLogin() && AppConfig.getInstance().isLoginIM()) {
            String uids = JMessageUtil.getInstance().getConversationUids();
            if (!TextUtils.isEmpty(uids)) {
                HttpUtil.getMultiInfo(uids, mGetMultiInfoCallback);
            }
        }
    }

    private HttpCallback mGetMultiInfoCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                List<ChatUserBean> list = JSON.parseArray(Arrays.toString(info), ChatUserBean.class);
                List<ChatUserBean> msgList = new ArrayList<>();
                JMessageUtil util = JMessageUtil.getInstance();
                for (ChatUserBean bean : list) {
                    bean = util.getLastMessageInfo(bean);
                    if (bean != null) {
                        msgList.add(bean);
                        mMap.put(bean.getId(), bean);
                    }
                }
                mAdapter.insertList(msgList);
            }
        }
    };

    private HttpCallback mGetMultiInfoCallback2 = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                ChatUserBean c = JSON.parseObject(info[0], ChatUserBean.class);
                if (c != null && mNewMessageBean != null && mAdapter != null) {
                    Message message = mNewMessageBean.getRawMessage();
                    JMessageUtil util = JMessageUtil.getInstance();
                    c.setLastTime(util.getMessageTimeString(message));
                    c.setLastMessage(util.getMessageString(message));
                    c.setUnReadCount(1);
                    if (!mMap.containsKey(c.getId())) {
                        mAdapter.insertItem(c);
                    } else {
                        mAdapter.updateItem(c.getId());
                    }
                    mMap.put(c.getId(), c);
                }
            }
        }
    };

    private void getLastMessage() {
        if (AppConfig.getInstance().isLogin()) {
            HttpUtil.getLastMessage(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (obj.containsKey("officeInfo")) {
                            JSONObject officeInfo = obj.getJSONObject("officeInfo");
                            ChatUserBean bean = mAdminList.get(0);
                            bean.setLastTime(JMessageUtil.getInstance().getMessageTimeString(officeInfo.getLongValue("addtime") * 1000));
                            bean.setLastMessage(officeInfo.getString("title"));
                            if (mAdapter != null) {
                                mAdapter.updateItem(0);
                            }
                        } else {
                            ChatUserBean bean = mAdminList.get(0);
                            bean.setLastTime("");
                            bean.setLastMessage("欢迎入驻" + WordUtil.getString(R.string.app_name));
                            if (mAdapter != null) {
                                mAdapter.updateItem(0);
                            }
                        }
                        if (obj.containsKey("sysInfo")) {
                            JSONObject sysInfo = obj.getJSONObject("sysInfo");
                            ChatUserBean bean = mAdminList.get(1);
                            bean.setLastTime(JMessageUtil.getInstance().getMessageTimeString(sysInfo.getLongValue("addtime") * 1000));
                            bean.setLastMessage(sysInfo.getString("title"));
                            if (mAdapter != null) {
                                mAdapter.updateItem(1);
                            }
                        } else {
                            ChatUserBean bean = mAdminList.get(1);
                            bean.setLastTime("");
                            bean.setLastMessage("暂无通知");
                            if (mAdapter != null) {
                                mAdapter.updateItem(1);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_LAST_MESSAGE);
        HttpUtil.cancel(HttpUtil.GET_MULTI_INFO);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 接收消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessageBean(ChatMessageBean bean) {
        if (bean != null && !bean.isFromSelf() && mAdapter != null) {
            if (mNotUpdateUid == null || !mMap.containsKey(mNotUpdateUid) || !mNotUpdateUid.equals(bean.getFrom())) {
                mNewMessageBean = bean;
                ChatUserBean chatUserBean = mMap.get(bean.getFrom());
                if (chatUserBean != null) {
                    if (mAdapter != null) {
                        chatUserBean.setUnReadCount(chatUserBean.getUnReadCount() + 1);
                        Message message = bean.getRawMessage();
                        JMessageUtil util = JMessageUtil.getInstance();
                        chatUserBean.setLastTime(util.getMessageTimeString(message));
                        chatUserBean.setLastMessage(util.getMessageString(message));
                        mAdapter.updateItem(bean.getFrom());
                    }
                } else {
                    HttpUtil.getMultiInfo(bean.getFrom(), mGetMultiInfoCallback2);
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJMessageLoginEvent(JMessageLoginEvent e) {
        if (e.isLogin()) {
            if (mAdapter != null) {
                mAdapter.updateAdminChatInfo();
            }
            getConversationInfo();
        } else {
            if (mMap != null) {
                mMap.clear();
                for (ChatUserBean bean : mAdminList) {
                    mMap.put(bean.getId(), bean);
                }
            }
            if (mAdapter != null) {
                mAdapter.clearData();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatRoomOpenEvent(ChatRoomOpenEvent e) {
        mNotUpdateUid = e.getToUid();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatRoomCloseEvent(ChatRoomCloseEvent e) {
        mNotUpdateUid = null;
        if (mAdapter != null) {
            String touid = e.getToUid();
            ChatUserBean bean = mMap.get(touid);
            if (bean != null) {
                bean.setUnReadCount(0);
                bean.setLastMessage(e.getLastMessage());
                bean.setLastTime(e.getLastTime());
                mAdapter.updateItem(touid);
            } else {
                ChatUserBean c = new ChatUserBean();
                c.wrapUserBean(e.getToUserBean());
                c.setUnReadCount(0);
                c.setLastMessage(e.getLastMessage());
                c.setLastTime(e.getLastTime());
                if (!mMap.containsKey(c.getId())) {
                    mAdapter.insertItem(c);
                } else {
                    mAdapter.updateItem(c.getId());
                }
                mMap.put(c.getId(), c);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoamMsgEvent(RoamMsgEvent e) {
        if (mMap == null || mAdapter == null) {
            return;
        }
        final ChatUserBean bean = e.getChatUserBean();
        String from = bean.getId();
        if (mMap.containsKey(from)) {
            ChatUserBean c = mMap.get(from);
            c.setUnReadCount(bean.getUnReadCount());
            c.setLastMessage(bean.getLastMessage());
            c.setLastTime(bean.getLastTime());
            c.setMsgType(bean.getMsgType());
            mAdapter.updateItem(from);
        } else {
            HttpUtil.getMultiInfo(from, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        ChatUserBean c = JSON.parseObject(info[0], ChatUserBean.class);
                        c.setUnReadCount(bean.getUnReadCount());
                        c.setLastMessage(bean.getLastMessage());
                        c.setLastTime(bean.getLastTime());
                        c.setMsgType(bean.getMsgType());
                        if (!mMap.containsKey(c.getId())) {
                            mAdapter.insertItem(c);
                        } else {
                            mAdapter.updateItem(c.getId());
                        }
                        mMap.put(c.getId(), c);
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOffLineMsgEvent(OffLineMsgEvent e) {
        if (mMap == null || mAdapter == null) {
            return;
        }
        final ChatUserBean bean = e.getChatUserBean();
        String from = bean.getId();
        if (mMap.containsKey(from)) {
            ChatUserBean c = mMap.get(from);
            c.setUnReadCount(c.getUnReadCount() + bean.getUnReadCount());
            c.setLastMessage(bean.getLastMessage());
            c.setLastTime(bean.getLastTime());
            c.setMsgType(bean.getMsgType());
            mAdapter.updateItem(from);
        } else {
            HttpUtil.getMultiInfo(from, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        ChatUserBean c = JSON.parseObject(info[0], ChatUserBean.class);
                        c.setUnReadCount(bean.getUnReadCount());
                        c.setLastMessage(bean.getLastMessage());
                        c.setLastTime(bean.getLastTime());
                        c.setMsgType(bean.getMsgType());
                        if (!mMap.containsKey(c.getId())) {
                            mAdapter.insertItem(c);
                        } else {
                            mAdapter.updateItem(c.getId());
                        }
                        mMap.put(c.getId(), c);
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginUserChangedEvent(LoginUserChangedEvent e) {
        getLastMessage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_user:
                forwardContactsActivity();
                //JMessageUtil.getInstance().removeAllConversation();
                break;
            case R.id.btn_fans:
                forwardFansMsgActivity();
                break;
            case R.id.btn_zan:
                forwardZanMsgActivity();
                break;
            case R.id.btn_at:
                forwardAtMsgActivity();
                break;
            case R.id.btn_comment:
                forwardCommentMsgActivity();
                break;
        }
    }

    /**
     * 粉丝
     */
    private void forwardFansMsgActivity() {
        startActivity(new Intent(mContext, FansMsgActivity.class));
    }

    /**
     * 赞
     */
    private void forwardZanMsgActivity() {
        startActivity(new Intent(mContext, ZanMsgActivity.class));
    }

    /**
     * "@我的"
     */
    private void forwardAtMsgActivity() {
        startActivity(new Intent(mContext, AtMsgActivity.class));
    }

    /**
     * 评论
     */
    private void forwardCommentMsgActivity() {
        startActivity(new Intent(mContext, CommentMsgActivity.class));
    }

    /**
     * 搜索联系人
     */
    private void forwardContactsActivity() {
        startActivity(new Intent(mContext, ContactsActivity.class));
    }

    public void getHead(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        headRecyclerView.setLayoutManager(layoutManager);
        headAdapter = new TypeHeadAdapter(getActivity(), headBeanList);
        headRecyclerView.setAdapter(headAdapter);
        headAdapter.setOnItemClickListener(new TypeHeadAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
               /* TypeHeadBean bean = headBeanList.get(position);
                startBrowserActivity(getActivity(), 0, bean.getUrl());*/
                TypeHeadBean bean = headBeanList.get(position);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(bean.getUrl());
                intent.setData(content_url);
                startActivity(intent);
            }
        });

    }


    // 获取广告
    public void getTypeAdvert() {
        OkHttp.getAsync(HTTP_URL + "service=Ad.getAdLists", new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonParser = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonParser.getAsJsonObject("data");
                JsonArray jsonElements = object.getAsJsonArray("ad_list");//获取JsonArray对象
                for (JsonElement json : jsonElements) {
                    TypeHeadBean bean = new Gson().fromJson(json, TypeHeadBean.class);
                    headBeanList.add(bean);
                }
                headAdapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();

            }
        });
    }
}
