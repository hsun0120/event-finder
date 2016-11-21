package edu.ucsd.cse110.group50.eventfinder;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyListFragment extends Fragment implements OnItemClickListener {


    RecyclerView recList;

    boolean ready;

    private static String TAG = "MyListFragment";

    public MyListFragment() {

        ready = false;

    }

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
        recList.setLayoutManager(llm);

        ready = true;

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Log.d("LIST", "Item: " + position + "selected");

    }




    @Override
    public void onStart()
    {
        super.onStart();
//        on_all_events_flag = MapView.user_on_all_events_flag;

        update();

    }

    void update()
    {

        if ( !ready ) {
            return;
        }

        EventAdapter oldAdapter = (EventAdapter) recList.getAdapter();
        if ( oldAdapter != null ) {
            oldAdapter.destroyCards();
        }

        Log.d( TAG, "Updating." );
        MapView.spinner.setVisibility( View.VISIBLE );
        //Get events from MapView
        ArrayList<Event> eventList = MapView.eventList;
        //Update event base on Flag
        if( !MapView.user_on_all_events_flag )
        {
            eventList = processSearch( false, MapView.eventList );
        }

        if( MapView.user_on_search_event_flag )
        {
            eventList = processSearch( true , eventList );
        }

        EventAdapter ca = new EventAdapter( eventList );
        recList.setAdapter( ca );
        Log.v( TAG, "Updated." );
        MapView.spinner.setVisibility( View.GONE );

    }


    //Yining: Dummy Local Search Functions:
    //searchModeFlag: false for check user, true for searched String
    private ArrayList<Event> processSearch( boolean searchModeFlag, ArrayList<Event> eventList )
    {
        ArrayList<Event> new_list = new ArrayList<>();

        if ( searchModeFlag )
        {
            String key = MapView.searchedText;
            for ( Event e : eventList ) {
                String currEventName = e.getName().toLowerCase();
                if ( currEventName.contains( key.toLowerCase() ) )
                {
                    new_list.add( e );
                    continue;
                }

                String currDes = e.getDescription().toLowerCase();
                if ( currDes.contains( key.toLowerCase() ) &&
                        ( !e.getHasPassword() || !MapView.user_on_all_events_flag ) ) {
                    new_list.add( e );
                    continue;
                }

                String currAddress = e.getAddress().toLowerCase();
                if ( currAddress.contains( key.toLowerCase() ) &&
                        ( !e.getHasPassword() || !MapView.user_on_all_events_flag ) ) {
                    new_list.add( e );
                    continue;
                }

                if ( e.getUid().isEmpty() ) {
                    new_list.add( e );
                }
            }
        } else {
            User user = MapView.curUser;
            ArrayList<Event> oldList = new ArrayList<>();
            for ( Event e : eventList ) {

                if ( e.getHost().equals( user.getUid() ) ) {
                    Log.v( TAG, "In MYEVENTS, UID MATCH - userid is " + e.getUid() + "." );
                    if ( user.getHostedEvents().contains( e.getUid() ) ) {
                        Log.v( TAG, "Scheduled event." );
                        new_list.add( e );
                    } else if ( user.getPastHosted().contains( e.getUid() ) ) {
                        Log.v( TAG, "Past event." );
                        oldList.add( e );
                    } else {
                        Log.w( TAG, "EVENT HAS A HOST, WHO DOES NOT HAVE THE EVENT - " +
                                "User: " + user.getUid() + " | Event: " + e.getUid() );
                        ServerLog.s( TAG, "EVENT HAS A HOST, WHO DOES NOT HAVE THE EVENT - " +
                                "User: " + user.getUid() + " | Event: " + e.getUid() );
                    }
                }

            }
            new_list.add( new Event( "", "=====PAST EVENTS=====", "" ) );
            new_list.addAll( oldList );

        }
        return new_list;
    }

    @Override
    public void onStop() {

        super.onStop();
        EventAdapter adapter = (EventAdapter) recList.getAdapter();
        adapter.destroyCards();

    }
}