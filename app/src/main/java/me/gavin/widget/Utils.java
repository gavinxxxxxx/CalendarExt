package me.gavin.widget;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 工具类
 *
 * @author gavin.xiong 2018/3/23
 */
public final class Utils {

    private final static Calendar sCalendar;

    static {
        sCalendar = Calendar.getInstance();
        sCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
    }

    /**
     * 获取星期名
     */
    public static String getWeekday(int dayOfWeek) {
        sCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek + 1);
        return sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
    }

    /**
     * 获取年份
     */
    public static int getYear(Date date) {
        sCalendar.setTime(date);
        return sCalendar.get(Calendar.YEAR);
    }

    /**
     * 获取月份
     */
    public static int getMonth(Date date) {
        sCalendar.setTime(date);
        return sCalendar.get(Calendar.MONTH);
    }

    /**
     * 获取日期
     */
    public static int getDayOfMonth(Date date) {
        sCalendar.setTime(date);
        return sCalendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定月天数
     */
    public static int getMaxDayOfMonth(int year, int month) {
        sCalendar.set(year, month, 1);
        return sCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定月第一天是一周的第几天
     */
    public static int getMonthFirstDayOfWeek(int year, int month) {
        sCalendar.set(year, month, 1);
        return sCalendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 指定日是当前月的第几周
     */
    public static int getDayOfWeekInMonth(int year, int month, int day) {
        sCalendar.set(year, month, day);
        return sCalendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 获取指定日期的偏移月
     */
    public static Date offsetMonth(Date date, int offset) {
        sCalendar.setTime(date);
        sCalendar.add(Calendar.MONTH, offset);
        return sCalendar.getTime();
    }

    /**
     * 获取指定日期的偏移周
     */
    public static Date offsetWeek(Date date, int offset) {
        sCalendar.setTime(date);
        sCalendar.add(Calendar.WEEK_OF_YEAR, offset);
        return sCalendar.getTime();
    }
}
