package edu.ucsd.cse110.group50.eventfinder;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

public class MapView extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    public static User curUser;
    public static String currUid;

    // Firebase instance variables
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;
    public static DatabaseReference mFirebaseReference;

    // for the drawer
    private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    //Flag used to determine which page the user is on. 0 for my_events, 1 for all_events_list;
    public static int user_on_all_events_flag;
    public static int user_on_earch_event_flag;


    //Search text entered by user.
    String searchedText;

    public static EventList eventList;
    MyListFragment nearbyEventListFragment = null;


    // inner class for drawer item listener
    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("DRAWER", "position "+position+" selected!");
        }
    }

    // Push a fragment into the container
    public void pushFragment(Fragment myFragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        ft.replace(R.id.container, myFragment);
        ft.commit();
    }

    // Pop a fragment into the container
    public void popFragment(Fragment myFragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_in);

        ft.replace(R.id.container, myFragment);
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginScreen.class));
            finish();
            return;
        }
        mFirebaseReference = FirebaseDatabase.getInstance().getReference();
        ServerLog.loadDatabase();

        Intent intent = getIntent();
        curUser = intent.getParcelableExtra( Identifiers.USER );
        if ( curUser == null ) {
            String user = mFirebaseAuth.getCurrentUser().getUid();
            currUid = user;
            User.readFromFirebase(
                    mFirebaseReference.child( Identifiers.FIREBASE_USERS ).child( user ),
                    new LoadListener() {

                        @Override
                        public void onLoadComplete(Object data) {

                            curUser = (User) data;

                        }

                    },
                    user );
        }

        // Initialize the drawer list
        mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mDrawerTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Setting up map
        FragmentManager fm = getSupportFragmentManager();
        final SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.container, supportMapFragment).commit();







        // Setting up list
        eventList = new EventList( mFirebaseReference.child(Identifiers.FIREBASE_EVENTS) );
        nearbyEventListFragment = new MyListFragment();
        //myListFragPointer = nearbyEventListFragment;
        //final MyListFragment myListFragment = new MyListFragment();

        // Setting up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setting up bottombar
        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        // Set default tab to location_item
        bottomBar.setDefaultTabPosition(2);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                int current=bottomBar.getCurrentTabId();
                switch (tabId){
                    case R.id.my_event_item:
                        user_on_all_events_flag = 0;
                        popFragment(supportMapFragment);
                        pushFragment(nearbyEventListFragment);
                        Log.d("TAB","My Event Item Selected");

                        break;
                    case R.id.list_item:
                        user_on_all_events_flag = 1;
                        Log.d("TAB","List Item Selected");
                        popFragment(supportMapFragment);
                        pushFragment(nearbyEventListFragment);
                        break;
                    case R.id.location_item:
                        Log.d("TAB","Location Item Selected");
                        popFragment(nearbyEventListFragment);
                        pushFragment(supportMapFragment);
                        break;
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
            }
        });

        // Set the color for the active tab. Ignored on mobile when there are more than three tabs.
        //bottomBar.setActiveTabColor(0xC2185B);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);


        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(1000);
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        //Attach search text listener.
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String query) {
                // this is your adapter that will be filtered
                searchedText = query;
                System.out.println("Searched TEXT is "+ searchedText);
                if(!query .equals(""))
                {
                    user_on_earch_event_flag = 1;

                    System.out.println("User entered SOMETHING!");
                    nearbyEventListFragment.onResume();
                }
                return true;
            }

            public boolean onQueryTextSubmit(String query) {

                //Hee u can get the value "query" which is entered in the search box.

                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);




        return super.onCreateOptionsMenu(menu);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    public void openFilter(MenuItem item) {
        // Opening filter
        Intent intent = new Intent(this, OpenFilter.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("CONNECT","onConnecting......");

        try{
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (SecurityException e){
            Log.d("SE",e.getMessage());
        }

        if (mLastLocation != null) {
            Log.d("LATITUDE",String.valueOf(mLastLocation.getLatitude()));
            Log.d("LONGITUDE",String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }


    public void gotoCreateEvent(View view)
    {

        Intent intent1 = new Intent(MapView.this, CreateEvent.class);
        intent1.putExtra( Identifiers.USER, curUser );
        startActivity( intent1 );
    }

}
