package com.mibai.phonelive.community.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import com.mibai.phonelive.R;
import com.mibai.phonelive.community.base.BaseRecyclerViewAdapter;
import com.mibai.phonelive.community.bean.CircleChildEntry;
import com.mibai.phonelive.community.ninegridlayout.NineGridTestLayout;
import com.mibai.phonelive.community.view.RoundAngleImageView;
import com.mibai.phonelive.utils.RelativeDateFormatUtils;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// 圈子适配器
public class CircleAdapter extends BaseRecyclerViewAdapter<CircleAdapter.ViewHolder> {

    private List<CircleChildEntry> list;
    private Context mContext;
    private OnItemAttentionListener onItemAttentionListener;


    private OnJustLikeListener onJustLikeListener;

    private OnSendGiftListener onSendGiftListener;

    private OnClickListener onClickListener;

    private OnReportClickListener onReportClickListener;

    private OnCommentClickListener onCommentClickListener;


    public CircleAdapter(Context context, List<CircleChildEntry> list) {
        super(context);
        this.mContext = context;
        this.list = list;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_follow);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        CircleChildEntry entry = list.get(position);
        if (TextUtils.isEmpty(entry.getVideo()) || "0".equals(entry.getVideo())) {
            holder.ly_nine.setVisibility(View.VISIBLE);
            holder.ly_video.setVisibility(View.GONE);
            String[] str = entry.getImg().split(",");
            holder.ly_nine.setUrlList(Arrays.asList(str));
        } else {
            holder.ly_nine.setVisibility(View.GONE);
            holder.ly_video.setVisibility(View.VISIBLE);
            //Glide.with(mContext).load(HttpConstant.VIDEO_URL + entry.getVideo() + "?vframe/jpg/offset/1").into(holder.img_video_cover);
        }

        if (1 == entry.getIsgz()) {
            Drawable drawableLeft = mContext.getDrawable(R.mipmap.icon_yizan);
            drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
            holder.tv_support.setCompoundDrawables(drawableLeft, null, null, null);
        } else {
            Drawable drawableLeft = mContext.getDrawable(R.mipmap.icon_zan);
            drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
            holder.tv_support.setCompoundDrawables(drawableLeft, null, null, null);
        }

        if ("1".equals(entry.getUserinfo().getIsvip())) {
            holder.tv_is_vip.setVisibility(View.VISIBLE);
        } else {
            holder.tv_is_vip.setVisibility(View.GONE);
        }

        if ("1".equals(entry.getUserinfo().getSex())) {
            holder.tv_nick_name.setCompoundDrawables(null, null, setDrawable(R.mipmap.icon_boy), null);
        } else {
            holder.tv_nick_name.setCompoundDrawables(null, null, setDrawable(R.mipmap.icon_girl), null);
        }

        // 是否关注
        if (1 == entry.getIsgz()) {
            holder.tv_attention.setBackgroundResource(R.drawable.shape_setup_attent_button);
            holder.tv_attention.setText("已关注");
            holder.tv_attention.setCompoundDrawables(null, null, null, null);
            holder.tv_attention.setPadding(0, 6, 0, 6);
        } else {
            holder.tv_attention.setBackgroundResource(R.drawable.shape_setup_button);
            holder.tv_attention.setText("关注");
            holder.tv_attention.setCompoundDrawables(setDrawable(R.mipmap.icon_add_follow), null, null, null);
            holder.tv_attention.setPadding(20, 6, 0, 6);
        }


