package com.mibai.phonelive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.InvitationEntry;

import java.util.ArrayList;
import java.util.List;

// 邀请记录
public class InvitationRecordAdapter extends BaseRecyclerViewAdapter<InvitationRecordAdapter.ViewHolder> {

    private Context mContext;
    private List<InvitationEntry> list;

    public InvitationRecordAdapter(Context context, List<InvitationEntry> list) {
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
        return new ViewHolder(parent, R.layout.item_invition_record);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvitationEntry entry = list.get(position);
        holder.tv_invitation_code.setText(entry.getUserinfo().getCode());
        holder.tv_phone.setText(entry.getUserinfo().getUser_login());
        holder.tv_register.setText(entry.getUserinfo().getUser_nicename());

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        TextView tv_invitation_code, tv_phone, tv_register;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tv_invitation_code = (TextView) findViewById(R.id.tv_invitation_code);
            tv_phone = (TextView) findViewById(R.id.tv_phone);
            tv_register = (TextView) findViewById(R.id.tv_register);
        }
    }
}
