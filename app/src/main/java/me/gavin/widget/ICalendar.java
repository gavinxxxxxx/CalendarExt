package me.gavin.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import java.util.Date;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2018/3/23
 */
public class ICalendar extends View implements CoordinatorLayout.AttachedBehavior {

    public static final int SCROLL_NONE = 0; // 水平滑动
    public static final int SCROLL_HORIZONTAL = 1; // 水平滑动
    public static final int SCROLL_VERTICAL = 2; // 竖直滑动

    private int mWidth, mHeight, mRequestHeight;
    private float mCellWidth, mCellHeight;
    private float mDiffY;

    private final Paint mTextPaint, mSPaint;
    private final Paint mDebugPaint;

    private ValueAnimator mXAnimator, mYAnimator;

    private Date mToday;
    private Date mSelectedDate;
    private int mScrollState;

    private DateData mData;
    private float mSelTop;

    private Consumer<Date> mDateSelectedListener;

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

        mToday = new Date();
        mSelectedDate = mToday;
        mData = DateData.get(mSelectedDate, mToday);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mCellWidth = mWidth / 7f;
        mCellHeight = mCellWidth * 0.9f;
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        float baseline = (mCellHeight - fontMetrics.bottom - fontMetrics.top) / 2f;
        mDiffY = baseline - mCellHeight / 2f;
        mRequestHeight = (int) (mCellHeight + mCellHeight * mData.months.get(1).weeks.size());
        mHeight = Math.min(mRequestHeight, MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(mWidth, mHeight);
    }

    public int consumed(int dy) {
        mHeight = getLayoutParams().height = Math.min(mRequestHeight, Math.max((int) mCellHeight * 2, mHeight - dy));
        requestLayout();
        return mHeight - dy > mCellHeight * 2 ? dy : 0;
    }

    public int getCurrHeight() {
        return mHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mData == null) return;

        for (int i = 0; i < 7; i++) {
            canvas.drawText(Utils.getWeekday(i),
                    mCellWidth * i + mCellWidth / 2f + getScrollX(),
                    mCellHeight / 2f + mDiffY, mTextPaint);
        }

