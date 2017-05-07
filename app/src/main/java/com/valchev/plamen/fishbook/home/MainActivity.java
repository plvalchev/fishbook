package com.valchev.plamen.fishbook.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.valchev.plamen.fishbook.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_POSITION = "CURRENT_POSITION";
    private static final String KEY_IMAGES = "IMAGES";

    protected SearchView mSearchView;
    protected TextView mSearchViewHint;
    protected MainPagerAdapter mMainPagerAdapter;
    protected ViewPager mViewPager;
    protected TabLayout mTabLayout;
    protected ArrayList<String> mImages;
    protected int mCurrentPosition = -1;
    protected FloatingActionButton mAddNewPostFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mSearchView = (SearchView) findViewById(R.id.search_view);
        mSearchViewHint = (TextView) findViewById(R.id.search_view_hint);
        mViewPager = (ViewPager) findViewById(R.id.navigation_view_pager);
        mAddNewPostFAB = (FloatingActionButton) findViewById(R.id.add_new_post);

        mAddNewPostFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, PostActivity.class);

                startActivity(intent);
            }
        });

        initViewPager();
        initSearchView();
        initTabLayout();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        mSearchViewHint.setVisibility(mSearchView.isIconified() ? View.VISIBLE : View.GONE);

        if (savedInstanceState != null) {

            mCurrentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION);
            mImages = savedInstanceState.getStringArrayList(KEY_IMAGES);

            if( mCurrentPosition >= 0 && mImages != null ) {

                showImages(mCurrentPosition, mImages);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(KEY_CURRENT_POSITION, mCurrentPosition);
        outState.putStringArrayList(KEY_IMAGES, mImages);

        super.onSaveInstanceState(outState);
    }

    protected void initViewPager() {

        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }

    protected void initSearchView() {

        mSearchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mSearchView.isIconified())
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

    protected void initTabLayout() {

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ArrayList<Drawable> tabLayoutDrawables = new ArrayList<Drawable>();

        int tabCount = mTabLayout.getTabCount();

        for( int index = 0; index < tabCount; index++ ) {

            tabLayoutDrawables.add(mTabLayout.getTabAt(index).getIcon());
        }

        mTabLayout.setupWithViewPager( mViewPager );

        for( int index = 0; index < tabCount; index++ ) {

            Drawable drawable = tabLayoutDrawables.get(index);

            mTabLayout.getTabAt(index).setIcon(drawable);
        }
    }

    public void showImages(int startPosition, ArrayList<String> images) {

        mImages = images;
        mCurrentPosition = startPosition;

        new ImageViewer.Builder(this, mImages)
                .setStartPosition(mCurrentPosition)
                .setImageMarginPx(20)
                .setImageChangeListener(new ImageViewer.OnImageChangeListener() {
                    @Override
                    public void onImageChange(int position) {
                        mCurrentPosition = position;
                    }
                })
                .setOnDismissListener(new ImageViewer.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mCurrentPosition = -1;
                    }
                })
                .show();
    }
}
