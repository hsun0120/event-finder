package edu.ucsd.cse110.group50.eventfinder.utility;

import java.util.Objects;

/**
 * Interface that designates an event handler for when a fetch-from-database operation
 * is complete.
 *
 * @author Thiago Marback
 * @version 1.0
 * @since 2016-11-13
 */
public interface LoadListener {

    /**
     * Called when the fetch operation is completed.
     *
     * @param data Data that was fetched.
     */
    void onLoadComplete( Object data );

}
