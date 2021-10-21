package com.example.customview.customview.flow;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.customview.R;
import com.example.customview.customview.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    public static final int DEFAULT_LINE = -1;
    //后面转单位，目前是px不适配
    public static final int DEFAULT_HORIZONTAL_MARGIN = SizeUtils.dip2px(5f);
    public static final int DEFAULT_VERTICAL_MARGIN = SizeUtils.dip2px(5f);
    public static final int DEFAULT_BORDER_RADIUS = SizeUtils.dip2px(5f);
    public static final int DEFAULT_TEXT_MAX_LENGTH = -1;
    private static final String TAG = "FlowLayout";
    private int mMaxLine;
    private int mHorizontalMargin;
    private int mVerticalMargin;
    private int mTextMaxLength;
    private int mTextColor;
    private int mBorderColor;
    private float mBorderRadius;
    private List<String> mData = new ArrayList<>();
    private OnItemClickListener mItemClickListener = null;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mMaxLine = a.getInteger(R.styleable.FlowLayout_maxLine, DEFAULT_LINE);
        if (mMaxLine != -1 && mMaxLine < 1) {
            throw new IllegalArgumentException("mMaxLine can not be less than 1.");
        }
        mHorizontalMargin = (int) a.getDimension(R.styleable.FlowLayout_itemHorizontalMargin, DEFAULT_HORIZONTAL_MARGIN);
        mVerticalMargin = (int) a.getDimension(R.styleable.FlowLayout_itemVerticalMargin, DEFAULT_VERTICAL_MARGIN);
        mTextMaxLength = a.getInt(R.styleable.FlowLayout_textMaxLength, DEFAULT_TEXT_MAX_LENGTH);
        if (mTextMaxLength != DEFAULT_TEXT_MAX_LENGTH && mTextMaxLength < 0) {
            throw new IllegalArgumentException("mTextMaxLength must be more than 0.");
        }
        mTextColor = a.getColor(R.styleable.FlowLayout_textColor, getResources().getColor(R.color.textGrey));
        mBorderColor = a.getColor(R.styleable.FlowLayout_borderColor, getResources().getColor(R.color.textGrey));
        mBorderRadius = a.getDimension(R.styleable.FlowLayout_borderRadius, DEFAULT_BORDER_RADIUS);
        Log.d(TAG, "mMaxLines == > " + mMaxLine);
        Log.d(TAG, "mHorizontalMargin == > " + mHorizontalMargin);
        Log.d(TAG, "mVerticalMargin == > " + mVerticalMargin);
        Log.d(TAG, "mTextMaxLength == > " + mTextMaxLength);
        Log.d(TAG, "mTextColor == > " + mTextColor);
        Log.d(TAG, "mBorderColor == > " + mBorderColor);
        Log.d(TAG, "mBorderRadius == > " + mBorderRadius);
        a.recycle();

    }

    public void setTextList(List<String> data) {
        this.mData.clear();
        this.mData.addAll(data);
        //根据数据创建子View,并且添加进来
        setUpChildren();
    }

    private void setUpChildren() {
        //清空原有的内容
        removeAllViews();
        //添加子View进来
        for (String datum : mData) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_flow_text, this, false);
            if (mTextMaxLength != DEFAULT_TEXT_MAX_LENGTH) {
                //设置TextView的最大长度
                textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTextMaxLength)});
            }
            textView.setText(datum);
            //todo:设置TextView相关属性:边距，颜色...
            final String tempData = datum;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClickListener(v, tempData);
                    }
                }
            });
            addView(textView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View v, String text);
    }

    private List<List<View>> mLines = new ArrayList<>();

    /**
     * 这俩个值来自于父控件，包含值和模式
     * int 类型==>4个字节==>4*8 bit==>32位
     * 0==>0
     * 1==>1
     * 2==>10
     * 3==>11
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "mode ==>" + mode);
        Log.d(TAG, " ==>" + parentWidthSize);
        Log.d(TAG, "widthMeasureSpec==>" + MeasureSpec.AT_MOST);
        Log.d(TAG, "EXACTLY==>" + MeasureSpec.EXACTLY);
        Log.d(TAG, "UNSPECIFIED==>" + MeasureSpec.UNSPECIFIED);
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        //先清空
        mLines.clear();
        //添加默认行
        List<View> line = new ArrayList<>();
        mLines.add(line);
        int childWidthSpace = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.AT_MOST);
        int childHeightSpace = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.AT_MOST);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != VISIBLE) {
                continue;
            }
            //测量孩子
            measureChild(child, childWidthSpace, childHeightSpace);
            if (line.size() == 0) {
                //可以添加
                line.add(child);
            } else {
                //判断是否可以添加到当前行
                boolean canBeAdd = checkChildCanBeAdd(line, child, parentWidthSize);
                if (!canBeAdd) {
                    if (mMaxLine != -1 && mLines.size() >= mMaxLine) {
                        //跳出循环，不再添加
                        break;
                    }
                    line = new ArrayList<>();
                    mLines.add(line);
                }
                line.add(child);
            }
            //拿到孩子的尺寸

        }
        //根据尺寸计算所有行高
        View child = getChildAt(0);
        int childHeight = child.getMeasuredHeight();
        int parentHeightTargetSize = childHeight * mLines.size()
                + (mLines.size() + 1) * mVerticalMargin
                + getPaddingTop()
                + getPaddingBottom();
        //测量自己
        setMeasuredDimension(parentWidthSize, parentHeightTargetSize);
    }

    private boolean checkChildCanBeAdd(List<View> line, View child, int parentWidthSize) {
        int measuredWidth = child.getMeasuredWidth();
        int totalWidth = mHorizontalMargin + getPaddingLeft();
        for (View view : line) {
            totalWidth += view.getMeasuredWidth() + mHorizontalMargin;
        }
        totalWidth += measuredWidth + mHorizontalMargin + getPaddingRight();
        //如果超出限制宽度，则不可以再添加
        //否则可以添加
        return totalWidth <= parentWidthSize;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        View child = getChildAt(0);
//        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
//        View child1 = getChildAt(1);
//        Log.d(TAG, "child get left==>"+child.getRight());
//        child1.layout(child.getRight(), 0, child.getRight() + child1.getMeasuredWidth(), child1.getMeasuredHeight());
        View firstChild = getChildAt(0);
        int currentLeft = mHorizontalMargin + getPaddingLeft();
        int currentTop = mVerticalMargin + getPaddingTop();
        int currentRight = mHorizontalMargin + getPaddingLeft();
        int currentBottom = firstChild.getMeasuredHeight() + mVerticalMargin + getPaddingTop();
        for (List<View> line : mLines) {
            for (View view : line) {
                //布局每一行
                int width = view.getMeasuredWidth();
                currentRight += width;
                //判断最右边边界
                if (currentRight > getMeasuredWidth() - mHorizontalMargin) {
                    currentRight = getMeasuredWidth() - mHorizontalMargin;
                }
                view.layout(currentLeft, currentTop, currentRight, currentBottom);
                currentLeft = currentRight + mHorizontalMargin;
                currentRight += mHorizontalMargin;
            }
            currentLeft = mHorizontalMargin + getPaddingLeft();
            currentRight = mHorizontalMargin + getPaddingLeft();
            currentBottom += firstChild.getMeasuredHeight() + mVerticalMargin;
            currentTop += firstChild.getMeasuredHeight() + mVerticalMargin;
        }
    }

    public int getMaxLine() {
        return mMaxLine;
    }

    public void setMaxLine(int maxLine) {
        mMaxLine = maxLine;
    }

    public float getHorizontalMargin() {
        return mHorizontalMargin;
    }

    public void setHorizontalMargin(int horizontalMargin) {
        mHorizontalMargin = SizeUtils.dip2px(horizontalMargin);
    }

    public float getVerticalMargin() {
        return mVerticalMargin;
    }

    public void setVerticalMargin(int verticalMargin) {
        mVerticalMargin = SizeUtils.dip2px(verticalMargin);
    }

    public int getTextMaxLength() {
        return mTextMaxLength;
    }

    public void setTextMaxLength(int textMaxLength) {
        mTextMaxLength = textMaxLength;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    public float getBorderRadius() {
        return mBorderRadius;
    }

    public void setBorderRadius(float borderRadius) {
        mBorderRadius = borderRadius;
    }
}
