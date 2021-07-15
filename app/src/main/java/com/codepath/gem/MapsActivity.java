package com.codepath.gem;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.codepath.gem.models.Experience;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.codepath.gem.databinding.ActivityMapsBinding;
// Parse Dependencies
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
// Java dependencies
import org.parceler.Parcels;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    public static final String TAG = "MapsActivity";
    Experience experience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        experience = (Experience) Parcels.unwrap(getIntent().getParcelableExtra(Experience.class.getSimpleName()));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showExperiencesInMap(googleMap);
    }

    private void showExperiencesInMap(final GoogleMap googleMap){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Experience");
        query.whereExists(Experience.KEY_LOCATION);
        query.whereEqualTo(Experience.KEY_TITLE, experience.getTitle());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override  public void done(List<ParseObject> experiences, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "size: " + experiences.size());
                    for(int i = 0; i < experiences.size(); i++) {
                        LatLng experienceLocation = new LatLng(experiences.get(i).getParseGeoPoint("location").getLatitude(), experiences.get(i).getParseGeoPoint("location").getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(experienceLocation).title(experiences.get(i).getString("title")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(experienceLocation));
                    }
                } else {
                    // handle the error
                    Log.d("experience", "Error: " + e.getMessage());
                }
            }
        });
        ParseQuery.clearAllCachedResults();
    }
}