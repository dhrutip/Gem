package com.codepath.gem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.codepath.gem.fragments.HomeFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SearchActivity";
    public static final String KEY_RADIUS = "radius";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_SEARCH_TAG = "tag";
    public static final int RESULT_CODE = 42;
    String searchPlaceName;
    Double searchLatitude, searchLongitude;
    RadioGroup rgSearchRadius;
    RadioButton rb10miles, rb25miles, rb50miles, rb100miles, rbDefault;
    RadioGroup rgFilterTags;
    RadioButton rbTagsFood, rbTagsNature, rbTagsAttractions, rbTagsAccessible, rbTagsAll, rbTagsCustom;
    Integer rbDistance;
    String rbTag;
    Button btnSearch;
    EditText etCustomTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        rgSearchRadius = findViewById(R.id.rgSearchRadius);
        rb10miles = findViewById(R.id.rb10miles);
        rb25miles = findViewById(R.id.rb25miles);
        rb50miles = findViewById(R.id.rb50miles);
        rb100miles = findViewById(R.id.rb100miles);
        rbDefault = findViewById(R.id.rbDefault);

        rgFilterTags = findViewById(R.id.rgFilterTags);
        rbTagsFood = findViewById(R.id.rbTagsFood);
        rbTagsNature = findViewById(R.id.rbTagsNature);
        rbTagsAttractions = findViewById(R.id.rbTagsAttractions);
        rbTagsAccessible = findViewById(R.id.rbTagsAccessible);
        rbTagsAll = findViewById(R.id.rbTagsAll);
        rbTagsCustom = findViewById(R.id.rbTagsCustom);

        btnSearch = findViewById(R.id.btnSearch);
        etCustomTag = findViewById(R.id.etCustomTag);

        // Initialize the SDK
        Places.initialize(SearchActivity.this, getResources().getString(R.string.google_maps_key));
        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Set type filter for cities
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                searchPlaceName = place.getName();
                searchLatitude = place.getLatLng().latitude;
                searchLongitude = place.getLatLng().longitude;
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d(TAG, "An error occurred: " + status);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPlaceName == null) {
                    Toast.makeText(SearchActivity.this, "Please enter a city to search!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setRbDistance();
                setRbTag();
                if (rbTag == null || rbTag.equals("")) {
                    Toast.makeText(SearchActivity.this, "Please enter a custom filter!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intentToHome = new Intent();
                intentToHome.putExtra(KEY_RADIUS, rbDistance);
                intentToHome.putExtra(KEY_LATITUDE, searchLatitude);
                intentToHome.putExtra(KEY_LONGITUDE, searchLongitude);
                intentToHome.putExtra(KEY_SEARCH_TAG, rbTag);
                setResult(RESULT_CODE, intentToHome);
                finish();
            }
        });
    }

    private void setRbDistance() {
        int rbClicked = rgSearchRadius.getCheckedRadioButtonId();
        if (rbClicked == rb10miles.getId()) {
            rbDistance = 10;
        } else if (rbClicked == rb25miles.getId()) {
            rbDistance = 25;
        } else if (rbClicked == rb50miles.getId()) {
            rbDistance = 50;
        } else if (rbClicked == rb100miles.getId()) {
            rbDistance = 100;
        } else if (rbClicked == rbDefault.getId()) {
            rbDistance = 7900;
        }
    }

    private void setRbTag() {
        int rbClicked = rgFilterTags.getCheckedRadioButtonId();
        if (rbClicked == rbTagsFood.getId()) {
            rbTag = HomeFragment.KEY_FOOD;
        } else if (rbClicked == rbTagsNature.getId()) {
            rbTag = HomeFragment.KEY_NATURE;
        } else if (rbClicked == rbTagsAttractions.getId()) {
            rbTag = HomeFragment.KEY_ATTRACTIONS;
        } else if (rbClicked == rbTagsAccessible.getId()) {
            rbTag = HomeFragment.KEY_ACCESSIBLE;
        } else if (rbClicked == rbTagsAll.getId()) {
            rbTag = HomeFragment.KEY_ALL;
        } else if (rbClicked == rbTagsCustom.getId()) {
            rbTag = etCustomTag.getText().toString().toLowerCase();
        }
    }
}