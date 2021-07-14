package com.codepath.gem.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void queryExperiences() {
        ParseQuery<Experience> query = ParseQuery.getQuery(Experience.class);
        query.include(Experience.KEY_HOST);
        query.setLimit(20);
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
//                allExperiences.addAll(experiencesList);
//                adapter.notifyDataSetChanged();
            }
        });
    }

}