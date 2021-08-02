package com.codepath.gem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.codepath.gem.models.Experience;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.codepath.gem.databinding.ActivityMapsBinding;
// Parse Dependencies
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
// Java dependencies
import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LocationManager locationManager;
    public static final String TAG = "MapsActivity";
    Experience experience;
    MarkerOptions marker;

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
        showExperiencesInMap(mMap);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                directionsToMarker();
                return true;
            }
        });
    }

    private void showExperiencesInMap(final GoogleMap googleMap){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Experience");
        query.whereExists(Experience.KEY_LOCATION);
        query.whereEqualTo(Experience.KEY_TITLE, experience.getTitle()); // TODO: assumes unique title, update to stronger filter
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override  public void done(List<ParseObject> experiences, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "size: " + experiences.size());
                    for(int i = 0; i < experiences.size(); i++) {
                        IconGenerator iconGenerator = new IconGenerator(MapsActivity.this);
                        iconGenerator.setStyle(IconGenerator.STYLE_WHITE);
                        Bitmap bitmap = iconGenerator.makeIcon("Click here to navigate!");
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                        LatLng experienceLocation = new LatLng(experiences.get(i).getParseGeoPoint(Experience.KEY_LOCATION).getLatitude(), experiences.get(i).getParseGeoPoint("location").getLongitude());
                        marker = new MarkerOptions()
                                .position(experienceLocation)
                                .icon(icon);
                        googleMap.addMarker(marker);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(experienceLocation));
                    }
                } else {
                    // handle the error
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
        ParseQuery.clearAllCachedResults();
    }

    private void directionsToMarker() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("Open Google Maps?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        String latitude = String.valueOf(marker.getPosition().latitude);
                        String longitude = String.valueOf(marker.getPosition().longitude);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        try {
                            if (mapIntent.resolveActivity(MapsActivity.this.getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }
                        } catch (NullPointerException e){
                            Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                            Toast.makeText(MapsActivity.this, "Couldn't open map", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}