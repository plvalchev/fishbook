package com.valchev.plamen.fishbook.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.shamanland.fonticon.FontIconView;
import com.valchev.plamen.fishbook.R;

public class MainActivity extends AppCompatActivity {

    protected NavigationManager mNavigationManager;
    protected FontIconView mNewsFeedButton;
    protected FontIconView mFriendsButton;
    protected FontIconView mNotificationsButton;
    protected FontIconView mEditProfileButton;
    protected SearchView mSearchView;
    protected TextView mSearchViewHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mNewsFeedButton = (FontIconView) findViewById(R.id.btn_news_feed);
        mFriendsButton = (FontIconView) findViewById(R.id.btn_friends);
        mNotificationsButton = (FontIconView) findViewById(R.id.btn_notifications);
        mEditProfileButton = (FontIconView) findViewById(R.id.btn_edit_profile);
        mSearchView = (SearchView) findViewById(R.id.search_view);
        mSearchViewHint = (TextView) findViewById(R.id.search_view_hint);

        initSearchView();
        initNavigationManager();

        mNavigationManager.setCurrentSelectedButton( mEditProfileButton );

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        mSearchViewHint.setVisibility( mSearchView.isIconified() ? View.VISIBLE : View.GONE );
    }

    protected void initSearchView() {

        mSearchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if( mSearchView.isIconified() )
                    mSearchView.setIconified(false);

                mSearchViewHint.setVisibility(View.GONE);
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mSearchView.performClick();
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {

                mSearchViewHint.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    protected void initNavigationManager() {

        mNavigationManager = new NavigationManager( null );

        mNavigationManager.addNavigationButton( mNewsFeedButton, 0, R.string.news_feed_icon, R.string.news_feed_pressed_icon );
        mNavigationManager.addNavigationButton( mFriendsButton, 0, R.string.friends_icon, R.string.friends_pressed_icon );
        mNavigationManager.addNavigationButton( mNotificationsButton, 0, R.string.notifications_icon, R.string.notifications_pressed_icon );
        mNavigationManager.addNavigationButton( mEditProfileButton, 0, R.string.edit_profile_icon, R.string.edit_profile_pressed_icon );
    }
}
