package com.codepath.gem.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.gem.ExperienceDetailsActivity;
import com.codepath.gem.MainActivity;
import com.codepath.gem.R;
import com.codepath.gem.SearchActivity;
import com.codepath.gem.adapters.ExperiencesAdapter;
import com.codepath.gem.models.Experience;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment implements ExperiencesAdapter.OnExperienceListener {

    public static final String TAG = "HomeFragment";
    public static final String KEY_FOOD = "food";
    public static final String KEY_NATURE = "nature";
    public static final String KEY_ATTRACTIONS = "attractions";
    public static final String KEY_ACCESSIBLE = "accessible";
    public static final String KEY_ALL = "all";
    protected RecyclerView rvExperiences;
    protected List<Experience> allExperiences;
    protected ExperiencesAdapter experiencesAdapter;
    private SwipeRefreshLayout swipeContainer;
    private Integer homeRadius;
    private Double homeLatitude, homeLongitude;
    private String homeTag;
    ParseGeoPoint geoPoint;

    final Set<String> defaultTags = new HashSet<>();
    final Set<String> foodTags = new HashSet<>();
    final Set<String> natureTags = new HashSet<>();
    final Set<String> attractionsTags = new HashSet<>();
    final Set<String> accessibleTags = new HashSet<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        rvExperiences = view.findViewById(R.id.rvExperiences);
        allExperiences = new ArrayList<>();
        experiencesAdapter = new ExperiencesAdapter(getContext(), allExperiences, this);
        populateDefaultTags();
        populateFoodTags();
        populateNatureTags();
        populateAttractionsTags();
        populateAccessibleTags();


        if (homeLatitude == null && homeLongitude == null) {
            homeLatitude = ParseUser.getCurrentUser().getParseGeoPoint("location").getLatitude();
            homeLongitude = ParseUser.getCurrentUser().getParseGeoPoint("location").getLongitude();
            geoPoint = new ParseGeoPoint(homeLatitude, homeLongitude);
            Log.i(TAG, "both lat and long null, made geopoint at " + geoPoint.getLatitude() + " " + geoPoint.getLongitude());
        }

        if (homeRadius == null) {
            Log.i(TAG, "home radius is null");
            homeRadius = 7900;
        }

        rvExperiences.setAdapter(experiencesAdapter);
        rvExperiences.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                experiencesAdapter.clear();
                geoPoint = new ParseGeoPoint(homeLatitude, homeLongitude);
                Log.i(TAG, "swiped to refresh, made geopoint at " + geoPoint.getLatitude() + " " + geoPoint.getLongitude());
                queryExperiences();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queryExperiences();
    }

    public void queryExperiences() {
        ParseQuery<Experience> query = ParseQuery.getQuery(Experience.class);
        query.include(Experience.KEY_HOST);
        query.setLimit(20);
        query.whereWithinMiles(Experience.KEY_LOCATION, geoPoint, homeRadius);
        if (homeTag != null && !homeTag.equals(KEY_ALL)) {
            if (defaultTags.contains(homeTag)) {
                query.whereContains(Experience.KEY_DESCRIPTION, homeTag); // default tag
            } else {
                String formattedHomeTag = properlyFormat(homeTag);
                query.whereContains(Experience.KEY_DESCRIPTION, formattedHomeTag); // custom tag
            }
        }
        query.addDescendingOrder(Experience.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Experience>() {
            @Override
            public void done(List<Experience> experiencesList, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // for debugging purposes, print every post description to logcat
                for (Experience experience : experiencesList) {
                    Log.i(TAG, "Experience: " + experience.getDescription() + ", username: " + experience.getHost().getUsername());
                }
                Log.i(TAG, "after query, lat: " + geoPoint.getLatitude() + " long: " + geoPoint.getLongitude() + " rad: " + homeRadius);
                allExperiences.addAll(experiencesList);
                experiencesAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onExperienceClicked(int position) {
        Experience clickedExperience = allExperiences.get(position);
        Intent intent = new Intent(getContext(), ExperienceDetailsActivity.class);
        intent.putExtra(Experience.class.getSimpleName(), Parcels.wrap(clickedExperience));
        startActivity(intent);
    }

    public void setHomeLatitude(Double searchLatitude) {
        homeLatitude = searchLatitude;
    }

    public void setHomeLongitude(Double searchLongitude) {
        homeLongitude = searchLongitude;
    }

    public void setHomeRadius(Integer searchRadius) {
        homeRadius = searchRadius;
    }

    public void setHomeTag(String searchTag) {
        homeTag = searchTag;
    }

    private void populateDefaultTags() {
        defaultTags.add("food");
        defaultTags.add("nature");
        defaultTags.add("attractions");
        defaultTags.add("accessible");
    }

    private void populateFoodTags() {
        foodTags.add("food");
        foodTags.add("breakfast");
        foodTags.add("lunch");
        foodTags.add("dinner");
        foodTags.add("meal");
        foodTags.add("eat");
    }

    private void populateNatureTags() {
        natureTags.add("nature");
        natureTags.add("trees");
        natureTags.add("mountains");
        natureTags.add("beach");
        natureTags.add("animals");
    }

    private void populateAttractionsTags() {
        attractionsTags.add("attractions");
        attractionsTags.add("tourist");
    }

    private void populateAccessibleTags() {
        accessibleTags.add("accessible");
        accessibleTags.add("wheelchair");
    }

    private String properlyFormat(String hTag) {
        return hTag.toLowerCase();
    }

    private boolean isTagged(String fullDescription, String tag) {
        fullDescription = fullDescription.toLowerCase();
        tag = tag.toLowerCase();
        return fullDescription.contains(tag);
    }
}