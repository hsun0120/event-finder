package edu.ucsd.cse110.group50.eventfinder;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Wrapper class that stores a list of events.
 *
 * @author Thiago Marback
 * @version 1.0
 * @since 2016-11-14
 */
public class EventList implements Iterable<Event>, List<Event> {

    private ArrayList<Event> eventList;
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
    public EventList( DatabaseReference mDatabase) {

        eventList = new ArrayList<>();
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

                                eventList.add( (Event) data );
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

                                eventList.remove( (Event) data );
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

    @Override
    public int size() {
        return eventList.size();
    }

    @Override
    public boolean isEmpty() {
        return eventList.isEmpty();
    }

    @Override
    public boolean contains( Object o ) {
        return eventList.contains( o );
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return eventList.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray( T[] a ) {
        return eventList.toArray( a );
    }

    @Override
    public boolean remove( Object o ) {
        return eventList.remove( o );
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return eventList.containsAll( c );
    }

    @Override
    public boolean addAll( Collection<? extends Event> c ) {
        return eventList.addAll( c );
    }

    @Override
    public boolean addAll( int index, Collection<? extends Event> c ) {
        return eventList.addAll( index, c );
    }

    @Override
    public boolean removeAll( Collection<?> c ) {
        return eventList.removeAll( c );
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        return eventList.retainAll( c );
    }

    @Override
    public void clear() {
        eventList.clear();
    }

    @Override
    public Event get( int index ) {
        return eventList.get( index );
    }

    @Override
    public Event set( int index, Event element ) {
        return eventList.set( index, element );
    }

    @Override
    public boolean add( Event e ) {
        return eventList.add( e );
    }

    @Override
    public void add( int index, Event element ) {
        eventList.add( index, element );
    }

    @Override
    public Event remove( int index ) {
        return eventList.remove( index );
    }

    @Override
    public int indexOf( Object o ) {
        return eventList.indexOf( o );
    }

    @Override
    public int lastIndexOf( Object o ) {
        return eventList.lastIndexOf( o );
    }

    @Override
    public ListIterator<Event> listIterator() {
        return eventList.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Event> listIterator( int index ) {
        return eventList.listIterator( index );
    }

    @NonNull
    @Override
    public List<Event> subList( int fromIndex, int toIndex ) {
        return eventList.subList( fromIndex, toIndex );
    }

    @Override
    public Iterator<Event> iterator() {
        return eventList.iterator();
    }


    public ArrayList<Event> getEventList(){
        return eventList;
    }


}
