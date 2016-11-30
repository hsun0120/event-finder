package edu.ucsd.cse110.group50.eventfinder;

import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.group50.eventfinder.storage.EvDate;

import static org.junit.Assert.*;

/**
 * Test suite for the EvDate class.
 *
 * @author Thiago Marback
 * @since 2016-11-28
 * @version 1.1
 */
public class EvDateTester {

    EvDate date;

    @Before
    public void setUp() {

        date = new EvDate( 12, 30, 11, 10, 1997 );

    }

    @Test
    public void testGetTime() {

        assertEquals( "Incorrect time string.", "12:30", date.getTime() );

    }

    @Test
    public void testGetDate() {

        assertEquals( "Incorrect date string.", "10/11/1997", date.getDate() );

    }

    @Test
    public void testSetTimeVals() {

        date.setTime( 11, 15 );
        assertEquals( "Incorrect hour.", 11, date.getHour() );
        assertEquals( "Incorrect minute.", 15, date.getMinute() );

    }

    @Test
    public void testSetTimeString() {

        date.setTime( "11:15" );
        assertEquals( "Incorrect hour.", 11, date.getHour() );
        assertEquals( "Incorrect minute.", 15, date.getMinute() );

    }

    @Test
    public void testSetDateVals() {

        date.setDate( 25, 12, 2016 );
        assertEquals( "Incorrect day.", 25, date.getDay() );
        assertEquals( "Incorrect month.", 12, date.getMonth() );
        assertEquals( "Incorrect year.", 2016, date.getYear() );

    }

    @Test
    public void testSetDateString() {

        date.setDate( "12/25/2016" );
        assertEquals( "Incorrect day.", 25, date.getDay() );
        assertEquals( "Incorrect month.", 12, date.getMonth() );
        assertEquals( "Incorrect year.", 2016, date.getYear() );

    }

    @Test
    public void testToString() {

        assertEquals( "Incorrect String representation.", "10/11/1997 12:30", date.toString() );

    }

    @Test
    public void testIsPast() {

        date = new EvDate();
        assertFalse( date.isPast() );
        date.setYear( date.getYear() - 1 );
        assertTrue( date.isPast() );
        date.setYear( date.getYear() + 2 );
        assertFalse( date.isPast() );

    }

    @Test
    public void testAfter() {

        EvDate date2 = new EvDate( date.getDay(), date.getMonth(), date.getYear() + 1 );
        assertFalse( date.after( date2 ) );
        assertTrue( date2.after( date ) );

    }

    @Test
    public void testBefore() {

        EvDate date2 = new EvDate( date.getDay(), date.getMonth(), date.getYear() + 1 );
        assertTrue( date.before( date2 ) );
        assertFalse( date2.before( date ) );

    }

    @Test
    public void testEquals() {

        EvDate date2 = new EvDate( date );
        assertTrue( "Incorrect inequality.", date.equals( date2 ) );
        date2.setYear( date2.getYear() - 1 );
        assertFalse( "Incorrect equality.", date.equals( date2 ) );
        date2.setYear( date2.getYear() + 2 );
        assertFalse( "Incorrect equality.", date.equals( date2 ) );

    }

    @Test
    public void testCompareTo() {

        EvDate date2 = new EvDate( date );
        assertEquals( "Incorrect return.", 0, date.compareTo( date2 ) );
        date2.setYear( date2.getYear() - 1 );
        assertTrue( "Incorrect return.", date.compareTo( date2 ) > 0 );
        date2.setYear( date2.getYear() + 2 );
        assertTrue( "Incorrect return.", date.compareTo( date2 ) < 0 );

    }

}
