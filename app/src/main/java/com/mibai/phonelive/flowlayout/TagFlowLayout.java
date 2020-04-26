package com.mibai.phonelive.flowlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;



/**
 * 作者： q2
 * 时间： 2017/10/31
 */

public class TagFlowLayout extends FlowLayout implements TagAdapter.OnTagsChangeListener {

    private TagAdapter mAdapter;
    private onTagItemClickListener mListener;
    private onTagItemLongClickListener mLongListener;
    private ObjectAnimator animator;
    private boolean isDeleteAnimating=false;


    public TagFlowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TagFlowLayout(Context context) {
        super(context);
    }


    public void setAdapter(TagAdapter adapter){
        this.mAdapter=adapter;
        refreshChildView();
    }

    //刷新所有的View
    private void refreshChildView(){
        if(mAdapter==null){
            throw new RuntimeException("TagAdapter not allow null");
        }
        if(mAdapter.getTags()==null||mAdapter.getTags().size()==0){
            throw new RuntimeException("TagAdapter's tags not allow null or size == 0");
        }
        mAdapter.setmLstener(this);
        for(int i=0;i<mAdapter.getTags().size();i++){
            addOneChild(i);
        }
    }
    //添加一个tag
    private void addOneChild(int i){
        final int pos=i;
        final Object item=mAdapter.getItem(i);
        final View tagView = mAdapter.getView(this, pos, mAdapter.getItem(pos));
        tagView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null) {
                    mListener.onItemClick(pos, item);
                }
            }
        });
        tagView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mLongListener!=null) {
                    mLongListener.onLongItemClick(pos,item);
                }
                return true;
            }
        });
        addView(tagView);
    }



    @Override
    public void addTagsItem(int pos) {
        addOneChild(pos);
    }

    @Override
    public void removeTagsItem(final int  pos) {
        if(isDeleteAnimating) return;
         final View view=  getChildAt(pos);
         animator= ObjectAnimator.ofFloat(view,"scaleX",1,0);
         animator.setDuration(200);
         animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isDeleteAnimating=true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView(view);
                mAdapter.removeTag(pos);
                isDeleteAnimating=false;
            }
        });
        animator.start();


    }

    @Override
    public void refreshTags() {
        removeAllViews();
        refreshChildView();
    }


    public  void setmItemClickListener(onTagItemClickListener mListener){
        this.mListener=mListener;
    }
    public  void setmLongClickListener(onTagItemLongClickListener mLongListener){
        this.mLongListener=mLongListener;
    }


    public interface onTagItemClickListener{
        void onItemClick(int pos, Object data);
    }

    public interface onTagItemLongClickListener{
        void onLongItemClick(int pos, Object data);
    }

}
