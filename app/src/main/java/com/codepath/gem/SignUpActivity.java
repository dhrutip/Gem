package com.codepath.gem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.royrodriguez.transitionbutton.TransitionButton;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";
    private static final int REQUEST_LOCATION = 1;
    private EditText etNewUsername;
    private EditText etPasswordOne;
    private EditText etPasswordTwo;
    private Button btnCreateAccount;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etNewUsername = findViewById(R.id.etNewUsername);
        etPasswordOne = findViewById(R.id.etPasswordOne);
        etPasswordTwo = findViewById(R.id.etPasswordTwo);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "sign up button clicked");
                String username = etNewUsername.getText().toString();
                if (etPasswordOne.getText().toString().equals(etPasswordTwo.getText().toString())){
                    String password = etPasswordOne.getText().toString();
                    createUser(username, password);
                } else {
                    Toast.makeText(SignUpActivity.this, "try entering your passwords again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUser(String username, String password) {
        Log.i(TAG, "attempting to set up: " + username);
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    goLoginActivity(); // successful signup
                } else {
                    Log.e(TAG, "issue with creating user" + e); // unsuccessful signup
                    return;
                }
            }
        });
    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}