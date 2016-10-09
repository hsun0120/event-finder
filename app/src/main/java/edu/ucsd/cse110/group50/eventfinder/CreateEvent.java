package edu.ucsd.cse110.group50.eventfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateEvent extends AppCompatActivity {

    private int selectedDay, selectedMonth, selectedYear;
    private int selectedHour, selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Calendar calendar = Calendar.getInstance();
        selectedDay = calendar.get( Calendar.DAY_OF_MONTH );
        selectedMonth = calendar.get( Calendar.MONTH );
        selectedYear = calendar.get( Calendar.YEAR );
        selectedHour = calendar.get( Calendar.HOUR_OF_DAY );
        selectedMinute = calendar.get( Calendar.MINUTE );
    }

    public void pickDate( View v ) {

        DatePicker datePicker = (DatePicker) findViewById( R.id.datePicker );
        datePicker.setVisibility( View.VISIBLE );
        datePicker.init( selectedYear, selectedMonth, selectedDay, new DateChangeListener() );

    }

    private class DateChangeListener implements DatePicker.OnDateChangedListener {

        @Override
        public void onDateChanged( DatePicker datePicker, int year, int month, int day ) {

            datePicker.setVisibility( View.INVISIBLE );
            selectedDay = day;
            selectedMonth = month;
            selectedYear = year;

            String date = month + "/" + day + "/" + year;
            Date selectedDate = new Date();
            try {
                selectedDate = new SimpleDateFormat("MM/dd/yyyy").parse( date );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String weekday = new SimpleDateFormat("EE").format( selectedDate );

            TextView eventDate = (TextView)findViewById( R.id.eventDate );
            TextView errorMessage = (TextView)findViewById( R.id.invalidDateMessage );

            eventDate.setText( weekday + ", " + date );
            if ( checkValidDate() ) {
                eventDate.setTextColor( 0xFF000000 );
                errorMessage.setVisibility( View.INVISIBLE );
            } else {
                eventDate.setTextColor( 0xFFF44336 );
                errorMessage.setVisibility( View.VISIBLE );
            }

        }

    }

    private boolean checkValidDate() {

        Calendar selected = Calendar.getInstance();
        selected.set( selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute );
        Calendar current = Calendar.getInstance();

        return !selected.before( current );

    }
}
