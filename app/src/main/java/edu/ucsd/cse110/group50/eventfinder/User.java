package edu.ucsd.cse110.group50.eventfinder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that stores the data for a single user.
 * Is identified by an UID that is defined at creation and cannot be changed.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 1.0
 */
public class User implements Parcelable {

    private final long UID;
    private String name;
    // private Event[] hostedEvents;
    // private Event[] pastHosted;
    // private float score;

    /**
     * Creates a new instance of this class with a given UID. All other fields are initally
     * set to 0 or blank (empty).
     *
     * @param uid UID of this instance.
     */
    public User ( long uid ) {

        this( uid, "" );

    }

    /**
     * Creates a new instance of this class with a given UID and name.
     *
     * @param uid UID of this instance.
     * @param name Initial name of this instance.
     */
    public User( long uid, String name ) {

        this.UID = uid;
        this.name = name;
        //this.hostedEvents = new Event[];
        //this.pastHosted = new Event[];
        //this.score = 0.0;

    }

    /**
     * Makes a copy of an existing instance.
     *
     * @param u Existing instance to be copied.
     */
    public User( User u ) {

        this.UID = u.UID;
        this.name = u.name;
        //this.hostedEvents = Arrays.copyOf( u.hostedEvents, u.hostedEvents.length );
        //this.pastHosted = Arrays.copyOf( u.pastHosted, u.pastHosted.length );
        //this.score = u.score;

    }

    /**
     * Constructor that recreates an instance of the class from a Parcel.
     * Can only be accessed by CREATOR.
     *
     * @param in Parcel that contains a flattened instance of the class.
     */
    private User( Parcel in ) {

        UID = in.readLong();
        name = in.readString();

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

}
