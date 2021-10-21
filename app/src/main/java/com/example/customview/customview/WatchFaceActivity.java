package com.example.customview.customview;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.customview.R;

public class WatchFaceActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_face);
    }
}
