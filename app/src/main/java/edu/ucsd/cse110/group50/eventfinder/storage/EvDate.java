package edu.ucsd.cse110.group50.eventfinder.storage;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Calendar;

/**
 * Class that stores a particular date and time.
 *
 * @author Thiago Marback
 * @version 1.0
 * @since 2016-11-21
 */
public class EvDate implements Parcelable {

    private int hour;
    private int minute;

    private int day;
    private int month;
    private int year;

    private static final String TIME_SEPARATOR = ":";
    private static final String DATE_SEPARATOR = "/";
    private static final String TAG = "EvDate";

    /**
     * Creates a new EvDate with the current date and time.
     */
    public EvDate() {

        Calendar calendar = Calendar.getInstance();

        this.hour = calendar.get( Calendar.HOUR_OF_DAY );
        this.minute = calendar.get( Calendar.MINUTE );

        this.day = calendar.get( Calendar.DAY_OF_MONTH );
        this.month = calendar.get( Calendar.MONTH ) + 1;
        this.year = calendar.get( Calendar.YEAR );

    }

    /**
     * Creates a new EvDate with the current date and a specified time.
     *
     * @param hour Hour of the new EvDate.
     * @param minute Minute of the new EvDate.
     */
    public EvDate( int hour, int minute ) {

        this.hour = hour;
        this.minute = minute;

        Calendar calendar = Calendar.getInstance();

        this.day = calendar.get( Calendar.DAY_OF_MONTH );
        this.month = calendar.get( Calendar.MONTH ) + 1;
        this.year = calendar.get( Calendar.YEAR );

    }

    /**
     * Creates a new EvDate with a specified date and the current time.
     *
     * @param day Day of the new EvDate.
     * @param month Month of the new EvDate.
     * @param year Year of the new EvDate.
     */
    public EvDate( int day, int month, int year ) {

        Calendar calendar = Calendar.getInstance();

        this.hour = calendar.get( Calendar.HOUR_OF_DAY );
        this.minute = calendar.get( Calendar.MINUTE );

        this.day = day;
        this.month = month;
        this.year = year;

    }

    /**
     * Creates a new EvDate with specified date and time.
     *
     * @param hour Hour of the new EvDate.
     * @param minute Minute of the new EvDate.
     * @param day Day of the new EvDate.
     * @param month Month of the new EvDate.
     * @param year Year of the new EvDate.
     */
    public EvDate( int hour, int minute, int day, int month, int year ) {

        this.hour = hour;
        this.minute = minute;

        this.day = day;
        this.month = month;
        this.year = year;

    }

    /**
     * Creates a new EvDate that is a copy of an existing one.
     *
     * @param d EvDate to be copied.
     */
    public EvDate( EvDate d ) {

        this.hour = d.hour;
        this.minute = d.minute;

        this.day = d.day;
        this.month = d.month;
        this.year = d.year;

    }

    /**
     * Constructor that recreates an instance of the class from a Parcel.
     * Can only be accessed by CREATOR.
     *
     * @param in Parcel that contains a flattened instance of the class.
     */
    private EvDate( Parcel in ) {

        hour = in.readInt();
        minute = in.readInt();

        day = in.readInt();
        month = in.readInt();
        year = in.readInt();

    }

    /* Utility methods */

    /**
     * Creates a String that represents the time of this EvDate.
     * Format hour:minute
     *
     * @return The time of this EvDate.
     */
    public String getTime() {

        return String.format( "%02d" + TIME_SEPARATOR + "%02d", hour, minute );

    }

    /**
     * Creates a String that represents the date of this EvDate.
     * Format month/day/year
     *
     * @return The date of this EvDate.
     */
    public String getDate() {

        return String.format( "%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%04d",
                month, day, year );

    }

    /**
     * Sets the time of this EvDate.
     *
     * @param hour New hour.
     * @param minute New minute.
     */
    public void setTime( int hour, int minute ) {

        this.hour = hour;
        this.minute = minute;

    }

    /**
     * Sets the time of this EvDate.
     *
     * @param time New time in the format hour:minute
     */
    public void setTime( String time ) {

        String[] timePieces = time.split( TIME_SEPARATOR );
        hour = Byte.valueOf( timePieces[0] );
        minute = Byte.valueOf( timePieces[1] );

    }

    /**
     * Sets the date of this EvDate.
     *
     * @param day New day.
     * @param month New month.
     * @param year New year.
     */
    public void setDate( int day, int month, int year ) {

        this.day = day;
        this.month = month;
        this.year = year;

    }

