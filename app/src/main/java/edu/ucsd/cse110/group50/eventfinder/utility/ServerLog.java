package edu.ucsd.cse110.group50.eventfinder.utility;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Class used to send logs to the server.
 * Logs contain the time they were sent, a specified level, a tag (usually where it was sent
 * from) and a message.
 *
 * @author Thiago Marback
 * @version 1.0
 * @since 2016-11-20
 */
public class ServerLog {

    private static final String DATE = "date";
    private static final String LEVEL = "level";
    private static final String TAG = "tag";
    private static final String MESSAGE = "message";

    private static DatabaseReference mDatabase;

    /**
     * Loads in the database (use after the user is authenticated).
     */
    public static void loadDatabase() {

        mDatabase = FirebaseDatabase.getInstance().getReference().child( Identifiers.FIREBASE_LOGS );

    }

    /**
     * Sends a log to the server.
     *
     * @param level Level of the log.
     * @param tag Tag of the log (where it was sent from).
     * @param message Message of the log.
     */
    public static void log( Level level, String tag, String message ) {

        DatabaseReference data = mDatabase.push();
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss z", Locale.US );
        String date = df.format( Calendar.getInstance().getTime() );
        data.child( LEVEL ).setValue( level.toString() );
        data.child( DATE ).setValue( date );
        data.child( TAG ).setValue( tag );
        data.child( MESSAGE ).setValue( message );

    }

    /**
     * Sends to the server a log of level SEVERE.
     *
     * @param tag Tag of the log (where it was sent from).
     * @param message Message of the log.
     */
    public static void s( String tag, String message ) {

        log( Level.SEVERE, tag, message );

    }

    /**
     * Sends to the server a log of level WARNING.
     *
     * @param tag Tag of the log (where it was sent from).
     * @param message Message of the log.
     */
    public static void w( String tag, String message ) {

        log( Level.WARNING, tag, message );

    }

    /**
     * Sends to the server a log of level INFO.
     *
     * @param tag Tag of the log (where it was sent from).
     * @param message Message of the log.
     */
    public static void i( String tag, String message ) {

        log( Level.INFO, tag, message );

    }

    /**
     * Sends to the server a log of level CONFIG.
     *
     * @param tag Tag of the log (where it was sent from).
     * @param message Message of the log.
     */
    public static void c( String tag, String message ) {

        log( Level.CONFIG, tag, message );

    }

    /**
     * Sends to the server a log of level FINE.
     *
     * @param tag Tag of the log (where it was sent from).
     * @param message Message of the log.
     */
    public static void f( String tag, String message ) {

        log( Level.FINE, tag, message );

    }

    /**
     * Sends to the server a log of level FINER.
     *
     * @param tag Tag of the log (where it was sent from).
     * @param message Message of the log.
     */
    public static void fr( String tag, String message ) {

        log( Level.FINER, tag, message );

    }

    /**
     * Sends to the server a log of level FINEST.
     *
     * @param tag Tag of the log (where it was sent from).
     * @param message Message of the log.
     */
    public static void fst( String tag, String message ) {

        log( Level.FINEST, tag, message );

    }

}
