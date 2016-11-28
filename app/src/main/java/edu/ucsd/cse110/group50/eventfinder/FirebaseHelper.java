package edu.ucsd.cse110.group50.eventfinder;

/**
 * Created by Leon_Lyn on 2016/11/6.
 */
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;

import edu.ucsd.cse110.group50.eventfinder.storage.Event;

public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved=null;
    ArrayList<Object> events=new ArrayList<>();
    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }
    //SAVE
    public Boolean save(Event Event)
    {
        if(Event==null)
        {
            saved=false;
        }else {
            try
            {
                db.child("events").push().setValue(Event);
                saved=true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }
    //READ
    public ArrayList<Object> retrieve()
    {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return events;
    }
    private void fetchData(DataSnapshot dataSnapshot)
    {
        events.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {

        }
    }
}