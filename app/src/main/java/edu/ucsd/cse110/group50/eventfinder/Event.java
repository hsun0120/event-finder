package edu.ucsd.cse110.group50.eventfinder;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that stores the data for a single event.
 * Is identified by an UID that is defined at creation and cannot be changed.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 2.0
 */
public class Event implements Parcelable {

    /* Instance fields */

    private final String uid;
    private String name;
    private final String host;

    private byte hour;
    private byte minute;

    private byte day;
    private byte month;
    private short year;

    private String address;
    //private Uri location;

    private boolean hasPassword;
    private String password;
    private boolean hasRestrictions;
    private ArrayList<String> restrictions;
    private String description;

    /* Constants */

    private static final byte TRUE = 1;
    private static final byte FALSE = 0;

    private static final String TIME_SEPARATOR = ":";
    private static final String DATE_SEPARATOR = "/";

    private static final String UID_CHILD = "uid";
    private static final String NAME_CHILD = "name";
    private static final String HOST_CHILD = "host";

    private static final String HOUR_CHILD = "hour";
    private static final String MINUTE_CHILD = "minute";

    private static final String DAY_CHILD = "day";
    private static final String MONTH_CHILD = "month";
    private static final String YEAR_CHILD = "year";

    private static final String ADDRESS_CHILD = "address";
    private static final String LOCATION_CHILD = "location";

    private static final String HAS_PASSWORD_CHILD = "hasPassword";
    private static final String PASSWORD_CHILD = "password";
    private static final String HAS_RESTRICTIONS_CHILD = "hasRestrictions";
    private static final String RESTRICTIONS_CHILD = "restrictions";
    private static final String DESCRIPTION_CHILD = "description";

    private static final String TAG = "User";

    /* Ctors */

    /**
     * Creates a new instance of this class with a given UID and host.
     *
     * @param uid UID of this instance.
     * @param host UID of the host of this instance.
     */
    public Event(String uid, String host ) {

        this( uid, "", host );

    }

    /**
     * Creates a new instance of this class with a given UID, name and host.
     *
     * @param uid UID of this instance.
     * @param name Initial name of this instance.
     * @param host UID of the host of this instance.
     */
    public Event(String uid, String name, String host ) {

        this.uid = uid;
        this.name = name;
        this.host = host;

        this.hour = 0;
        this.minute = 0;

        this.day = 0;
        this.month = 0;
        this.year = 0;

        this.address = "";

        this.hasPassword = false;
        this.password = "";
        this.hasRestrictions = false;
        this.restrictions = new ArrayList<>();
        this.description = "";

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

        this.hour = u.hour;
        this.minute = u.minute;

        this.day = u.day;
        this.month = u.month;
        this.year = u.year;

        this.address = u.address;

        this.hasPassword = u.hasPassword;
        this.password = u.password;
        this.hasRestrictions = u.hasRestrictions;
        this.restrictions = new ArrayList<>( u.restrictions );
        this.description = u.description;

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

        hour = in.readByte();
        minute = in.readByte();

        day = in.readByte();
        month = in.readByte();
        year = (short) in.readInt();

        address = in.readString();
        //location = in.readParcelable( Uri.class.getClassLoader() );

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

    }

    /* Utility methods */

    /**
     * Creates a String that represents the time this event will be held.
     *
     * @return The time when this event will be held.
     */
    public String getTime() {

        return String.format( "%02d" + TIME_SEPARATOR + "%02d", hour, minute );

    }

    /**
     * Creates a String that represents the date this event will be held.
     *
     * @return The date when this event will be held.
     */
    public String getDate() {

        return String.format( "%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%04d",
                              month, day, year );

    }

    /**
     * Sets the time of this event.
     *
     * @param hour Hour when this event will be held.
     * @param minute Minute when this event will be held.
     */
    public void setTime( byte hour, byte minute ) {

        this.hour = hour;
        this.minute = minute;

    }

    /**
     * Sets the time of this event.
     *
     * @param time Time the event will be held, in the format hour:minute
     */
    public void setTime( String time ) {

        String[] timePieces = time.split( TIME_SEPARATOR );
        hour = Byte.valueOf( timePieces[0] );
        minute = Byte.valueOf( timePieces[1] );

    }

    /**
     * Sets the date of this event.
     *
     * @param day Day when this event will be held.
     * @param month Month when this event will be held.
     * @param year Year when this event will be held.
     */
    public void setDate( byte day, byte month, short year ) {

        this.day = day;
        this.month = month;
        this.year = year;

    }

