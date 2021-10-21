package com.example.customview;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customview.customview.keypad.KeypadView;

// implements InputNumberView.OnNumberChangeListener
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KeypadView keypadView = this.findViewById(R.id.key_pad_view);
        keypadView.setOnKeyClickListener(new KeypadView.OnKeyClickListener() {
            @Override
            public void onNumberClick(int value) {
                Log.d(TAG, "click number is ==> " + value);
            }

            @Override
            public void onDeleteClick() {
                Log.d(TAG, "click delete...");
            }
        });


//        InputNumberView inputNumberView = this.findViewById(R.id.input_number_view);
//        inputNumberView.setOnNumberChangeListener(this);

//        FlowLayout页面
//        FlowLayout flowLayout = this.findViewById(R.id.flow_layout);
//        List<String> data = new ArrayList<>();
//        data.add("键盘");
//        data.add("显示器");
//        data.add("鼠标");
//        data.add("iPad");
//        data.add("air pod");
//        data.add("Android手机");
//        data.add("mac pro");
//        data.add("耳机");
//        data.add("春夏秋冬超帅装");
//        data.add("春夏秋冬超帅装春夏秋冬超帅装春夏秋冬超帅装春夏秋冬超帅装春夏秋冬超帅装春夏秋冬超帅装");
//        data.add("男鞋");
//        data.add("女装");
//        flowLayout.setTextList(data);
//        flowLayout.setOnItemClickListener(new FlowLayout.OnItemClickListener() {
//            @Override
//            public void onItemClickListener(View v, String text) {
//                Log.d(TAG, "setOnItemClickListener==>" + text);
//            }
//        });
//        flowLayout.setMaxLine(4);
    }

//    login页面
//    public void toLoginPage(View view)
//    {
//        //点击跳转到登录页面
//        Intent intent=new Intent(this,LoginActivity.class);
//        startActivity(intent);
//    }

//    @Override
//    public void onNumberChange(int value) {
//        Log.d(TAG, "current value is==>" + value);
//    }
//
//    @Override
//    public void onNumberMax(int maxValue) {
//        Log.d(TAG, "maxValue==>" + maxValue);
//    }
//
//    @Override
//    public void onNumberMin(int minValue) {
//        Log.d(TAG, "minValue==>" + minValue);
//    }
}