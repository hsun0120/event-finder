package edu.ucsd.cse110.group50.eventfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.LinkedList;

public class MyEvents extends AppCompatActivity {


    private boolean mTwoPane;
    private LinkedList<Card> cards;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);


        View recyclerView = findViewById(R.id.event_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.event_list) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }



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



    //for view of cards.
    private void setUpCards(){
        cards = new LinkedList<Card>();
        cards.add(new Card("Enrollment Begin",
                "2016/10/30", "description"));
        cards.add(new Card("Drop without W",
                "2016/10/31", "description"));
        cards.add(new Card("Drop without W",
                "2016/10/32", "description"));
        cards.add(new Card("Drop without W",
                "2016/10/33", "description"));
    }

    /**
     * Setup recyclerView and swipe and dismiss
     * @param recyclerView
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        setUpCards();
        recyclerView.setHasFixedSize(true);
        CardAdapter adapter = new CardAdapter(cards);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        /* Swipe and dismiss configuarations */
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }


}
