package com.codepath.gem.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.gem.ExperienceDetailsActivity;
import com.codepath.gem.LoginActivity;
import com.codepath.gem.MainActivity;
import com.codepath.gem.R;
import com.codepath.gem.SetLocationActivity;
import com.codepath.gem.adapters.ExperiencesAdapter;
import com.codepath.gem.adapters.ProfileTabAdapter;
import com.codepath.gem.models.Commitment;
import com.codepath.gem.models.Experience;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
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
public class ProfileFragment extends Fragment {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final String TAG = "ProfileFragment";
    TextView tvUsername;
    TextView tvBio;
    ImageView ivProfilePic;
    ImageButton ibUpdateProfilePic;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    TabLayout tabLayout;
    ViewPager viewPager;

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
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBio = view.findViewById(R.id.tvBio);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        ibUpdateProfilePic = view.findViewById(R.id.ibUpdateProfilePic);
        tabLayout = view.findViewById(R.id.sliding_tabs);
        viewPager = view.findViewById(R.id.viewpager);

        ParseUser currentUser = ParseUser.getCurrentUser();
        tvUsername.setText(currentUser.getUsername());
        tvBio.setText(currentUser.getString("bio"));
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

        tabLayout.addTab(tabLayout.newTab().setText("My Commitments"));
        tabLayout.addTab(tabLayout.newTab().setText("My Listings"));
        // creating tab pages
        final ProfileTabAdapter profileTabAdapter = new ProfileTabAdapter(this.getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(profileTabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
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
}