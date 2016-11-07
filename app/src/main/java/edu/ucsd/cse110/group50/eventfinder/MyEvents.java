package edu.ucsd.cse110.group50.eventfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.LinkedList;

public class MyEvents extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private boolean mTwoPane;


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    //private FirebaseRecyclerAdapter<Card, CardAdapter.ViewHolder> mFirebaseAdapter;
    //private FirebaseListAdapter<Card> mFirebaseAdapter;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    FirebaseHelper helper;
    CardAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setup content view and tool bar.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        if(savedInstanceState == null)
        {
            System.out.println("savedinstance is NULL!!!!");
        }
        else
        {
            System.out.println("Savedinstance is NOT NULL");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.event_list) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.event_list);
        assert mMessageRecyclerView != null;
        //mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);


        //Setup user Auth
        //Download from firebase
        String mUsername = "anonymous";
        //mFirebaseAuth = FirebaseAuth.getInstance();
        //mFirebaseUser = mFirebaseAuth.getCurrentUser();
        // Initialize Firebase Auth
//        if (mFirebaseUser == null) {
//            // Not signed in, launch the Sign In activity
//            startActivity(new Intent(this, LoginScreen.class));
//            finish();
//            return;
//        } else {
//            mUsername = mFirebaseUser.getDisplayName();
//        }



//        //SETUP RV
//        rv= (RecyclerView) findViewById(R.id.rv);
//        rv.setLayoutManager(new LinearLayoutManager(this));
        //SETUP FB
        //mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference = MapViewActivity.mFirebaseReference;
        helper = new FirebaseHelper(mFirebaseDatabaseReference);
        //ADAPTER
        mMessageRecyclerView.setAdapter(adapter);
        adapter=new CardAdapter(this,helper.retrieve());







        setupRecyclerView((RecyclerView) mMessageRecyclerView);





//
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API)
//                .build();

        // Initialize ProgressBar and RecyclerView.
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setStackFromEnd(false);





//        // New child entries
//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
//       helper = new FirebaseHelper(mFirebaseDatabaseReference);
//        //mFirebaseDatabaseReference = new DatabaseReference("https://windy-oxide-146019.firebaseio.com/");
//        mFirebaseAdapter = new FirebaseRecyclerAdapter<Card, CardAdapter.ViewHolder>(
//                Card.class,
//                R.layout.event_list,
//                CardAdapter.ViewHolder.class,
//                mFirebaseDatabaseReference.child("events")) {
//
//            @Override
//            protected void populateViewHolder(CardAdapter.ViewHolder viewHolder,
//                                              Card eventCard, int position) {
////                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                    viewHolder.eventName.setText(eventCard.getEventName());
//                    viewHolder.eventDate.setText(eventCard.getEventDate());
//                    viewHolder.eventDes.setText(eventCard.getEventDescription());
//
//            }
//        };
//        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mMessageRecyclerView.setAdapter(mFirebaseAdapter);



        //ADAPTER










//        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                int numOfEvents = mFirebaseAdapter.getItemCount();
//                int lastVisiblePosition =
//                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//                // If the recycler view is initially being loaded or the
//                // user is at the bottom of the list, scroll to the bottom
//                // of the list to show the newly added message.
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (numOfEvents - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    mMessageRecyclerView.scrollToPosition(positionStart);
//                }
//            }
//        });
        //super.onCreate(savedInstanceState);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_events_toolbar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_add:
                intent = new Intent( MyEvents.this, CreateEvent.class );
                startActivity( intent );
                return true;

            case R.id.action_calendarView:
                intent = new Intent( MyEvents.this, MyCalendar.class );
                startActivity( intent );
                return true;

            case R.id.action_filter:
                intent = new Intent( MyEvents.this, MyEventsFilter.class );
                startActivity( intent );
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


//
    /**
     * Setup recyclerView and swipe and dismiss
     * @param recyclerView
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        System.out.println("setup recyler is called!!!!!!!!!!");


        ArrayList<Card> events = helper.events;

        recyclerView.setHasFixedSize(true);
        //CardAdapter adapter = new CardAdapter(this,events);
        //adapter=new CardAdapter(this,this.helper.retrieve());



        recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(mLinearLayoutManager);

        /* Swipe and dismiss configuarations */
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);

        //CardAdapter adapter = new CardAdapter(this,cards);
        //adapter=new CardAdapter(this,this.helper.retrieve());



        helper.attachToRecyclerView(recyclerView);

        //mMessageRecyclerView.setAdapter(adapter);

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    // An unresolvable error has occurred and Google APIs (including Sign-In) will not
    // be available.
     Log.d("LoginScreen", "onConnectionFailed:" + connectionResult);
     Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        //SETUP FB
//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        helper = new FirebaseHelper(mFirebaseDatabaseReference);
//        //ADAPTER
//        mMessageRecyclerView.setAdapter(adapter);
        adapter=new CardAdapter(this,helper.retrieve());
        setupRecyclerView((RecyclerView) mMessageRecyclerView);
        System.out.println("On Resume Called!");
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        adapter.cleanup();
//    }

}
