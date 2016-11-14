package edu.ucsd.cse110.group50.eventfinder;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class that stores the data for a single user.
 * Is identified by an UID that is defined at creation and cannot be changed.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 2.1
 */
public class User implements Parcelable {

    /* Instance fields */

    private final String uid;
    private String name;

    private ArrayList<String> hostedEvents;
    private ArrayList<String> pastHosted;
    // private float score;

    private ArrayList<LoadListener> listeners;

    /* Constants */

    private static final String UID_CHILD = "uid";
    private static final String NAME_CHILD = "name";
    private static final String HOSTED_CHILD = "hostedEvents";
    private static final String PAST_CHILD = "pastHosted";
    private static final String TAG = "User";

    /* Ctors */

    /**
     * Creates a new instance of this class with a given UID.
     *
     * @param uid UID of this instance.
     */
    public User( String uid ) {

        this( uid, "" );

    }

    /**
     * Creates a new instance of this class with a given UID and name.
     *
     * @param uid UID of this instance.
     * @param name Initial name of this instance.
     */
    public User( String uid, String name ) {

        this.uid = uid;
        this.name = name;

        this.hostedEvents = new ArrayList<>();
        this.pastHosted = new ArrayList<>();
        //this.score = 0.0;

        this.listeners = new ArrayList<>();

    }

    /**
     * Makes a copy of an existing instance.
     *
     * @param u Existing instance to be copied.
     */
    public User( User u ) {

        this.uid = u.uid;
        this.name = u.name;

        this.hostedEvents = new ArrayList<>( u.hostedEvents );
        this.pastHosted = new ArrayList<>( u.pastHosted );
        //this.score = u.score;

        this.listeners = u.listeners;

    }

    /**
     * Constructor that recreates an instance of the class from a Parcel.
     * Can only be accessed by CREATOR.
     *
     * @param in Parcel that contains a flattened instance of the class.
     */
    private User( Parcel in ) {

        uid = in.readString();
        name = in.readString();

        hostedEvents = in.createStringArrayList();
        pastHosted = in.createStringArrayList();

    }

    /* Event-list methods */

    /**
     * Adds a new event to the list of hosted events.
     *
     * @param uid UID of the hosted event.
     * @return true if successfully added;
     *         false if was already in the list.
     */
    public boolean addHosted( String uid ) {

        return hostedEvents.add( uid );

    }

    /**
     * Removes an event from the list of hosted events.
     *
     * @param uid UID of the cancelled event.
     * @return true if successfully removed;
     *         false if not found.
     */
    public boolean removeHosted( String uid ) {

        return hostedEvents.remove( uid );

    }

    /**
     * Moves an event from the "going to host" list to the "hosted in the past" list.
     *
     * @param uid UID of the completed event.
     * @return true if successfully moved;
     *         false if not found as a hosted.
     */
    public boolean eventDone( String uid ) {

        boolean success = hostedEvents.remove( uid );
        if ( success ) {
            pastHosted.add( uid );
        }

        return success;

    }

