package com.example.customview.customview.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.customview.App;
import com.example.customview.R;
import com.example.customview.customview.keypad.KeypadView;

import java.lang.reflect.Field;

/**
 * 点击获取验证码--> 条件：手机号码是正确
 * 点击登录--> 条件：正确的手机号+验证码+同意协议
 */
public class LoginPageView extends FrameLayout {
    public static final int SIZE_VERITY_CODE_DEFAULT = 4;
    private static final String TAG = "LoginPageView";
    private int mColor;
    private int mVerifyCodeSize = SIZE_VERITY_CODE_DEFAULT;
    private CheckBox mIsConfirm;
    private EditText mVerityCodeInp;
    private OnLoginPageActionListener mActionListener = null;
    //    private LoginKeyboardView mLoginKeyboardView;
    private KeypadView mLoginKeyboardView;
    private EditText mPhoneNumInp;
    private TextView mGetVerifyCodeBtn;
    private Boolean isPhoneNumOk = false;
    private Boolean isAgreementOk = false;
    private Boolean isVerifyCodeOk = false;
    //手机号码的规则
    public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";
    private View mLoginBtn;
    /**
     * 关注点：
     * 倒计时多少时间：duration
     * 时间间隔：dt
     * 通知UI更新
     */
    private static final int DURATION_DEFAULT = 60 * 1000;
    private static final int D_TIME_DEFAULT = 1000;
    //ms
    private int mCountDownDuration = DURATION_DEFAULT;
    private int restTime = mCountDownDuration;
    private Handler mHandler;
    private boolean isCountDowning = false;
    private CountDownTimer mCountDownTimer;
    private TextView mAgreementView;

    public void startCountDown() {
        //final 确保内外一致 or 设置成员变量
        mHandler = App.getHandler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                restTime = D_TIME_DEFAULT;
                Log.d(TAG, "rest==>" + restTime);

                if (restTime > 0) {
                    mHandler.postDelayed(this, D_TIME_DEFAULT);
                    //直接更新UI
                    mGetVerifyCodeBtn.setText("(" + restTime / 1000 + ")秒");
                    mGetVerifyCodeBtn.setEnabled(false);
                    isCountDowning = true;
                } else {
                    restTime = mCountDownDuration;
                    isCountDowning = false;
                    mGetVerifyCodeBtn.setEnabled(true);
                    mGetVerifyCodeBtn.setText("获取验证码");
                    updateAllBtnState();
                }
            }
        });
    }

    private void beginCountDown() {
        isCountDowning = true;
        mGetVerifyCodeBtn.setEnabled(false);
        mCountDownTimer = new CountDownTimer(mCountDownDuration, D_TIME_DEFAULT) {

            @Override
            public void onTick(long millisUntilFinished) {
                //通知UI更新
                int rest = (int) (millisUntilFinished / 1000);
                mGetVerifyCodeBtn.setText("(" + rest + ")秒");
            }

            @Override
            public void onFinish() {
                //倒计时结束
                isCountDowning = false;
                mGetVerifyCodeBtn.setEnabled(true);
                mGetVerifyCodeBtn.setText("获取验证码");
                updateAllBtnState();
                mCountDownTimer = null;
            }
        }.start();
    }

    /**
     * 验证码错误
     */
    public void onVerifyCodeError() {
        //第一步:清空验证码输入框中的内容
        mVerityCodeInp.getText().clear();
        //第二步:停止倒计时
        if (isCountDowning && mCountDownTimer != null) {
            isCountDowning = false;
            mCountDownTimer.cancel();
            mCountDownTimer.onFinish();
        }
    }

    /**
     * 登录成功
     */
    public void onSuccess() {
        Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
    }

    public LoginPageView(@NonNull Context context) {
        this(context, null);
    }

    public LoginPageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginPageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化属性
        initAttrs(context, attrs);
        //初始化控件
        initView();
        disableEdtFocus2KeyPad();
        initEvent();
    }

    private void disableEdtFocus2KeyPad() {
        mPhoneNumInp.setShowSoftInputOnFocus(false);
        mVerityCodeInp.setShowSoftInputOnFocus(false);
    }

    private void initEvent() {
        mAgreementView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mAgreementView--->click...");
            }
        });
        mIsConfirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAgreementOk = isChecked;
                updateAllBtnState();
            }
        });
        mLoginKeyboardView.setOnKeyClickListener(new KeypadView.OnKeyClickListener() {
            @Override
            public void onNumberClick(int value) {
                //数字被点击
                //插入数字
                EditText focusEdt = getFocusEdt();
                if (focusEdt != null) {
                    Editable text = focusEdt.getText();
                    int index = focusEdt.getSelectionEnd();
                    Log.d(TAG, "index is " + index);
                    text.insert(index, String.valueOf(value));
                }
            }

            @Override
            public void onDeleteClick() {
                //退格键被点击
                EditText focusEdt = getFocusEdt();
                if (focusEdt != null) {
                    int index = focusEdt.getSelectionEnd();
                    Log.d(TAG, "index is " + index);
                    Editable editable = focusEdt.getText();
                    if (index > 0) {
                        editable.delete(index - 1, index);
                    }
                }
            }
        });
