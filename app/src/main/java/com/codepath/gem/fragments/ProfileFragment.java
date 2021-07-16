package com.codepath.gem.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.gem.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    TextView tvUsername;
    ImageView ivProfilePic;

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

        ParseUser currentUser = ParseUser.getCurrentUser();
        tvUsername.setText(currentUser.getUsername());
        ParseFile profilePic = currentUser.getParseFile("profilePic");
        if (profilePic != null) {
            Glide.with(view.getContext())
                    .load(profilePic.getUrl())
                    .circleCrop()
                    .into(ivProfilePic);
        }
    }
}