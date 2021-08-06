package com.codepath.gem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = "EditProfileActivity";
    ImageView ivEditProfilePic;
    ImageButton ibCameraEditProfilePic;
    TextView tvEditUsername;
    EditText etEditBio;
    Button btnSaveEditProfile;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private File photoFile;
    public String photoFileName = "photo.jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ivEditProfilePic = findViewById(R.id.ivEditProfilePic);
        ibCameraEditProfilePic = findViewById(R.id.ibCameraEditProfilePic);
        tvEditUsername = findViewById(R.id.tvEditUsername);
        etEditBio = findViewById(R.id.etEditBio);
        btnSaveEditProfile = findViewById(R.id.btnSaveEditProfile);
        getSupportActionBar().setTitle("Edit Profile");

        ParseUser currentUser = ParseUser.getCurrentUser();
        tvEditUsername.setText(currentUser.getUsername());
        etEditBio.setText(currentUser.getString("bio"));
        ParseFile profilePic = currentUser.getParseFile("profilePic");
        if (profilePic != null) {
            Glide.with(this)
                    .load(profilePic.getUrl())
                    .circleCrop()
                    .into(ivEditProfilePic);
        }

        ibCameraEditProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnSaveEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBio();
                Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName); // reference for future access

        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                updateProfilePic();
            } else { // failure
                Toast.makeText(this, "picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // gets the uniform resource identifier
    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        return file;
    }

    private void updateBio() {
        String updatedBio = etEditBio.getText().toString();
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("bio", updatedBio);
        currentUser.saveInBackground();
    }

    private void updateProfilePic() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseFile newProfilePic = new ParseFile(photoFile);
        currentUser.put("profilePic", newProfilePic);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Glide.with(EditProfileActivity.this)
                        .load(newProfilePic.getUrl())
                        .circleCrop()
                        .into(ivEditProfilePic);
            }
        });
    }
}