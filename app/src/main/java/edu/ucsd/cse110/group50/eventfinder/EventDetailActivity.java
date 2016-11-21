package edu.ucsd.cse110.group50.eventfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.net.Uri;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * An activity representing a single Event detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {}.
 */
public class EventDetailActivity extends AppCompatActivity {
    public static boolean userEnteredCorrectPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        //Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setData();

    }

//    @Override
//    public void onResume()
//    {
//        System.out.println("EventDetail onResume called");
//        super.onResume();
//        setData();
//    }



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



    public void createEvent( View v ){
        finish();
    }

    public void showMap(View v) {
        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    private void setData(){
        Event card = getIntent().getParcelableExtra("event_card");
        String eventPassword = card.getPassword();

        if(!userEnteredCorrectPassword) {
            if (!eventPassword.equals("")) {
                Intent checkpasswordIntent = new Intent(this, CheckEventPassword.class);
                checkpasswordIntent.putExtra("password", eventPassword);
                startActivity(checkpasswordIntent);
            }
        }





        //Set data here
        setTitle(card.getName());
        TextView dateView = (TextView) findViewById(R.id.event_detail_date_view);
        dateView.setText(card.getDate());
        TextView desView = (TextView) findViewById(R.id.event_detail_description_view);
        desView.setText(card.getDescription());
        TextView addressView = (TextView)findViewById(R.id.event_detail_address_text_view);
        addressView.setText(card.getAddress());
    }

}