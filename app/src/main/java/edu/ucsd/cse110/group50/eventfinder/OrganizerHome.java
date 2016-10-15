package edu.ucsd.cse110.group50.eventfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Thiago on 10/9/2016.
 */

public class OrganizerHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_home);
    }

    public void newEvent( View v ) {

        Intent intent = new Intent( OrganizerHome.this, CreateEvent.class );
        startActivity( intent );
    }

    public void newDetails( View v ) {
        Intent details = new Intent(OrganizerHome.this, EventDetailActivity.class);
        startActivity(details);
    }

}
