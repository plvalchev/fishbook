package com.valchev.plamen.fishbook.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int arg0) {

        return new EditProfileFragment();
    }

    @Override
    public int getCount() {

        return 4;
    }
}
