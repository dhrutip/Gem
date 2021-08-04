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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codepath.gem.ExperienceDetailsActivity;
import com.codepath.gem.MainActivity;
import com.codepath.gem.R;
import com.codepath.gem.SearchActivity;
import com.codepath.gem.adapters.ExperiencesAdapter;
import com.codepath.gem.models.Experience;
import com.codepath.gem.utilities.KotlinSearchTagSets;
import com.codepath.gem.utilities.SearchTagSets;
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
    private ParseGeoPoint geoPoint;
    private RelativeLayout rlLoading;

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
        rlLoading = view.findViewById(R.id.rlLoading);
        rlLoading.setVisibility(View.VISIBLE);
        allExperiences = new ArrayList<>();
        experiencesAdapter = new ExperiencesAdapter(getContext(), allExperiences, this);
        KotlinSearchTagSets.populateDefaultTags();
        KotlinSearchTagSets.populateFoodTags();
        KotlinSearchTagSets.populateNatureTags();
        KotlinSearchTagSets.populateAttractionsTags();
        KotlinSearchTagSets.populateAccessibleTags();

        if (homeLatitude == null && homeLongitude == null) {
            homeLatitude = ParseUser.getCurrentUser().getParseGeoPoint("location").getLatitude();
            homeLongitude = ParseUser.getCurrentUser().getParseGeoPoint("location").getLongitude();
            setGeopoint(homeLatitude, homeLongitude);
            Log.i(TAG, "both lat and long null, made geopoint at " + geoPoint.getLatitude() + " " + geoPoint.getLongitude());
        }

        if (homeRadius == null) {
            Log.i(TAG, "home radius is null");
            homeRadius = 8000;
        }

        rvExperiences.setAdapter(experiencesAdapter);
        rvExperiences.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                experiencesAdapter.clear();
                setGeopoint(homeLatitude, homeLongitude);
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
        if (homeTag != null && !homeTag.equals(KEY_ALL) && !KotlinSearchTagSets.defaultTags.contains(homeTag)) {
            query.whereContains(Experience.KEY_DESCRIPTION, homeTag); // custom tag
        }
        query.addDescendingOrder(Experience.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Experience>() {
            @Override
            public void done(List<Experience> experiencesList, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // filter by related keyword sets if the tag is a default tag
                if (homeTag != null && !homeTag.equals(KEY_ALL) && KotlinSearchTagSets.defaultTags.contains(homeTag)) {
                    ArrayList<Experience> removeExperiences = new ArrayList<>();
                    for (int i = 0; i < experiencesList.size(); i++) {
                        Experience experience = experiencesList.get(i);
                        Log.i(TAG, "Experience: " + experience.getDescription());
                        // if not tagged, add to list of experiences to be removed
                        if (!isTagged(experience.getDescription(), homeTag)) {
                            Log.i(TAG, "Experience TO BE REMOVED: " + experience.getDescription());
                            removeExperiences.add(experience);
                        }
                    }
                    // remove all from list of not tagged experiences
                    for (Experience exp : removeExperiences) {
                        experiencesList.remove(exp);
                    }
                }
                rlLoading.setVisibility(View.INVISIBLE);
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

    public boolean isTagged(String fullDescription, String tag) {
        fullDescription = fullDescription.toLowerCase();
        tag = tag.toLowerCase();
        // classifying based on keyword sets
        Set<String> selectedTags = new HashSet<>();
        if (tag.equals(KEY_FOOD)) {
            selectedTags = KotlinSearchTagSets.foodTags;
        } else if (tag.equals(KEY_NATURE)) {
            selectedTags = KotlinSearchTagSets.natureTags;
        } else if (tag.equals(KEY_ATTRACTIONS)) {
            selectedTags = KotlinSearchTagSets.attractionsTags;
        } else if (tag.equals(KEY_ACCESSIBLE)) {
            selectedTags = KotlinSearchTagSets.accessibleTags;
        }
        if (selectedTags.size() > 0) {
            for (String subTag : selectedTags) {
                if (fullDescription.contains(subTag)) {
                    return true;
                }
            }
        }
        return fullDescription.contains(tag);
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

    public void setGeopoint(double homeLat, double homeLong) {
        geoPoint = new ParseGeoPoint(homeLat, homeLong);
    }

    public void searchRefresh() {
        experiencesAdapter.clear();
        setGeopoint(homeLatitude, homeLongitude);
        queryExperiences();
    }

}