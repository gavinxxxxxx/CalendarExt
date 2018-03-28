package me.gavin.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2018/3/28
 */
public class DateData {

    public final List<Month> months = new ArrayList<>(3);

    public static class Month {
        public final List<Week> weeks = new ArrayList<>(6);

        @Override
        public String toString() {
            return "Month{" +
                    "weeks=" + weeks +
                    '}';
        }
    }

    public static class Week {
        public final List<Day> days = new ArrayList<>(7);

        @Override
        public String toString() {
            return "Week{" +
                    "days=" + days +
                    '}';
        }
    }

    public static class Day {
        public final Date date;
        public int year; // 年
        public int month; // 月 - 自然月减一
        public int day; // 日
        public boolean today; // 是今天
        public boolean selected; // 已选中
        public boolean different; // 不同月

        public Day(Date date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return "Day{" +
                    "date=" + String.format(Locale.getDefault(), "%tF", date) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DateData{" +
                "months=" + months +
                '}';
    }

    public static DateData get(Date date) {
        DateData result = new DateData();
        result.months.add(getMonth(date, -1));
        result.months.add(getMonth(date, 0));
        result.months.add(getMonth(date, 1));
        return result;
    }

    private static DateData.Month getMonth(Date date, int offset) {
        DateData.Month result = new DateData.Month();
        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setTime(date);
        sCalendar.add(Calendar.MONTH, offset);
        // 当月天数
        int dayCount = sCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 当月 1 号对于周第一天的偏移天数
        sCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayOffset = sCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        // 周数
        int weekCount = (dayCount + dayOffset) / 7 + ((dayCount + dayOffset) % 7 > 0 ? 1 : 0);
        // 定位到补齐后首一天
        sCalendar.add(Calendar.DAY_OF_MONTH, -dayOffset);
        for (int i = 0; i < weekCount * 7; i++) {
            if (i % 7 == 0) {
                result.weeks.add(i / 7, new DateData.Week());
            }
            result.weeks.get(i / 7).days.add(new DateData.Day(sCalendar.getTime()));
            sCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }
}
