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
import android.widget.ImageView;
import android.widget.TextView;

import edu.ucsd.cse110.group50.eventfinder.storage.Event;
import edu.ucsd.cse110.group50.eventfinder.utility.Identifiers;
import edu.ucsd.cse110.group50.eventfinder.utility.LoadListener;

/**
 * An activity representing a single Event detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {}.
 */
public class EventDetailActivity extends AppCompatActivity {

    public static boolean userEnteredCorrectPassword;

    private static int currentPosition;
    private Event card;
    private LoadListener updater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(MapView.user_on_all_events_flag)
             userEnteredCorrectPassword = false;
        else
            userEnteredCorrectPassword = true;
        setContentView(R.layout.activity_event_detail);

        currentPosition = getIntent().getIntExtra("event_position", 1000);
        //System.out.println("Event detail, user swiped position "+ currentPosition);

        card = getIntent().getParcelableExtra( Identifiers.EVENT );
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

        card = (Event) getIntent().getParcelableExtra( Identifiers.EVENT );
        updater = new LoadListener() {

            @Override
            public void onLoadComplete(Object data) {

                setData();

            }

        };
        card.addListener( updater );

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
        dateView.setText( "From " + card.getDate().toString() );
        dateView.setTextColor(Color.BLACK);
        TextView endDate = (TextView) findViewById(R.id.event_detail_end_date);
        endDate.setText( "to " + card.getEndTime().toString() );
        endDate.setTextColor(Color.BLACK);
        TextView desView = (TextView) findViewById(R.id.event_detail_description_view);
        desView.setText(card.getDescription());
        TextView addressView = (TextView)findViewById(R.id.event_detail_address_text_view);
        addressView.setText(card.getAddress());
        addressView.setTextColor(Color.BLACK);
        TextView restrictionView = (TextView)findViewById(R.id.event_restriction);
        String restrictions = "Restrictions:\n";
        for ( String restriction : card.getRestrictions() ) {

            restrictions += restriction + "\n";

        }
        restrictionView.setText( restrictions );
        if(card.getHasPassword() && !userEnteredCorrectPassword)
        {
            dateView.setText("You don't have permission for details.");
            dateView.setTextColor(Color.RED);
            endDate.setText("");
            addressView.setText("Please reopen this event and enter password.");
            addressView.setTextColor(Color.RED);
            desView.setText("");
            restrictionView.setText("");
        }
    }

    /**
     * This is the button call back for when the user pressed on edit event button.
     * This call back redirects the user to create event class to edit current event.
     * @param v current view.
     */
    public void event_detail_edit(View v)
    {

        ////System.out.println("Event position is "+ eventPosition);
        MapView.swiped_position = currentPosition;
        MapView.swiped_item_uid = card.getUid();
        Intent in = new Intent(this, CreateEvent.class);
        in.putExtra( Identifiers.EDIT, true );
        in.putExtra( Identifiers.EVENT, card );
        startActivity(in);

    }

    @Override
    public void onStart()
    {
        super.onStart();
        //System.out.println("Event Detail on STAAAAAAAAART called.");

        if( !card.getHasRestrictions() )
        {
            ((TextView)findViewById(R.id.event_restriction)).setVisibility(View.INVISIBLE);
            ((ImageView)findViewById(R.id.imageView)).setVisibility(View.INVISIBLE);
        }
        else
        {
            ((TextView)findViewById(R.id.event_restriction)).setVisibility(View.VISIBLE);
            ((ImageView)findViewById(R.id.imageView)).setVisibility(View.VISIBLE);
        }
        //if(CreateEvent.editedCard != null)
        //System.out.println("Edited card is "+ CreateEvent.editedCard.getName());
        //System.out.println("Real card is "+( (Event)getIntent().getParcelableExtra("event_card")).getName());

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        card.removeListener( updater );

    }

}