    /**
     * Fills in the data (other than UID) for this instance from a given database.
     * The root of the database should be the node that corresponds to this object as a
     * whole.
     *
     * This method also attaches a change listener to the database, so this object will
     * always have the most recent data from server.
     *
     * @param mDatabase Database where the data is to be read from.
     */
    public void updateFromDatabase( final DatabaseReference mDatabase ) {

        // Listener that reads the data initially and every time something changes.
        ValueEventListener userListener = new ValueEventListener() {

            /**
             * Fills in the fields of the corresponding instance with the data from server
             * when any of it is updated in the server.
             *
             * @param data Snapshot of the data on the server.
             * @throws NullPointerException if the snapshot received is null.
             * @throws IllegalArgumentException when the snapshot received corresponds to data
             *                                  from a different UID.
             */
            @Override
            @SuppressWarnings("unchecked")
            public void onDataChange( DataSnapshot data ) throws NullPointerException,
                    IllegalArgumentException {

                if ( data == null ) {
                    throw new NullPointerException();
                }

                if ( !data.getKey().equals( uid ) ) {
                    Log.wtf( TAG, "Reading data from a different UID." );
                    throw new IllegalArgumentException( "UID mismatch" );
                }

                name = (String) data.child( NAME_CHILD ).getValue();
                List<String> list = (List<String>) data.child( HOSTED_CHILD ).getValue();
                List<String> list2 = (List<String>) data.child( PAST_CHILD ).getValue();
                if ( list != null ) {
                    for (String s : list) {

                        hostedEvents.add(s);

                    }
                }
                if ( list2 != null ) {
                    for (String s : list2) {

                        pastHosted.add(s);

                    }
                }

                for ( int i = 0; i < listeners.size(); i++ ) {

                    listeners.get( i ).onLoadComplete( User.this );

                }

            }

            /**
             * If fails to read from database, logs an error.
             *
             * @param databaseError Error received.
             */
            @Override
            public void onCancelled( DatabaseError databaseError ) {

                // Failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

            }
        };
        mDatabase.addValueEventListener( userListener );

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

    /* Getters */

    /**
     * Retrieves the UID of this instance.
     *
     * @return The UID of this instance.
     */
    public String getUid() {

        return uid;

    }

    /**
     * Retrieves the name of this instance.
     *
     * @return The name of this instance.
     */
    public String getName() {

        return name;

    }

    /**
     * Retrieves the UIDs of the events being hosted by this user.
     *
     * @return The events being hosted.
     */
    public ArrayList<String> getHostedEvents() {

        return new ArrayList<>( hostedEvents );

    }

    /**
     * Retrieves the UIDs of the events hosted by this user in the past.
     *
     * @return The events hosted by the user in the past.
     */
    public ArrayList<String> getPastHosted() {

        return new ArrayList<>( pastHosted );

    }

    /* Setters */

    /**
     * Sets the name of this instance.
     *
     * @param name New name of this instance.
     */
    public void setName( String name ) {

        this.name = name;

    }

    /* Parcelable requirements */

    /**
     * Flattens this instance to a Parcel.
     *
     * @param dest Parcel where the flattened instance should be stored.
     * @param flags Not used.
     *
     */
    @Override
    public void writeToParcel( Parcel dest, int flags ) {

        dest.writeString( uid );
        dest.writeString( name );

        dest.writeStringList( hostedEvents );
        dest.writeStringList( pastHosted );

    }

    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * Used to generate an instance of this class from a Parcel.
     */
    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel( Parcel in ) {

            return new User( in );

        }

        @Override
        public User[] newArray( int size ) {

            return new User[size];

        }

    };

    /**
     * Creates a new User instance from the data stored in the given database.
     * The root of the database corresponds to the node containing to the desired User object.
     * If the User does not currently exist, initializes it.
     *
     * @param mDatabase Database to be read.
     * @param listener Event handler for when the User is completed.
     * @param uid UID of the user being read.
     */
    public static void readFromFirebase( final DatabaseReference mDatabase,
                                         LoadListener listener, String uid ) {

        // Listener that reads the UID and host, and creates the new instance.
        UserBuilder userListener = new UserBuilder( mDatabase, listener, uid );
        mDatabase.addListenerForSingleValueEvent( userListener );

    }

    /**
     * Listener class that performs the initial read of an User from the database.
     * Creates the User in the database if it did not exist.
     *
     * @author Thiago Marback
     * @version 1.1
     * @since 2016-11-13
     */
    private static class UserBuilder implements ValueEventListener {

        private User newUser;
        final DatabaseReference mDatabase;
        LoadListener listener;
        String uid;

        /**
         * Creates a new builder with the given database.
         *
         * @param mDatabase database to be read.
         * @param listener Event handler for when the User is completed.
         * @param uid UID of the User.
         */
        UserBuilder( final DatabaseReference mDatabase, LoadListener listener,
                     String uid ) {

            this.mDatabase = mDatabase;
            this.listener = listener;
            this.uid = uid;

        }

        /**
         * Reads the UID of the User in the database given, making a new User object with
         * that UID. Then fills it in with the remaining data in the database.
         *
         * @param data Snapshot of the data on the server.
         * @throws NullPointerException if the snapshot received is null.
         */
        @Override
        @SuppressWarnings("unchecked")
        public void onDataChange( DataSnapshot data ) throws NullPointerException {

            System.out.println( "CHANGE" );
            if ( data == null ) {
                throw new NullPointerException();
            }

            if ( data.exists() ) {
                String uid = (String) data.child( UID_CHILD ).getValue();
                newUser = new User( uid );
            } else {
                newUser = new User( this.uid );
                mDatabase.setValue( newUser );
            }

            newUser.updateFromDatabase( mDatabase );
            listener.onLoadComplete( newUser );

        }

        /**
         * If fails to read from database, logs an error.
         *
         * @param databaseError Error received.
         */
        @Override
        public void onCancelled( DatabaseError databaseError ) {

            // Failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

        }
    };

}
