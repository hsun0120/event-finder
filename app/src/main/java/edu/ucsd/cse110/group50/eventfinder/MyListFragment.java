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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyListFragment extends Fragment implements OnItemClickListener {

    DatabaseReference databaseReference;
    RecyclerView recList;

    private static String TAG = "MyListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        Event.loadAllEvents(databaseReference.child(Identifiers.FIREBASE_EVENTS),
                new LoadListener() {

                    @Override
                    public void onLoadComplete(Object data) {

                        EventAdapter ca = new EventAdapter( (ArrayList<Event>) data );
                        recList.setAdapter( ca );

                    }

                });

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

    // Manually create example event card list
    // Should be removed after connection to firebase is successful
    // Hardcoded test function
    private List<EventCard> createList(int size) {
        List<EventCard> result = new ArrayList<>();
        for (int i=1; i <= size; i++) {
            EventCard ci = new EventCard();
            ci.name = EventCard.NAME_PREFIX + i;
            ci.description = EventCard.DESC_PREFIX + i;
            ci.address = EventCard.ADDRESS_PREFIX + i;

            result.add(ci);
        }
        return result;
    }
}