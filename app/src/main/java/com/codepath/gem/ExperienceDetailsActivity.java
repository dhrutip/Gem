package com.codepath.gem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.gem.models.Commitment;
import com.codepath.gem.models.Experience;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class ExperienceDetailsActivity extends AppCompatActivity {

    public static final String TAG = "ExperienceDetailsActivity";
    Experience experience;
    TextView tvDetailsTitle;
    TextView tvDetailsDescription;
    ImageView ivDetailsImageOne;
    ImageView ivDetailsImageTwo;
    ImageButton btnDetailsAddInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_details);

        tvDetailsTitle = findViewById(R.id.tvDetailsTitle);
        tvDetailsDescription = findViewById(R.id.tvDetailsDescription);
        ivDetailsImageOne = findViewById(R.id.ivDetailsImageOne);
        ivDetailsImageTwo = findViewById(R.id.ivDetailsImageTwo);
        btnDetailsAddInterest = findViewById(R.id.btnDetailsAddInterest);

        experience = (Experience) Parcels.unwrap(getIntent().getParcelableExtra(Experience.class.getSimpleName()));
        tvDetailsTitle.setText(experience.getTitle());
        tvDetailsDescription.setText(experience.getDescription());
        Glide.with(this)
                .load(experience.getImageOne().getUrl())
                .into(ivDetailsImageOne);

        if (experience.getImageTwo() != null) {
            Glide.with(this)
                    .load(experience.getImageTwo().getUrl())
                    .into(ivDetailsImageTwo);
        }

        btnDetailsAddInterest.setOnTouchListener(new OnDoubleTapListener(this) {
            @Override
            public void onDoubleTap(MotionEvent e) {
                createCommitment();
                btnDetailsAddInterest.setImageResource(R.drawable.favorite_filled);
            }
        });
    }

    private void createCommitment() {
        Log.i(TAG, "attempting to create commitment");
        ParseObject newCommitment = ParseObject.create("Commitment");
        newCommitment.put(Commitment.KEY_USER, ParseUser.getCurrentUser());
        newCommitment.put(Commitment.KEY_EXPERIENCE, experience);
        newCommitment.saveInBackground();
    }
}