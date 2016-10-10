package edu.ucsd.cse110.group50.eventfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.sql.Time;
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

        CardView dateCard = (CardView) findViewById( R.id.dateCard );
        dateCard.setVisibility( View.VISIBLE );
        ImageView blur = (ImageView) findViewById( R.id.backgroundBlur );
        blur.setVisibility( View.VISIBLE );
        setFormClickable( false );

        DatePicker datePicker = (DatePicker) findViewById( R.id.datePicker );
        datePicker.init( selectedYear, selectedMonth, selectedDay, new DateChangeListener() );

    }

    private class DateChangeListener implements DatePicker.OnDateChangedListener {

        @Override
        public void onDateChanged( DatePicker datePicker, int year, int month, int day ) {

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

            TextView eventDate = (TextView) findViewById( R.id.eventDate );
            eventDate.setText( weekday + ", " + date );
            checkValidDate();

            CardView dateCard = (CardView) findViewById( R.id.dateCard );
            dateCard.setVisibility( View.INVISIBLE );
            ImageView blur = (ImageView) findViewById( R.id.backgroundBlur );
            blur.setVisibility( View.INVISIBLE );
            setFormClickable( true );

        }

    }

    public void pickTime( View v ) {

        CardView timeCard = (CardView) findViewById( R.id.timeCard );
        timeCard.setVisibility( View.VISIBLE );
        ImageView blur = (ImageView) findViewById( R.id.backgroundBlur );
        blur.setVisibility( View.VISIBLE );
        setFormClickable( false );

        TimePicker timePicker = (TimePicker) findViewById( R.id.timePicker );
        timePicker.setHour( selectedHour );
        timePicker.setMinute( selectedMinute );

    }

    public void setTime( View v ) {

        TimePicker selected = (TimePicker) findViewById( R.id.timePicker );
        int hour = selected.getHour();
        int minute = selected.getMinute();

        selectedHour = hour;
        selectedMinute = minute;

        String time = hour + ":" + minute;
        TextView eventTime = (TextView) findViewById( R.id.eventTime );
        eventTime.setText( time );
        checkValidDate();
        closeTime( v );

    }

    public void closeTime( View v ) {

        CardView timeCard = (CardView) findViewById( R.id.timeCard );
        timeCard.setVisibility( View.INVISIBLE );
        ImageView blur = (ImageView) findViewById( R.id.backgroundBlur );
        blur.setVisibility( View.INVISIBLE );
        setFormClickable( true );

    }

    public void createEvent( View v ) {

    }

    private void checkValidDate() {

        TextView eventDate = (TextView) findViewById( R.id.eventDate );
        TextView eventTime = (TextView) findViewById( R.id.eventTime );
        TextView errorMessage = (TextView) findViewById( R.id.invalidDateMessage );
        Calendar selected = Calendar.getInstance();
        selected.set( selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute );
        Calendar current = Calendar.getInstance();

        if ( !selected.before( current ) ) {
            eventDate.setTextColor( 0xFF000000 );
            eventTime.setTextColor( 0xFF000000 );
            errorMessage.setVisibility( View.INVISIBLE );
        } else {
            eventDate.setTextColor( 0xFFF44336 );
            eventTime.setTextColor( 0xFFF44336 );
            errorMessage.setVisibility( View.VISIBLE );
        }

    }

    private void setFormClickable( boolean clickable ) {

        EditText name = (EditText) findViewById( R.id.eventName );
        EditText address = (EditText) findViewById( R.id.eventAddress );
        TextView date = (TextView) findViewById( R.id.eventDate ) ;
        TextView time = (TextView) findViewById( R.id.eventTime ) ;
        Switch passwordToggle = (Switch) findViewById( R.id.passOption );
        EditText password = (EditText) findViewById( R.id.eventPassword );
        Switch restrictionsToggle = (Switch) findViewById( R.id.restrictionsToggle );
        EditText restrictions = (EditText) findViewById( R.id.eventRestrictions );
        EditText description = (EditText) findViewById( R.id.eventDescription );
        Button finish = (Button) findViewById( R.id.doneButton );

        name.setFocusable( clickable );
        address.setFocusable( clickable );
        date.setClickable( clickable );
        time.setClickable( clickable );
        passwordToggle.setClickable( clickable );
        password.setFocusable( clickable );
        restrictionsToggle.setClickable( clickable );
        restrictions.setFocusable( clickable );
        description.setFocusable( clickable );
        finish.setClickable( clickable );

    }

}
