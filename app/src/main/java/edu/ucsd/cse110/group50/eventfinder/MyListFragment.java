package edu.ucsd.cse110.group50.eventfinder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;

import edu.ucsd.cse110.group50.eventfinder.storage.EvDate;
import edu.ucsd.cse110.group50.eventfinder.storage.Event;
import edu.ucsd.cse110.group50.eventfinder.storage.EventList;
import edu.ucsd.cse110.group50.eventfinder.storage.User;

/**
 * A class that implements the fragment for displaying event lists
 */
public class MyListFragment extends Fragment implements OnItemClickListener {
    RecyclerView recList;
    boolean ready;
    private int curEvents;

    private static String TAG = "MyListFragment";

    /**
     * Default constructor
     */
    public MyListFragment() {

        ready = false;

    }

    /**
     * Callback function; create and setup view
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState current state
     * @return View created
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.v( TAG, "MYLISTFRAGMENT ONCREATE Called" );
        // Get the view of the fragment
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        // Create new RecyclerView
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        // Improve performance
        recList.setHasFixedSize(true);

        // Layour manager for RecyclerView
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        /* Setup adapter */
        EventAdapter adapter = new EventAdapter(MapView.eventList, curEvents);
        recList.setAdapter(adapter);
        recList.setLayoutManager(llm);

        /* Swipe and dismiss configuarations */
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recList);

        ready = true;
        return view;
    }

    /**
     * Callback for the activity created
     * @param savedInstanceState current state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Callback when an item is clicked
     * @param parent parent activity/view
     * @param view view
     * @param position position of item clicked
     * @param id item id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Log.d("LIST", "Item: " + position + "selected");
    }

    /**
     * Callback for starting fragment activity
     */
    @Override
    public void onStart()
    {
        super.onStart();
        update(); //Update event list
    }

    /**
     * Update event list (local)
     */
    void update()
    {
        if ( !ready ) { //Stop if the view is not created normally
            return;
        }

        /* Dispatch old adaptera and destroy old data */
        EventAdapter oldAdapter = (EventAdapter) recList.getAdapter();
        if ( oldAdapter != null ) {
            oldAdapter.destroyCards();
        }

        Log.d( TAG, "Updating." );
        MapView.spinner.setVisibility( View.VISIBLE );
        //Get events from MapView
        ArrayList<Event> eventList = EventList.getInstance();

        //Update event base on Flag
        if(MapView.date_filtered != null)
        {
            eventList = processFilter(MapView.date_filtered, eventList);
        }

        if( !MapView.user_on_all_events_flag )
        {
            eventList = processSearch( false, eventList );
        }

        if( MapView.user_on_search_event_flag )
        {
            eventList = processSearch( true , eventList );
        }

        /* Setup new adapter */
        EventAdapter ca = new EventAdapter( eventList, curEvents );
        recList.setAdapter( ca );

        /* Swipe and dismiss configurations */
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(ca);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recList);

        Log.v( TAG, "Updated." );
        MapView.spinner.setVisibility( View.GONE );
    }

    /**
     * Filter to sort out out-of-date events
     * @param d event data
     * @param eventList event list
     * @return a list of valid events
     */
    private ArrayList<Event> processFilter(EvDate d, ArrayList<Event> eventList)
    {
        ArrayList<Event> new_list = new ArrayList<>();

        for(Event e : eventList){
            if(e.getDate().compareTo(d) <=0 ) //Validate date
            {
                new_list.add(e);
            }
        }

        return new_list;
    }


    /**
     * Local Search Functions
     * @param searchModeFlag true for search string; false for check user
     * @param eventList local event list
     */
    private ArrayList<Event> processSearch(boolean searchModeFlag, ArrayList<Event> eventList )
    {
        ArrayList<Event> new_list = new ArrayList<>(); //Search results

        if ( searchModeFlag ) //Search for string
        {
            String key = MapView.searchedText;
            for ( Event e : eventList ) {
                String currEventName = e.getName().toLowerCase();
                if ( currEventName.contains( key.toLowerCase() ) ) //Search title
                {
                    new_list.add( e ); //Append to search result list
                    continue;
                }

                /* Search in description */
                String currDes = e.getDescription().toLowerCase();
                if ( currDes.contains( key.toLowerCase() ) &&
                        ( !e.getHasPassword() || !MapView.user_on_all_events_flag ) ) {
                    new_list.add( e );
                    continue;
                }

                /* Search in address */
                String currAddress = e.getAddress().toLowerCase();
                if ( currAddress.contains( key.toLowerCase() ) &&
                        ( !e.getHasPassword() || !MapView.user_on_all_events_flag ) ) {
                    new_list.add( e );
                }

            }
            curEvents = new_list.size();
        } else { //Search for event created by a specific user
            User user = MapView.curUser;
            ArrayList<Event> old_list = new ArrayList<>(); //Out-of-date event list
            curEvents = 0;
            for ( Event e : eventList ) {
                if ( e.getHost().equals( user.getUid() ) ) {
                    Log.v( TAG, "In MYEVENTS, UID MATCH - userid is " + e.getUid() + "." );
                    if ( !e.getDate().isPast() ) { //Check date
                        Log.v( TAG, "Scheduled event." );
                        new_list.add( e );
                        curEvents++;
                    } else {
                        Log.v( TAG, "Past event." );
                        old_list.add( e );
                    }
                }
            }
            new_list.addAll( old_list ); //New events come first
        }

        return new_list;
    }

    /**
     * Callback for fragment activity stop
     */
    @Override
    public void onStop() {
        super.onStop();
        EventAdapter adapter = (EventAdapter) recList.getAdapter();
        adapter.destroyCards();
    }


    /**
     * Callback for fragment activity resume
     */
    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, "ON resume Called");
        update(); //Update fragment
    }
}