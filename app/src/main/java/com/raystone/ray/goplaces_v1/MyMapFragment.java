package com.raystone.ray.goplaces_v1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by Ray on 11/15/2015.
 */
public class MyMapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener{

    private static GoogleMap mMap;
    public static Activity currentActivity;
    private GoogleApiClient mGoogleApiClient;
    private static Intent locationService;
    private static LocationReceiver mLocationReceiver;
    private static double mLatitude;
    private static double mLongitude;
    private EditText searchEditText;
    private TextView searchTextView;
    private ImageView myLocationImageView;
    private Toolbar toolbar;
    private View view;


    public static MyMapFragment newInstance()
    {
        MyMapFragment mapFragment = new MyMapFragment();
        return mapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.navigation_layout, container, false);
        searchEditText = (EditText)v.findViewById(R.id.address);
        searchTextView = (TextView)v.findViewById(R.id.search);
        searchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchEditText.getText().toString() !=null && !searchEditText.getText().toString().equals("")){
                    new LocationTask().execute(searchEditText.getText().toString());
                }
            }
        });
        myLocationImageView = (ImageView)v.findViewById(R.id.my_location);
        myLocationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationService = new Intent(currentActivity,MyCurrentLocationService.class);
                currentActivity.startService(locationService);
                IntentFilter filter = new IntentFilter("com.raystone.ray.goplaces_v1.LOCATION_SERVICE");
                mLocationReceiver = new MyMapFragment.LocationReceiver();
                currentActivity.registerReceiver(mLocationReceiver, filter);
            }
        });
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout)v. findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView)v.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        view = v;
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        buildGoogleApiClient();
        currentActivity = getActivity();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mGoogleApiClient.disconnect();
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

        if (id == R.id.nav_camera) {
            Intent i = new Intent(getActivity(),LoginActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) view .findViewById(R.id.drawer_layout);
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
    public void onConnected(Bundle connectionHint) {
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if(mMap != null){
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {

                    return true;
                }
            });}
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
        {
            mLatitude = intent.getDoubleExtra("Latitude",0);
            mLongitude = intent.getDoubleExtra("Longitude",0);
            updateUI();
            //currentActivity.unregisterReceiver(mLocationReceiver);

        }

    }

    private static void updateUI()
    {
        LatLng old = new LatLng(mLatitude, mLongitude);
        mMap.addMarker(new MarkerOptions().position(old).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(old));
        currentActivity.stopService(locationService);

    }

    public class LocationTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName)
        {
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> addresses = null;

            try
            {
                addresses = geocoder.getFromLocationName(locationName[0],1);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses)
        {
            if(addresses == null || addresses.size() == 0)
                Toast.makeText(getActivity(),"No Location Found",Toast.LENGTH_SHORT).show();
            else {
                Address address = addresses.get(0);
                LatLng old = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(old).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(old));
            }
            //currentActivity.stopService(locationService);
        }

    }

}
