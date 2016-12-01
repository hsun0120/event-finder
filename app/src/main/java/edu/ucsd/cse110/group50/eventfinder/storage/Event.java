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
import java.util.Calendar;
import java.util.List;

import edu.ucsd.cse110.group50.eventfinder.utility.Identifiers;
import edu.ucsd.cse110.group50.eventfinder.utility.LoadListener;
import edu.ucsd.cse110.group50.eventfinder.utility.ServerLog;

/**
 * Class that stores the data for a single event.
 * Is identified by an UID that is defined at creation and cannot be changed.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 3.1
 */
public class Event implements Parcelable {

    /* Instance fields */

    private final String uid;
    private String name;
    private final String host;

    private EvDate date;
    private int duration;

    private String address;
    //private String locId;
    private double lng;
    private double lat;

    private boolean hasPassword;
    private String password;
    private boolean hasRestrictions;
    private ArrayList<String> restrictions;
    private String description;

    private ArrayList<LoadListener> listeners;

    private ValueEventListener updater;
    private DatabaseReference database;

    /* Constants */

    private static final byte TRUE = 1;
    private static final byte FALSE = 0;

    static final String UID_CHILD = "uid";
    private static final String NAME_CHILD = "name";
    private static final String HOST_CHILD = "host";

    private static final String DATE_CHILD = "date";
    private static final String DURATION_CHILD = "duration";

    private static final String ADDRESS_CHILD = "address";
    private static final String LNG_CHILD = "lng";
    private static final String LAT_CHILD = "lat";

    private static final String HAS_PASSWORD_CHILD = "hasPassword";
    private static final String PASSWORD_CHILD = "password";
    private static final String HAS_RESTRICTIONS_CHILD = "hasRestrictions";
    private static final String RESTRICTIONS_CHILD = "restrictions";
    private static final String DESCRIPTION_CHILD = "description";

    private static final String TAG = "Event";

    /* Ctors */

    /**
     * Creates a new instance of this class with a given UID and host.
     *
     * @param uid UID of this instance.
     * @param host UID of the host of this instance.
     */
    public Event( String uid, String host ) {

        this( uid, "", host );

    }

    /**
     * Creates a new instance of this class with a given UID, name and host.
     *
     * @param uid UID of this instance.
     * @param name Initial name of this instance.
     * @param host UID of the host of this instance.
     */
    public Event( String uid, String name, String host ) {

        this.uid = uid;
        this.name = name;
        this.host = host;

        this.date = new EvDate();
        this.duration = 0;

        this.address = "";
        this.lat = 0.5;
        this.lng = 0.5;

        this.hasPassword = false;
        this.password = "";
        this.hasRestrictions = false;
        this.restrictions = new ArrayList<>();
        this.description = "";

        this.listeners = new ArrayList<>();

    }

    /**
     * Makes a copy of an existing instance.
     *
     * @param u Existing instance to be copied.
     */
    public Event( Event u ) {

        this.uid = u.uid;
        this.name = u.name;
        this.host = u.host;

        this.date = new EvDate( u.date );
        this.duration = u.duration;

        this.address = u.address;
        this.lng = u.lng;
        this.lat = u.lat;

        this.hasPassword = u.hasPassword;
        this.password = u.password;
        this.hasRestrictions = u.hasRestrictions;
        this.restrictions = new ArrayList<>( u.restrictions );
        this.description = u.description;

        this.listeners = u.listeners;

    }

    /**
     * Constructor that recreates an instance of the class from a Parcel.
     * Can only be accessed by CREATOR.
     *
     * @param in Parcel that contains a flattened instance of the class.
     */
    private Event( Parcel in ) {

        uid = in.readString();
        name = in.readString();
        host = in.readString();

        date = in.readParcelable( EvDate.class.getClassLoader() );
        duration = in.readInt();

        address = in.readString();
        lng = in.readDouble();
        lat = in.readDouble();

        if ( in.readByte() == FALSE ) {
            hasPassword = false;
        } else {
            hasPassword = true;
        }
        password = in.readString();
        if ( in.readByte() == FALSE ) {
            hasRestrictions = false;
        } else {
            hasRestrictions = true;
        }
        restrictions = in.createStringArrayList();
        description = in.readString();

        listeners = new ArrayList<>();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        updateFromDatabase( database.child( Identifiers.FIREBASE_EVENTS ).child( uid ) );

        Log.v( TAG, "Extracted Event " + name + " (" + uid + ") from Parcel." );

    }

    /* Utility methods */

    /**
     * Gets the time when the event ends.
     *
     * @return End time of the event.
     */
    public EvDate getEndTime() {

        Calendar endTime = Calendar.getInstance();
        endTime.set( date.getYear(), date.getMonth() - 1, date.getDay(), date.getHour(),
                     date.getMinute() + duration );

        return new EvDate( endTime );

    }

