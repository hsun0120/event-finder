package edu.ucsd.cse110.group50.eventfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class OpenFilter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_filter);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar3);
        final TextView within_distance_view = (TextView) findViewById(R.id.filter_events_within_meters);


        // Initialize the textview with '0'.

        within_distance_view.setText("Within " + seekBar.getProgress()/seekBar.getMax()*1000 + " meters");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               // within_distance_view.setText("Covered: " + progress + "/" + seekBar.getMax());
                within_distance_view.setText("Within " + (int)((float)progress/seekBar.getMax()*100) + " kilometers");
                Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
