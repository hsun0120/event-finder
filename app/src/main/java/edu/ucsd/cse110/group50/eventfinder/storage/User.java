package edu.ucsd.cse110.group50.eventfinder.storage;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.ucsd.cse110.group50.eventfinder.utility.Identifiers;
import edu.ucsd.cse110.group50.eventfinder.utility.LoadListener;
import edu.ucsd.cse110.group50.eventfinder.utility.ServerLog;

/**
 * Class that stores the data for a single user.
 * Is identified by an UID that is defined at creation and cannot be changed.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 3.0
 */
public class User implements Parcelable {

    /* Instance fields */

    private final String uid;
    private String name;

    // private float score;

    private ArrayList<LoadListener> listeners;

    /* Constants */

    private static final String UID_CHILD = "uid";
    private static final String NAME_CHILD = "name";
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

        listeners = new ArrayList<>();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        updateFromDatabase( database.child( Identifiers.FIREBASE_USERS ).child( uid ) );

        Log.v( TAG, "Extracted User " + name + " (" + uid + ") from Parcel." );

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
                    Log.wtf( TAG, "Was reading " + uid + ", got " + data.getKey() );
                    throw new IllegalArgumentException( "UID mismatch: " + uid + " | " + data.getKey() );
                }

                name = (String) data.child( NAME_CHILD ).getValue();

                for ( int i = 0; i < listeners.size(); i++ ) {

                    listeners.get( i ).onLoadComplete( User.this );

                }

                Log.v( TAG, "Updated User " + name + " (" + uid + ")." );

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

        Log.v( TAG, "Flattened User " + name + " (" + uid + ") to Parcel." );

    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
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
        public void onDataChange( DataSnapshot data ) throws NullPointerException {

            if ( data == null ) {
                throw new NullPointerException();
            }

            User newUser = new User( uid );
            if ( data.exists() ) {
                if ( !data.child( UID_CHILD ).getValue().equals( uid ) ) {
                    Log.e( TAG, "ID MISMATCH ON DATABASE - NODE IS " + uid + ", OBJECT IS " +
                            data.child( UID_CHILD ).getValue() );
                    ServerLog.s( TAG, "USER ID MISMATCH ON " + " DATABASE - NODE IS " + uid +
                            ", OBJECT IS " + data.child( UID_CHILD ).getValue() );
                    mDatabase.child( UID_CHILD ).setValue( uid );
                }
            } else {
                mDatabase.setValue( newUser );
            }

            newUser.updateFromDatabase( mDatabase );
            listener.onLoadComplete( newUser );

            Log.v( TAG, "Reading User ID " + uid + " from database." );

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
