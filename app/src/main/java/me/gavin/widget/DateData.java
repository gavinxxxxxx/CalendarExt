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
    public int sLine;

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

        public boolean selected;

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
            day = Utils.getDayOfMonth(date);
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

    public int getSLine() {
        for (Week week : months.get(1).weeks) {
            for (Day day : week.days) {
                if (day.selected) {
                    return months.get(1).weeks.indexOf(week);
                }
            }
        }
        return 0;
    }

    public static DateData get(Date sel, Date today) {
        DateData result = new DateData();
        result.months.add(getMonth(sel, today, -1));
        result.months.add(getMonth(sel, today, 0));
        result.months.add(getMonth(sel, today, 1));
        result.sLine = result.getSLine();
        return result;
    }

    private static DateData.Month getMonth(Date sel, Date today, int offset) {
        DateData.Month result = new DateData.Month();
        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setTime(sel);
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
            DateData.Day day = new DateData.Day(sCalendar.getTime());
            day.today = today.equals(day.date);
            day.selected = sel.equals(day.date);
            if (day.selected) {
                result.weeks.get(i / 7).selected = true;
            }
            day.different = i < 7 && day.day > 7 || i > 14 && day.day < 7;
            result.weeks.get(i / 7).days.add(day);
            sCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }
}
