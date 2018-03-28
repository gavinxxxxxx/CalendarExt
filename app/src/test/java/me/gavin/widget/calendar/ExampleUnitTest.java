package me.gavin.widget.calendar;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import me.gavin.widget.Utils;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void ttt() {
        System.out.println(Utils.getWeekday(Calendar.SUNDAY));

        System.out.println(Utils.getMaxDayOfMonth(2016, 2));

        System.out.println(Utils.getYear(new Date()));
        System.out.println(Utils.getMonth(new Date()));
        System.out.println(Utils.getDayOfMonth(new Date()));

        System.out.println(Utils.getMonthFirstDayOfWeek(Utils.getYear(new Date()), Utils.getMonth(new Date())));
        System.out.println(Utils.getDayOfWeekInMonth(Utils.getYear(new Date()), Utils.getMonth(new Date()), Utils.getDayOfMonth(new Date())));

        System.out.println(Utils.getYear(Utils.offsetMonth(new Date(), -3)));
        System.out.println(Utils.getMonth(Utils.offsetMonth(new Date(), -3)));
        System.out.println(Utils.getDayOfMonth(Utils.offsetMonth(new Date(), -3)));
    }
}