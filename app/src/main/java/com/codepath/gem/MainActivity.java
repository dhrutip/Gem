package com.codepath.gem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.gem.fragments.CreateFragment;
import com.codepath.gem.fragments.HomeFragment;
import com.codepath.gem.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
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
                        FRAGMENT_TAG = "HomeFragment";
                        break;
                    case R.id.action_create:
                        fragment = new CreateFragment();
                        FRAGMENT_TAG = "CreateFragment";
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        FRAGMENT_TAG = "ProfileFragment";
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
            startActivity(goToSearch);
            HomeFragment homeFragment = (HomeFragment)
                    getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
            // sending dummy values to home fragment, just to check that data sending works correctly
            // TODO: update to real values obtained from search activity
            homeFragment.setHomeRadius(46);
            homeFragment.setHomeLatitude(41.871941);
            homeFragment.setHomeLongitude(12.56738);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onLogoutButton() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    goLoginActivity();
                } else {
                    Toast.makeText(MainActivity.this, "logout issue:(", Toast.LENGTH_SHORT).show();
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