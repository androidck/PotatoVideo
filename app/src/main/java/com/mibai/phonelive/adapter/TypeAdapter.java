package com.mibai.phonelive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.TypeEntry;

import java.util.ArrayList;
import java.util.List;

public class TypeAdapter extends BaseRecyclerViewAdapter<TypeAdapter.ViewHolder> {

    private Context mContext;
    private List<TypeEntry> list;
    private OnItemClickListener onItemClickListener;

    public TypeAdapter(Context context, List<TypeEntry> list) {
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
        return new ViewHolder(parent, R.layout.item_type);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        TypeEntry entry = list.get(position);
        holder.tv_tip.setText("#" + entry.getTitle());
        Glide.with(mContext).load(entry.getImage()).into(holder.item_img);
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
        ImageView item_img;
        TextView tv_tip;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            item_img = (ImageView) findViewById(R.id.item_img);
            tv_tip = (TextView) findViewById(R.id.tv_tip);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