        // 头像
        Glide.with(mContext)
                .load(entry.getUserinfo().getAvatar())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.user_head);
        holder.bindData(entry);


        // 关注按钮点击事件
        holder.tv_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemAttentionListener.onAttentionClick(position);
            }
        });

        // 点赞
        holder.tv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onJustLikeListener.onJustLike(position);
            }
        });

        // 送礼
        holder.img_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendGiftListener.onSendGift(position);
            }
        });

        // item 点击事件
        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(position);
            }
        });

        // 举报
        holder.tv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReportClickListener.onReport(position);
            }
        });

        // 点击个人资料详情
        holder.user_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 播放视频
        holder.ly_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 评论
        holder.img_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCommentClickListener.onComment(position);
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Drawable setDrawable(int desId) {
        Drawable drawable = mContext.getDrawable(desId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        CircleImageView user_head;
        TextView tv_nick_name, img_gift, img_comment;
        ImageView tv_is_vip;
        TextView tv_location, tv_time, tv_attention, tv_report, tv_content, tv_label, tv_read_count, tv_support, tv_comment;
        NineGridTestLayout ly_nine;
        AutoLinearLayout ly_item;
        RoundAngleImageView img_video_cover;
        AutoRelativeLayout ly_video;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            ly_item = (AutoLinearLayout) findViewById(R.id.ly_item);
            user_head = (CircleImageView) findViewById(R.id.user_head);
            tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);
            tv_is_vip = (ImageView) findViewById(R.id.tv_is_vip);
            img_gift = (TextView) findViewById(R.id.img_gift);
            img_comment = (TextView) findViewById(R.id.img_comment);

            tv_location = (TextView) findViewById(R.id.tv_location);
            tv_time = (TextView) findViewById(R.id.tv_time);
            tv_attention = (TextView) findViewById(R.id.tv_attention);
            tv_report = (TextView) findViewById(R.id.tv_report);
            tv_content = (TextView) findViewById(R.id.tv_content);
            tv_label = (TextView) findViewById(R.id.tv_label);
            tv_read_count = (TextView) findViewById(R.id.tv_read_count);
            tv_support = (TextView) findViewById(R.id.tv_support);
            tv_comment = (TextView) findViewById(R.id.tv_comment);

            ly_nine = (NineGridTestLayout) findViewById(R.id.ly_nine);
            img_video_cover = (RoundAngleImageView) findViewById(R.id.img_video_cover);
            ly_video = (AutoRelativeLayout) findViewById(R.id.ly_video);
        }

        public void bindData(CircleChildEntry entry) {
            // 头像
            tv_nick_name.setText(entry.getUserinfo().getUser_nicename());
            // 内容
            tv_content.setText(entry.getContent());
            // 城市
            tv_location.setText(entry.getLocaltion());
            // 标签
            tv_label.setText(entry.getLabel());
            // 是否是vip
            if ("1".equals(entry.getUserinfo().getIsvip())) {
                tv_is_vip.setVisibility(View.VISIBLE);
            } else {
                tv_is_vip.setVisibility(View.GONE);
            }
            // 礼物量
            // img_gift.setText(entry.getLwsl());
            // 阅读量
            tv_read_count.setText("浏览量：" + entry.getView_num());
            // 阅读量
            tv_comment.setText(entry.getView_num());
            // 点赞
            tv_support.setText(entry.getLike_num());
            // 时间
            tv_time.setText(RelativeDateFormatUtils.format(new Date(Long.parseLong(entry.getAddtime()) * 1000)));


        }
    }


    // 关注/ 取消关注
    public interface OnItemAttentionListener {
        void onAttentionClick(int position);
    }

    public void setOnItemAttentionListener(OnItemAttentionListener onItemAttentionListener) {
        this.onItemAttentionListener = onItemAttentionListener;
    }

    public interface OnJustLikeListener {
        void onJustLike(int position);
    }

    public void setOnJustLikeListener(OnJustLikeListener onJustLikeListener) {
        this.onJustLikeListener = onJustLikeListener;
    }

    public interface OnSendGiftListener {
        void onSendGift(int position);
    }

    public void setOnSendGiftListener(OnSendGiftListener onSendGiftListener) {
        this.onSendGiftListener = onSendGiftListener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnReportClickListener {
        void onReport(int position);
    }

    public void setOnReportClickListener(OnReportClickListener onReportClickListener) {
        this.onReportClickListener = onReportClickListener;
    }

    public interface OnCommentClickListener {
        void onComment(int position);
    }

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
    }
}
