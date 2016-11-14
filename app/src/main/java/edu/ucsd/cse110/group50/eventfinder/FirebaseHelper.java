package edu.ucsd.cse110.group50.eventfinder;

/**
 * Created by Leon_Lyn on 2016/11/6.
 */
import android.provider.ContactsContract;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.Iterator;

public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved=null;
    ArrayList<Card> events=new ArrayList<>();
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
    public ArrayList<Card> retrieve()
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
        System.out.println("IN retrieve(), events size is "+events.size());
        return events;
    }
    private void fetchData(DataSnapshot dataSnapshot)
    {
        //events.clear();
        if(dataSnapshot.getChildrenCount() == events.size())
        {
            return;
        }
        //System.out.println(dataSnapshot.getRef());
        //System.out.println(dataSnapshot.getKey());
        //System.out.println(dataSnapshot.getChildrenCount());


        //System.out.println(dataSnapshot.getChildren());
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            if(!dataSnapshot.getKey().equals("events"))
            {
                continue;
            }
            System.out.println("LOOP ENTERED");
            System.out.println(ds);
            //continue;
            Card event = new Card(
                    ds.getValue(Card.class).getOwnerName(),
                    ds.getValue(Card.class).getEventName(),
                    ds.getValue(Card.class).getEventDate(),
                    ds.getValue(Card.class).getEventDescription(),
                    ds.getValue(Card.class).getLocation(),
                    ds.getValue(Card.class).getAddress()
            );
            //Card event=ds.getValue(Card.class);
            events.add(event);

            System.out.println("IN fetchData(), current eventsize is "+ events.size());
        }
    }
}