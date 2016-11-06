package edu.ucsd.cse110.group50.eventfinder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Class that stores the data for a single event.
 * Is identified by an UID that is defined at creation and cannot be changed.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 1.0
 */
public class Event implements Parcelable {

    private final long UID;
    private String name;
    private final long host;

    private byte hour;
    private byte minute;

    private byte day;
    private byte month;
    private byte year;

    private String address;
    // private ?? location; (coordinates)

    private boolean hasPassword;
    private String password;
    private boolean hasRestrictions;
    private ArrayList<String> restrictions;
    private String description;

    private static final byte TRUE = 1;
    private static final byte FALSE = 0;

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

        this.UID = uid;
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

        this.UID = u.UID;
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

        UID = in.readLong();
        name = in.readString();
        host = in.readLong();

        hour = in.readByte();
        minute = in.readByte();

        day = in.readByte();
        month = in.readByte();
        year = in.readByte();

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

    /**
     * {@inheritDoc}
     *
     * @param dest {@inheritDoc}
     * @param flags Not used.
     *
     */
    @Override
    public void writeToParcel( Parcel dest, int flags ) {

        dest.writeLong( UID );
        dest.writeString( name );
        dest.writeLong( host );

        dest.writeByte( hour );
        dest.writeByte( minute );

        dest.writeByte( day );
        dest.writeByte( month );
        dest.writeByte( year );

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
