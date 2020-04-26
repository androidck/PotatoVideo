package com.mibai.phonelive.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mibai.phonelive.R;
import com.mibai.phonelive.purse.entry.JinBiDetailsEntry;
import com.mibai.phonelive.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

// 视频适配器
public class ColdCoinAdapter extends BaseRecyclerViewAdapter<ColdCoinAdapter.ViewHolder> {

    private Context mContext;
    private List<JinBiDetailsEntry> list;

    public ColdCoinAdapter(Context context, List<JinBiDetailsEntry> list) {
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
        return new ViewHolder(parent, R.layout.item_cold_coin);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JinBiDetailsEntry entry = list.get(position);
        if ("0".equals(entry.getType())) {
            holder.img_icon.setImageResource(R.mipmap.icon_chong);
            holder.tv_tip.setText("充值");
            holder.tv_money.setText(entry.getMoney());
            holder.tv_time.setText(TimeUtil.convertTimestamp2Date(Long.valueOf(entry.getAddtime()),"yyyy.MM.dd"));
            holder.tv_tip.setTextColor(Color.parseColor("#ed82c1"));
        } else if ("1".equals(entry.getType())) {
            holder.img_icon.setImageResource(R.mipmap.icon_ti);
            holder.tv_tip.setText("提现");
            holder.tv_money.setText(entry.getMoney());
            holder.tv_time.setText(TimeUtil.convertTimestamp2Date(Long.valueOf(entry.getAddtime()),"yyyy.MM.dd"));
            holder.tv_tip.setTextColor(Color.parseColor("#d830ea"));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private ImageView img_icon;
        private TextView tv_money, tv_time, tv_tip;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            img_icon = (ImageView) findViewById(R.id.img_icon);
            tv_money = (TextView) findViewById(R.id.tv_money);
            tv_time = (TextView) findViewById(R.id.tv_time);
            tv_tip = (TextView) findViewById(R.id.tv_tip);
        }
    }
}