//        mLoginKeyboardView.setOnKeyPressListener(new LoginKeyboardView.OnKeyPressListener() {
//            @Override
//            public void onNumberPress(int number) {
//                //数字被点击
//                //插入数字
//                EditText focusEdt = getFocusEdt();
//                if (focusEdt != null) {
//                    Editable text = focusEdt.getText();
//                    int index = focusEdt.getSelectionEnd();
//                    Log.d(TAG, "index is " + index);
//                    text.insert(index, String.valueOf(number));
//                }
//            }
//
//            @Override
//            public void onBackPress() {
//                //退格键被点击
//                EditText focusEdt = getFocusEdt();
//                if (focusEdt != null) {
//                    int index = focusEdt.getSelectionEnd();
//                    Log.d(TAG, "index is " + index);
//                    Editable editable = focusEdt.getText();
//                    if (index > 0) {
//                        editable.delete(index - 1, index);
//                    }
//                }
//            }
//        });
        mGetVerifyCodeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:get verify code.
                if (mActionListener != null) {
                    //拿到手机号码
                    String phoneNum = mPhoneNumInp.getText().toString().trim();
                    Log.d(TAG, "phoneNum==>" + phoneNum);
                    mActionListener.onGetVerifyCodeClick(phoneNum);
                    //开启倒计时
                    //startCountDown();
                    beginCountDown();
                } else {
                    throw new IllegalArgumentException("no action to get verify code");
                }
            }
        });
        mVerityCodeInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isVerifyCodeOk = s.length() == 4;
                updateAllBtnState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPhoneNumInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //变化的时候区检查手机号码是否正确
                //todo:
                Log.d(TAG, "content-->" + s);
                String phoneNum = s.toString();
                boolean isMatch = phoneNum.matches(REGEX_MOBILE_EXACT);
                isPhoneNumOk = phoneNum.length() == 11 && isMatch;
                updateAllBtnState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:根据产品经理要求，是否点击登录之后禁止按钮，UI上防止重复提交
                //登录
                if (mActionListener != null) {
                    //拿到手机号码和验证码
                    mActionListener.onConfirmClick(getVerifyCode(), getPhoneNum());
                }
            }
        });
    }

    private String getPhoneNum() {
        return mPhoneNumInp.getText().toString().trim();
    }

    private String getVerifyCode() {
        return mVerityCodeInp.getText().toString().trim();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.login_page_view, this);
        mIsConfirm = this.findViewById(R.id.report_check_box);
        mVerityCodeInp = this.findViewById(R.id.verify_code_input_box);
        if (mColor != -1) {
            mIsConfirm.setTextColor(mColor);
        }
        mVerityCodeInp.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mVerifyCodeSize)});
        mLoginKeyboardView = this.findViewById(R.id.number_key_pad);
        mPhoneNumInp = this.findViewById(R.id.phone_num_input_box);
        mPhoneNumInp.requestFocus();
        mGetVerifyCodeBtn = this.findViewById(R.id.get_verify_code_btn);
        disableCopyAndPaste(mPhoneNumInp);
        disableCopyAndPaste(mVerityCodeInp);
        mLoginBtn = this.findViewById(R.id.login_btn);
        mAgreementView = this.findViewById(R.id.agreement_tips);
        this.updateAllBtnState();
    }

    private void initAttrs(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoginPageView);
        mColor = a.getColor(R.styleable.LoginPageView_mainColor, -1);
        mVerifyCodeSize = a.getInt(R.styleable.LoginPageView_verifyCodeSize, SIZE_VERITY_CODE_DEFAULT);
        mCountDownDuration = a.getInt(R.styleable.LoginPageView_countDownDuration, DURATION_DEFAULT);
        a.recycle();
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    private void updateAllBtnState() {
        if (!isCountDowning) {
            mGetVerifyCodeBtn.setEnabled(isPhoneNumOk);
        }
        mLoginBtn.setEnabled(isPhoneNumOk && isAgreementOk && isVerifyCodeOk);
        //修改文字颜色
        mAgreementView.setTextColor(isAgreementOk ? getResources().getColor(R.color.mainColor) :
                getResources().getColor(R.color.mainDeepColor));
    }

    /**
     * 获取当前有聚焦的输入框
     * <p>
     * 使用要判空
     * </p>
     *
     * @return null or editText instance
     */
    private EditText getFocusEdt() {
        View view = this.findFocus();
        if (view instanceof EditText) {
            return (EditText) view;
        }
        return null;
    }

    public int getVerifyCodeSize() {
        return mVerifyCodeSize;
    }

    public void setVerifyCodeSize(int verifyCodeSize) {
        mVerifyCodeSize = verifyCodeSize;
    }

    public void setOnLoginPageActionListener(OnLoginPageActionListener listener) {
        this.mActionListener = listener;
    }

    public interface OnLoginPageActionListener {
        void onGetVerifyCodeClick(String phoneNum);

        void onOpenAgreementClick();

        void onConfirmClick(String verifyCode, String phoneNum);
    }

    //Android开发如何禁止EditText选中复制粘贴
    @SuppressLint("ClickableViewAccessibility")
    public void disableCopyAndPaste(final EditText editText) {
        try {
            if (editText == null) {
                return;
            }

            editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            editText.setLongClickable(false);
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // setInsertionDisabled when user touches the view
                        setInsertionDisabled(editText);
                    }

                    return false;
                }
            });
            editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInsertionDisabled(EditText editText) {
        try {
            Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            Object editorObject = editorField.get(editText);

            // if this view supports insertion handles
            Class editorClass = Class.forName("android.widget.Editor");
            Field mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled");
            mInsertionControllerEnabledField.setAccessible(true);
            mInsertionControllerEnabledField.set(editorObject, false);

            // if this view supports selection handles
            Field mSelectionControllerEnabledField = editorClass.getDeclaredField("mSelectionControllerEnabled");
            mSelectionControllerEnabledField.setAccessible(true);
            mSelectionControllerEnabledField.set(editorObject, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCountDownDuration() {
        return mCountDownDuration;
    }

    public void setCountDownDuration(int countDownDuration) {
        mCountDownDuration = countDownDuration;
    }
}
