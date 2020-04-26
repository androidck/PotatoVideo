package com.mibai.phonelive.community.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.mibai.phonelive.R;
import com.mibai.phonelive.community.base.BaseRecyclerViewAdapter;
import com.mibai.phonelive.community.bean.CircleChildHeaderEntry;

import java.util.ArrayList;
import java.util.List;

// 发布帖子适配器
public class SendPostCircleAdapter extends BaseRecyclerViewAdapter<SendPostCircleAdapter.ViewHolder> {

    private List<CircleChildHeaderEntry> list;
    private Context mContext;

    private OnItemClickListener onItemClickListener;

    public SendPostCircleAdapter(Context context, List<CircleChildHeaderEntry> list) {
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
        return new ViewHolder(parent, R.layout.item_circle);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CircleChildHeaderEntry entry = list.get(position);
        holder.tv_title.setText(entry.getTitle());
        if (entry.isSelect() == true) {
            holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.tv_title.setBackgroundResource(R.drawable.shape_circle_bg);
        } else {
            holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.textColor));
            holder.tv_title.setBackgroundResource(R.drawable.shape_circle_bg_unselect);
        }

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            onItemClickListener.onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        TextView tv_title;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tv_title = (TextView) findViewById(R.id.tv_title);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
