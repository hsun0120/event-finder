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

        event = new Event( "420", "A new world", "1337" );

        event.setDate( new EvDate( 10, 2, 15, 3, 2015 ) );

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

}
