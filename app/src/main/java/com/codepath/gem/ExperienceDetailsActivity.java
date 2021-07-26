package com.codepath.gem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
    public static final String KEY_USER_USERNAME = "username";
    Experience experience;
    TextView tvDetailsTitle, tvDetailsDescription, tvStartDateDetails, tvEndDateDetails;
    ImageView ivDetailsImageOne, ivDetailsImageTwo;
    ImageButton ibDetailsAddInterest, ibDeleteExperience, ibRemoveCommitment;
    Button btnDetailsLocate;
    boolean commitmentExists;
    boolean deleteCommitment = false;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_details);

        tvDetailsTitle = findViewById(R.id.tvDetailsTitle);
        tvDetailsDescription = findViewById(R.id.tvDetailsDescription);
        tvStartDateDetails = findViewById(R.id.tvStartDateDetails);
        tvEndDateDetails = findViewById(R.id.tvEndDateDetails);
        ivDetailsImageOne = findViewById(R.id.ivDetailsImageOne);
        ivDetailsImageTwo = findViewById(R.id.ivDetailsImageTwo);
        ibDetailsAddInterest = findViewById(R.id.ibDetailsAddInterest);
        ibDeleteExperience = findViewById(R.id.ibDeleteExperience);
        ibRemoveCommitment = findViewById(R.id.ibRemoveCommitment);
        btnDetailsLocate = findViewById(R.id.btnDetailsLocate);
        context = this;

        experience = (Experience) Parcels.unwrap(getIntent().getParcelableExtra(Experience.class.getSimpleName()));
        tvDetailsTitle.setText(experience.getTitle());
        tvDetailsDescription.setText(experience.getDescription());
        if (experience.getStartDate() != null && experience.getEndDate() != null) {
            tvStartDateDetails.setText(experience.getStartDate().toString());
            tvEndDateDetails.setText(experience.getEndDate().toString());
        }
        if (experience.getImageOne() != null) {
            Glide.with(this)
                    .load(experience.getImageOne().getUrl())
                    .into(ivDetailsImageOne);
        } else {
            ivDetailsImageOne.setVisibility(View.INVISIBLE);
        }
        if (experience.getImageTwo() != null) {
            Glide.with(this)
                    .load(experience.getImageTwo().getUrl())
                    .into(ivDetailsImageTwo);
        } else {
            ivDetailsImageTwo.setVisibility(View.INVISIBLE);
        }

        String currUsername = "";
        try {
            currUsername = ParseUser.getCurrentUser().fetchIfNeeded().getString(KEY_USER_USERNAME);
        } catch (ParseException e) {
            Log.v(TAG, e.toString());
            e.printStackTrace();
        }

        String hostUsername = "";
        try {
            hostUsername = experience.getHost().fetchIfNeeded().getString(KEY_USER_USERNAME);
        } catch (ParseException e) {
            Log.v(TAG, e.toString());
            e.printStackTrace();
        }
        // allow host to delete their own experiences
        if (currUsername.equals(hostUsername)) {
            ibDeleteExperience.setVisibility(View.VISIBLE);
            ibDeleteExperience.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ExperienceDetailsActivity.this);
                    builder.setMessage("Are you sure you want to delete this experience?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    experience.deleteInBackground();
                                    returnToMain();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        } else {
            ibDeleteExperience.setVisibility(View.INVISIBLE);
        }

        // fill heart icon if already committed, otherwise allow user to add a commitment via double tap
        checkCommitment();
        Handler handler = new Handler(); // required for checkCommitment() to finish first
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (commitmentExists == true) {
                    ibDetailsAddInterest.setImageResource(R.drawable.favorite_filled);
                    ibRemoveCommitment.setVisibility(View.VISIBLE);
                    ibRemoveCommitment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ExperienceDetailsActivity.this);
                            builder.setMessage("Are you sure you want to delete this commitment?")
                                    .setCancelable(true)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            deleteCommitment = true;
                                            checkCommitment();
                                            returnToMain();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            dialog.cancel();
                                        }
                                    });
                            final AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                } else {
                    ibRemoveCommitment.setVisibility(View.INVISIBLE);
                    ibDetailsAddInterest.setOnTouchListener(new OnDoubleTapListener(context) {
                        @Override
                        public void onDoubleTap(MotionEvent e) {
                            createCommitment();
                            ibDetailsAddInterest.setImageResource(R.drawable.favorite_filled);
                            ibRemoveCommitment.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }, 400);

        btnDetailsLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ExperienceDetailsActivity.this, MapsActivity.class);
                i.putExtra(Experience.class.getSimpleName(), Parcels.wrap(experience));
                startActivity(i);
            }
        });
    }

    private void returnToMain() {
        Intent i = new Intent(ExperienceDetailsActivity.this, MainActivity.class);
        startActivity(i);
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
                    if (deleteCommitment) {
                        commitment.deleteInBackground();
                    }
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