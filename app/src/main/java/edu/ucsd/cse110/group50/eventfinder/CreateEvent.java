package edu.ucsd.cse110.group50.eventfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import edu.ucsd.cse110.group50.eventfinder.storage.EvDate;
import edu.ucsd.cse110.group50.eventfinder.storage.Event;
import edu.ucsd.cse110.group50.eventfinder.storage.User;
import edu.ucsd.cse110.group50.eventfinder.utility.Identifiers;


/**
 * This class manages activity_create_event.
 */
public class CreateEvent extends AppCompatActivity {
    private static final  int NAME_LENGTH_MAX = 30;
    private static final  int NAME_LENGTH_TOREAD = 30;
    private static final String TAG = "CreateEvent";

    private EvDate selected;

    private User curUser;
    private Place place;
    int REQUEST_PLACE_PICKER = 1;

    public static Event editedCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //((Button)findViewById(R.id.doneButton)).setText("Edit Event!");
        if(EventDetailActivity.user_editting_flag == 1)
        {
            ((Button)findViewById(R.id.doneButton)).setText("Edit Event!");
            Event card = getIntent().getParcelableExtra("event_card");
            //System.out.println("event got is " + card.toString());
            ((TextView)findViewById(R.id.eventName)).setText(card.getName());
            ((TextView)findViewById(R.id.eventAddress)).setText(card.getAddress());
            ((TextView)findViewById(R.id.eventDate)).setText(card.getDate().getDate().toString());
            ((TextView)findViewById(R.id.eventTime)).setText(card.getDate().getTime().toString());
            ((TextView)findViewById(R.id.eventPassword)).setText(card.getPassword());
            ((TextView)findViewById(R.id.eventRestrictions)).setText(card.getRestrictions().get(0));
            ((TextView)findViewById(R.id.eventDescription)).setText(card.getDescription());

            if(card.getHasPassword())
                ((Switch) findViewById( R.id.passOption )).toggle();
            if(card.getHasRestrictions())
                ((Switch) findViewById( R.id.restrictionsToggle )).toggle();


            selected = card.getDate();
            curUser = MapView.curUser;
        }

