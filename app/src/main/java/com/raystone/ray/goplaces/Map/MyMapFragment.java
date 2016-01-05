package com.raystone.ray.goplaces.Map;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.raystone.ray.goplaces.Helper.MoveAmongFragments;
import com.raystone.ray.goplaces.Helper.MyBitMap;
import com.raystone.ray.goplaces.Helper.MyCurrentLocationService;
import com.raystone.ray.goplaces.Helper.Place;
import com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel3.PlaceDetailFragment;
import com.raystone.ray.goplaces.PlaceDetail.EditPlace.EditPlaceFragment;
import com.raystone.ray.goplaces.PlaceList.PlaceListFragment;
import com.raystone.ray.goplaces.PlaceList.Places;
import com.raystone.ray.goplaces.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 11/15/2015.
 */
public class MyMapFragment extends android.app.Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener{

    private static GoogleMap mMap;                                         // This fragment has a google map object
    public static Activity mCurrentActivity;                               // This is equal to getActivity()
    private GoogleApiClient mGoogleApiClient;                            //  More google map stuff
    private static Intent mLocationService;                               //  The intent for location service
    private static LocationReceiver mLocationReceiver;                    //  Location receiver when location has been located
    private static double mLatitude;                                     //  Latitude of the location
    private static double mLongitude;                                    //  Longitude of the location
    private EditText searchEditText;                                     //  The address search bar
    private TextView searchTextView;                                     //  Click this will perform search
    private ImageView myLocationImageView;                               //  Click this will locate the current location
    private Toolbar mToolbar;                                              //  The toolbar
    private View mView;                                                    // This is intended to get access to the view outside of the onCreateView()
    private TextView mUser;                                                //  This will appear in the DrawerLayout
    private boolean isLocationReceiverRegistered = false;              //  tell if the location receiver has registered or not
    private ImageView mPreviousMoment;                                    //  Click this will take one to the previous "moment" on the map
    private ImageView mNextMoment;                                        //  Click this will take one to the next "moment" on the map
    private int mCurrentMoment = -1;                                     //  This is the initial index for the current moment
    private int mTotalMoments;                                           //  The total number of "moments" one has
    private List<LatLng> mMomentsLocation= new ArrayList<>();            //  This is used to store the location of all the moments
    private ImageView mFacebookProfilePic;                               //  This shows the profile pic
    private String TAG = "MyMapFragment";


    public static MyMapFragment newInstance()
    {
        MyMapFragment mapFragment = new MyMapFragment();
        return mapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        load();
    }

