package com.valchev.plamen.fishbook.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.valchev.plamen.fishbook.chat.ChatFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int arg0) {

        Fragment fragment = null;

        switch (arg0) {

            case 0:
                fragment = new FeedFragment();
                break;

            case 1:
                fragment = new ChatFragment();
                break;

            case 2:
                fragment = new EventFragment();
                break;

            default:
                fragment = new EditProfileFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {

        return 4;
    }
}
