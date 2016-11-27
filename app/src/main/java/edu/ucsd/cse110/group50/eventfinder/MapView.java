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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    private static final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 1;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    final static int UPDATE_INTERVAL = 300;
    final static int FASTEST_INTERVAL = 100;

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

    static EventList eventList;
    MyListFragment nearbyEventListFragment = null;
    private Fragment curFragment;

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
        starting = true;

        setContentView( R.layout.activity_map_view );

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
            startup();
        }

    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {

        if ( requestCode == SIGN_IN_REQUEST ) {
            if ( resultCode == LoginScreen.LOGGED_IN ) {
                Log.i( TAG, "User sign-in successful." );
                loggedIn = true;
                startup();
            } else {
                Log.i( TAG, "User sign-in aborted." );
                startActivityForResult( new Intent( this, LoginScreen.class ), SIGN_IN_REQUEST );
            }
        }

    }

    public void startup() {

        mFirebaseReference = FirebaseDatabase.getInstance().getReference();
        ServerLog.loadDatabase();

        if ( curUser == null ) {
            String userID = mFirebaseAuth.getCurrentUser().getUid();
            User.readFromFirebase(
                    mFirebaseReference.child( Identifiers.FIREBASE_USERS ).child( userID ),
                    new LoadListener() {

                        @Override
                        public void onLoadComplete(Object data) {

                            curUser = (User) data;

                        }

                    },
                    userID );
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

        // Setting up toolbar
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    protected void onPostResume() {

        super.onPostResume();

        if ( starting && loggedIn ) {

            // Setting up list
            eventList = new EventList( mFirebaseReference.child(Identifiers.FIREBASE_EVENTS) );
            nearbyEventListFragment = new MyListFragment();
            final SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();

            // Setting up bottombar
            final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);

            // Set default tab to location_item
            bottomBar.setDefaultTabPosition(1);
            curFragment = supportMapFragment;

            bottomBar.setOnTabSelectListener( new OnTabSelectListener() {

                @Override
                public void onTabSelected( @IdRes int tabId ) {

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
                public void onTabReSelected(@IdRes int tabId) {

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

            public boolean onQueryTextSubmit(String query) {

                //Hee u can get the value "query" which is entered in the search box.

                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);




        return super.onCreateOptionsMenu(menu);
    }

    protected void onStart() {

        // Create an instance of GoogleAPIClient.
        if ( mGoogleApiClient == null ) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();
        super.onStart();

    }

    protected void onStop() {
        // only stop if it's connected, otherwise we crash
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
        Log.d("CONNECT", "onConnecting......");
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                //mLocationRequest, this);

        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (SecurityException e) {
            Log.d("SE", e.getMessage());
        }

        if (mLastLocation != null) {
            Log.d("LATITUDE", String.valueOf(mLastLocation.getLatitude()));
            Log.d("LONGITUDE", String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("CONNECTION_FAILED","onConnectionFailed:"+connectionResult.getErrorCode()+","
                +connectionResult.getErrorMessage());
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.v( TAG, "Map ready." );
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.e( TAG, "Location permissions not set." );
            return;
        }
        map.setMyLocationEnabled(true);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LatLng loc;
        if( mLastLocation == null ) {
            loc = new LatLng( 32.8801, -117.2340 );
        } else {
            loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        map.addMarker( new MarkerOptions()
                .position(loc)
                .title("You're here")
                .draggable(true) );
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
    }


    public boolean gotoCreateEvent(MenuItem item)
    {

        Intent intent1 = new Intent(MapView.this, CreateEvent.class);
        intent1.putExtra( Identifiers.USER, curUser );
        startActivity( intent1 );

        return true;
    }



    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);

            return;
        }
        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    onStop();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

//    //Yining: Dummy Local Search Functions:
//    public ArrayList<Event> processSearch(ArrayList<Event> curr_list,  String hostID)
//    {
//        ArrayList<Event> new_list = new ArrayList<>();
//
//        for(Event e : curr_list)
//        {
//            if(e.getHost().equals(hostID))
//            {
//                System.out.println("In MYEVENTS, UID MATCH\n userid is "+e.getUid());
//                new_list.add(e);
//            }
//            else
//            {
//                System.out.println("In MYEVENTS, UID NOT MATCH\n curr userid is "+ hostID + "\nHOst UID in data is "+ e.getHost());
//            }
//        }
//
//        return new_list;
//    }
}
