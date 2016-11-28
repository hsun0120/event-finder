package edu.ucsd.cse110.group50.eventfinder;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class OpenFilter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_filter);

        //SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar3);
        final TextView within_distance_view = (TextView) findViewById(R.id.filter_events_within_meters);


        // Initialize the textview with '0'.

        //within_distance_view.setText("Within " + seekBar.getProgress()/seekBar.getMax()*1000 + " meters");

//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            int progress = 0;
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
//                progress = progresValue;
//                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//               // within_distance_view.setText("Covered: " + progress + "/" + seekBar.getMax());
//                within_distance_view.setText("Within " + (int)((float)progress/seekBar.getMax()*100) + " kilometers");
//                //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void aweek(View v)
    {
        Calendar calendar = Calendar.getInstance();
        ((Button) findViewById(R.id.filter_event_within_a_month)).setTextColor(Color.BLACK);
        ((Button) findViewById(R.id.filter_event_within_6_months)).setTextColor(Color.BLACK);
        ((Button) findViewById(R.id.event_filter_none)).setTextColor(Color.BLACK);
        int day = calendar.get( Calendar.DAY_OF_MONTH ) + 7;
        int month = calendar.get( Calendar.MONTH ) + 1;
        int year = calendar.get( Calendar.YEAR );

        //System.out.println("Current date is  "+ (day-7) + "/" + month+ "/" + year);


        EvDate d = new EvDate(day, month, year);

        //System.out.println("date to compare with is "+ d);

        Button but = (Button) findViewById(R.id.filter_event_within_a_week);
        but.setTextColor(Color.RED);

        MapView.date_filtered = d;
    }

    public void amonth(View v)
    {
        Calendar calendar = Calendar.getInstance();
        ((Button) findViewById(R.id.filter_event_within_a_week)).setTextColor(Color.BLACK);
        ((Button) findViewById(R.id.filter_event_within_6_months)).setTextColor(Color.BLACK);
        ((Button) findViewById(R.id.event_filter_none)).setTextColor(Color.BLACK);
        int day = calendar.get( Calendar.DAY_OF_MONTH );
        int month = calendar.get( Calendar.MONTH ) + 1 + 1;
        int year = calendar.get( Calendar.YEAR );

        EvDate d = new EvDate(day, month, year);

        //System.out.println("date to compare with is "+ d);
        Button but = (Button) findViewById(R.id.filter_event_within_a_month);
        but.setTextColor(Color.RED);

        MapView.date_filtered = d;
    }

    public void sixmonths(View v)
    {
        Calendar calendar = Calendar.getInstance();
        ((Button) findViewById(R.id.filter_event_within_a_week)).setTextColor(Color.BLACK);
        ((Button) findViewById(R.id.filter_event_within_a_month)).setTextColor(Color.BLACK);
        ((Button) findViewById(R.id.event_filter_none)).setTextColor(Color.BLACK);

        int day = calendar.get( Calendar.DAY_OF_MONTH );
        int month = calendar.get( Calendar.MONTH ) + 1 + 6;
        int year = calendar.get( Calendar.YEAR );

        EvDate d = new EvDate(day, month, year);

        //System.out.println("date to compare with is "+ d);
        Button but = (Button) findViewById(R.id.filter_event_within_6_months);
        but.setTextColor(Color.RED);

        MapView.date_filtered = d;
    }


    public void filter_none(View v)
    {
        Calendar calendar = Calendar.getInstance();
        ((Button) findViewById(R.id.filter_event_within_a_week)).setTextColor(Color.BLACK);
        ((Button) findViewById(R.id.filter_event_within_a_month)).setTextColor(Color.BLACK);
        ((Button) findViewById(R.id.filter_event_within_6_months)).setTextColor(Color.BLACK);


        int day = calendar.get( Calendar.DAY_OF_MONTH );
        int month = calendar.get( Calendar.MONTH ) + 1;
        int year = calendar.get( Calendar.YEAR )+1000;

        EvDate d = new EvDate(day, month, year);

        System.out.println("date to compare with is "+ d);
        Button but = (Button) findViewById(R.id.event_filter_none);
        but.setTextColor(Color.RED);

        MapView.date_filtered = d;
    }

    public void event_filter_done(View v)
    {
        finish();
    }

}
