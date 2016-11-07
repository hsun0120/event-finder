package edu.ucsd.cse110.group50.eventfinder;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test suite for the User class.
 *
 * @author Thiago Marback
 * @since 2016-11-06
 * @version 1.0
 */
public class UserTester {

    User user;

    /**
     * Sets up the test fixture before each test.
     */
    @Before
    public void setUp() {

        user = new User( 1337, "A new user" );
        user.addHosted( 42 );
        user.addHosted( 1000 );
        user.addHosted( 1 );
        user.addHosted( 66 );

    }

    /**
     * Tests if the addHosted() method is adding events correctly.
     */
    @Test
    public void testAddHosted() {

        ArrayList<Long> hosted = user.getHostedEvents();
        assertTrue( "Event not hosted.", hosted.contains( (long) 42 ) );
        assertTrue( "Event not hosted.", hosted.contains( (long) 1000 ) );
        assertTrue( "Event not hosted.", hosted.contains( (long) 1 ) );
        assertTrue( "Event not hosted.", hosted.contains( (long) 66 ) );
        assertFalse( "Event should not be hosted.", hosted.contains( (long) 999 ) );

    }

    /**
     * Tests if the removeHosted() method is cancelling events correctly.
     */
    @Test
    public void testRemoveHosted() {

        user.removeHosted( 1000 );

        ArrayList<Long> hosted = user.getHostedEvents();
        assertTrue( "Event not hosted.", hosted.contains( (long) 42 ) );
        assertTrue( "Event not hosted.", hosted.contains( (long) 1 ) );
        assertTrue( "Event not hosted.", hosted.contains( (long) 66 ) );
        assertFalse( "Event should not be hosted.", hosted.contains( (long) 1000 ) );

    }

    /**
     * Tests if the eventDone() method is moving hosted events to past correctly.
     */
    @Test
    public void testEventDone() {

        user.eventDone( 66 );
        user.eventDone( 1 );

        // Checks still to be hosted events.
        ArrayList<Long> hosted = user.getHostedEvents();
        assertTrue( "Event not hosted.", hosted.contains( (long) 42 ) );
        assertTrue( "Event not hosted.", hosted.contains( (long) 1000 ) );
        assertFalse( "Event should not be hosted.", hosted.contains( (long) 66 ) );
        assertFalse( "Event should not be hosted.", hosted.contains( (long) 1 ) );

        // Checks past events.
        ArrayList<Long> past = user.getPastHosted();
        assertTrue( "Event not past.", past.contains( (long) 66 ) );
        assertTrue( "Event not past.", past.contains( (long) 1 ) );
        assertFalse( "Event should not be past.", past.contains( (long) 42 ) );
        assertFalse( "Event should not be past.", past.contains( (long) 1000 ) );

    }

}