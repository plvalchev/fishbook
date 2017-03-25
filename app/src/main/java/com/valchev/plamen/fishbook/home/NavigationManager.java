package com.valchev.plamen.fishbook.home;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shamanland.fonticon.FontIconView;
import com.valchev.plamen.fishbook.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 12.3.2017 г..
 */

public class NavigationManager implements OnClickListener, ViewPager.OnPageChangeListener {

    protected class NavigationButton {

        public FontIconView fontIconView;
        public int position;
        public int iconResource;
        public int selectedIconResource;

        NavigationButton(FontIconView fontIconView, int position, int iconResource, int selectedIconResource) {

            this.fontIconView = fontIconView;
            this.position = position;
            this.iconResource = iconResource;
            this.selectedIconResource = selectedIconResource;
        }
    }

    private ArrayList<NavigationButton> mNavigationButtonsArray;
    private NavigationButton mCurrentSelectedButton;
    private ViewPager mViewPager;

    public NavigationManager(ViewPager viewPager)
    {
        mViewPager = viewPager;

        if( mViewPager != null ) {

            mViewPager.addOnPageChangeListener(this);
        }
    }

    public void addNavigationButton(FontIconView fontIconView, int position, int iconResource, int selectedIconResource) {

        fontIconView.setOnClickListener(this);

        if( mNavigationButtonsArray == null ) {

            mNavigationButtonsArray = new ArrayList<NavigationButton>();
        }

        mNavigationButtonsArray.add(new NavigationButton( fontIconView, position, iconResource, selectedIconResource ));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        setCurrentSelectedButton( position );
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {

        if( view instanceof FontIconView ) {

            setCurrentSelectedButton((FontIconView) view);
        }
    }

    public void setCurrentSelectedButton(int position) {

        mCurrentSelectedButton = null;

        for (NavigationButton navigationButton : mNavigationButtonsArray) {

            if( position == navigationButton.position ) {

                navigationButton.fontIconView.setText(navigationButton.selectedIconResource);

                mCurrentSelectedButton = navigationButton;

            } else {

                navigationButton.fontIconView.setText(navigationButton.iconResource);
            }
        }
    }

    public void setCurrentSelectedButton( FontIconView view ) {

        mCurrentSelectedButton = null;

        for (NavigationButton navigationButton : mNavigationButtonsArray) {

            if (view == navigationButton.fontIconView) {

                navigationButton.fontIconView.setText(navigationButton.selectedIconResource);

                mCurrentSelectedButton = navigationButton;

                if (mViewPager != null) {

                    mViewPager.setCurrentItem(navigationButton.position, true);
                }

            } else {

                navigationButton.fontIconView.setText(navigationButton.iconResource);
            }
        }
    }
}
