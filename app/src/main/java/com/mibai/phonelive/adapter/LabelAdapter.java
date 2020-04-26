package com.mibai.phonelive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mibai.phonelive.R;

import java.util.ArrayList;
import java.util.List;

// 标签适配器
public class LabelAdapter extends BaseRecyclerViewAdapter<LabelAdapter.ViewHolder> {

    private Context mContext;
    private List<String> list;

    public LabelAdapter(Context context, List<String> list) {
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
        return new ViewHolder(parent, R.layout.item_lable);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String str = list.get(position);
        holder.item_tv_label.setText(str);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        TextView item_tv_label;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            item_tv_label= (TextView) findViewById(R.id.item_tv_label);
        }
    }
}
