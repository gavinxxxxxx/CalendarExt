package me.gavin.widget.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Date;

import me.gavin.widget.Consumer;
import me.gavin.widget.ICalendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ICalendar cal = findViewById(R.id.calendar);
        cal.setOnDateSelectedListener(new Consumer<Date>() {
            @Override
            public void accept(Date date) {
                Toast.makeText(MainActivity.this, String.format("%tF", date), Toast.LENGTH_LONG).show();
            }
        });
    }
}
