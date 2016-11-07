package edu.ucsd.cse110.group50.eventfinder;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that stores the data for a single event.
 * Is identified by an UID that is defined at creation and cannot be changed.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 1.0
 */
public class Event implements Parcelable {

    /* Instance fields */

    private final long uid;
    private String name;
    private final long host;

    private byte hour;
    private byte minute;

    private byte day;
    private byte month;
    private short year;

    private String address;
    // private ?? location; (coordinates)

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

    /* Ctors */

    /**
     * Creates a new instance of this class with a given UID and host.
     *
     * @param uid UID of this instance.
     * @param host UID of the host of this instance.
     */
    public Event( long uid, long host ) {

        this( uid, "", host );

    }

    /**
     * Creates a new instance of this class with a given UID, name and host.
     *
     * @param uid UID of this instance.
     * @param name Initial name of this instance.
     * @param host UID of the host of this instance.
     */
    public Event( long uid, String name, long host ) {

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

        uid = in.readLong();
        name = in.readString();
        host = in.readLong();

        hour = in.readByte();
        minute = in.readByte();

        day = in.readByte();
        month = in.readByte();
        year = (short) in.readInt();

        address = in.readString();

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

    /* Getters */

    /**
     * Retrieves the UID of this instance.
     *
     * @return The UID of this instance.
     */
    public long getUid() {

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
    public long getHost() {

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

        dest.writeLong( uid );
        dest.writeString( name );
        dest.writeLong( host );

        dest.writeByte( hour );
        dest.writeByte( minute );

        dest.writeByte( day );
        dest.writeByte( month );
        dest.writeInt( year );

        dest.writeString( address );

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

}
