package edu.ucsd.cse110.group50.eventfinder;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test suite for the EvDate class.
 *
 * @author Thiago Marback
 * @since 2016-11-28
 * @version 1.0
 */
public class EvDateTester {

    EvDate date;

    @Before
    public void setUp() {

        date = new EvDate();

    }

    @Test
    public void testIsPast() {

        date.setYear( date.getYear() - 1 );
        assertTrue( date.isPast() );
        date = new EvDate();
        assertFalse( date.isPast() );
        date.setYear( date.getYear() + 1 );
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

}
