package com.valchev.plamen.fishbook.Home;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.valchev.plamen.fishbook.R;
import com.viewpagerindicator.CirclePageIndicator;

public class HomeActivity extends FragmentActivity {

    protected final static long VIEWPAGER_SLIDE_DELAY		= 3000;
    protected final static long VIEWPAGER_DELAY_USER_VIEW 	= 6000;

    protected ViewPager mViewPager;
    protected HomePagerAdapter mHomePagerAdapter;
    protected CirclePageIndicator mViewPagerIndicator;
    protected boolean mStopSliding = false;
    protected Runnable mAnimateViewPager;
    protected Handler mHandler;
    protected Button mSignInButton;
    protected Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mViewPager = (ViewPager) findViewById(R.id.home_viewpager);
        mViewPagerIndicator	= (CirclePageIndicator) findViewById(R.id.home_viewpager_indicator);
        mSignInButton = (Button) findViewById(R.id.home_login_button);
        mSignUpButton = (Button) findViewById(R.id.home_sign_up_button);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AuthenticateUserDialogFragment authenticateUserDialog = new AuthenticateUserDialogFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();

                authenticateUserDialog.setTitle(R.string.sign_up);
                authenticateUserDialog.show(fragmentManager, "authenticate_dialog_fragment");
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AuthenticateUserDialogFragment authenticateUserDialog = new AuthenticateUserDialogFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();

                authenticateUserDialog.setTitle(R.string.sign_in);
                authenticateUserDialog.show(fragmentManager, "authenticate_dialog_fragment");
            }
        });

        initViewPager();
    }

    protected void initViewPager() {

        mHomePagerAdapter = new HomePagerAdapter( getSupportFragmentManager() );

        mViewPager.setAdapter(mHomePagerAdapter);
        mViewPagerIndicator.setViewPager(mViewPager);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch( event.getAction() ) {

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    case MotionEvent.ACTION_UP:
                        // calls when touch release on ViewPager
                        mStopSliding = false;

                        slideViewPager(mHomePagerAdapter.getCount());
                        mHandler.postDelayed(mAnimateViewPager, VIEWPAGER_DELAY_USER_VIEW);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // calls when ViewPager touch
                        if( mHandler != null && !mStopSliding )
                            mHandler.removeCallbacks(mAnimateViewPager);
                        break;

                }

                return false;
            }
        });

        slideViewPager(mHomePagerAdapter.getCount());
        mHandler.postDelayed(mAnimateViewPager, VIEWPAGER_DELAY_USER_VIEW);
    }

    public void slideViewPager( final int size ) {

        mHandler = new Handler();
        mAnimateViewPager = new Runnable() {

            @Override
            public void run() {

                if( !mStopSliding ) {

                    if( mViewPager.getCurrentItem() == size - 1 )
                        mViewPager.setCurrentItem(0);
                    else
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);

                    mHandler.postDelayed(mAnimateViewPager, VIEWPAGER_SLIDE_DELAY);
                }
            }
        };
    }
}
