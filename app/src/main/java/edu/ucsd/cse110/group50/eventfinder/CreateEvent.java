package edu.ucsd.cse110.group50.eventfinder;

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


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;



public class CreateEvent extends AppCompatActivity {
    private static final  int NAME_LENGTH_MAX = 30;
    private static final  int NAME_LENGTH_TOREAD = 30;

    private int selectedDay, selectedMonth, selectedYear;
    private int selectedHour, selectedMinute;

    //event card to add to firebase
    private Card eventToAdd = new Card();


    public static class MessageViewHolder extends RecyclerView.ViewHolder {





        public TextView eventName;
        public TextView eventDate;
        public TextView eventDes;
        public MessageViewHolder(View v) {
            super(v);
            eventName = (TextView) itemView.findViewById(R.id.info_title);
            eventDate = (TextView) itemView.findViewById(R.id.info_date);
            eventDes = (TextView) itemView.findViewById(R.id.info_des);
        }

    }
    //vars needed for database communication
    // Firebase instance variables
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mMessageRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Card, MessageViewHolder> mFirebaseAdapter;
    private Button create_button;
    //edit text
    EditText eventNameEdit;
    EditText eventDateEdit;
    EditText eventDescriptionEdit;










    /**************Other work****************/

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
        create_button = (Button) findViewById(R.id.doneButton);




//        //Communicate with firebase
//        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        mFirebaseAdapter = new FirebaseRecyclerAdapter<Card,
//                MessageViewHolder>(
//                Card.class,
//                R.layout.activity_my_events,
//                MessageViewHolder.class,
//                mFirebaseDatabaseReference.child("events")) {
//
//            @Override
//            protected void populateViewHolder(MessageViewHolder viewHolder,
//                                              Card eventCard, int position) {
//                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                viewHolder.eventName.setText(eventCard.getCardName());
//                viewHolder.eventDate.setText(eventCard.getDate());
//                viewHolder.eventDes.setText(eventCard.getDescription());
//
//            }
//        };

//        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                int numOfEvents = mFirebaseAdapter.getItemCount();
////                int lastVisiblePosition =
////                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//                // If the recycler view is initially being loaded or the
//                // user is at the bottom of the list, scroll to the bottom
//                // of the list to show the newly added message.
////                if (lastVisiblePosition == -1 ||
////                        (positionStart >= (numOfEvents - 1) &&
////                                lastVisiblePosition == (positionStart - 1))) {
////                    mMessageRecyclerView.scrollToPosition(positionStart);
////                }
//            }
//        });


//        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mMessageRecyclerView.setAdapter(mFirebaseAdapter);





    //listener for event name text field
        eventNameEdit = (EditText) findViewById(R.id.eventName);
//        eventNameEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
//                .getInt(NAME_LENGTH_TOREAD, NAME_LENGTH_MAX))});
        eventNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    create_button.setEnabled(true);
                } else {
                    create_button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        //listner for event description
        eventDescriptionEdit = (EditText) findViewById(R.id.eventDescription);
//        eventNameEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
//                .getInt(NAME_LENGTH_TOREAD, NAME_LENGTH_MAX))});
        eventDescriptionEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    create_button.setEnabled(true);
                } else {
                    create_button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });






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

    public void closeTime( View v ) {

        CardView timeCard = (CardView) findViewById( R.id.timeCard );
        timeCard.setVisibility( View.INVISIBLE );
        ImageView blur = (ImageView) findViewById( R.id.backgroundBlur );
        blur.setVisibility( View.INVISIBLE );
        setFormClickable( true );

    }

    public void createEvent( View v ) {
        //send event to firebase
        eventToAdd.setEventDate(selectedYear+"/"+selectedMonth+"/"+selectedDay+" "+selectedHour+":"+selectedMinute);
        eventToAdd.setEventName(eventNameEdit.getText().toString());
        eventToAdd.setEventDescription(eventDescriptionEdit.getText().toString());
        mFirebaseDatabaseReference.child("events")
                .push().setValue(eventToAdd);
        eventNameEdit.setText("");

        //System.out.println(selectedYear+"/"+selectedMonth+"/"+selectedDay+" "+selectedHour+":"+selectedMinute);

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

}
