package edu.ucsd.cse110.group50.eventfinder.utility;

/**
 * Constants for identifying project-wide messages or locales (Firebase nodes, Intent extra names,
 * etc).
 *
 * @author Thiago Marback
 * @since 2016-11-14
 * @version 2.0
 */
public interface Identifiers {

    /**
     * Name used to pass a User object in an Intent.
     */
    String USER = "user";
    /**
     * Name used to pass an Event object in an Intent.
     */
    String EVENT = "event";
    /**
     * Name used to pass a list of Event objects in an Intent.
     */
    String EVENTS = "events";
    /**
     * Name used to pass a username in an intent.
     */
    String USER_NAME = "userName";
    /**
     * Name used to pass in an intent a flag indicating current action is an edit.
     */
    String EDIT = "edit";

    /**
     * Node in Firebase that contains user data.
     */
    String FIREBASE_USERS = "users";
    /**
     * Node in Firebase that contains event data.
     */
    String FIREBASE_EVENTS = "events";
    /**
     * Node in Firebase that contains logs.
     */
    String FIREBASE_LOGS = "logs";

}
