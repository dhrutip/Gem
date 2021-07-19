package com.codepath.gem.adapters;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codepath.gem.MainActivity;
import com.codepath.gem.fragments.CommitmentsFragment;

import org.jetbrains.annotations.NotNull;

public class ProfileTabAdapter extends SmartFragmentStatePagerAdapter {
    int tabCount;

    public ProfileTabAdapter(FragmentManager fragmentManager, int numberOfTabs) {
        super(fragmentManager);
        this.tabCount = numberOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CommitmentsFragment();
            case 1:
                return new CommitmentsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
