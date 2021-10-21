package com.example.customview;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customview.customview.slide.SlideMenuView;

public class SlideMenuActivity extends AppCompatActivity {

    private static final String TAG = "SlideMenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        final SlideMenuView slideMenuView = this.findViewById(R.id.slide_view_item);
        slideMenuView.setOnEditClickListener(new SlideMenuView.OnEditClickListener() {
            @Override
            public void onReadClick() {
                Log.d(TAG, "onReadClick...");
                Log.d(TAG, "slideMenuView ==> is open" + slideMenuView.isOpen());
            }

            @Override
            public void onTopClick() {
                Log.d(TAG, "onTopClick...");
            }

            @Override
            public void onDeleteClick() {
                Log.d(TAG, "onDeleteClick...");
            }
        });
    }
}