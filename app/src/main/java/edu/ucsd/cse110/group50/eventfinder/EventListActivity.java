package edu.ucsd.cse110.group50.eventfinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.ucsd.cse110.group50.eventfinder.dummy.DummyContent;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * An activity representing a list of Events. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EventDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EventListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static ArrayList<Card> cards;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
   // private RecyclerView mMessageRecyclerView;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseHelper helper;
    private CardAdapter adapter;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_toolbar_menu, menu);

        MenuItem item = menu.findItem(R.id.to_List_view);
        item.setVisible(false);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.to_My_Activities:
                Intent intent = new Intent( EventListActivity.this, MyEvents.class );
                startActivity( intent );
                return true;

            case R.id.to_Map_View_Button:
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.event_list);
        //SETUP FB
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelper(mFirebaseDatabaseReference);
        //ADAPTER
        recyclerView.setAdapter(adapter);
        adapter=new CardAdapter(this,helper.retrieve());
        cards = helper.events;



        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.event_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


    }

//    private void setUpCards(){
//        cards = new ArrayList<>();
//        cards.add(new Card("umaru!", "2016/11/1", "(σ｀・д･)σ space " +
//                "space space space space space space space space space space " +
//                "space space space space space space space space space space " +
//                "space space space space space space space space space space " +
//                "space space space space space space space space space space ",
//                Uri.parse("geo:37.7749,-122.4194")));
//        cards.add(new Card("Dummy Content",
//                "1999/12/31", "Testcase 1234567890", Uri.parse("geo:37.7749,-122.4194")));
//        cards.add(new Card("Drop without W", "2048/255/255", "A long card\n\nLimits!(＠_＠;)\n" +
//                "\n\n\n\n\n\n\n\n\n",
//                Uri.parse("geo:37.7749,-122.4194")));
//        cards.add(new Card("Intentionally Left Blank", "", "",
//                Uri.parse("geo:37.7749,-122.4194")));
//        for(int i = 0; i < 5; i++)
//            cards.add(new Card());
//    }

    /**
     * Setup recyclerView and swipe and dismiss
     * @param recyclerView
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //setUpCards();
        recyclerView.setHasFixedSize(true);
        CardAdapter adapter = new CardAdapter(this,cards);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        /* Swipe and dismiss configuarations */
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    public void toggleMapView(View v){
        Intent intent = new Intent (EventListActivity.this, MapViewActivity.class);
        startActivity(intent);
    }


    @Override
    public void onResume()
    {
        super.onResume();

        //SETUP FB
        //mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //helper = new FirebaseHelper(mFirebaseDatabaseReference);
        //ADAPTER
        //recyclerView.setAdapter(adapter);
        adapter=new CardAdapter(this,helper.retrieve());
        cards = helper.events;
        setupRecyclerView((RecyclerView) recyclerView);

        System.out.println("On Resume Called!");
    }
}
