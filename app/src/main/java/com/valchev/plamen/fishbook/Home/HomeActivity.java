package com.valchev.plamen.fishbook.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.authentication.AuthenticateUserDialogFragment;
import com.viewpagerindicator.CirclePageIndicator;

public class HomeActivity extends FragmentActivity {

    private abstract class AbstractUserAuthenticator implements
            AuthenticateUserDialogFragment.UserAuthenticator,
            View.OnClickListener,
            OnFailureListener,
            OnSuccessListener<AuthResult> {

        protected AuthenticateUserDialogFragment mDialog;
        protected ProgressDialog mProgressDialog;
        protected HomeActivity mActivity;
        protected String mDialogTitle;
        protected String mEmail;
        protected String mPassword;

        public AbstractUserAuthenticator(String dialogTitle, String email, String password, HomeActivity activity) {

            mDialogTitle = dialogTitle;
            mActivity = activity;
            mEmail = email;
            mPassword = password;
        }

        public AbstractUserAuthenticator(String dialogTitle, HomeActivity activity) {

            mDialogTitle = dialogTitle;
            mActivity = activity;
        }

        @Override
        public void onClick(View v) {

            if( mDialog == null ) {

                mDialog = new AuthenticateUserDialogFragment();
            }

            mDialog.show(mDialogTitle, null, mEmail, mPassword, mActivity.getSupportFragmentManager(), this);
        }

        @Override
        public void authenticateUser(String email, String password) {

            mEmail = email;
            mPassword = password;

            String message = getString(R.string.authenticating);

            mProgressDialog = ProgressDialog.show(HomeActivity.this, null, message);

            Task<AuthResult> task = executeAuthenticationTask(email, password);

            task.addOnFailureListener(mActivity, this);
            task.addOnSuccessListener(mActivity, this);
        }

        @Override
        public void onFailure(@NonNull Exception e) {

            if( mProgressDialog != null && mProgressDialog.isShowing() )
                mProgressDialog.dismiss();

            String message = mDialogTitle + " " + getString(R.string.failed) + ".";
            FirebaseException exception = (FirebaseException)e;

            message = message + " " + exception.getLocalizedMessage();

            mDialog.show(mDialogTitle, message, mEmail, mPassword, mActivity.getSupportFragmentManager(), this);

            mEmail = null;
            mPassword = null;
        }

        @Override
        public void onSuccess(AuthResult authResult) {

            if( mProgressDialog != null && mProgressDialog.isShowing() )
                mProgressDialog.dismiss();

            mEmail = null;
            mPassword = null;

            Intent intent = new Intent(mActivity.getBaseContext(), MainActivity.class);

            mActivity.startActivity(intent);
        }

        protected abstract Task<AuthResult> executeAuthenticationTask(String email, String password);
    }

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

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if( firebaseUser != null ) {

            Intent intent = new Intent(getBaseContext(), MainActivity.class);

            startActivity(intent);
        }

        setContentView(R.layout.activity_home);

        mViewPager = (ViewPager) findViewById(R.id.home_viewpager);
        mViewPagerIndicator	= (CirclePageIndicator) findViewById(R.id.home_viewpager_indicator);
        mSignInButton = (Button) findViewById(R.id.home_login_button);
        mSignUpButton = (Button) findViewById(R.id.home_sign_up_button);

        mSignUpButton.setOnClickListener(new AbstractUserAuthenticator(getString(R.string.sign_up), HomeActivity.this) {

            @Override
            protected Task<AuthResult> executeAuthenticationTask(String email, final String password) {

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                return firebaseAuth.createUserWithEmailAndPassword(email, password);
            }
        });

        mSignInButton.setOnClickListener(new AbstractUserAuthenticator(getString(R.string.sign_in), HomeActivity.this) {

            @Override
            protected Task<AuthResult> executeAuthenticationTask(String email, String password) {

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                return firebaseAuth.signInWithEmailAndPassword(email, password);
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
                    if( mHandler != null && !mStopSliding ) {

                        mHandler.removeCallbacks(mAnimateViewPager);
                    }
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
                    mViewPager.setCurrentItem(0, true);
                else
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);

                mHandler.postDelayed(mAnimateViewPager, VIEWPAGER_SLIDE_DELAY);
            }
            }
        };
    }
}
