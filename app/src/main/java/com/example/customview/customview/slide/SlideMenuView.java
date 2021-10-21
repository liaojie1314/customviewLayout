package com.example.customview.customview.slide;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.customview.R;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class SlideMenuView extends ViewGroup implements View.OnClickListener {
    private static final String TAG = "SlideMenuView";
    private int mFunction;
    private View mContentView;
    private View mEditView;
    private OnEditClickListener mEditClickListener = null;
    private TextView mReadTv;
    private TextView mDeleteTv;
    private TextView mTopTv;
    private float mDownX;
    private float mDownY;
    private Scroller mScroller;
    private float mInterceptDownX;
    private float mInterceptDownY;

    public SlideMenuView(Context context) {
        this(context, null);
    }

    public SlideMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideMenuView);
        mFunction = a.getInt(R.styleable.SlideMenuView_function, 0x30);
        Log.d(TAG, "function ==> " + mFunction);
//        Log.d(TAG, Integer.toBinaryString(48));
//        Log.d(TAG, Integer.toBinaryString(80));
        a.recycle();
        mScroller = new Scroller(context);
    }

    //是否已经打开
    private boolean isOpen = false;
    private Direction mCurrentDirection = null;

    enum Direction {
        LEFT, RIGHT, NONE,
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //选择拦截
        switch (ev.getAction()) {
            case ACTION_DOWN:
                mInterceptDownX = ev.getX();
                mInterceptDownY = ev.getY();
                break;
            case ACTION_MOVE:
                float interceptMoveX = ev.getX();
                float interceptMoveY = ev.getY();
                if (Math.abs(interceptMoveX - mInterceptDownX) > 0) {
                    //自己销毁
                    return true;
                }
                break;
            case ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean isOpen() {
        return isOpen;
    }

    //mDuration 走完mEditView的4/5宽度
    private int mMaxDuration = 800;
    private int mMinDuration = 300;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                Log.d(TAG, "ACTION_DOWN...");
                break;
            case ACTION_MOVE:
                int scrollX = getScrollX();
                Log.d(TAG, "scrollX ==>" + scrollX);
                float moveX = event.getX();
                float moveY = event.getY();
                //移动的差值
                int dx = (int) (moveX - mDownX);
                if (dx > 0) {
                    mCurrentDirection = Direction.RIGHT;
                } else {
                    mCurrentDirection = Direction.LEFT;
                }
                //判断边界安全
                int resultScrollX = -dx + scrollX;
                if (resultScrollX < 0) {
                    scrollTo(0, 0);
                } else if (scrollX > mEditView.getMeasuredWidth()) {
                    scrollTo(mEditView.getMeasuredWidth(), 0);
                } else {
                    //使用差值
                    scrollBy(-dx, 0);
                }
                requestLayout();
                mDownX = moveX;
                mDownY = moveY;
                Log.d(TAG, "ACTION_MOVE...");
                break;
            case ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                //俩个关注点
                //是否已经打开
                //方向
                //处理释放后是显示还是收缩回去
                int hasBeenScrollX = getScrollX();
                int editViewWidth = mEditView.getMeasuredWidth();
                if (isOpen) {
                    //当前状态为打开
                    if (mCurrentDirection == Direction.RIGHT) {
                        //方向向右，如果小于3/4,那么就关闭
                        // 否则打开
                        if (hasBeenScrollX <= editViewWidth * 4 / 5) {
                            close();
                        } else {
                            open();
                        }
                    } else if (mCurrentDirection == Direction.LEFT) {
                        open();
                    }
                } else {
                    //当前状态为关闭
                    if (mCurrentDirection == Direction.LEFT) {
                        //向左滑动,判断滑动的距离
                        if (hasBeenScrollX > editViewWidth / 5) {
                            open();
                        } else {
                            close();
                        }
                    } else if (mCurrentDirection == Direction.RIGHT) {
                        //向右滑动
                        close();
                    }
                }
                Log.d(TAG, "ACTION_UP...");
                break;
        }
        return true;
    }

    public void open() {
        //显示
//      scrollTo(mEditView.getMeasuredWidth(),0);
        int dx = mEditView.getMeasuredWidth() - getScrollX();
        int duration = (int) (dx / (mEditView.getMeasuredWidth() * 4 / 5f) * mMaxDuration);
        int absDuration = Math.abs(duration);
        if (absDuration < mMinDuration) {
            absDuration = mMinDuration;
        }
        mScroller.startScroll(getScrollX(), 0, dx, 0, absDuration);
        invalidate();
        isOpen = true;
    }

    public void close() {
        //隐藏
//      scrollTo(0,0);
        int dx = -getScrollX();
        int duration = (int) (dx / (mEditView.getMeasuredWidth() * 4 / 5f) * mMaxDuration);
        int absDuration = Math.abs(duration);
        if (absDuration < mMinDuration) {
            absDuration = mMinDuration;
        }
        mScroller.startScroll(getScrollX(), 0, dx, 0, absDuration);
        invalidate();
        isOpen = false;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            Log.d(TAG, "currX == >" + currX);
            //滑动到指定位置
            scrollTo(currX, 0);
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        Log.d(TAG, "child count == >" + childCount);
        //加个判断，只能有一个子View
        if (childCount > 1) {
            throw new IllegalArgumentException("no more than one child.");
        }
        mContentView = getChildAt(0);
        //getChildAt(0)
        //根据属性继续添加子View
        mEditView = LayoutInflater.from(getContext()).inflate(R.layout.item_slide_action, this, false);
        initEditView();
        this.addView(mEditView);
        int afterCount = getChildCount();
        Log.d(TAG, "after child count == >" + afterCount);
    }

    private void initEditView() {
        mReadTv = mEditView.findViewById(R.id.read_text_tv);
        mDeleteTv = mEditView.findViewById(R.id.delete_tv);
        mTopTv = mEditView.findViewById(R.id.top_tv);
        mReadTv.setOnClickListener(this);
        mDeleteTv.setOnClickListener(this);
        mTopTv.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //测量第一个孩子，也就是内容部分
        //宽度跟父控件一样宽，高度有3种情况:如果指定大小，那我们获取他的大小，直接测量
        //如果包裹内容，at_most .如果是match_parent，那就给他大小
        LayoutParams contentLayoutParams = mContentView.getLayoutParams();
        int contentHeight = contentLayoutParams.height;
        Log.d(TAG, "contentHeight == >" + contentHeight);
        int contentHeightMeasureSpace;
        if (contentHeight == LayoutParams.MATCH_PARENT) {
            contentHeightMeasureSpace = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        } else if (contentHeight == LayoutParams.WRAP_CONTENT) {
            contentHeightMeasureSpace = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
        } else {
            //指定大小
            contentHeightMeasureSpace = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY);
        }
        mContentView.measure(widthMeasureSpec, contentHeightMeasureSpace);
        //拿到内容测量值
        int contentMeasuredHeight = mContentView.getMeasuredHeight();
        //测量编辑部分,宽度：3/4 高度跟内容高度一样
        int edtWidthSize = widthSize * 3 / 4;
        mEditView.measure(MeasureSpec.makeMeasureSpec(edtWidthSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(contentMeasuredHeight, MeasureSpec.EXACTLY));
        //测量自己
        //宽度就是前面宽度总和,高度和内容一样
        setMeasuredDimension(widthSize + edtWidthSize, contentMeasuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //摆放内容
        int mContentLeft = 0;
        int contentTop = 0;
        int contentRight = mContentLeft + mContentView.getMeasuredWidth();
        int contentBottom = contentTop + mContentView.getMeasuredHeight();
        mContentView.layout(mContentLeft, contentTop, contentRight, contentBottom);
        int edtViewLeft = contentRight;
        int edtViewTop = 0;
        int edtViewRight = edtViewLeft + mEditView.getMeasuredWidth();
        int edtViewBottom = edtViewTop + mEditView.getMeasuredHeight();
        //摆放编辑内容
        mEditView.layout(edtViewLeft, edtViewTop, edtViewRight, edtViewBottom);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.mEditClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mEditView == null) {
            Log.d(TAG, "mEditClickListener is null...");
        }
//        if (v == mReadTv) {
//            mEditClickListener.onReadClick();
//        } else if (v == mTopTv) {
//            mEditClickListener.onTopClick();
//        } else if (v == mDeleteTv) {
//            mEditClickListener.onDeleteClick();
//        }
        close();
        int viewId = v.getId();
        switch (viewId) {
            case R.id.read_text_tv:
                mEditClickListener.onReadClick();
                break;
            case R.id.top_tv:
                mEditClickListener.onTopClick();
                break;
            case R.id.delete_tv:
                mEditClickListener.onDeleteClick();
                break;
        }
    }

    /**
     * 动作的一个监听
     */
    public interface OnEditClickListener {
        void onReadClick();

        void onTopClick();

        void onDeleteClick();
    }
}
