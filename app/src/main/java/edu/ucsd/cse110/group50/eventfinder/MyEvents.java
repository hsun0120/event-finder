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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedList;

public class MyEvents extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private boolean mTwoPane;
    private LinkedList<Card> cards;





    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Card, CardAdapter.ViewHolder>
            mFirebaseAdapter;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

//    public static class CardAdapter.ViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView eventName;
//        public TextView eventDate;
//        public TextView eventDes;
//        public CardAdapter.ViewHolder(View v) {
//            super(v);
//            eventName = (TextView) itemView.findViewById(R.id.info_title);
//            eventDate = (TextView) itemView.findViewById(R.id.info_date);
//            eventDes = (TextView) itemView.findViewById(R.id.info_des);
//        }
//
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);


       // View recyclerView = findViewById(R.id.event_list);
        //assert recyclerView != null;
        //setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.event_list) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }



        //Download from firebase
        String mUsername = "anonymous";


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginScreen.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

//        // Initialize ProgressBar and RecyclerView.
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.event_list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
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
//                    viewHolder.eventName.setText(eventCard.getCardName());
//                    viewHolder.eventDate.setText(eventCard.getDate());
//                    viewHolder.eventDes.setText(eventCard.getDescription());
//
//            }
//        };
//
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

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);





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



//    //for view of cards.
//    private void setUpCards(){
//        cards = new LinkedList<Card>();
//        cards.add(new Card("Enrollment Begin",
//                "2016/10/30", "description"));
//        cards.add(new Card("Drop without W",
//                "2016/10/31", "description"));
//        cards.add(new Card("Drop without W",
//                "2016/10/32", "description"));
//        cards.add(new Card("Drop without W",
//                "2016/10/33", "description"));
//    }
//
//    /**
//     * Setup recyclerView and swipe and dismiss
//     * @param recyclerView
//     */
//    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        setUpCards();
//        recyclerView.setHasFixedSize(true);
//        CardAdapter adapter = new CardAdapter(cards);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//
//        /* Swipe and dismiss configuarations */
//        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
//        ItemTouchHelper helper = new ItemTouchHelper(callback);
//        helper.attachToRecyclerView(recyclerView);
//    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    // An unresolvable error has occurred and Google APIs (including Sign-In) will not
    // be available.
     Log.d("LoginScreen", "onConnectionFailed:" + connectionResult);
     Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
