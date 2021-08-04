package com.codepath.gem.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codepath.gem.ExperienceDetailsActivity;
import com.codepath.gem.R;
import com.codepath.gem.adapters.ExperiencesAdapter;
import com.codepath.gem.models.Commitment;
import com.codepath.gem.models.Experience;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CommitmentsFragment extends Fragment implements ExperiencesAdapter.OnExperienceListener {

    public static final String TAG = "CommitmentsFragment";
    protected RecyclerView rvExperiences;
    protected List<Experience> allExperiences;
    protected ExperiencesAdapter experiencesAdapter;
    private SwipeRefreshLayout swipeContainer;
    RelativeLayout rlLoading;

    public CommitmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_commitments, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvExperiences = view.findViewById(R.id.rvExperiences);
        allExperiences = new ArrayList<>();
        experiencesAdapter = new ExperiencesAdapter(getContext(), allExperiences, this);
        rlLoading = view.findViewById(R.id.rlLoading);
        rlLoading.setVisibility(View.VISIBLE);

        rvExperiences.setAdapter(experiencesAdapter);
        rvExperiences.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                experiencesAdapter.clear();
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

    protected void queryExperiences() {
        ParseQuery<Commitment> query = ParseQuery.getQuery(Commitment.class);
        query.include(Commitment.KEY_USER);
        query.whereEqualTo(Commitment.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Commitment.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Commitment>() {
            @Override
            public void done(List<Commitment> commitmentsList, ParseException e) {
                rlLoading.setVisibility(View.INVISIBLE);
                ArrayList<Experience> experienceList = new ArrayList<>();
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                Log.i(TAG, "Commitments: size of " + commitmentsList.size());
                for (Commitment commitment : commitmentsList) {
                    Log.i(TAG, "Commitments: " + ", id: " + commitment.getObjectId());
                    Experience commitmentExp = (Experience) commitment.getExperience();
                    Log.i(TAG, "Commitments: " + ", exp id: " + commitmentExp.getObjectId());
                    experienceList.add(commitmentExp);
                }
                allExperiences.addAll(experienceList);
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
}