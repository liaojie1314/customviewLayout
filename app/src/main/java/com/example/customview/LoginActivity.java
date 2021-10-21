package com.example.customview;


import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.customview.customview.loginpage.LoginPageView;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
            LoginPageView loginPageView = this.findViewById(R.id.login_page_view);
            loginPageView.setOnLoginPageActionListener(new LoginPageView.OnLoginPageActionListener() {
                @Override
                public void onGetVerifyCodeClick(String phoneNum) {
                    //todo:去获取验证码
                    Toast.makeText(LoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onOpenAgreementClick() {
                    //todo:打开协议页面
                }

                @Override
                public void onConfirmClick(String verifyCode, String phoneNum) {
                    //todo：登录
                    App.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //toast提示
                            Toast.makeText(LoginActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                            loginPageView.onVerifyCodeError();
                        }
                    },4000);
                }
            });
    }
}
