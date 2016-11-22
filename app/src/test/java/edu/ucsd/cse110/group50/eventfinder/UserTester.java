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

        user = new User( "1337", "A new user" );

    }

}
