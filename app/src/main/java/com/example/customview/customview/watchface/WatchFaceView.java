package com.example.customview.customview.watchface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.customview.R;
import com.example.customview.customview.utils.SizeUtils;

import java.util.Calendar;
import java.util.TimeZone;

public class WatchFaceView extends View {

    private static final String TAG = "WatchFaceView";
    private int mSecondColor;
    private int mMinColor;
    private int mHourColor;
    private int mScaleColor;
    private int mBgResId;
    private boolean mIsScaleShow;
    private Paint mSecondPaint;
    private Paint mMinPaint;
    private Paint mHourPaint;
    private Paint mScalePaint;
    private Bitmap mBackgroundImage = null;
    private int mWidth;
    private int mHeight;
    private Rect mSrcRect;
    private Rect mDesRect;
    private Calendar mCalendar;
    private final int mInnerCircleRadius = SizeUtils.dip2px(5);

    public WatchFaceView(Context context) {
        this(context, null);
    }

    public WatchFaceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WatchFaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取相关属性
        initAttrs(context, attrs);
        //拿到日历实例
        mCalendar = Calendar.getInstance();
        //设置时区
        mCalendar.setTimeZone(TimeZone.getDefault());
        //创建画笔
        initPaints();
    }


    /**
     * 创建画笔
     */
    private void initPaints() {
        //秒针
        mSecondPaint = new Paint();
        mSecondPaint.setColor(mSecondColor);
        mSecondPaint.setStyle(Paint.Style.STROKE);
        mSecondPaint.setStrokeWidth(5f);
        mSecondPaint.setStrokeCap(Paint.Cap.ROUND);
        mSecondPaint.setAntiAlias(true);//抗锯齿
        //分针
        mMinPaint = new Paint();
        mMinPaint.setColor(mMinColor);
        mMinPaint.setStyle(Paint.Style.STROKE);
        mMinPaint.setStrokeWidth(10f);
        mMinPaint.setStrokeCap(Paint.Cap.ROUND);
        mMinPaint.setAntiAlias(true);//抗锯齿
        //时针
        mHourPaint = new Paint();
        mHourPaint.setColor(mHourColor);
        mHourPaint.setStyle(Paint.Style.STROKE);
        mHourPaint.setStrokeWidth(15f);
        mHourPaint.setStrokeCap(Paint.Cap.ROUND);
        mHourPaint.setAntiAlias(true);//抗锯齿
        //刻度
        mScalePaint = new Paint();
        mScalePaint.setColor(mScaleColor);
        mScalePaint.setStyle(Paint.Style.STROKE);
        mScalePaint.setStrokeWidth(5f);
        mScalePaint.setStrokeCap(Paint.Cap.ROUND);
        mScalePaint.setAntiAlias(true);//抗锯齿
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WatchFace);
        mSecondColor = a.getColor(R.styleable.WatchFace_secondColor, getResources().getColor(R.color.secondDefaultColor));
        mMinColor = a.getColor(R.styleable.WatchFace_minColor, getResources().getColor(R.color.minDefaultColor));
        mHourColor = a.getColor(R.styleable.WatchFace_hourColor, getResources().getColor(R.color.hourDefaultColor));
        mScaleColor = a.getColor(R.styleable.WatchFace_scaleColor, getResources().getColor(R.color.scaleDefaultColor));
        mBgResId = a.getResourceId(R.styleable.WatchFace_watchFaceBackground, -1);
        mIsScaleShow = a.getBoolean(R.styleable.WatchFace_scaleShow, true);
        if (mBgResId != -1) {
            mBackgroundImage = BitmapFactory.decodeResource(getResources(), mBgResId);
        }
        a.recycle();
    }

    private boolean isUpdate = false;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isUpdate = true;
        post(new Runnable() {
            @Override
            public void run() {
                if (isUpdate) {
                    invalidate();
                    postDelayed(this, 1000);
                } else {
                    removeCallbacks(this);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isUpdate = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量自己
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "widthMode==" + widthMode);
        Log.d(TAG, "widthSize==" + widthSize);
        Log.d(TAG, "heightMode==" + heightMode);
        Log.d(TAG, "heightSize==" + heightSize);
        //减去外边距
        int widthTargetSize = widthSize - getPaddingLeft() + getPaddingRight();
        int heightTargetSize = heightSize - getPaddingTop() - getPaddingBottom();
        //判断大小，取小的值
        int targetSize = Math.min(widthTargetSize, heightTargetSize);
        setMeasuredDimension(targetSize, targetSize);
        //初始化Rect
        initRect();
    }

    private void initRect() {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (mBackgroundImage == null) {
            Log.d(TAG, "mBackgroundImage is null ...");
            return;
        }
        //源坑-->从图片中截取，如果跟图片大小一样，那么就截取图片所有内容
        mSrcRect = new Rect();
        mSrcRect.left = 0;
        mSrcRect.top = 0;
        mSrcRect.right = mBackgroundImage.getWidth();
        mSrcRect.bottom = mBackgroundImage.getHeight();
        //目标坑-->要填放源内容的地方
        mDesRect = new Rect();
        mDesRect.left = 0;
        mDesRect.top = 0;
        mDesRect.right = mWidth;
        mDesRect.bottom = mHeight;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        long currentTimeMillis = System.currentTimeMillis();
        mCalendar.setTimeInMillis(currentTimeMillis);
        int radius = (int) (mWidth / 2f);
        canvas.drawColor(Color.parseColor("#000000"));
        //绘制刻度
        drawScale(canvas);
        int secondValue = mCalendar.get(Calendar.SECOND);
        if (secondValue == 0) {
            //秒针
            drawSecondLine(canvas, radius);
            //分针
            drawMinLine(canvas, radius);
            //时针
            drawHourLine(canvas, radius);
        } else {
            //时针
            drawHourLine(canvas, radius);
            //分针
            drawMinLine(canvas, radius);
            //秒针
            drawSecondLine(canvas, radius);
        }
    }

    private void drawSecondLine(Canvas canvas, int radius) {
        canvas.save();
        //旋转
        int secondRadius = (int) (radius * 0.75f);
        int secondValue = mCalendar.get(Calendar.SECOND);
        float secondRotate = secondValue * 6f;
        canvas.rotate(secondRotate, radius, radius);
        canvas.drawLine(radius, radius - secondRadius, radius, radius - mInnerCircleRadius, mSecondPaint);
        canvas.restore();
    }

    private void drawMinLine(Canvas canvas, int radius) {
        canvas.save();
        int minRadius = (int) (radius * 0.60f);
        int minValue = mCalendar.get(Calendar.MINUTE);
        float minRotate = minValue * 6f;
        canvas.rotate(minRotate, radius, radius);
        canvas.drawLine(radius, radius - minRadius, radius, radius - mInnerCircleRadius, mMinPaint);
        canvas.restore();
    }

    private void drawHourLine(Canvas canvas, int radius) {
        //时针
        int hourValue = mCalendar.get(Calendar.HOUR);
        int hourRadius = (int) (radius * 0.45f);
        float hourOffsetRotate = mCalendar.get(Calendar.MINUTE) / 2f;
        float hourRotate = hourValue * 30 + hourOffsetRotate;
        //求旋转角度
        canvas.save();
        canvas.rotate(hourRotate, radius, radius);
        canvas.drawLine(radius, radius - hourRadius, radius, radius - mInnerCircleRadius, mHourPaint);
        canvas.restore();
    }

    private void drawScale(Canvas canvas) {
        if (mBackgroundImage != null) {
            canvas.drawBitmap(mBackgroundImage, mSrcRect, mDesRect, mScalePaint);
        } else {
            //半径
            int radius = (int) (mWidth / 2f);
            //内环半径
            int innerC = (int) (mWidth / 2 * 0.85f);
            //外环半径
            int outerC = (int) (mWidth / 2 * 0.95f);
            //绘制刻度的方法一
            //for (int i = 0; i < 12; i++) {
            //    double th = i * Math.PI * 2 / 12;
            //    //内环
            //    int innerB = (int) (Math.cos(th) * innerC);
            //    int innerX = mHeight / 2 - innerB;
            //    int innerA = (int) (innerC * Math.sin(th));
            //    int innerY = innerA + mWidth / 2;
            //    //外环
            //    int outerB = (int) (Math.cos(th) * outerC);
            //    int outerX = mHeight / 2 - outerB;
            //    int outerA = (int) (outerC * Math.sin(th));
            //    int outerY = outerA + mWidth / 2;
            //    canvas.drawLine(innerX, innerY, outerX, outerY, mScalePaint);
            //}
            //绘制刻度的方法二
            canvas.drawCircle(radius, radius, mInnerCircleRadius, mScalePaint);
            canvas.save();
            for (int i = 0; i < 12; i++) {
                canvas.drawLine(radius, radius - outerC, radius, radius - innerC, mScalePaint);
                canvas.rotate(30, radius, radius);
            }
            canvas.restore();
        }
    }
}
