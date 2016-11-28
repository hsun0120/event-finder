package edu.ucsd.cse110.group50.eventfinder.storage;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.ucsd.cse110.group50.eventfinder.utility.Identifiers;
import edu.ucsd.cse110.group50.eventfinder.utility.LoadListener;

/**
 * List class that stores a list of events.
 * Follows the Singleton pattern.
 *
 * @author Thiago Marback
 * @version 3.0
 * @since 2016-11-14
 */
public class EventList extends ArrayList<Event> {

    private ArrayList<LoadListener> listeners;

    private static EventList instance;

    private static final String UID_CHILD = "uid";
    private static final String HOST_CHILD = "host";

    private static final String TAG = "EventList";

    /**
     * Creates a new list of events that contains all Event objects that are stored
     * in the events node in Firebase.
     */
    private EventList() {

        super();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child( Identifiers.FIREBASE_EVENTS );
        listeners = new ArrayList<>();

        ChildEventListener childListener = new ChildEventListener() {

            /**
             * When a new child is added to the database, adds it to the list.
             *
             * @param dataSnapshot Snapshot of the new child.
             * @param s Sibling location ordered before this child.
             */
            @Override
            public void onChildAdded( DataSnapshot dataSnapshot, String s ) {

                Event.readFromFirebase( dataSnapshot.getRef(),
                        new LoadListener() {

                            @Override
                            public void onLoadComplete( Object data ) {

                                add( (Event) data );
                                notifyListeners( EventList.this );

                            }

                        },
                        (String) dataSnapshot.child( UID_CHILD ).getValue(),
                        (String) dataSnapshot.child( HOST_CHILD ).getValue() );

            }

            /**
             * Triggered when a child is changed. Already handled by the Event objects.
             *
             * @param dataSnapshot Snapshot of the child changed.
             * @param s Sibling location ordered before this child.
             */
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // Does nothing.

            }

            /**
             * When a child is deleted from the database, removes it from the list.
             *
             * @param dataSnapshot Snapshot of the removed child.
             */
            @Override
            public void onChildRemoved( DataSnapshot dataSnapshot ) {
//
//                System.out.println("onChildRemoved Called");
//
//                Event.readFromFirebase( dataSnapshot.getRef(),
//                        new LoadListener() {
//
//                            @Override
//                            public void onLoadComplete( Object data ) {
//
//                                remove( (Event) data );
//                                notifyListeners( EventList.this );
//
//                            }
//
//                        },
//                        (String) dataSnapshot.child( UID_CHILD ).getValue(),
//                        (String) dataSnapshot.child( HOST_CHILD ).getValue() );

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                // Do nothing.

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

            }
        };
        mDatabase.addChildEventListener( childListener );

    }

    public static EventList getInstance() {

        if ( instance == null ) {
            instance = new EventList();
        }
        return instance;

    }

    @Override
    public boolean remove( Object o ) throws IllegalArgumentException {

        if ( o.getClass() != Event.class ) {
            throw new IllegalArgumentException();
        }

        if ( !contains( o ) ) {
            return false;
        }

        Event ev = (Event) o;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child( Identifiers.FIREBASE_EVENTS ).child( ev.getUid() );
        mDatabase.removeValue();
        return super.remove(ev);
    }

    /**
     * Adds a new listener to be notified when the data on this instance is updated.
     *
     * @param listener Listener to be notified.
     */
    public void addListener( LoadListener listener ) {

        listeners.add( listener );

    }

    /**
     * Removes a listener from the notification list.
     *
     * @param listener Listener that should not be notified.
     */
    public void removeListener( LoadListener listener ) {

        listeners.remove( listener );

    }

    private void notifyListeners( Object o ) {

        for ( LoadListener listener : listeners ) {

            listener.onLoadComplete( o );

        }

    }

}
