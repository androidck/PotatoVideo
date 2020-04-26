package com.mibai.phonelive.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.UserBean;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.custom.RefreshAdapter;
import com.mibai.phonelive.glide.ImgLoader;

/**
 * Created by cxf on 2018/6/7.
 * 个人中心 喜欢
 */

public class UserLikeAdapter extends RefreshAdapter<VideoBean> {

    public UserLikeAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_user_like, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mLikes;
        VideoBean mBean;
        int mPosition;


        public Vh(View itemView) {
            super(itemView);
            mThumb = (ImageView) itemView.findViewById(R.id.thumb);
            mLikes = (TextView) itemView.findViewById(R.id.likes);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setData(VideoBean bean, int position) {
            mBean = bean;
            mPosition = position;
            ImgLoader.display(bean.getThumb(), mThumb);
            UserBean u = bean.getUserinfo();
            if(u!=null){
                mLikes.setText(bean.getLikes());
            }
        }
    }
}
