package edu.ucsd.cse110.group50.eventfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import edu.ucsd.cse110.group50.eventfinder.utility.Identifiers;

public class NewUser extends AppCompatActivity {

    static final int CREATED = 420;
    static final String TAG = "NewUser";

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        Log.d( TAG, "New user detected." );

    }

    public void registerUser( View v ) {

        EditText nameBox = (EditText) findViewById( R.id.newUserName );
        String name = nameBox.getText().toString();
        if ( name.isEmpty() ) {
            Toast.makeText( NewUser.this, "Name cannot be empty.",
                    Toast.LENGTH_SHORT ).show();
            return;
        }
        name = name.trim();

        Intent data = new Intent();
        data.putExtra( Identifiers.USER_NAME, name );
        setResult( CREATED, data );
        finish();

    }

}
