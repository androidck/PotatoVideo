package com.mibai.phonelive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mibai.phonelive.R;
import com.mibai.phonelive.purse.entry.MyPurseEntry;

import java.util.ArrayList;
import java.util.List;

// 钱包适配器
public class MyPurseAdapter extends BaseRecyclerViewAdapter<MyPurseAdapter.ViewHolder> {

    private List<MyPurseEntry> list;
    private Context mContext;

    private OnItemClickListener onItemClickListener;

    public MyPurseAdapter(Context context, List<MyPurseEntry> list) {
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
        return new ViewHolder(parent, R.layout.item_my_purse);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        MyPurseEntry entry = list.get(position);
        holder.tv_jinbi.setText(entry.getTitle());
        holder.tv_money.setText("￥" + entry.getMoney());
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

        private TextView tv_jinbi, tv_money;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tv_jinbi = (TextView) findViewById(R.id.tv_jinbi);
            tv_money = (TextView) findViewById(R.id.tv_money);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
