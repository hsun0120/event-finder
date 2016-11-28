package edu.ucsd.cse110.group50.eventfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.*;

public class DeleteDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_dialog);
    }


    public void delete_yes(View v)
    {
        MapView.deleteItem();
        finish();
    }

    public void delete_no(View v)
    {
        finish();
    }
}