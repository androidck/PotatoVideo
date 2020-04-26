package com.mibai.phonelive.flowlayout;

import android.view.View;

import java.util.ArrayList;

/**
 * 作者： q2
 * 时间： 2017/10/31
 */

public abstract class TagAdapter<T> {

    private ArrayList<T> tags;

    public TagAdapter(ArrayList<T> tags){
        this.tags=tags;
    }

    public ArrayList<T> getTags() {
        return tags;
    }


    public T getItem(int position)
    {
        return tags.get(position);
    }


    //批量刷新
    public void refreshTags(ArrayList<T> tags) {
        this.tags = tags;
        mLstener.refreshTags();
    }

    //动态添加tag
    public void addTagsItem(T tag){
        if(tags!=null){
            tags.add(tag);
            mLstener.addTagsItem(tags.size()-1);
        }
    }

    //动态移除tag
    public void removeTagsItem(int pos){
        if(tags!=null&&tags.size()>pos){
            mLstener.removeTagsItem(pos);
        }
    }

    public void removeTag(int pos){
            tags.remove(pos);
    }

    //
    public abstract View getView(FlowLayout parent, int position, T t);

    OnTagsChangeListener mLstener;

    public void setmLstener(OnTagsChangeListener mLstener){
        this.mLstener=mLstener;
    }


    public interface OnTagsChangeListener{
        void addTagsItem(int pos);
        void removeTagsItem(int pos);
        void refreshTags();
    }

}
