package me.gavin.widget.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Locale;

import me.gavin.widget.ICalendar;
import me.gavin.widget.OnMonthSelectedListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ICalendar cal = findViewById(R.id.calendar);
        cal.setOnMonthSelectedListener(new OnMonthSelectedListener() {
            @Override
            public void accept(int year, int month) {
                Toast.makeText(MainActivity.this, String.format(Locale.getDefault(), "%04d-%02d", year, month + 1), Toast.LENGTH_LONG).show();
            }
        });
    }
}
