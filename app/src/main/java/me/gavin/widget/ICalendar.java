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
    private int mScrollState;
    private int closeHeigth;

    private DateData mData;
    private float mSelTop;

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
        mDebugPaint.setColor((int) (Math.random() * 0xFFFFFF) + 0x40000000);

        mScroller = new Scroller(context);

        mToday = new Date();
        // mSelectedDate = mToday;
        mSelectedDate = new Date(1521043200000L);
        mData = DateData.get(mSelectedDate, mToday);
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

        for (int i = 0; i < 7; i++) {
            canvas.drawText(Utils.getWeekday(i),
                    mCellWidth * i + mCellWidth / 2f + getScrollX(),
                    mCellHeight / 2f + mDiffY + getScrollY(), mTextPaint);
        }

//        Path path = new Path();
//        path.addRect(20, 20, mWidth - 20, 180, Path.Direction.CCW);
//        path.addRect(20, 220, mWidth - 20, 380, Path.Direction.CCW);
//        canvas.clipPath(path);
        canvas.clipRect(20, mCellHeight + getScrollY(), mWidth - 20, 900);

        canvas.drawColor(0x40FF0000);
//        canvas.clipRect(20, 20, mWidth - 20, 280, Region.Op.UNION);
//        canvas.clipRect(20, 320, mWidth - 20, 580, Region.Op.UNION);

        drawMonth(canvas, -1);
        drawMonth(canvas, 0);
        drawMonth(canvas, 1);

//        canvas.save();
//        canvas.clipRect(20, 320, mWidth - 20, 580, Region.Op.REVERSE_DIFFERENCE);
//        canvas.restore();

//        canvas.drawRect(getScrollX(), getScrollY(), mWidth + getScrollX(), mCellHeight + getScrollY(), mDebugPaint);
    }

    private void drawMonth(Canvas canvas, int offset) {
        DateData.Month month = mData.months.get(offset + 1);
        for (int i = 0; i < month.weeks.size(); i++) {
            drawWeek(canvas, month.weeks.get(i), offset, mCellHeight * i + mCellHeight * 1.5f);
        }
    }

    private void drawWeek(Canvas canvas, DateData.Week week, int offset, float y) {
        if (week.selected) {
            y = Math.max(y, getScrollY() + mCellHeight * 1.5f);
        }
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
                mScroller.forceFinished(true);
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mScrollState == SCROLL_NONE) {
                    if (Math.abs(mLastX - event.getX()) > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                        mScrollState = SCROLL_HORIZONTAL;
                        mVelocityTracker = VelocityTracker.obtain();
                    } else if (Math.abs(mLastY - event.getY()) > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                        mScrollState = SCROLL_VERTICAL;
                        mVelocityTracker = VelocityTracker.obtain();
                    }
                }
                if (mScrollState == SCROLL_HORIZONTAL) {
                    mVelocityTracker.addMovement(event);
                    setScrollX(getScrollX() > 0
                            ? Math.min(mWidth, getScrollX() + (int) (mLastX - event.getX()))
                            : Math.max(-mWidth, getScrollX() + (int) (mLastX - event.getX())));
                    mLastX = event.getX();
                    mLastY = event.getY();
                } else if (mScrollState == SCROLL_VERTICAL) {
                    mVelocityTracker.addMovement(event);
                    setScrollY(getScrollY() > 0
                            ? Math.min(mHeight, getScrollY() + (int) (mLastY - event.getY()))
                            : Math.max(-mHeight, getScrollY() + (int) (mLastY - event.getY())));
                    mLastX = event.getX();
                    mLastY = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mScrollState != SCROLL_NONE) {
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
                }
                mScrollState = SCROLL_NONE;
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
