package me.gavin.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.Date;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2018/3/23
 */
public class ICalendar extends View {

    public static final int MODE_MONTH = 0; // 月模式
    public static final int MODE_WEEK = 1; // 周模式

    public static final int SCROLL_NONE = 0; // 水平滑动
    public static final int SCROLL_HORIZONTAL = 1; // 水平滑动
    public static final int SCROLL_VERTICAL = 2; // 竖直滑动

    private int mWidth, mHeight;
    private float mCellWidth, mCellHeight;
    private float mDiffY;

    private final Paint mTextPaint, mSPaint;
    private final Paint mDebugPaint;

    private Scroller mScroller;

    private Date mToday;
    private Date mSelectedDate;
    private int mMode;
    private int mScrollOrientation;
    private int closeHeigth;

    private DateData mData;

    private OnMonthSelectedListener mOnMonthSelectedListener;

    public ICalendar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xfff5f5f5);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));

        mSPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSPaint.setStyle(Paint.Style.FILL);

        mDebugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDebugPaint.setColor((int) (Math.random() * 0xFFFFFF) + 0xFF000000);

        mScroller = new Scroller(context);

        mToday = new Date();
        mSelectedDate = mToday;
        mData = DateData.get(mSelectedDate, mToday);
//        mSelectedDate = new Date(148002000000L);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        mCellWidth = mWidth / 7f;
        mCellHeight = mCellWidth * 0.9f;

        RectF targetRect = new RectF(0, 0, mCellWidth, mCellHeight);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        float baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2f;
        mDiffY = baseline - mCellHeight / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mData == null) return;
        drawMonth(canvas, -1);
        drawMonth(canvas, 0);
        drawMonth(canvas, 1);
    }

    private void drawMonth(Canvas canvas, int offset) {
        DateData.Month month = mData.months.get(offset + 1);
        for (int i = 0; i < month.weeks.size(); i++) {
            drawWeek(canvas, month.weeks.get(i), offset, mCellHeight * i + mCellHeight / 2);
        }
    }

    private void drawWeek(Canvas canvas, DateData.Week week, int offset, float y) {
        for (int i = 0; i < week.days.size(); i++) {
            drawDay(canvas, week.days.get(i), mCellWidth * i + mCellWidth / 2f + offset * mWidth, y);
        }
    }

    private void drawDay(Canvas canvas, DateData.Day day, float x, float y) {
        // 非当月
        if (day.different) return;

        if (day.today) {
            mSPaint.setColor(0xB0CC44AA);
            canvas.drawCircle(x, y, 42, mSPaint);
        } else if (day.selected) {
            mSPaint.setColor(0x80FFFFFF);
            canvas.drawCircle(x, y, 42, mSPaint);
        }
        canvas.drawText(String.valueOf(day.day), x, y + mDiffY, mTextPaint);
    }

    private float mLastX, mLastY;
    private VelocityTracker mVelocityTracker;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO: 2018/3/27  event.getPointerCount() > 1
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);
                mScroller.forceFinished(true);
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                setScrollX(getScrollX() + (int) (mLastX - event.getX()));
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xv = mVelocityTracker.getXVelocity();
                int minFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
                if (Math.abs(xv) < minFlingVelocity) {
                    smoothScrollBy(-getScrollX(), 0, 500);
                } else if (xv > 0) {
                    smoothScrollBy((-mWidth - getScrollX()) % mWidth, 0, 500);
                } else {
                    smoothScrollBy((mWidth - getScrollX()) % mWidth, 0, 500);
                }
                mVelocityTracker.recycle();
                break;
        }
        return true;
    }

    public void smoothScrollBy(int dx, int dy, int duration) {
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset() && mScroller.isFinished()) { // 复位结束
            // TODO: 2018/3/27  复位结束
            if (getScrollX() != 0) {
                mSelectedDate = Utils.offsetMonth(mSelectedDate, getScrollX() > 0 ? 1 : -1);
                mData = DateData.get(mSelectedDate, mToday);
                if (mOnMonthSelectedListener != null) {
                    mOnMonthSelectedListener.accept(Utils.getYear(mSelectedDate), Utils.getMonth(mSelectedDate));
                }
            }
            scrollTo(0, 0);
            postInvalidate();
        }
        if (mScroller.computeScrollOffset()) { // 复位中
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void setOnMonthSelectedListener(OnMonthSelectedListener onMonthSelectedListener) {
        this.mOnMonthSelectedListener = onMonthSelectedListener;
    }
}
