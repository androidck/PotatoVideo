package com.mibai.phonelive.community.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.mibai.phonelive.R;
import com.mibai.phonelive.community.base.BaseRecyclerViewAdapter;
import com.mibai.phonelive.community.bean.CommentEntry;
import com.mibai.phonelive.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// 评论适配器
public class CommentNewAdapter extends BaseRecyclerViewAdapter<CommentNewAdapter.ViewHolder> {

    private Context mContext;
    private List<CommentEntry> list;

    private OnDelCommentListener onDelCommentListener;

    private OnLoveListener onLoveListener;

    public CommentNewAdapter(Context context, List<CommentEntry> list) {
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
        return new ViewHolder(parent, R.layout.item_comment);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        CommentEntry entry = list.get(position);
        holder.tv_content.setText(entry.getContent());
        holder.tv_time.setText(TimeUtils.getShortTime(Long.parseLong(entry.getAddtime())));
        holder.tv_location.setText(entry.getUserinfo().getCity());
        holder.tv_nick_name.setText(entry.getUserinfo().getUser_nicename());
        Glide.with(mContext).load(entry.getUserinfo().getAvatar()).placeholder(R.mipmap.ic_launcher).into(holder.user_head);
        holder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDelCommentListener.onDelComment(position);
            }
        });

        // holder.tv_love.setText(entry.getLove());

        holder.tv_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoveListener.onLove(position);
            }
        });

     /*   if (entry.getUserinfo().getIslove() == 0) {
            Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.icon_comment_unlove);
            drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
            holder.tv_love.setCompoundDrawables(drawableLeft, null, null, null);
        } else {
            Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.icon_comment_love);
            drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
            holder.tv_love.setCompoundDrawables(drawableLeft, null, null, null);
        }*/
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        CircleImageView user_head;
        TextView tv_nick_name, tv_location, tv_time, tv_content, tv_love;
        ImageView img_more;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            user_head = (CircleImageView) findViewById(R.id.user_head);
            tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);
            tv_location = (TextView) findViewById(R.id.tv_location);
            tv_time = (TextView) findViewById(R.id.tv_time);
            tv_content = (TextView) findViewById(R.id.tv_content);
            img_more = (ImageView) findViewById(R.id.img_more);
            tv_love = (TextView) findViewById(R.id.tv_love);
        }
    }

    public interface OnDelCommentListener {
        void onDelComment(int position);
    }

    public void setOnDelCommentListener(OnDelCommentListener onDelCommentListener) {
        this.onDelCommentListener = onDelCommentListener;
    }

    public interface OnLoveListener {
        void onLove(int position);
    }

    public void setOnLoveListener(OnLoveListener onLoveListener) {
        this.onLoveListener = onLoveListener;
    }
}
