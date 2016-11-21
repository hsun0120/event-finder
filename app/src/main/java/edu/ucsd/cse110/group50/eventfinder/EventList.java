package edu.ucsd.cse110.group50.eventfinder;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * List class that stores a list of events.
 *
 * @author Thiago Marback
 * @version 2.0
 * @since 2016-11-14
 */
public class EventList extends ArrayList<Event> {

    private ArrayList<LoadListener> listeners;

    private static final String UID_CHILD = "uid";
    private static final String HOST_CHILD = "host";

    private static final String TAG = "EventList";

    /**
     * Creates a new list of events that contains all Event objects that are stored
     * directly under the given Database node.
     *
     * @param mDatabase Database to read the Events from. Must be the root of a list of Events.
     */
    public EventList( DatabaseReference mDatabase ) {

        super();
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

                Event.readFromFirebase( dataSnapshot.getRef(),
                        new LoadListener() {

                            @Override
                            public void onLoadComplete( Object data ) {

                                remove( (Event) data );
                                notifyListeners( EventList.this );

                            }

                        },
                        (String) dataSnapshot.child( UID_CHILD ).getValue(),
                        (String) dataSnapshot.child( HOST_CHILD ).getValue() );

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