    /**
     * Checks if this is equal to a given object. It will be equal if the object is another
     * Event with the same UID.
     *
     * @param obj Object to be compared.
     * @return true if this and obj represent the same object.
     *         false otherwise.
     */
    @Override
    public boolean equals( Object obj ) {

        if ( obj == null ) {
            return false;
        }

        if ( obj.getClass() != Event.class ) {
            return false;
        }

        Event ev = (Event) obj;

        return uid.equals( ev.uid );

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

        database = mDatabase;

        // Listener that reads the data initially and every time something changes.
        updater = new ValueEventListener() {

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

                if ( !data.exists()) {
                    return;
                }

                if ( !data.getKey().equals( uid ) ) {
                    Log.wtf( TAG, "Reading data from a different UID." );
                    Log.wtf( TAG, "Was reading " + uid + ", got " + data.getKey() );
                    throw new IllegalArgumentException( "UID mismatch: " + uid + " | " + data.getKey() );
                }

                Log.v( TAG, "Loading event ID " + uid + " from database." );

                name = (String) data.child( NAME_CHILD ).getValue();

                date = data.child( DATE_CHILD ).getValue( EvDate.class );

                if(data.child(DURATION_CHILD).getValue() == null) throw new NullPointerException();

                long dur = (Long) data.child( DURATION_CHILD ).getValue();
                duration = (int) dur;

                address = (String) data.child( ADDRESS_CHILD ).getValue();
                lng = (double) data.child(LNG_CHILD).getValue();
                lat = (double) data.child(LAT_CHILD).getValue();

                hasPassword = (boolean) data.child( HAS_PASSWORD_CHILD ).getValue();
                password = (String) data.child( PASSWORD_CHILD ).getValue();
                hasRestrictions = (boolean) data.child( HAS_RESTRICTIONS_CHILD ).getValue();
                restrictions = new ArrayList<>();
                List<String> list = (List<String>) data.child( RESTRICTIONS_CHILD ).getValue();
                if ( list != null ) {
                    for (String s : list) {

                        restrictions.add(s);

                    }
                }
                description = (String) data.child( DESCRIPTION_CHILD ).getValue();

                for ( int i = 0; i < listeners.size(); i++ ) {

                    listeners.get( i ).onLoadComplete( Event.this );

                }

                Log.v( TAG, "Updated Event " + name + " (" + uid + ")." );

            }

            /**
             * If fails to read from database, logs an error.
             *
             * @param databaseError Error received.
             */
            @Override
            public void onCancelled( DatabaseError databaseError ) {

                // Failed, log a message
                Log.w(TAG, "Failed to load Event.", databaseError.toException());

            }
        };
        mDatabase.addValueEventListener( updater );

    }

    public void deleteFromFirebase() {

        database.removeEventListener( updater );
        database.removeValue();

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
     * Retrieves the host of this event.
     *
     * @return The host of this event.
     */
    public String getHost() {

        return host;

    }

    /**
     * Retrieves the date of this event.
     *
     * @return The date of this event.
     */
    public EvDate getDate() {

        return date;

    }

    /**
     * Retrieves the duration of this event, in minutes.
     *
     * @return The duration of this event.
     */
    public int getDuration() {

        return duration;

    }

    /**
     * Retrieves the address of this event.
     *
     * @return The address of this event.
     */
    public String getAddress() {

        return address;

    }

    /**
     * Retrieves the longitude
     * @return
     */
    public double getLng() {
        return this.lng;
    }

    /**
     * Retrieves the latitude
     * @return
     */
    public double getLat() {
        return this.lat;
    }

    /**
     * Checks if this event requires a password.
     *
     * @return true if password is required; false otherwise.
     */
    public boolean getHasPassword() {

        return hasPassword;

    }

    /**
     * Retrieves the password of this event.
     *
     * @return The password of this event.
     */
    public String getPassword() {

        return password;

    }

    /**
     * Checks if this event has restrictions.
     *
     * @return true if the event is restricted; false otherwise.
     */
    public boolean getHasRestrictions() {

        return hasRestrictions;

    }

    /**
     * Retrieves the restrictions of this event.
     *
     * @return The list of restrictions of this event.
     */
    public ArrayList<String> getRestrictions() {

        return new ArrayList<>( restrictions );

    }

    /**
     * Retrieves the description of this event.
     *
     * @return The description of this event.
     */
    public String getDescription() {

        return description;

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

    /**
     * Sets the date of this event.
     *
     * @param date New date of this event.
     */
    public void setDate( EvDate date ) {

        this.date = new EvDate( date );

    }

    /**
     * Sets the duration of this event, in minutes.
     *
     * @param duration New duration of this event.
     */
    public void setDuration( int duration ) {

        this.duration = duration;

    }

    /**
     * Sets the address of this event.
     * @param address New address of this event.
     */
    public void setAddress( String address ) {

        this.address = address;

    }

    /**
     * Set the longitude of this event
     * @param lng
     */
    public void setLng( Double lng) {
        this.lng = lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * Sets whether this event requires a password.
     *
     * @param hasPassword Whether this event requires a password.
     *                    true = requires password;
     *                    false = does not require password.
     */
    public void setHasPassword( boolean hasPassword ) {

        this.hasPassword = hasPassword;

    }

    /**
     * Sets the password of this event.
     *
     * @param password New password of this event.
     */
    public void setPassword( String password ) {

        this.password = password;

    }

    /**
     * Sets whether this events has restrictions.
     *
     * @param hasRestrictions Whether this event has restrictions.
     *                        true = restricted;
     *                        false = not restricted.
     */
    public void setHasRestrictions( boolean hasRestrictions ) {

        this.hasRestrictions = hasRestrictions;

    }

    /**
     * Sets the restrictions of this event.
     *
     * @param restrictions New restrictions of this event.
     */
    public void setRestrictions( List<String> restrictions ) {

        this.restrictions = new ArrayList<>( restrictions );

    }

    /**
     * Sets the description of this event.
     *
     * @param description New description of this event.
     */
    public void setDescription( String description ) {

        this.description = description;

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
        dest.writeString( host );

        dest.writeParcelable( date, 0 );
        dest.writeInt( duration );

        dest.writeString( address );
        dest.writeDouble(lng);
        dest.writeDouble(lat);

        if ( hasPassword ) {
            dest.writeByte( TRUE );
        } else {
            dest.writeByte( FALSE );
        }
        dest.writeString( password );
        if ( hasRestrictions ) {
            dest.writeByte( TRUE );
        } else {
            dest.writeByte( FALSE );
        }
        dest.writeStringList( restrictions );
        dest.writeString( description );

        Log.v( TAG, "Flattened Event " + name + " (" + uid + ") to Parcel." );

    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Used to generate an instance of this class from a Parcel.
     */
    public static final Parcelable.Creator<Event> CREATOR
            = new Parcelable.Creator<Event>() {

        @Override
        public Event createFromParcel( Parcel in ) {

            return new Event( in );

        }

        @Override
        public Event[] newArray( int size ) {

            return new Event[size];

        }

    };

    /**
     * Creates a new Event instance from the data stored in the given database.
     * The root of the database corresponds to the node containing to the desired Event object.
     * If the host ID in the database is different from the expected ID, cancels the read,
     * and the listener will receive null instead of the loaded Event.
     *
     * @param mDatabase Database to be read.
     * @param listener Event handler for when the Event is completed.
     * @param uid UID of the event being read.
     * @param host UID of the host of the event.
     */
    public static void readFromFirebase( final DatabaseReference mDatabase,
                                         LoadListener listener,
                                         String uid, String host ) {

        // Listener that reads the UID and host, and creates the new instance.
        EventBuilder eventListener = new EventBuilder( mDatabase, listener, uid, host );
        mDatabase.addListenerForSingleValueEvent( eventListener );

    }

    /**
     * Listener class that performs the initial read of an Event from the database.
     *
     * @author Thiago Marback
     * @version 1.3
     * @since 2016-11-13
     */
    private static class EventBuilder implements ValueEventListener {

        final DatabaseReference mDatabase;
        LoadListener listener;
        String uid;
        String host;

        /**
         * Creates a new builder with the given database.
         *
         * @param mDatabase database to be read.
         * @param listener Event handler for when the User is completed.
         * @param uid UID of the Event.
         * @param host UID of the host of the Event.
         */
        EventBuilder( final DatabaseReference mDatabase, LoadListener listener,
                      String uid, String host ) {

            this.mDatabase = mDatabase;
            this.listener = listener;
            this.uid = uid;
            this.host = host;

        }

        /**
         * Reads the UID of the User in the database given, making a new User object with
         * that UID. Then fills it in with the remaining data in the database.
         * If the host ID in the database is different from the expected ID, cancels the read.
         *
         * @param data Snapshot of the data on the server.
         * @throws NullPointerException if the snapshot received is null.
         */
        @Override
        @SuppressWarnings("unchecked")
        public void onDataChange( DataSnapshot data ) throws NullPointerException {

            if ( data == null ) {
                throw new NullPointerException();
            }

            Event newEvent = new Event( uid, host );
            if ( data.exists() ) {
                if ( !data.child( UID_CHILD ).getValue().equals( uid ) ) {
                    Log.e( TAG, "EVENT ID MISMATCH ON DATABASE - NODE IS " + uid + ", OBJECT IS " +
                            data.child( UID_CHILD ).getValue() );
                    ServerLog.s( TAG, "EVENT ID MISMATCH ON " + " DATABASE - NODE IS " + uid +
                            ", OBJECT IS " + data.child( UID_CHILD ).getValue() );
                    mDatabase.child( UID_CHILD ).setValue( uid );
                }
                if ( !data.child( HOST_CHILD ).getValue().equals( host ) ) {
                    Log.wtf( TAG, "HOST ID MISMATCH - EXPECTED " + host + ", GOT " +
                            data.child( HOST_CHILD ).getValue() );
                    listener.onLoadComplete( null );
                    return;
                }
            } else {
                Log.e( TAG, "Attempt to load a non-existant Event." );
                listener.onLoadComplete( null );
                return;
            }
            newEvent.updateFromDatabase( mDatabase );
            listener.onLoadComplete( newEvent );

            Log.v( TAG, "Reading Event ID " + uid + " from database." );

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

    }

}
