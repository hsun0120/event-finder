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

public class MyListFragment extends Fragment implements OnItemClickListener {


    RecyclerView recList;


    private static String TAG = "MyListFragment";

    public MyListFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        System.out.println("MYLISTFRAGMENT ONCREATE Called");
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





        // Example event card manually created


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


        //Get events from MapView
        EventList eventList = new EventList(MapView.eventList.getEventList());
        //Update event base on Flag
        //eventList.eventList.clear();
        EventAdapter ca = new EventAdapter( eventList );
        recList.setAdapter( ca );
    }

    @Override
    public void onResume()
    {
        super.onResume();
        System.out.println("My List OnResume Called!!");
        //Get events from MapView
        EventList eventList = new EventList(MapView.eventList.getEventList());
        //Update event base on Flag
        if(MapView.user_on_all_events_flag == 0)
        {
            eventList = processSearch(0 ,eventList, MapView.currUid);
        }

        if(MapView.user_on_earch_event_flag == 1)
        {
            eventList = processSearch(1,eventList, MapView.searchedText);
        }

        EventAdapter ca = new EventAdapter( eventList);
        recList.setAdapter( ca );
    }


    //Yining: Dummy Local Search Functions:
    //searchModeFlag: 0 for check user, 1 for searched String
    public EventList processSearch( int searchModeFlag, EventList eventList,  String key)
    {
        ArrayList<Event> new_list = new ArrayList<>();

        for(Event e : eventList)
        {
            if(searchModeFlag == 0) {
                if (e.getHost().equals(key)) {
                    System.out.println("In MYEVENTS, UID MATCH\n userid is " + e.getUid());
                    new_list.add(e);
                } else {
                    System.out.println("In MYEVENTS, UID NOT MATCH\n curr userid is " + key + "\nHOst UID in data is " + e.getHost());
                }
            }

            if(searchModeFlag == 1)
            {
                String currEventName = e.getName().toLowerCase();
                if(currEventName.contains(key.toLowerCase()))
                {
                    new_list.add(e);
                    continue;
                }

                String currDes = e.getDescription().toLowerCase();
                if(currDes.contains(key.toLowerCase()))
                {
                    new_list.add(e);
                    continue;
                }

                String currAddress = e.getAddress().toLowerCase();
                if(currAddress.contains(key.toLowerCase()))
                {
                    new_list.add(e);
                }
            }
        }
        return new EventList(new_list);
    }
}