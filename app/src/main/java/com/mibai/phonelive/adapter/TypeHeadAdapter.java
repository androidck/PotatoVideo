package com.mibai.phonelive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.TypeHeadBean;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// 视频适配器
public class TypeHeadAdapter extends BaseRecyclerViewAdapter<TypeHeadAdapter.ViewHolder> {

    private Context mContext;
    private List<TypeHeadBean> list;
    private OnItemClickListener onItemClickListener;

    public TypeHeadAdapter(Context context, List<TypeHeadBean> list) {
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
        return new ViewHolder(parent, R.layout.item_header_type);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        TypeHeadBean bean = list.get(position);
        holder.tv_title.setText(bean.getName());
        holder.tv_account.setText(bean.getDes());
        Glide.with(mContext).load(AppConfig.HOST+bean.getThumb()).into(holder.user_head);
        // 点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        LinearLayout item_bg;
        TextView tv_title, tv_ranking, tv_account;
        CircleImageView user_head;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            item_bg = (LinearLayout) findViewById(R.id.item_bg);
            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_ranking = (TextView) findViewById(R.id.tv_ranking);
            tv_account = (TextView) findViewById(R.id.tv_account);
            user_head = (CircleImageView) findViewById(R.id.user_head);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
