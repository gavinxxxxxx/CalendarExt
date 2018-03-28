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

import me.gavin.widget.calendar.L;

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

    private final Paint mTextPaint;
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

        mDebugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDebugPaint.setColor((int) (Math.random() * 0xFFFFFF) + 0xFF000000);

        mScroller = new Scroller(context);

        mToday = new Date();
        mSelectedDate = mToday;
//        mSelectedDate = new Date(148002000000L);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        mCellWidth = mWidth / 7f;
        mCellHeight = mCellWidth * 0.9f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        L.e(DateData.get(mSelectedDate));

        drawMonth(canvas, mSelectedDate, 0);
        drawMonth(canvas, Utils.offsetMonth(mSelectedDate, -1), -mWidth);
        drawMonth(canvas, Utils.offsetMonth(mSelectedDate, 1), mWidth);
    }

    private void drawMonth(Canvas canvas, int offset) {
        DateData.Month month = mData.months.get(offset + 1);
        for (int i = 0; i < month.weeks.size(); i++) {

        }
        for (DateData.Week t : mData.months.get(offset + 1).weeks) {

        }
    }

    private void drawDay(Canvas canvas, DateData.Day day, float x, float y) {
        RectF targetRect = new RectF(x - mCellWidth / 2 + 2, y - mCellHeight / 2 + 2,
                x + mCellWidth / 2 - 2, y + mCellHeight / 2 - 2);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        float baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2f;
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(day.day), targetRect.centerX(), baseline, mTextPaint);
    }

    private void drawMonth(Canvas canvas, Date date, int offset) {
        int year = Utils.getYear(date);
        int month = Utils.getMonth(date);
        int daySum = Utils.getMaxDayOfMonth(year, month);
        int dayOffset = Utils.getMonthFirstDayOfWeek(year, month) - 1;
        int lineCount = (daySum + dayOffset) / 7 + ((daySum + dayOffset) % 7 > 0 ? 1 : 0);

        canvas.drawRect(offset, 0, mWidth + offset, mCellHeight * lineCount, mDebugPaint);

//        for (int i = 0; i < 7; i++) {
//            canvas.drawText(Utils.getWeekday(i), mCellWidth * i + mCellWidth / 2f + offset, mCellHeight / 2, mTextPaint);
//        }

        for (int i = 0; i < lineCount; i++) {
            float y = mCellHeight * i + mCellHeight / 2;
            drawWeek(canvas, i * 7 + 1 - dayOffset, Math.min(i * 7 + 1 - dayOffset + 6, daySum), offset, y);
//            drawWeek(canvas, year, month, i * 7 + 1 - dayOffset, y, offset);
//            for (int j = 0; j < 7 && i * 7 + j < daySum + dayOffset; j++) {
//                if (i * 7 + j + 1 - dayOffset > 0) {
//                    drawDay(canvas, year, month, i * 7 + j + 1 - dayOffset, mCellWidth * j + mCellWidth / 2f + offset, y);
//                }
//            }
        }
    }

    private void drawWeek(Canvas canvas, int startDay, int endDay, int offset, float y) {
        for (int i = startDay; i <= endDay; i++) {
            if (i > 0) {
                drawDay(canvas, i, mCellWidth * (i - startDay) + mCellWidth / 2f + offset, y);
            }
        }
    }

    private void drawDay(Canvas canvas, int day, float x, float y) {
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setStyle(Paint.Style.FILL);
//        if (year == Utils.getYear(mToday)
//                && month == Utils.getMonth(mToday)
//                && day == Utils.getDayOfMonth(mToday)) {
//            paint.setColor(0xB0CC44AA);
//            canvas.drawCircle(x, y, 38, paint);
//        } else if (year == Utils.getYear(mSelectedDate)
//                && month == Utils.getMonth(mSelectedDate)
//                && day == Utils.getDayOfMonth(mSelectedDate)) {
//            paint.setColor(0x80FFFFFF);
//            canvas.drawCircle(x, y, 38, paint);
//        }
        RectF targetRect = new RectF(x - mCellWidth / 2 + 2, y - mCellHeight / 2 + 2,
                x + mCellWidth / 2 - 2, y + mCellHeight / 2 - 2);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        float baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2f;
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(day), targetRect.centerX(), baseline, mTextPaint);
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
