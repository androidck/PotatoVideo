package com.mibai.phonelive.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;

import com.mibai.phonelive.R;


public class FlowLayout extends ViewGroup {

    private int horizontalSpace = 0;//水平间距
    private int verticalSpace = 0;//垂直间距
    private boolean isShowAddViewAnimation;

    public FlowLayout(Context context) {
        super(context);

    }

    public FlowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout);
        verticalSpace = (int) ta.getDimension(R.styleable.FlowLayout_verticalSpace,20f);
        horizontalSpace= (int) ta.getDimension(R.styleable.FlowLayout_horizontalSpace,20f);
        isShowAddViewAnimation= ta.getBoolean(R.styleable.FlowLayout_isShowAddViewAnimation,true  );
        ta.recycle();
        initAddViewAnimation();
    }

    //第一次进入并且配置是需要动画的时候展示
    //addView动画
    private void initAddViewAnimation() {
        if (isShowAddViewAnimation) {
            ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1);
            sa.setDuration(200);
            LayoutAnimationController lac = new LayoutAnimationController(sa, 0.5f);
            lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
            setLayoutAnimation(lac);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取控件的宽高和模式
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //模式是wrap_content的时候需要宽高
        int width = 0;
        int height = 0;
        //当前行的宽高
        int lineWidth = 0;
        int lineHeight = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            //测绘child
            XYLayoutParams lp = (XYLayoutParams) child.getLayoutParams();
            final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                    getPaddingLeft() + this.getPaddingRight()+horizontalSpace*2, lp.width);
            final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                    getPaddingTop() + getPaddingBottom()+verticalSpace*2, lp.height);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            //相对父控件的位置
            int posX = 0;
            int posY = 0;
            //获取当前child所需的宽高
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin + horizontalSpace;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin + verticalSpace;

            //当前行的宽度加上child的宽度超过当前行允许的宽度，就换行
            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()-horizontalSpace*2) {
                //需要换行
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
                posX = getPaddingLeft() + horizontalSpace;
                posY = getPaddingTop() + height + verticalSpace;
            } else {
                //不需要换行
                posX = getPaddingLeft() + lineWidth + horizontalSpace;
                posY = getPaddingTop() + height + verticalSpace;
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            lp.setPosition(posX, posY);
        }

        //实际控件所需的宽高加上最后一行
        width = Math.max(lineWidth, width);
        height += lineHeight;

        setMeasuredDimension(
                //如果是精确测绘模式就用原来的数据，不然就用计算所需的大小。
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()//
        );

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE)
            {
                continue;
            }

            XYLayoutParams lp = (XYLayoutParams) child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
        }
    }




    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof XYLayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new XYLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new XYLayoutParams(getContext(), attributeSet);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new XYLayoutParams(p);
    }


    public static class XYLayoutParams extends MarginLayoutParams {
        private int x;
        private int y;
        public XYLayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }
        public XYLayoutParams(int width, int height) {
            super(width, height);
        }
        public XYLayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
        }
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}
