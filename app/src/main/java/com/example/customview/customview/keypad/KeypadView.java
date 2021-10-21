package com.example.customview.customview.keypad;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.customview.R;
import com.example.customview.customview.utils.SizeUtils;

public class KeypadView extends ViewGroup {
    //4行
    public static final int DEFAULT_ROW = 4;
    //3列
    public static final int DEFAULT_COLUMN = 3;
    public static final int DEFAULT_MARGIN = SizeUtils.dip2px(2);
    private static final String TAG = "KeypadView";
    private int mTextColor;
    private float mTextSize;
    private int mItemPressBgColor;
    private int mItemNormalBgColor;
    private int row = DEFAULT_ROW;
    private int column = DEFAULT_COLUMN;
    private int mItemMargin;
    private OnKeyClickListener mOnNumberClickListener = null;

    public KeypadView(Context context) {
        this(context, null);
    }

    public KeypadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeypadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        initAttrs(context, attrs);
        //
        setUpItem();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }

    public int getItemPressBgColor() {
        return mItemPressBgColor;
    }

    public void setItemPressBgColor(int itemPressBgColor) {
        mItemPressBgColor = itemPressBgColor;
    }

    public int getItemNormalBgColor() {
        return mItemNormalBgColor;
    }

    public void setItemNormalBgColor(int itemNormalBgColor) {
        mItemNormalBgColor = itemNormalBgColor;
    }

    public int getItemMargin() {
        return mItemMargin;
    }

    public void setItemMargin(int itemMargin) {
        mItemMargin = itemMargin;
    }

    private void setUpItem() {
        removeAllViews();
        for (int i = 0; i < 11; i++) {
            TextView item = new TextView(getContext());
            //内容
            if (i == 10) {
                item.setTag(true);
                item.setText("删除");
            } else {
                item.setTag(false);
                item.setText(String.valueOf(i));
            }
            //大小
            if (mTextSize != -1) {
                item.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            }
            //居中
            item.setGravity(Gravity.CENTER);
            //字体颜色
            item.setTextColor(mTextColor);
            //设置背景
            item.setBackground(providerItemBg());
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnNumberClickListener != null) {
                        boolean isDelete = (boolean) v.getTag();
                        if (!isDelete)
                        {
                            String valueText = ((TextView) v).getText().toString();
                            mOnNumberClickListener.onNumberClick(Integer.parseInt(valueText));
                        }else {
                            mOnNumberClickListener.onDeleteClick();
                        }
                    }
                }
            });
            addView(item);
        }
    }

    private Drawable providerItemBg() {
        //按下了bg
        GradientDrawable pressDrawable = new GradientDrawable();
        pressDrawable.setColor(mItemPressBgColor);
        pressDrawable.setCornerRadius(SizeUtils.dip2px(5));
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
        //普通状态的bg
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(mItemNormalBgColor);
        normalDrawable.setCornerRadius(SizeUtils.dip2px(5));
        bg.addState(new int[]{}, normalDrawable);
        return bg;
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeypadView);
        //获取属性
        mTextColor = a.getColor(R.styleable.KeypadView_numberColor, context.getResources().getColor(R.color.white));
        mTextSize = a.getDimensionPixelSize(R.styleable.KeypadView_numberSize, -1);
        Log.d(TAG, "mTextSize==>" + mTextSize);
        mItemMargin = a.getDimensionPixelSize(R.styleable.KeypadView_itemMargin, DEFAULT_MARGIN);
        mItemPressBgColor = a.getColor(R.styleable.KeypadView_itemPressColor, context.getResources().getColor(R.color.keyItemPressColor));
        mItemNormalBgColor = a.getColor(R.styleable.KeypadView_itemNormalColor, context.getResources().getColor(R.color.keyItemColor));
        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int verticalPadding = getPaddingBottom() + getPaddingTop();
        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        Log.d(TAG, "widthSize == >" + widthSize);
//        Log.d(TAG, "widthMode == >" + widthMode);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        Log.d(TAG, "heightSize == >" + heightSize);
//        Log.d(TAG, "heightMode == >" + heightMode);
        //一行三列，求出列宽，三等分
        int perItemWidth = (widthSize - (column + 1) * mItemMargin - horizontalPadding) / column;
        int perItemHeight = (heightSize - (row + 1) * mItemMargin - verticalPadding) / row;
        int normalWidthSpec = MeasureSpec.makeMeasureSpec(perItemWidth, MeasureSpec.EXACTLY);
        int deleteWidthSpec = MeasureSpec.makeMeasureSpec(perItemWidth * 2 + mItemMargin, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(perItemHeight, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            View item = getChildAt(i);
            boolean isDelete = (boolean) item.getTag();
            item.measure(isDelete ? deleteWidthSpec : normalWidthSpec, heightSpec);
        }
        //测量孩子
        //测量自己
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int leftPadding = getPaddingLeft();
        int topPadding = getPaddingTop();
        int left = mItemMargin + leftPadding, top, right, bottom;
        for (int i = 0; i < childCount; i++) {
            //求出当前元素在第几行第几列
            int rowIndex = i / column;
            int columnIndex = i % column;
            if (columnIndex == 0) {
                left = mItemMargin + leftPadding;
            }
            View item = getChildAt(i);
            top = rowIndex * item.getMeasuredHeight() + mItemMargin * (rowIndex + 1) + topPadding;
            right = left + item.getMeasuredWidth();
            bottom = top + item.getMeasuredHeight();
            item.layout(left, top, right, bottom);
            left += item.getMeasuredWidth() + mItemMargin;
        }
    }

    public void setOnKeyClickListener(OnKeyClickListener listener) {
        this.mOnNumberClickListener = listener;
    }

    public interface OnKeyClickListener {
        void onNumberClick(int value);

        void onDeleteClick();
    }
}