    /**
     * Sets the date of this event.
     *
     * @param date Date the event will be held, in the format month/day/year
     */
    public void setDate( String date ) {

        String[] datePieces = date.split( DATE_SEPARATOR );
        month = Byte.valueOf( datePieces[0] );
        day = Byte.valueOf( datePieces[1] );
        year = Short.valueOf( datePieces[2] );

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
        ValueEventListener postListener = new ValueEventListener() {

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

                hour = (byte) data.child( HOUR_CHILD ).getValue();
                minute = (byte) data.child( MINUTE_CHILD ).getValue();

                day = (byte) data.child( DAY_CHILD ).getValue();
                month = (byte) data.child( MONTH_CHILD ).getValue();
                year = (short) data.child( YEAR_CHILD ).getValue();

                address = (String) data.child( ADDRESS_CHILD ).getValue();
                //location = (Uri) data.child( LOCATION_CHILD ).getValue();

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
        mDatabase.addValueEventListener( postListener );

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
     * Retrieves the hour of this event.
     *
     * @return The hour of this event.
     */
    public byte getHour() {

        return hour;

    }

    /**
     * Retrieves the minute of this event.
     *
     * @return The minute of this event.
     */
    public byte getMinute() {

        return minute;

    }

    /**
     * Retrieves the day of this event.
     *
     * @return The day of this event.
     */
    public byte getDay() {

        return day;

    }

    /**
     * Retrieves the month of this event.
     *
     * @return The month of this event.
     */
    public byte getMonth() {

        return month;

    }

    /**
     * Retrieves the year of this event.
     *
     * @return The year of this event.
     */
    public short getYear() {

        return year;

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
     * Sets the hour of this event.
     *
     * @param hour New hour of this event.
     */
    public void setHour( byte hour ) {

        this.hour = hour;

    }

    /**
     * Sets the minute of this event.
     *
     * @param minute New minute of this event.
     */
    public void setMinute( byte minute ) {

        this.minute = minute;

    }

    /**
     * Sets the day of this event.
     *
     * @param day New day of this event.
     */
    public void setDay( byte day ) {

        this.day = day;

    }

    /**
     * Sets the month of this event.
     *
     * @param month New month of this event.
     */
    public void setMonth( byte month ) {

        this.month = month;

    }

    /**
     * Sets the year of this event.
     *
     * @param year New year of this event.
     */
    public void setYear( short year ) {

        this.year = year;

    }

    /**
     * Sets the address of this event.
     * @param address New address of this event.
     */
    public void setAddress( String address ) {

        this.address = address;

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
    public void setRestrictions( ArrayList<String> restrictions ) {

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

        dest.writeByte( hour );
        dest.writeByte( minute );

        dest.writeByte( day );
        dest.writeByte( month );
        dest.writeInt( year );

        dest.writeString( address );
        //dest.writeParcelable( location, 0 );

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

    }

    @Override
    public int describeContents() {
        return 0;
    }

    /*
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
     *
     * @param mDatabase Database to be read.
     * @return The Event stored in the database.
     */
    public static Event readFromFirebase( final DatabaseReference mDatabase ) {

        // Listener that reads the UID and host, and creates the new instance.
        EventBuilder eventListener = new EventBuilder( mDatabase );
        mDatabase.addListenerForSingleValueEvent( eventListener );
        return eventListener.getNewEvent();

    }

    /**
     * Listener class that performs the initial read of an Event from the database.
     *
     * @author Thiago Marback
     * @version 1.0
     * @since 2016-11-13
     */
    private static class EventBuilder implements ValueEventListener {

        Event newEvent;
        final DatabaseReference mDatabase;

        /**
         * Creates a new builder with the given database.
         *
         * @param mDatabase database to be read.
         */
        EventBuilder( final DatabaseReference mDatabase ) {

            this.mDatabase = mDatabase;

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
        public synchronized void onDataChange( DataSnapshot data ) throws NullPointerException {

            if ( data == null ) {
                throw new NullPointerException();
            }

            String uid = (String) data.child( UID_CHILD ).getValue();
            String host = (String) data.child( HOST_CHILD ).getValue();
            newEvent = new Event( uid, host );
            newEvent.updateFromDatabase( mDatabase );

        }

        /**
         * Gets the User that was created by this instance.
         * If the user is currently being built, this will wait for it to be completed
         * (sychronized method).
         *
         * @return The newly created User.
         */
        public synchronized Event getNewEvent() {

            return newEvent;

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
