package com.example.customview.customview.utils;

import android.content.Context;

import com.example.customview.App;

public class SizeUtils {

    public static int dip2px(float dpValue) {
        float scale = App.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
