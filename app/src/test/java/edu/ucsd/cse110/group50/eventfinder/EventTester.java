package edu.ucsd.cse110.group50.eventfinder;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test suite for the Event class.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 1.0
 */
public class EventTester {

    Event event;

    /**
     * Sets up the test fixture before each test.
     */
    @Before
    public void setUp() {

        event = new Event( 420, "A new world", 1337 );

        event.setHour( (byte) 10 );
        event.setMinute( (byte) 2 );
        event.setDay( (byte) 15 );
        event.setMonth( (byte) 3 );
        event.setYear( (short) 2015 );

        event.setAddress( "UCSD" );
        event.setHasPassword( true );
        event.setPassword( "Banana" );
        event.setHasRestrictions( false );
        ArrayList<String> restrictions = new ArrayList<>();
        restrictions.add( "Hoomans" );
        restrictions.add( "Over 18" );
        restrictions.add( "Must be able to do dank 720 MLG quickscopes" );
        event.setRestrictions( restrictions );
        event.setDescription( "Come do stuff" );

    }

    /**
     * Tests if the getTime() method is returning the correct time string.
     */
    @Test
    public void testGetTime() {

        assertEquals( "Wrong time string returned.", "10:02", event.getTime() );

        // Tests padding zeroes.
        event.setHour( (byte) 0 );
        event.setMinute( (byte) 0 );
        assertEquals( "Wrong time string returned.", "00:00", event.getTime() );

    }

    /**
     * Tests if the getDate() method is returning the correct date string.
     */
    @Test
    public void testGetDate() {

        assertEquals( "Wrong date string returned.", "03/15/2015", event.getDate() );

        // Tests padding zeroes.
        event.setDay( (byte) 0 );
        event.setMonth( (byte) 0 );
        event.setYear( (short) 0 );
        assertEquals( "Wrong date string returned.", "00/00/0000", event.getDate() );

    }

    /**
     * Tests if the setTime() (using individual values) method is setting event time correctly.
     */
    @Test
    public void testSetTimeValues() {

        event.setTime( (byte) 2, (byte) 59 );

        assertEquals( "Wrong hour set.", 2, event.getHour() );
        assertEquals( "Wrong minute set.", 59, event.getMinute() );

    }

    /**
     * Tests if the setTime() (using a String) method is setting event time correctly.
     */
    @Test
    public void testSetTimeString() {

        event.setTime( "11:45" );

        assertEquals( "Wrong hour set.", 11, event.getHour() );
        assertEquals( "Wrong minute set.", 45, event.getMinute() );

    }

    /**
     * Tests if the setDate() (using individual values) method is setting event date correctly.
     */
    @Test
    public void testSetDateValues() {

        event.setDate( (byte) 24, (byte) 12, (short) 2042 );

        assertEquals( "Wrong day set.", 24, event.getDay() );
        assertEquals( "Wrong month set.", 12, event.getMonth() );
        assertEquals( "Wrong year set.", 2042, event.getYear() );

    }

    /**
     * Tests if the setDate() (using a String) method is setting event time correctly.
     */
    @Test
    public void testSetDateString() {

        event.setDate( "10/11/1997" );

        assertEquals( "Wrong day set.", 11, event.getDay() );
        assertEquals( "Wrong month set.", 10, event.getMonth() );
        assertEquals( "Wrong year set.", 1997, event.getYear() );

    }

}
