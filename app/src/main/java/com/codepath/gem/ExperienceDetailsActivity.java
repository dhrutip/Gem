package com.codepath.gem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.gem.functions.OnDoubleTapListener;
import com.codepath.gem.functions.OnSwipeTouchListener;
import com.codepath.gem.models.Commitment;
import com.codepath.gem.models.Experience;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
    Button btnDetailsLocate;
    boolean commitmentExists;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_details);

        tvDetailsTitle = findViewById(R.id.tvDetailsTitle);
        tvDetailsDescription = findViewById(R.id.tvDetailsDescription);
        ivDetailsImageOne = findViewById(R.id.ivDetailsImageOne);
        ivDetailsImageTwo = findViewById(R.id.ivDetailsImageTwo);
        btnDetailsAddInterest = findViewById(R.id.btnDetailsAddInterest);
        btnDetailsLocate = findViewById(R.id.btnDetailsLocate);
        context = this;

        experience = (Experience) Parcels.unwrap(getIntent().getParcelableExtra(Experience.class.getSimpleName()));
        tvDetailsTitle.setText(experience.getTitle());
        tvDetailsDescription.setText(experience.getDescription());
        if (experience.getImageOne() != null) {
            Glide.with(this)
                    .load(experience.getImageOne().getUrl())
                    .into(ivDetailsImageOne);
        } else {
            ivDetailsImageOne.setImageResource(0);
        }
        if (experience.getImageTwo() != null) {
            Glide.with(this)
                    .load(experience.getImageTwo().getUrl())
                    .into(ivDetailsImageTwo);
        } else {
            ivDetailsImageOne.setImageResource(0);
        }
        // fill heart icon if already favorited, otherwise allow user to add a commitment via double tap
        checkCommitment();
        Handler handler = new Handler(); // required for checkCommitment() to finish first
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (commitmentExists == true) {
                    btnDetailsAddInterest.setImageResource(R.drawable.favorite_filled);
                } else {
                    btnDetailsAddInterest.setOnTouchListener(new OnDoubleTapListener(context) {
                        @Override
                        public void onDoubleTap(MotionEvent e) {
                            createCommitment();
                            btnDetailsAddInterest.setImageResource(R.drawable.favorite_filled);
                        }
                    });
                }
            }
        }, 400);

        btnDetailsLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ExperienceDetailsActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

    }

    private void checkCommitment() {
        // Toast.makeText(ExperienceDetailsActivity.this, "checking for commitment", Toast.LENGTH_SHORT).show();
        // check if commitment already exists
        ParseQuery<Commitment> query = ParseQuery.getQuery(Commitment.class);
        query.whereEqualTo(Commitment.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Commitment.KEY_EXPERIENCE, experience);
        query.getFirstInBackground(new GetCallback<Commitment>() {
            public void done(Commitment commitment, ParseException e) {
                if (e != null) {
                    final int statusCode = e.getCode();
                    if (statusCode == ParseException.OBJECT_NOT_FOUND) {
                        // commitment did not exist on the parse backend
                        commitmentExists = false;
                    }
                } else {
                    Log.i(TAG, "commitment already exists, no need for new commitment");
                    // Toast.makeText(ExperienceDetailsActivity.this, "commitment already exists!", Toast.LENGTH_SHORT).show();
                    commitmentExists = true;
                }
            }
        });
    }

    public void createCommitment() {
        // Toast.makeText(ExperienceDetailsActivity.this, "creating new commitment!", Toast.LENGTH_SHORT).show();
        ParseObject newCommitment = ParseObject.create("Commitment");
        newCommitment.put(Commitment.KEY_USER, ParseUser.getCurrentUser());
        newCommitment.put(Commitment.KEY_EXPERIENCE, experience);
        newCommitment.saveInBackground();
    }
}