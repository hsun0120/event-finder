package edu.ucsd.cse110.group50.eventfinder;


import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

import edu.ucsd.cse110.group50.eventfinder.storage.EvDate;
import edu.ucsd.cse110.group50.eventfinder.storage.Event;
import edu.ucsd.cse110.group50.eventfinder.storage.EventList;
import edu.ucsd.cse110.group50.eventfinder.storage.User;
import edu.ucsd.cse110.group50.eventfinder.utility.Identifiers;
import edu.ucsd.cse110.group50.eventfinder.utility.LoadListener;
import edu.ucsd.cse110.group50.eventfinder.utility.ServerLog;

/**
 * The main activity of the app.The main screen will be the mapView, with the ability to switch
 * to MyEvent list and All Event list.
 * Reference of inspiration:
 * Yining Liang :
 *  1. https://codelabs.developers.google.com/codelabs/firebase-android/#0
 *      I followed this tutorial for introduction on firebase.
 */
public class MapView extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 1;

    GoogleApiClient mGoogleApiClient; //Google Map Api client
    GoogleApiClient geoInfo; //Google Geo Info Api client
    Location mLastLocation;

    static User curUser;
    boolean starting;
    boolean loggedIn;

    // Firebase instance variables
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;
    public static DatabaseReference mFirebaseReference;

    // for the drawer
    private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    // menu
    private Menu mOptionsMenu;
    static ProgressBar spinner;

    //Flag used to determine which page the user is on. 0 for my_events, 1 for all_events_list;
    static boolean user_on_all_events_flag;
    static boolean user_on_search_event_flag;


    //Search text entered by user.
    static String searchedText;

    private static final int SIGN_IN_REQUEST = 9000;
    private static final String TAG = "MapView";

    //Used for managing view of event list.
    static EventList eventList;
    MyListFragment nearbyEventListFragment = null;
    private Fragment curFragment;

    //Used for swipe and click on card interactions.
    static EvDate date_filtered;
    static int swiped_position;
    static String swiped_item_uid;
    private static Context currContext;

    /**
     * Callback function for onMarkerClickListener
     * @param marker Marker clicked
     * @return false to show info window
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    // inner class for drawer item listener
    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
            Log.d( "DRAWER", "position " + position + " selected!" );
            if ( position == 1 ) {
                FirebaseAuth.getInstance().signOut();
                curUser = null;
                recreate();
            }

        }
    }

    /**
     * Push fragment to the container
     * @param myFragment fragment used to replace
     */
    public void pushFragment(Fragment myFragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.container, myFragment); //Replace fragment
        ft.commit();
    }

    /**
     * Pop a fragment from the container
     * @param myFragment fragment used to replace
     */
    public void popFragment(Fragment myFragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_in);
        ft.replace(R.id.container, myFragment);
        ft.commit();
    }

    /**
     * Create the MapView activity
     * @param savedInstanceState current state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currContext = getApplicationContext();
        starting = true;

        setContentView( R.layout.activity_map_view ); //Load MapView
        spinner = (ProgressBar) findViewById(R.id.loading_spinner);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if ( mFirebaseUser == null ) {
            // Not signed in, launch the Sign In activity
            loggedIn = false;
            startActivityForResult( new Intent( this, LoginScreen.class ), SIGN_IN_REQUEST );
        } else {
            Log.i( TAG, "User already signed in." );
            loggedIn = true;
            startup(); //Setup components of the page
        }
    }

    /**
     * Handle user login activity
     * @param requestCode user sign in request code
     * @param resultCode user sign in result
     * @param data user data
     */
    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if ( requestCode == SIGN_IN_REQUEST ) {
            if ( resultCode == LoginScreen.LOGGED_IN ) {
                Log.i( TAG, "User sign-in successful." );
                loggedIn = true;
                startup(); //Setup components
            } else {
                Log.i( TAG, "User sign-in aborted." );
                startActivityForResult( new Intent( this, LoginScreen.class ),SIGN_IN_REQUEST );
            }
        }

    }

    /**
     * Setup back-end database and components of the page
     */
    public void startup() {
        mFirebaseReference = FirebaseDatabase.getInstance().getReference();
        ServerLog.loadDatabase();
        // Initialize the drawer list
        mDrawerTitles = new String[2];
        mDrawerTitles[0] = "";
        mDrawerTitles[1] = "";

        if ( curUser == null ) { //Read from database
            String userID = mFirebaseAuth.getCurrentUser().getUid();
            User.readFromFirebase(
                    mFirebaseReference.child( Identifiers.FIREBASE_USERS ).child( userID ),
                    new LoadListener() {
                        @Override
                        public void onLoadComplete(Object data) {
                            curUser = (User) data;
                            curUser.addListener( new LoadListener() {
                                @Override
                                public void onLoadComplete( Object data ) {
                                    mDrawerTitles[0] = curUser.getName();
                                    // Update the adapter for the list view
                                    mDrawerList.setAdapter(new ArrayAdapter<>(MapView.this,
                                            R.layout.drawer_list_item, mDrawerTitles));
                                    curUser.removeListener( this );
                                }
                            });

                        }
                    },
                    userID );
        }
        else
        {
            mDrawerTitles[0] = curUser.getName();
        }

        mDrawerTitles[1] = "Logout";
        //mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mDrawerTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener( new DrawerItemClickListener() );

        // Setting up toolbar
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Switch among different tabs
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if ( starting && loggedIn ) {
            // Setting up list
            eventList = EventList.getInstance();
            nearbyEventListFragment = new MyListFragment();
            final SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();

            // Setting up bottombar
            final BottomBar bottomBar = (BottomBar) findViewById( R.id.bottomBar );

            // Set default tab to location_item
            bottomBar.setDefaultTabPosition(1);
            curFragment = supportMapFragment;

            if(date_filtered != null)
            {
                nearbyEventListFragment.update();
            }

            bottomBar.setOnTabSelectListener( new OnTabSelectListener() {

                @Override
                public void onTabSelected( @IdRes int tabId ) { //Handle tab selection
                    switch ( tabId ) {
                        case R.id.my_event_item:
                            Log.d("TAB", "My Event Item Selected");
                            user_on_all_events_flag = false;
                            invalidateOptionsMenu();
                            popFragment( curFragment );
                            pushFragment( nearbyEventListFragment );
                            curFragment = nearbyEventListFragment;
                            nearbyEventListFragment.update();
                            break;
                        case R.id.location_item:
                            Log.d("TAB", "Location Item Selected");
                            invalidateOptionsMenu();
                            popFragment( curFragment );
                            pushFragment( supportMapFragment );
                            curFragment = supportMapFragment;
                            supportMapFragment.getMapAsync(MapView.this);
                            break;
                        case R.id.list_item:
                            Log.d("TAB", "List Item Selected");
                            user_on_all_events_flag = true;
                            invalidateOptionsMenu();
                            popFragment( curFragment );
                            pushFragment( nearbyEventListFragment );
                            curFragment = nearbyEventListFragment;
                            nearbyEventListFragment.update();
                            break;
                    }

                }

            });

            bottomBar.setOnTabReselectListener(new OnTabReselectListener() {

                @Override
                public void onTabReSelected(@IdRes int tabId) { //Handle tab reselection
                    switch (tabId) {
                        case R.id.my_event_item:
                            Log.d("TAB", "My Event Item Reselected");
                            nearbyEventListFragment.update();
                        case R.id.location_item:
                            Log.d("TAB", "Location Item Reselected");
                        case R.id.list_item:
                            Log.d("TAB", "List Item Reselected");
                            nearbyEventListFragment.update();
                            break;
                    }
                }

            });

            // Set the color for the active tab. Ignored on mobile when there are more than three tabs.
            //bottomBar.setActiveTabColor(0xC2185B);
            starting = false;
        }
    }

    /**
     * Search and filter function
     * @param menu options menu
     * @return true if the menu is created successfully; otherwise false
     */
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the options menu from XML
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        mOptionsMenu = menu;

        MenuItem b_filter = mOptionsMenu.findItem(R.id.action_filter_toolbar);
        MenuItem b_add = mOptionsMenu.findItem(R.id.action_add_event);

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        if ( bottomBar.getCurrentTabId() != R.id.my_event_item ) {
            b_add.setVisible(false);
            b_filter.setVisible(true);

        } else {
            b_add.setVisible(true);
            b_filter.setVisible(false);
        }


        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(1000);
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        //Attach search text listener.
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            /**
             * Callback for text change
             * @param query query to search
             * @return true if search successfully; otherwise false
             */
            public boolean onQueryTextChange(String query) {
                // this is your adapter that will be filtered
                searchedText = query;
                Log.d( TAG, "Searched TEXT is "+ searchedText );
                if(!query .equals(""))
                {
                    user_on_search_event_flag = true;

                    Log.v( TAG, "User entered SOMETHING!" );
                    nearbyEventListFragment.update();
                }
                else
                {
                    user_on_search_event_flag = false;
                    Log.v( TAG, "User entered NOTHING!" );
                    nearbyEventListFragment.update();
                }
                return true;
            }

            /**
             * Callback for text submitted
             * @param query query to search
             * @return true if text is submitted successfully; otherwise fase
             */
            public boolean onQueryTextSubmit(String query) {

                //Hee u can get the value "query" which is entered in the search box.

                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);




        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Start the MapView activity
     */
    protected void onStart() {

        //Clear edited card
        CreateEvent.editedCard = null;

        // Create an instance of GoogleAPIClient.
        if ( mGoogleApiClient == null ) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (geoInfo == null) { //Initialize Google Geo Info API
            geoInfo = new GoogleApiClient.Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }

        /* Connect and start activity */
        mGoogleApiClient.connect();
        geoInfo.connect();
        super.onStart();

    }

    /**
     * Callback for activity stop
     */
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) { //Disconnect api client
            mGoogleApiClient.disconnect();
        }

        if(geoInfo.isConnected()) //Disconnect api client
            geoInfo.disconnect();
        super.onStop();
    }

    /**
     * Save current state
     * @param state current state
     */
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    /**
     * Launch the filter
     * @param item menu
     */
    public void openFilter(MenuItem item) {
        // Opening filter
        Intent intent = new Intent(this, OpenFilter.class);
        startActivity(intent);
    }

    /**
     * Callback for connecting server
     * @param bundle current activity
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("CONNECT", "onConnecting......");
        /* Check location permission */
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);
            return;
        }

        try { //Get most recent known location
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (SecurityException e) {
            Log.d("SE", e.getMessage());
        }

        if (mLastLocation != null) { //Fail to obtain most recent location, use default
            Log.d("LATITUDE", String.valueOf(mLastLocation.getLatitude()));
            Log.d("LONGITUDE", String.valueOf(mLastLocation.getLongitude()));
        }
    }

    /**
     * Callback for connection suspend
     * @param i error code
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.",
                    Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback for connection failure
     * @param connectionResult connection result
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("CONNECTION_FAILED","onConnectionFailed:"+connectionResult.getErrorCode()+","
                +connectionResult.getErrorMessage());
    }

    /**
     * Callback when the MapView is ready to display
     * @param map Google Map to display
     */
    @Override
    public void onMapReady(GoogleMap map) {
        Log.v( TAG, "Map ready." );
        /* Check user permission */
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);

            Log.e( TAG, "Location permissions not set." );
            return;
        }

        map.setMyLocationEnabled(true); //Enable current location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient); //Get most recent location
        LatLng loc;
        if( mLastLocation == null ) {
            loc = new LatLng( 32.8801, -117.2340 ); //Default location
        } else {
            loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        /* Add marker for current location */
        map.addMarker( new MarkerOptions()
                .position(loc)
                .title("You're here")
                .snippet("Current location\nEvent nerby is shown\n ")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        markAllEvent(map); //Add marker for all valid events
        map.setInfoWindowAdapter(new MyInfoWindowAdapter(this));
        map.setOnMarkerClickListener(this);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f)); //Zoom to current location
    }

    /**
     * Open create event page
     * @param item menu component
     * @return true if the page is load successfully; otherwise false
     */
    public boolean gotoCreateEvent(MenuItem item)
    {
        Intent intent1 = new Intent(MapView.this, CreateEvent.class);
        intent1.putExtra( Identifiers.USER, curUser );
        startActivity( intent1 );
        return true;
    }

    /**
     * Callback for permission request
     * @param requestCode permission request code
     * @param permissions list of permissions
     * @param grantResults list of results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    onStop();
                }
                return;
            }
        }
    }

    /**
     * Make markers for all the events that are not out-of-date
     * @param map Google map
     */
    private void markAllEvent(GoogleMap map) {
        ArrayList<Event> event_list = eventList;
        if(event_list.size() == 0) return; //event list is not ready yet

        for(int i = 0; i < event_list.size(); i++){ //Make markers
            Event event = event_list.get(i);
            if (!event.getDate().isPast()) { //Validate events
                LatLng loc = new LatLng(event.getLat(), event.getLng());
                String info = event.getDescription() + "\n" + event.getDate().getDate() + "\n"
                        + event.getDate().getTime();
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(loc)
                        .title(event.getName())
                        .snippet(info));
            }
        }
    }

    /**
     * Callback function of item swipe
     * @param swiped_position1 swiped item
     */
    public static void itemSwiped(int swiped_position1)
    {
        swiped_position = swiped_position1;
        swiped_item_uid = eventList.get(swiped_position1).getUid();
        Intent n = new Intent(currContext, DeleteDialog.class);
        n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        currContext.startActivity(n); //Update event list
    }

    /**
     * Delete item from the database and local storage
     */
    public static void deleteItem()
    {
        System.out.println("Position to delete is "+swiped_position);
//        mFirebaseReference.child("events").child(swiped_item_uid).
//                removeValue();

        Log.d( TAG, "REMOVE: " + eventList.remove( new Event(swiped_item_uid, "") ) );

    }

}
