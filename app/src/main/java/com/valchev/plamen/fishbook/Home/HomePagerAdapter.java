package com.valchev.plamen.fishbook.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomePagerAdapter extends FragmentPagerAdapter {

    public HomePagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int arg0) {

        return new HomeFragment();
    }

    @Override
    public int getCount() {

        return HomeFragment.getPagesCount();
    }
}
