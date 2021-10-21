package com.example.customview.customview.numberinput;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.customview.R;

public class InputNumberView extends LinearLayout {
    private static final String TAG = "InputNumberView";
    private int mCurrentNumber = 0;
    private View mMinusBtn;
    private EditText mValueEdt;
    private View mPlusBtn;
    private OnNumberChangeListener mOnNumberChangeListener = null;
    private int mMax;
    private int mMin;
    private int mStep;
    private int mDefaultValue;
    private boolean mDisable;
    private int mBtnBgRes;

    public InputNumberView(Context context) {
        this(context, null);
    }

    public InputNumberView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取相关属性
        initAttrs(context, attrs);
        initView(context);
        //设置事件
        setUpEvent();
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InputNumberView);
        mMax = a.getInt(R.styleable.InputNumberView_max, 100);
        mMin = a.getInt(R.styleable.InputNumberView_min, 0);
        mStep = a.getInt(R.styleable.InputNumberView_step, 1);
        mDefaultValue = a.getInt(R.styleable.InputNumberView_defaultValue, 0);
        this.mCurrentNumber = mDefaultValue;

        mDisable = a.getBoolean(R.styleable.InputNumberView_disable, false);
        mBtnBgRes = a.getResourceId(R.styleable.InputNumberView_btnBackground, -1);
        Log.d(TAG, "mMax==>" + mMax);
        Log.d(TAG, "mMin==>" + mMin);
        Log.d(TAG, "mStep==>" + mStep);
        Log.d(TAG, "mDefaultValue==>" + mDefaultValue);
        Log.d(TAG, "mDisable==>" + mDisable);
        Log.d(TAG, "mBtnBgRes==>" + mBtnBgRes);

        //
        a.recycle();
    }

    private void setUpEvent() {
        mMinusBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlusBtn.setEnabled(true);
                mCurrentNumber -= mStep;
                if (mMax != 0 && mCurrentNumber <= mMin) {
                    v.setEnabled(false);
                    mCurrentNumber = mMin;
                    Log.d(TAG, "current is min value...");
                    if (mOnNumberChangeListener != null) {
                        mOnNumberChangeListener.onNumberMin(mMin);
                    }
                }
                updateText();
            }
        });

        mPlusBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMinusBtn.setEnabled(true);
                mCurrentNumber += mStep;
                if (mMax != 0 && mCurrentNumber >= mMax) {
                    v.setEnabled(false);
                    mCurrentNumber = mMax;
                    Log.d(TAG, "current is max value ...");
                    if (mOnNumberChangeListener != null) {
                        mOnNumberChangeListener.onNumberMax(mMax);
                    }
                }
                updateText();
            }
        });
    }

    private void initView(Context context) {
        //以下代码等价
        //LayoutInflater.from(context).inflate(R.layout.input_number_view, this, true);
        //
        //LayoutInflater.from(context).inflate(R.layout.input_number_view, this);
        //
        View view = LayoutInflater.from(context).inflate(R.layout.input_number_view, this, false);
        addView(view);
        //以上代码功能一样，都是把view添加当当前容器里
        mMinusBtn = this.findViewById(R.id.minus_btn);
        mValueEdt = this.findViewById(R.id.value_edt);
        mPlusBtn = this.findViewById(R.id.plus_btn);
        //初始化控件值
        updateText();
        mMinusBtn.setEnabled(!mDisable);
        mPlusBtn.setEnabled(!mDisable);
    }

    public int getNumber() {
        return mCurrentNumber;
    }

    public void setNumber(int value) {
        mCurrentNumber = value;
        this.updateText();
    }


    public View getMinusBtn() {
        return mMinusBtn;
    }

    public void setMinusBtn(View minusBtn) {
        mMinusBtn = minusBtn;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        mMin = min;
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        mStep = step;
    }

    public int getDefaultValue() {
        return mDefaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        mDefaultValue = defaultValue;
        this.mCurrentNumber = defaultValue;
        this.updateText();
    }

    public boolean isDisable() {
        return mDisable;
    }

    public void setDisable(boolean disable) {
        mDisable = disable;
    }

    public int getBtnBgRes() {
        return mBtnBgRes;
    }

    public void setBtnBgRes(int btnBgRes) {
        mBtnBgRes = btnBgRes;
    }

    /**
     * 更新数据
     */
    private void updateText() {
        mValueEdt.setText(String.valueOf(mCurrentNumber));
        if (mOnNumberChangeListener != null) {
            mOnNumberChangeListener.onNumberChange(this.mCurrentNumber);
        }
    }

    public void setOnNumberChangeListener(OnNumberChangeListener listener) {
        this.mOnNumberChangeListener = listener;
    }

    public interface OnNumberChangeListener {
        void onNumberChange(int value);

        void onNumberMax(int maxValue);

        void onNumberMin(int minValue);

    }

}
