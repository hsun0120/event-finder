package edu.ucsd.cse110.group50.eventfinder;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

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

    private ArrayList<Long> hostedEvents;
    private ArrayList<Long> pastHosted;
    // private float score;

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

        long[] hosted = in.createLongArray();
        hostedEvents = new ArrayList<>( hosted.length );
        for ( long uid : hosted ) {

            hostedEvents.add( uid );

        }
        long[] past = in.createLongArray();
        pastHosted = new ArrayList<Long>( past.length );
        for ( long uid : past ) {

            pastHosted.add( uid );

        }

    }

    /* Event-list methods */

    /**
     * Adds a new event to the list of hosted events.
     *
     * @param uid UID of the hosted event.
     * @return true if successfully added;
     *         false if was already in the list.
     */
    public boolean addHosted( long uid ) {

        return hostedEvents.add( uid );

    }

    /**
     * Removes an event from the list of hosted events.
     *
     * @param uid UID of the cancelled event.
     * @return true if successfully removed;
     *         false if not found.
     */
    public boolean removeHosted( long uid ) {

        return hostedEvents.remove( uid );

    }

    /**
     * Moves an event from the "going to host" list to the "hosted in the past" list.
     *
     * @param uid UID of the completed event.
     * @return true if successfully moved;
     *         false if not found as a hosted.
     */
    public boolean eventDone( long uid ) {

        boolean success = hostedEvents.remove( uid );
        if ( success ) {
            pastHosted.add( uid );
        }

        return success;

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
     * Retrieves the UIDs of the events being hosted by this user.
     *
     * @return The events being hosted.
     */
    public ArrayList<Long> getHostedEvents() {

        return hostedEvents;

    }

    /**
     * Retrieves the UIDs of the events hosted by this user in the past.
     *
     * @return The events hosted by the user in the past.
     */
    public ArrayList<Long> getPastHosted() {

        return pastHosted;

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

        long[] hosted = new long[hostedEvents.size()];
        int i = 0;
        for ( long uid : hostedEvents ) {

            hosted[i++] = uid;

        }
        dest.writeLongArray( hosted );
        long[] past = new long[pastHosted.size()];
        i = 0;
        for ( long uid : pastHosted ) {

            past[i++] = uid;

        }
        dest.writeLongArray( past );

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
