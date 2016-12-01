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

                                if ( data == null ) {
                                    return;
                                }
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

                String uid = (String) dataSnapshot.child( Event.UID_CHILD ).getValue();
                Event deleted = new Event( uid , "" );
                remove( deleted );
                notifyListeners( EventList.this );

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

    /**
     * Returns the EventList. Creates it if it hasn't been initialized yet.
     *
     * @return The instance of EventList.
     */
    public static EventList getInstance() {

        if ( instance == null ) {
            instance = new EventList();
        }
        return instance;

    }

    /**
     * Removes an Event from the list and from the database.
     *
     * @param o Event to be removed.
     * @return true if successfully removed.
     *         false if not found.
     * @throws IllegalArgumentException if the object received is not an Event.
     */
    @Override
    public boolean remove( Object o ) throws IllegalArgumentException {

        if ( o.getClass() != Event.class ) {
            throw new IllegalArgumentException();
        }

        int idx = indexOf( o );
        if ( idx == -1 ) {
            return false;
        }

        Event ev = get( idx );
        Log.v( TAG, "Removing " + ev.getName() + "|" + ev.getUid() );
        remove( idx );
        ev.deleteFromFirebase();

        return true;

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
