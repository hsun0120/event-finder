package edu.ucsd.cse110.group50.eventfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.*;

import edu.ucsd.cse110.group50.eventfinder.storage.Event;
import edu.ucsd.cse110.group50.eventfinder.storage.EventList;
import edu.ucsd.cse110.group50.eventfinder.utility.Identifiers;

/**
 * This class is a dialog window that helps to confirm whether the user want to delete an event.
 * @Author Yining Liang
 */
public class DeleteDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_delete_dialog );

    }


    public void delete_yes( View v ) {

        Event ev = getIntent().getParcelableExtra( Identifiers.EVENT );
        EventList list = EventList.getInstance();
        Log.v( "DeleteDialog", "Accepted deletion of " + ev.getUid() );
        list.remove( ev );
        finish();

    }

    public void delete_no( View v )
    {
        finish();
    }
}
