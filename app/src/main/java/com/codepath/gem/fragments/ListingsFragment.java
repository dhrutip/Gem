package com.codepath.gem.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.gem.R;
import com.codepath.gem.models.Experience;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ListingsFragment extends HomeFragment {

    public ListingsFragment() {
        // Required empty public constructor
    }

    @Override
    protected void queryExperiences() {
        ParseQuery<Experience> query = ParseQuery.getQuery(Experience.class);
        query.include(Experience.KEY_HOST);
        query.setLimit(20);
        query.whereEqualTo(Experience.KEY_HOST, ParseUser.getCurrentUser());
        query.addDescendingOrder(Experience.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Experience>() {
            @Override
            public void done(List<Experience> experiencesList, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting experiences", e);
                    return;
                }
                // for debugging purposes, print every post description to logcat
                for (Experience experience : experiencesList) {
                    Log.i(TAG, "Experience: " + experience.getDescription() + ", username: " + experience.getHost().getUsername());
                }
                allExperiences.addAll(experiencesList);
                experiencesAdapter.notifyDataSetChanged();
            }
        });
    }
}