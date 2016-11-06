package edu.ucsd.cse110.group50.eventfinder;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Class that stores the data for a single user.
 * Is identified by an UID that is defined at creation and cannot be changed.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 1.0
 */
public class User implements Parcelable {

    /* Instance fields */

    private final long uid;
    private String name;

    private ArrayList<Event> hostedEvents;
    private ArrayList<Event> pastHosted;
    // private float score;

    /* Constants */

    private static final String HOSTED_EVENTS = "hosted";
    private static final String PAST_EVENTS = "past";

    /* Ctors */

    /**
     * Creates a new instance of this class with a given UID.
     *
     * @param uid UID of this instance.
     */
    public User( long uid ) {

        this( uid, "" );

    }

    /**
     * Creates a new instance of this class with a given UID and name.
     *
     * @param uid UID of this instance.
     * @param name Initial name of this instance.
     */
    public User( long uid, String name ) {

        this.uid = uid;
        this.name = name;

        this.hostedEvents = new ArrayList<>();
        this.pastHosted = new ArrayList<>();
        //this.score = 0.0;

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

    }

    /**
     * Constructor that recreates an instance of the class from a Parcel.
     * Can only be accessed by CREATOR.
     *
     * @param in Parcel that contains a flattened instance of the class.
     */
    private User( Parcel in ) {

        uid = in.readLong();
        name = in.readString();

        Bundle events = in.readBundle();
        hostedEvents = events.getParcelableArrayList( HOSTED_EVENTS );
        pastHosted = events.getParcelableArrayList( PAST_EVENTS );

    }

    /* Getters */

    /**
     * Retrieves the UID of this instance.
     *
     * @return The UID of this instance.
     */
    public long getUID() {

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
     * Retrieves the events being hosted by this user.
     *
     * @return The events being hosted.
     */
    public ArrayList<Event> getHostedEvents() {

        return hostedEvents;

    }

    /**
     * Retrieves the events hosted by this user in the past.
     *
     * @return The events hosted by the user in the past.
     */
    public ArrayList<Event> getPastHosted() {

        return pastHosted;

    }

    /* Parcelable requirements */

    /**
     * {@inheritDoc}
     *
     * @param dest {@inheritDoc}
     * @param flags Not used.
     *
     */
    @Override
    public void writeToParcel( Parcel dest, int flags ) {

        dest.writeLong( uid );
        dest.writeString( name );

        Bundle events = new Bundle();
        events.putParcelableArrayList( HOSTED_EVENTS, hostedEvents );
        events.putParcelableArrayList( PAST_EVENTS, pastHosted );
        dest.writeBundle( events );

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