        else {

            selected = new EvDate();

            Intent intent = getIntent();
            curUser = intent.getParcelableExtra(Identifiers.USER);
        }
    }

    public void pickDate( View v ) {

        CardView dateCard = (CardView) findViewById( R.id.dateCard );
        dateCard.setVisibility( View.VISIBLE );
        ImageView blur = (ImageView) findViewById( R.id.backgroundBlur );
        blur.setVisibility( View.VISIBLE );
        setFormClickable( false );

        DatePicker datePicker = (DatePicker) findViewById( R.id.datePicker );
        Log.v( TAG, "Init date picker with date " + selected.getDate() );
        datePicker.init( selected.getYear(), selected.getMonth() - 1, selected.getDay(),
                new DateChangeListener() );

    }

    private class DateChangeListener implements DatePicker.OnDateChangedListener {

        @Override
        public void onDateChanged( DatePicker datePicker, int year, int month, int day ) {

            selected.setDate( day, month + 1, year );

            String date = selected.getDate();
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
        Log.v( TAG, "Init time picker with date " + selected.getTime() );
        timePicker.setHour( selected.getHour() );
        timePicker.setMinute( selected.getMinute() );

    }

    public void setTime( View v ) {

        TimePicker picked = (TimePicker) findViewById( R.id.timePicker );

        selected.setTime( picked.getHour(), picked.getMinute() );

        String time = selected.getTime();
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

        checkValidDate();
        TextView errorMessage = (TextView) findViewById( R.id.invalidDateMessage );
        if ( errorMessage.getVisibility() == View.VISIBLE ) {
            Toast.makeText( CreateEvent.this, "Invalid date/time.",
                    Toast.LENGTH_SHORT ).show();
            return;
        }

        // Gets all the data fields.
        EditText nameBox = (EditText) findViewById( R.id.eventName );
        String name = nameBox.getText().toString().trim();

        EditText addressBox = (EditText) findViewById( R.id.eventAddress );
        String address = addressBox.getText().toString().trim();

        Switch passwordToggle = (Switch) findViewById( R.id.passOption );
        EditText passwordBox = (EditText) findViewById( R.id.eventPassword );
        String password = passwordBox.getText().toString().trim();
        Switch restrictionsToggle = (Switch) findViewById( R.id.restrictionsToggle );
        EditText restrictionsBox = (EditText) findViewById( R.id.eventRestrictions );
        String restrictions = restrictionsBox.getText().toString().trim();

        EditText descriptionBox = (EditText) findViewById( R.id.eventDescription );
        String description = descriptionBox.getText().toString().trim();

        // Checks for input errors.
        if ( name.isEmpty() ) {
            Toast.makeText( CreateEvent.this, "Name field cannot be empty.",
                    Toast.LENGTH_SHORT ).show();
            return;
        }
        if ( address.isEmpty() ) {
            Toast.makeText( CreateEvent.this, "Address field cannot be empty.",
                    Toast.LENGTH_SHORT ).show();
            return;
        }
        if ( passwordToggle.isChecked() && ( password.isEmpty() ) ) {
            Toast.makeText( CreateEvent.this, "Password is enabled, but none was chosen.",
                    Toast.LENGTH_SHORT ).show();
            return;
        }
        if ( restrictionsToggle.isChecked() && ( restrictions.isEmpty() ) ) {
            Toast.makeText( CreateEvent.this, "Restrictions are enabled, but none was chosen.",
                    Toast.LENGTH_SHORT ).show();
            return;
        }
        if ( description.isEmpty() ) {
            Toast.makeText( CreateEvent.this, "Description field cannot be empty.",
                    Toast.LENGTH_SHORT ).show();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventInDatabase = mDatabase.child( Identifiers.FIREBASE_EVENTS );
        if ( EventDetailActivity.user_editting_flag == 1 ) {
            eventInDatabase = eventInDatabase.child( MapView.swiped_item_uid );
        } else {
            eventInDatabase = eventInDatabase.push();
        }
        String uid = eventInDatabase.getKey();
        Event newEvent = new Event( uid, curUser.getUid() );




            // Records the data in the Event.
            newEvent.setName(name);

            newEvent.setAddress(address);




        if(EventDetailActivity.user_editting_flag == 1)
        {
            Event card = getIntent().getParcelableExtra("event_card");

            if (place == null && !address.equals(card.getAddress())) {
                Toast.makeText(CreateEvent.this, "You must pick a location from map!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            newEvent.setLng(card.getLng());
            newEvent.setLat(card.getLat());

        }
        else {
            if (place == null) {
                Toast.makeText(CreateEvent.this, "You must pick a location from map!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            newEvent.setLng(place.getLatLng().longitude);
            newEvent.setLat(place.getLatLng().latitude);


        }
            newEvent.setDate(selected);

            newEvent.setHasPassword(passwordToggle.isChecked());
            newEvent.setPassword(password);
            newEvent.setHasRestrictions(restrictionsToggle.isChecked());
            String[] restrictionList = restrictions.split("\n");
            newEvent.setRestrictions(Arrays.asList(restrictionList));

            newEvent.setDescription(description);

            eventInDatabase.setValue(newEvent);


            if(EventDetailActivity.user_editting_flag == 1)
            {
                editedCard = newEvent;
                EventDetailActivity.user_editting_flag = 0;

            }

            finish();


    }

    private void checkValidDate() {

        TextView eventDate = (TextView) findViewById( R.id.eventDate );
        TextView eventTime = (TextView) findViewById( R.id.eventTime );
        TextView errorMessage = (TextView) findViewById( R.id.invalidDateMessage );

        if ( !selected.isPast() ) {
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