    /**
     * Sets the date of this EvDate.
     *
     * @param date New date, in the format month/day/year
     */
    public void setDate( String date ) {

        String[] datePieces = date.split( DATE_SEPARATOR );
        month = Byte.valueOf( datePieces[0] );
        day = Byte.valueOf( datePieces[1] );
        year = Short.valueOf( datePieces[2] );

    }

    @Override
    public String toString() {

        return getDate() + " " + getTime();

    }

    /**
     * Checks if the moment stored in this object has already passed.
     *
     * @return true if the moment in time stored in this instance is before the present time.
     *         false otherwise.
     */
    public boolean isPast() {

        Calendar selected = Calendar.getInstance();
        selected.set( year, month - 1, day, hour, minute );
        Calendar current = Calendar.getInstance();

        if ( selected.before( current ) ) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Checks if this instance represents a date that comes after another given EvDate.
     *
     * @param d EvDate that this should be compared to.
     * @return true if this represents a later point in time than d.
     *         false otherwise.
     */
    public boolean after( EvDate d ) {

        Calendar current = Calendar.getInstance();
        current.set( this.year, this.month - 1, this.day, this.hour, this.minute );
        Calendar other = Calendar.getInstance();
        other.set( d.year, d.month - 1, d.day, d.hour, d.minute );

        return other.before( current );

    }

    /**
     * Checks if this instance represents a date that comes before another given EvDate.
     *
     * @param d EvDate that this should be compared to.
     * @return true if this represents an earlier point in time than d.
     *         false otherwise.
     */
    public boolean before( EvDate d ) {

        Calendar current = Calendar.getInstance();
        current.set( this.year, this.month - 1, this.day, this.hour, this.minute );
        Calendar other = Calendar.getInstance();
        other.set( d.year, d.month - 1, d.day, d.hour, d.minute );

        return current.before( other );

    }

    /**
     * Checks if this EvDate represents the same point in time as a given EvDate.
     *
     * @param other EvDate that this should be compared to.
     * @return true if both objects have the same values (represent the same point in time).
     *         false otherwise.
     */
    public boolean equals( EvDate other ) {

        if ( ( this.hour == other.hour ) &&
                ( this.minute == other.minute ) &&
                ( this.day == other.day ) &&
                ( this.month == other.month ) &&
                ( this.year == other.year ) ) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Compares two dates using rough estimation.
     * @param
     * @return negative if this<other, 0 if this ==other, positive if this >other
     */
    public int compareTo(EvDate other)
    {
        return this.year*365 + this.month * 30 + this.day - (other.year*365 + other.month * 30 + other.day);
    }




    /* Getters */

    /**
     * Retrieves the hour.
     *
     * @return The hour of this EvDate.
     */
    public int getHour() {

        return hour;

    }

    /**
     * Retrieves the minute.
     *
     * @return The minute of this EvDate.
     */
    public int getMinute() {

        return minute;

    }

    /**
     * Retrieves the day.
     *
     * @return The day of this EvDate.
     */
    public int getDay() {

        return day;

    }

    /**
     * Retrieves the month.
     *
     * @return The month of this EvDate.
     */
    public int getMonth() {

        return month;

    }

    /**
     * Retrieves the year.
     *
     * @return The year of this EvDate.
     */
    public int getYear() {

        return year;

    }

    /* Setters */

    /**
     * Sets the hour.
     *
     * @param hour New hour of this EvDate.
     */
    public void setHour( int hour ) {

        this.hour = hour;

    }

    /**
     * Sets the minute.
     *
     * @param minute New minute of this EvDate.
     */
    public void setMinute( int minute ) {

        this.minute = minute;

    }

    /**
     * Sets the day.
     *
     * @param day New day of this EvDate.
     */
    public void setDay( int day ) {

        this.day = day;

    }

    /**
     * Sets the month.
     *
     * @param month New month of this EvDate.
     */
    public void setMonth( int month ) {

        this.month = month;

    }

    /**
     * Sets the year.
     *
     * @param year New year of this EvDate.
     */
    public void setYear( int year ) {

        this.year = year;

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

        dest.writeInt( hour );
        dest.writeInt( minute );

        dest.writeInt( day );
        dest.writeInt( month );
        dest.writeInt( year );

        Log.v( TAG, "Flattened EvDate " + toString() + " to Parcel." );

    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Used to generate an instance of this class from a Parcel.
     */
    public static final Parcelable.Creator<EvDate> CREATOR
            = new Parcelable.Creator<EvDate>() {

        @Override
        public EvDate createFromParcel(Parcel in ) {

            return new EvDate( in );

        }

        @Override
        public EvDate[] newArray(int size ) {

            return new EvDate[size];

        }

    };

}
