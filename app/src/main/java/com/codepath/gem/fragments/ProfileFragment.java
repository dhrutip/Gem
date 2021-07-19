package com.codepath.gem.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.gem.ExperienceDetailsActivity;
import com.codepath.gem.R;
import com.codepath.gem.SetLocationActivity;
import com.codepath.gem.adapters.ExperiencesAdapter;
import com.codepath.gem.models.Commitment;
import com.codepath.gem.models.Experience;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements ExperiencesAdapter.OnExperienceListener {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final String TAG = "ProfileFragment";
    TextView tvUsername;
    ImageView ivProfilePic;
    ImageButton ibUpdateProfilePic;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    protected RecyclerView rvExperiences;
    protected List<Experience> allExperiences;
    protected ExperiencesAdapter experiencesAdapter;
    private SwipeRefreshLayout swipeContainer;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        ibUpdateProfilePic = view.findViewById(R.id.ibUpdateProfilePic);

        ParseUser currentUser = ParseUser.getCurrentUser();
        tvUsername.setText(currentUser.getUsername());
        ParseFile profilePic = currentUser.getParseFile("profilePic");
        if (profilePic != null) {
            Glide.with(view.getContext())
                    .load(profilePic.getUrl())
                    .circleCrop()
                    .into(ivProfilePic);
        }
        ibUpdateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "update profile pic", Toast.LENGTH_LONG).show();
                launchCamera();
            }
        });

        rvExperiences = view.findViewById(R.id.rvExperiences);
        allExperiences = new ArrayList<>();
        experiencesAdapter = new ExperiencesAdapter(getContext(), allExperiences, this);

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

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName); // reference for future access

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                saveUser();
            } else { // failure
                Toast.makeText(getContext(), "picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseFile newProfilePic = new ParseFile(photoFile);
        currentUser.put("profilePic", newProfilePic);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Glide.with(getContext())
                        .load(newProfilePic.getUrl())
                        .circleCrop()
                        .into(ivProfilePic);
            }
        });
    }

    // gets the uniform resource identifier
    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        return file;
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