        if (isMonthMode()) {
            canvas.clipRect(getScrollX(), mCellHeight, mWidth + getScrollX(), mHeight);
            drawMonth(canvas, -1);
            drawMonth(canvas, 0);
            drawMonth(canvas, 1);
        } else {
            drawWeek(canvas, mData.weeks.get(0), -1, mCellHeight, mData.getSLine());
            drawWeek(canvas, mData.weeks.get(1), 0, mCellHeight, mData.getSLine());
            drawWeek(canvas, mData.weeks.get(2), 1, mCellHeight, mData.getSLine());
        }
    }

    private void drawMonth(Canvas canvas, int offset) {
        DateData.Month month = mData.months.get(offset + 1);
        for (int i = 0; i < month.weeks.size(); i++) {
            drawWeek(canvas, month.weeks.get(i), offset, mCellHeight * i + mCellHeight * 1.5f, i);
        }
    }

    private void drawWeek(Canvas canvas, DateData.Week week, int offset, float y, int line) {
        line -= mData.sLine;
        y = Math.max(line * mCellHeight + mCellHeight * 1.5f, y - mRequestHeight + mHeight);
        for (int i = 0; i < week.days.size(); i++) {
            drawDay(canvas, week.days.get(i), mCellWidth * i + mCellWidth / 2f + offset * mWidth, y);
        }
    }

    private void drawDay(Canvas canvas, DateData.Day day, float x, float y) {
        // 非当月
        if (day.different && isMonthMode()) return;

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
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mXAnimator != null && mXAnimator.isRunning()) {
                    mXAnimator.cancel();
                }
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
                    setScrollX((int) Math.min(mWidth, Math.max(-mWidth, getScrollX() + mLastX - event.getX())));
                    mLastX = event.getX();
                    mLastY = event.getY();
                } else if (mScrollState == SCROLL_VERTICAL) {
                    mVelocityTracker.addMovement(event);
                    getLayoutParams().height = Math.max((int) mCellHeight * 2, mHeight - (int) (mLastY - event.getY()));
                    requestLayout();
                    mLastX = event.getX();
                    mLastY = event.getY();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mScrollState == SCROLL_NONE && event.getY() > mCellHeight) {
                    int xi = (int) event.getX() / (int) mCellWidth;
                    int yi = (int) event.getY() / (int) mCellHeight - 1;
                    DateData.Day day = isMonthMode()
                            ? mData.months.get(1).weeks.get(yi).days.get(xi)
                            : mData.weeks.get(1).days.get(xi);
                    if (!isMonthMode() || !day.different) {
                        mSelectedDate = day.date;
                        mData = DateData.get(mSelectedDate, mToday);
                        if (mDateSelectedListener != null) {
                            mDateSelectedListener.accept(mSelectedDate);
                            postInvalidate();
                        }
                    }
                }

                if (mScrollState != SCROLL_NONE) {
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000);
                }

                if (mScrollState == SCROLL_HORIZONTAL) {
                    float xv = mVelocityTracker.getXVelocity();
                    int minFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity() * 2;
                    if (Math.abs(xv) < minFlingVelocity) {
                        smoothScrollXBy(getScrollX(), Math.abs(getScrollX()) < mWidth / 2 ? 0 : getScrollX() > 0 ? mWidth : -mWidth);
                    } else {
                        smoothScrollXBy(getScrollX(), getScrollX() / mWidth * mWidth + xv > 0 ? -mWidth : mWidth);
                    }
                } else if (mScrollState == SCROLL_VERTICAL) {
                    float yv = mVelocityTracker.getYVelocity();
                    int minFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity() * 2;
                    if (Math.abs(yv) < minFlingVelocity) {
                        smoothScrollYBy(mHeight, mHeight - mCellHeight * 2 > mRequestHeight - mHeight ? mRequestHeight : (int) mCellHeight * 2);
                    } else {
                        smoothScrollYBy(mHeight, yv > 0 ? mRequestHeight : (int) mCellHeight * 2);
                    }
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mScrollState = SCROLL_NONE;
                break;
        }
        return true;
    }

    /**
     * 当前是否为月模式
     */
    private boolean isMonthMode() {
        return mHeight > Math.ceil(mCellHeight * 2);
    }

    public void smoothScrollXBy(int cw, int tw) {
        if (mXAnimator == null) {
            mXAnimator = ValueAnimator.ofInt(cw, tw).setDuration(256);
            mXAnimator.setInterpolator(new DecelerateInterpolator());
            mXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setScrollX((int) animation.getAnimatedValue());
                    postInvalidate();
                }
            });
            mXAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (getScrollX() != 0) {
                        mSelectedDate = isMonthMode()
                                ? Utils.offsetMonth(mSelectedDate, getScrollX() > 0 ? 1 : -1)
                                : Utils.offsetWeek(mSelectedDate, getScrollX() > 0 ? 1 : -1);
                        mData = DateData.get(mSelectedDate, mToday);
                        if (mDateSelectedListener != null) {
                            mDateSelectedListener.accept((Date) mSelectedDate.clone());
                        }
                    }
                    setScrollX(0);
                    postInvalidate();
                }
            });
        } else {
            mXAnimator.setIntValues(cw, tw);
        }
        mXAnimator.start();
    }

    public void smoothScrollYBy(int ch, int th) {
        if (mYAnimator == null) {
            mYAnimator = ValueAnimator.ofInt(ch, th).setDuration(256);
            mYAnimator.setInterpolator(new DecelerateInterpolator());
            mYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    getLayoutParams().height = (int) animation.getAnimatedValue();
                    requestLayout();
                }
            });
        } else {
            mYAnimator.setIntValues(ch, th);
        }
        mYAnimator.start();
    }

    public void setOnDateSelectedListener(Consumer<Date> listener) {
        this.mDateSelectedListener = listener;
    }

    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return new IBehavior();
    }
}
