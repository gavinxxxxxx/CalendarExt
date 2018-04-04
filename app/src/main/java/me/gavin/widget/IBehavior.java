package me.gavin.widget;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;

import me.gavin.widget.calendar.L;

public class IBehavior extends CoordinatorLayout.Behavior<ICalendar> {

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ICalendar child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        L.d("onStartNestedScroll");
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ICalendar child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        L.d("onNestedScrollAccepted");
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ICalendar child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        L.d("onNestedPreScroll - " + target.getScrollY());
        if (dy > 0) {
            consumed[1] = child.consumed(dy);
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ICalendar child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        L.d("onNestedScroll - " + target.getScrollY());
        if (dyUnconsumed < 0) {
            child.consumed(dyUnconsumed);
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ICalendar child, @NonNull View target, int type) {
        L.d("onStopNestedScroll");
    }
}