    public void load()
    {
        setHasOptionsMenu(true);
        MoveAmongFragments.editPlace = null;
        MyBitMap.bmp = new ArrayList<>();
        MyBitMap.dir = new ArrayList<>();
        MyBitMap.max = 0;
        buildGoogleApiClient();
        mCurrentActivity = getActivity();
        mGoogleApiClient.connect();
        mTotalMoments = Places.get(getActivity()).getPlaces().size();  //  The total number of places(moments) ones has added
    }



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            mUser.setText(Place.mUserName);
            mFacebookProfilePic.setImageBitmap(Place.mUserProfilePic);
            if (parent != null)
                parent.removeView(mView);
            load();
        }

        try
        {
            mView = inflater.inflate(R.layout.navigation_layout, container, false);
            // The address search bar
            searchEditText = (EditText)mView.findViewById(R.id.address);
            searchTextView = (TextView)mView.findViewById(R.id.search);
            searchTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(searchEditText.getText().toString() !=null && !searchEditText.getText().toString().equals("")){
                        new LocationTask().execute(searchEditText.getText().toString());
                    }
                }
            });

            // Locating the current location
            myLocationImageView = (ImageView)mView.findViewById(R.id.my_location);
            myLocationImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Use the location service to locate
                    isLocationReceiverRegistered = true;
                    mLocationService = new Intent(getActivity(),MyCurrentLocationService.class);
                    getActivity().startService(mLocationService);

                    //  Add a filter for the receiver
                    IntentFilter filter = new IntentFilter("com.raystone.ray.goplaces_v1.LOCATION_SERVICE");
                    mLocationReceiver = new MyMapFragment.LocationReceiver();
                    getActivity().registerReceiver(mLocationReceiver, filter);
                }
            });

            // more work for the toolbar and the DrawerLayout
            mToolbar = (Toolbar) mView.findViewById(R.id.toolbar);
            //((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            DrawerLayout drawer = (DrawerLayout)mView. findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView)mView.findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            // Add a headView in the DrawerLayout
            LinearLayout headerView = (LinearLayout)LayoutInflater.from(getActivity()).inflate(R.layout.nav_header_navigation,null);
            mUser = (TextView)headerView.findViewById(R.id.user);
            mFacebookProfilePic = (ImageView)headerView.findViewById(R.id.facebook_pic);
            navigationView.addHeaderView(headerView);
            mUser.setText(Place.mUserName);
            mFacebookProfilePic.setImageBitmap(Place.mUserProfilePic);

            //  Buttons for jump to next/previous moments. They are "GONE" initially and will appear when click "View Moments on Map" in the DrawerLayout
            mPreviousMoment = (ImageView)mView.findViewById(R.id.previous_moment);
            mNextMoment = (ImageView)mView.findViewById(R.id.next_moment);
            mPreviousMoment.setVisibility(View.GONE);
            mNextMoment.setVisibility(View.GONE);
            mPreviousMoment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCurrentMoment == -1)     // When first click the "previous", it will take you to the last moment
                        mCurrentMoment = mMomentsLocation.size();
                    mCurrentMoment = mCurrentMoment - 1;
                    if(mCurrentMoment != -1)
                    {   //  Move the map focus to the current moment and zoom to level 10
                        LatLng currentLatLng = mMomentsLocation.get(mCurrentMoment);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 7));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 3000, null);
                    }
                }
            });
            mNextMoment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCurrentMoment >= mMomentsLocation.size()-1)           // When first click the "next", it will take you to the first moment.
                        mCurrentMoment = -1;
                    mCurrentMoment = mCurrentMoment + 1;
                    LatLng currentLatLng = mMomentsLocation.get(mCurrentMoment);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 7));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 3000, null);
                }
            });
        }catch(InflateException e)
        {}

        return mView;
    }

    private void showMomentsOnMap()
    {   //  When click "View Moments on Map" in the DrawerLayout, the "previous moment" and "next moment" will appear. It will also add markers on map if its corresponding LatLng is known. The marker are indexed so it will help to retrieve their corresponding moments.
        mPreviousMoment.setVisibility(View.VISIBLE);
        mNextMoment.setVisibility(View.VISIBLE);
        for (int i = 0; i < mTotalMoments; i ++)
        {   //  Get all the places(moments). Retrieve their LatLng and store them in the mMomentsLocation list.
            Place currentPlace = Places.get(getActivity()).getPlaces().get(i);
            double latitude = currentPlace.getPlaceLatitude();
            double longitude = currentPlace.getPlaceLongitude();
            LatLng old = new LatLng(latitude, longitude);
            mMomentsLocation.add(old);
            if(latitude == 0 && longitude == 0){    // for some places whose location has not defined by using the location service, their LatLng will be 0. Add index to markers.
                mMap.addMarker(new MarkerOptions().position(old).title("Location undefined.Edit the place by tapping").snippet(Integer.toString(i)));}
            else
                mMap.addMarker(new MarkerOptions().position(old).title(currentPlace.getAddress()).snippet(Integer.toString(i)));
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTitle().equals("Current Location"))
                {   // When click on the "current location" marker, it will prompt to add a new place(moment)
                    MoveAmongFragments.addPlaceMode  = true;
                    mapToDetail();
                }else{  // When click on other markers, it will jump to their corresponding place(moment) detail page where one can edit and save the place(moment).
                    MoveAmongFragments.markerToDetail = true;
                    MoveAmongFragments.editPlaceMode = true;
                    MoveAmongFragments.editPlace = Places.get(getActivity()).getPlaces().get(Integer.parseInt(marker.getSnippet()));;
                    MoveAmongFragments.STATE = "LISTFROMPLACE";
                    listToEdit();
                }
                return false;
            }
        });
    }

    private void listToEdit()
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("EDITPLACE");
        if(fragment == null) {
            fragment = EditPlaceFragment.newInstance();
        }
        trans.replace(R.id.login_fragment_container, fragment,"EDITPLACE");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "EDITPLACE";
    }

    private void mapToDetail()
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("PLACEDETAIL");
        if(fragment == null) {
            fragment = PlaceDetailFragment.newInstance();
        }
        trans.replace(R.id.login_fragment_container, fragment,"PLACEDETAIL");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "PLACEDETAIL";
    }

    private void mapToList()
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("PLACELISTDETAIL");
        if(fragment == null) {
            fragment = PlaceListFragment.newInstance();
        }
        trans.replace(R.id.login_fragment_container, fragment,"PLACELISTDETAIL");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "PLACELISTDETAIL";
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mMap = null;
        mGoogleApiClient.disconnect();
        if(isLocationReceiverRegistered){  // unregister the location receiver
            getActivity().unregisterReceiver(mLocationReceiver);
            mLocationService = null;
            mLocationReceiver = null;
            isLocationReceiverRegistered = false;
        }
        mCurrentActivity = null;

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_moments) {  // Add a new place
            MoveAmongFragments.addPlaceMode  = true;
            mapToDetail();
        } else if (id == R.id.view_list) {  // View places on list
            mapToList();
        } else if (id == R.id.map_show) {   // View places on map
            showMomentsOnMap();
            MoveAmongFragments.markerToDetail = true;
        }

        DrawerLayout drawer = (DrawerLayout) mView .findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {   // set up a few things for google map
        mMap = getMapFragment().getMap();
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if(MoveAmongFragments.markerToDetail){
            showMomentsOnMap();}
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("", "Connection suspended");
        mGoogleApiClient.connect();
    }

    public static class LocationReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context,Intent intent)
        {   // when the current location has been located, it will move focus to that place
            mLatitude = intent.getDoubleExtra("Latitude",0);
            mLongitude = intent.getDoubleExtra("Longitude",0);
            updateUI();
            abortBroadcast();
        }
    }

    // different OS will have to use different ways to get MapFragment
    private MapFragment getMapFragment() {
        FragmentManager fm = null;
        Log.d(TAG, "sdk: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "release: " + Build.VERSION.RELEASE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "using getFragmentManager");
            fm = getFragmentManager();
        } else {
            Log.d(TAG, "using getChildFragmentManager");
            fm = getChildFragmentManager();
        }

        return (MapFragment) fm.findFragmentById(R.id.map);
    }

    private static void updateUI()
    {
        LatLng old = new LatLng(mLatitude, mLongitude);
        mMap.addMarker(new MarkerOptions().position(old).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(old));
        mCurrentActivity.stopService(mLocationService);

    }

    //  This AsyncTask is used to get the location when search the address in the search bar
    public class LocationTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName)
        {
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> addresses = null;

            try
            {   // get the first result
                addresses = geocoder.getFromLocationName(locationName[0],1);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses)
        {   //   if location has been located, it will add a marker on the map
            if(addresses == null || addresses.size() == 0)
                Toast.makeText(getActivity(),"No Location Found",Toast.LENGTH_SHORT).show();
            else {
                Address address = addresses.get(0);
                LatLng old = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(old).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(old));
            }
        }
    }
}
