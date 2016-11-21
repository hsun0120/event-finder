package edu.ucsd.cse110.group50.eventfinder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.sql.Array;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;



public class CreateEvent extends AppCompatActivity {
    private static final  int NAME_LENGTH_MAX = 30;
    private static final  int NAME_LENGTH_TOREAD = 30;

    private int selectedDay, selectedMonth, selectedYear;
    private int selectedHour, selectedMinute;

    private User curUser;
    private Place place;
    int REQUEST_PLACE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Calendar calendar = Calendar.getInstance();
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedYear = calendar.get(Calendar.YEAR);
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendar.get(Calendar.MINUTE);

        Intent intent = getIntent();
        curUser = intent.getParcelableExtra( Identifiers.USER );

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

        String time = String.format( "%02d:%02d", hour, minute );
        TextView eventTime = (TextView) findViewById( R.id.eventTime );
        eventTime.setText( time );
        checkValidDate();
        closeTime( v );

    }

    public void pickPlace( View v ) {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

    public void closeTime( View v ) {

        CardView timeCard = (CardView) findViewById( R.id.timeCard );
        timeCard.setVisibility( View.INVISIBLE );
        ImageView blur = (ImageView) findViewById( R.id.backgroundBlur );
        blur.setVisibility( View.INVISIBLE );
        setFormClickable( true );

    }

    public void createEvent( View v ) {

        TextView errorMessage = (TextView) findViewById( R.id.invalidDateMessage );
        if ( errorMessage.getVisibility() == View.VISIBLE ) {
            Toast.makeText(CreateEvent.this, "Invalid date/time.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventInDatabase = mDatabase.child( Identifiers.FIREBASE_EVENTS ).push();
        String uid = eventInDatabase.getKey();
        Event newEvent = new Event( uid, curUser.getUid() );

        // Gets all the data fields.
        EditText name = (EditText) findViewById( R.id.eventName );

        EditText address = (EditText) findViewById( R.id.eventAddress );

        Switch passwordToggle = (Switch) findViewById( R.id.passOption );
        EditText password = (EditText) findViewById( R.id.eventPassword );
        Switch restrictionsToggle = (Switch) findViewById( R.id.restrictionsToggle );
        EditText restrictions = (EditText) findViewById( R.id.eventRestrictions );

        EditText description = (EditText) findViewById( R.id.eventDescription );

        // Records the data in the Event.
        newEvent.setName( name.getText().toString() );

        newEvent.setAddress( address.getText().toString() );
        newEvent.setLocId((place.getId()));

        newEvent.setTime( (byte) selectedHour, (byte) selectedMinute );
        newEvent.setDate( (byte) selectedDay, (byte) selectedMonth, (short) selectedYear );

        newEvent.setHasPassword( passwordToggle.isChecked() );
        newEvent.setPassword( password.getText().toString() );
        newEvent.setHasRestrictions( restrictionsToggle.isChecked() );
        String[] restrictionList = restrictions.getText().toString().split("\n");
        newEvent.setRestrictions( Arrays.asList( restrictionList ) );

        newEvent.setDescription( description.getText().toString() );

        eventInDatabase.setValue( newEvent );

        finish();

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

        name.setEnabled( clickable );
        address.setEnabled( clickable );
        date.setClickable( clickable );
        time.setClickable( clickable );
        passwordToggle.setClickable( clickable );
        password.setEnabled( clickable );
        restrictionsToggle.setClickable( clickable );
        restrictions.setEnabled( clickable );
        description.setEnabled( clickable );
        finish.setClickable( clickable );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                this.place = place;
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                EditText address = (EditText) findViewById(R.id.eventAddress);
                address.setText(place.getAddress());
            }
        }
    }

}
