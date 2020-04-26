package com.mibai.phonelive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

public class TodayHotAndZanAdapter extends BaseRecyclerViewAdapter<TodayHotAndZanAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoBean> list;

    private OnItemClickListener onItemClickListener;

    public TodayHotAndZanAdapter(Context context, List<VideoBean> list) {
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
        return new ViewHolder(parent, R.layout.item_today_and_zan);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        VideoBean hotEntry = list.get(position);
        Glide.with(mContext).load(hotEntry.getThumb()).into(holder.roundedImageView);
        holder.tv_title.setText(hotEntry.getTitle());
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
        RoundedImageView roundedImageView;
        TextView tv_title;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            roundedImageView = (RoundedImageView) findViewById(R.id.roundedImageView);
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
