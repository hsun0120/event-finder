package edu.ucsd.cse110.group50.eventfinder;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import edu.ucsd.cse110.group50.eventfinder.storage.EvDate;
import edu.ucsd.cse110.group50.eventfinder.storage.Event;

import static org.junit.Assert.*;

/**
 * Test suite for the Event class.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 2.0
 */
public class EventTester {

    Event event;

    /**
     * Sets up the test fixture before each test.
     */
    @Before
    public void setUp() {

        event = new Event( "420", "A new world", "1337" );

        event.setDate( new EvDate( 10, 2, 15, 3, 2015 ) );
        event.setDuration( 100 );

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
     * Given a use has already signed in,
     * when he or she switched to event detail page,
     * then the correct event end time showed.
     */
    @Test
    public void testGetEndTime() {
        EvDate date = new EvDate( 11, 42, 15, 3, 2015 );
        assertEquals( "Incorrect endtime calculated.", date, event.getEndTime() );
        event.setDuration( 1440 );
        date = new EvDate( 10, 2, 16, 3, 2015 );
        assertEquals( "Incorrect endtime calculated.", date, event.getEndTime() );
    }

    /**
     * Given an event organizer has already signed in,
     * when he or she created two new events,
     * and named the events the same name,
     * then an error message popped up.
     */
    @Test
    public void testEquals() {

        Event ev1 = new Event( event.getUid(), event.getHost() );
        Event ev2 = new Event( "nullptr", event.getHost() );
        Event ev3 = new Event( "nullptr", "ship" );

        assertTrue( "Event should be equal.", event.equals( ev1 ) );
        assertFalse( "Event should be different.", event.equals( ev2 ) );
        assertFalse( "Event should be different.", event.equals( ev3 ) );

    }

}
