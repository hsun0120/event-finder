package edu.ucsd.cse110.group50.eventfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.net.Uri;
import android.widget.TextView;

import edu.ucsd.cse110.group50.eventfinder.storage.Event;

/**
 * An activity representing a single Event detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {}.
 */
public class EventDetailActivity extends AppCompatActivity {
    public static boolean userEnteredCorrectPassword;

    public static int user_editting_flag;

    static int currentPosition;
    static Context currContext;
    static Event card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currContext = getApplicationContext();
        if(MapView.user_on_all_events_flag)
             userEnteredCorrectPassword = false;
        else
            userEnteredCorrectPassword = true;
        setContentView(R.layout.activity_event_detail);

        currentPosition = getIntent().getIntExtra("event_position", 1000);
        //System.out.println("Event detail, user swiped position "+ currentPosition);

        card = getIntent().getParcelableExtra("event_card");
        String eventPassword = card.getPassword();

        if(!userEnteredCorrectPassword) {
            if (!eventPassword.equals("")) {
                Intent checkpasswordIntent = new Intent(this, CheckEventPassword.class);
                checkpasswordIntent.putExtra("password", eventPassword);
                startActivity(checkpasswordIntent);
            }
        }

        if(MapView.user_on_all_events_flag)
        {
            ((FloatingActionButton)findViewById(R.id.floatingActionButton2)).setVisibility(View.INVISIBLE);
        }


        //System.out.println("Event detail ONCREEEEEATE Called");

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MapView.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void showMap(View v) {
        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    /**
     * This method helps to initialize view of event detail.
     * It hides event detail when an user has not entered the correct password for current selected event.
     */
    private void setData(){
        ////System.out.println("User entered correct pasword is "+userEnteredCorrectPassword);

        //Set data here
        ((TextView) findViewById(R.id.event_detail_title)).setText(card.getName());
        TextView dateView = (TextView) findViewById(R.id.event_detail_date_view);
        dateView.setText( card.getDate().getDate() );
        dateView.setTextColor(Color.BLACK);
        TextView desView = (TextView) findViewById(R.id.event_detail_description_view);
        desView.setText(card.getDescription());
        TextView addressView = (TextView)findViewById(R.id.event_detail_address_text_view);
        addressView.setText(card.getAddress());
        addressView.setTextColor(Color.BLACK);

        if(card.getHasPassword() && !userEnteredCorrectPassword)
        {
            dateView.setText("You don't have permission for details.");
            dateView.setTextColor(Color.RED);
            addressView.setText("Please reopen this event and enter password.");
            addressView.setTextColor(Color.RED);
            desView.setText("");
        }
    }

    /**
     * This is the button call back for when the user pressed on edit event button.
     * This call back redirects the user to create event class to edit current event.
     * @param v current view.
     */
    public void event_detail_edit(View v)
    {
        user_editting_flag = 1;

        ////System.out.println("Event position is "+ eventPosition);
        MapView.swiped_position = currentPosition;
        MapView.swiped_item_uid = card.getUid();
        Intent in = new Intent(this, CreateEvent.class);
        in.putExtra( "event_card", getIntent().getParcelableExtra("event_card") );
        startActivity(in);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //System.out.println("Event Detail on STAAAAAAAAART called.");

        card = (CreateEvent.editedCard != null) ? CreateEvent.editedCard : (Event)getIntent().getParcelableExtra("event_card");
        //if(CreateEvent.editedCard != null)
        //System.out.println("Edited card is "+ CreateEvent.editedCard.getName());
        //System.out.println("Real card is "+( (Event)getIntent().getParcelableExtra("event_card")).getName());
        setData();


    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

}