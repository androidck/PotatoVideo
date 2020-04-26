package com.mibai.phonelive.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mibai.phonelive.R;
import com.mibai.phonelive.purse.entry.VipPayEntry;

import java.util.ArrayList;
import java.util.List;

// 会员充值适配器
public class VipPayAdapter extends BaseRecyclerViewAdapter<VipPayAdapter.ViewHolder> {

    private List<VipPayEntry> list;
    private Context mContext;

    private OnItemClickListener onItemClickListener;

    public VipPayAdapter(Context context, List<VipPayEntry> list) {
        super(context);
        this.mContext = context;
        this.list = list;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_vip_pay);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        VipPayEntry entry = list.get(position);
        holder.tv_name.setText(entry.getTitle());
        holder.tv_xj.setText(entry.getMoney());
        holder.tv_yj.setText("原价" + entry.getOld_money() + "元");
        holder.tv_time.setText(entry.getDescn());
        holder.tv_yj.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        if (position == 0) {
            holder.tv_name.setTextColor(Color.parseColor("#ed82c1"));
            holder.tv_yj.setTextColor(Color.parseColor("#ed82c1"));
            holder.tv_time.setTextColor(Color.parseColor("#ed82c1"));
            holder.icon_img.setImageResource(R.mipmap.icon_vip);
        } else if (position == 1) {
            holder.tv_name.setTextColor(Color.parseColor("#d5bd9a"));
            holder.tv_yj.setTextColor(Color.parseColor("#d5bd9a"));
            holder.tv_time.setTextColor(Color.parseColor("#d5bd9a"));
            holder.icon_img.setImageResource(R.mipmap.icon_ji);
        } else if (position == 2) {
            holder.tv_name.setTextColor(Color.parseColor("#d830ea"));
            holder.tv_yj.setTextColor(Color.parseColor("#d830ea"));
            holder.tv_time.setTextColor(Color.parseColor("#d830ea"));
            holder.icon_img.setImageResource(R.mipmap.icon_nian);
        } else {
            holder.tv_name.setTextColor(Color.parseColor("#ed82c1"));
            holder.tv_yj.setTextColor(Color.parseColor("#ed82c1"));
            holder.tv_time.setTextColor(Color.parseColor("#ed82c1"));
            holder.icon_img.setImageResource(R.mipmap.icon_vip);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(position);
            }
        });
    }

    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        ImageView icon_img;
        TextView tv_name, tv_time, tv_xj, tv_yj;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            icon_img = (ImageView) findViewById(R.id.icon_img);
            tv_name = (TextView) findViewById(R.id.tv_name);
            tv_time = (TextView) findViewById(R.id.tv_time);
            tv_xj = (TextView) findViewById(R.id.tv_xj);
            tv_yj = (TextView) findViewById(R.id.tv_yj);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
