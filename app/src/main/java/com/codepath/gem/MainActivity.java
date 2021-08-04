package com.codepath.gem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.gem.fragments.ConversationsFragment;
import com.codepath.gem.fragments.CreateFragment;
import com.codepath.gem.fragments.HomeFragment;
import com.codepath.gem.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private final int REQUEST_CODE = 42;
    private BottomNavigationView bottomNavigationView;
    private String FRAGMENT_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        FRAGMENT_TAG = HomeFragment.TAG;
                        break;
                    case R.id.action_create:
                        fragment = new CreateFragment();
                        FRAGMENT_TAG = CreateFragment.TAG;
                        break;
                    case R.id.action_chat:
                        fragment = new ConversationsFragment();
                        FRAGMENT_TAG = ConversationsFragment.TAG;
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        FRAGMENT_TAG = ProfileFragment.TAG;
                        break;
                    default:
                        return true;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment, FRAGMENT_TAG).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home); // default selection
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btnLogout) {
            onLogoutButton();
            return true;
        }
        if (item.getItemId() == R.id.itemSearch) {
            Intent goToSearch = new Intent(this, SearchActivity.class);
            startActivityForResult(goToSearch, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == REQUEST_CODE) {
            HomeFragment homeFragment = (HomeFragment)
                    getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
            if (data.hasExtra(SearchActivity.KEY_RADIUS)) {
                homeFragment.setHomeRadius(data.getIntExtra(SearchActivity.KEY_RADIUS, 8000));
            }
            if (data.hasExtra(SearchActivity.KEY_LATITUDE)) {
                homeFragment.setHomeLatitude(data.getDoubleExtra(SearchActivity.KEY_LATITUDE, 0.0));
            }
            if (data.hasExtra(SearchActivity.KEY_LONGITUDE)) {
                homeFragment.setHomeLongitude(data.getDoubleExtra(SearchActivity.KEY_LONGITUDE, 0.0));
            }
            if (data.hasExtra(SearchActivity.KEY_SEARCH_TAG)) {
                homeFragment.setHomeTag(data.getStringExtra(SearchActivity.KEY_SEARCH_TAG));
            }
            homeFragment.searchRefresh();
        }
    }

    private void onLogoutButton() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    goSplashScreenActivity();
                } else {
                    Toast.makeText(MainActivity.this, "logout issue:(", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void goSplashScreenActivity() {
        Intent i = new Intent(this, SplashScreenActivity.class);
        startActivity(i);
        finish();
    }
}