package com.example.customview.customview.loginpage;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.customview.R;

public class LoginKeyboardView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "LoginKeyboard";
    private OnKeyPressListener mKeyPressListener = null;

    public LoginKeyboardView(Context context) {
        this(context, null);
    }

    public LoginKeyboardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginKeyboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        LayoutInflater.from(context).inflate(R.layout.num_key_pad, this);
        initView();
    }

    private void initView() {
        this.findViewById(R.id.num_1).setOnClickListener(this);
        this.findViewById(R.id.num_2).setOnClickListener(this);
        this.findViewById(R.id.num_3).setOnClickListener(this);
        this.findViewById(R.id.num_4).setOnClickListener(this);
        this.findViewById(R.id.num_5).setOnClickListener(this);
        this.findViewById(R.id.num_6).setOnClickListener(this);
        this.findViewById(R.id.num_7).setOnClickListener(this);
        this.findViewById(R.id.num_8).setOnClickListener(this);
        this.findViewById(R.id.num_9).setOnClickListener(this);
        this.findViewById(R.id.num_0).setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (mKeyPressListener == null) {
            Log.d(TAG, "mKeyPressListener == null need not callback...");
            return;
        }
        if (viewId == R.id.back) {
            //back
            mKeyPressListener.onBackPress();
        } else {
            //数字结果
            String text = ((TextView) v).getText().toString();
            Log.d(TAG, "click text-->" + text);
            mKeyPressListener.onNumberPress(Integer.parseInt(text));
        }
    }

    public void setOnKeyPressListener(OnKeyPressListener listener) {
        this.mKeyPressListener = listener;
    }

    public interface OnKeyPressListener {
        void onNumberPress(int number);

        void onBackPress();
    }
}
