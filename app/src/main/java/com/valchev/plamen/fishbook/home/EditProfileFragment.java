package com.valchev.plamen.fishbook.home;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.MultiDraweeHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.firebase.database.DatabaseError;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.CoverPhoto;
import com.valchev.plamen.fishbook.models.ProfilePicture;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditProfileFragment extends Fragment {

    private final static int REQUEST_CODE_COVER_PHOTO_PICKER = 2000;
    private final static int REQUEST_CODE_PROFILE_PICTURE_PICKER = 2001;

    protected FishbookUser mFishbookUser;
    protected User mUserData;
    protected TextView mDisplayName;
    protected ExpandableTextView mTopFishingRegions;
    protected ExpandableTextView mMostChasedSpecies;
    protected ExpandableTextView mFishingMethods;
    protected SimpleDraweeView mProfilePicture;
    protected SimpleDraweeView mCoverPhoto;
    protected FloatingActionButton mActionButton;
    protected ProgressBar mProgressBar;
    protected PopupMenu mPopupMenu;
    protected ArrayList<String> mProfilePictures;
    protected ArrayList<String> mCoverPhotos;
    protected int mProfilePicturesCurrentPosition;
    protected int mCoverPhotosCurrentPosition;

    public EditProfileFragment() {

        mProfilePicturesCurrentPosition = -1;
        mCoverPhotosCurrentPosition = -1;
        mUserData = new User();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_profile, container, false);

        View regions = view.findViewById(R.id.regions);
        View species = view.findViewById(R.id.species);
        View methods = view.findViewById(R.id.methods);
        TextView topFishingRegionsTitle = (TextView) regions.findViewById(R.id.title);
        TextView mostChasedSpeciesTitle = (TextView) species.findViewById(R.id.title);
        TextView fishingMethodsTitle = (TextView) methods.findViewById(R.id.title);

        mTopFishingRegions = (ExpandableTextView) regions.findViewById(R.id.expand_text_view);
        mMostChasedSpecies = (ExpandableTextView) species.findViewById(R.id.expand_text_view);
        mFishingMethods = (ExpandableTextView) methods.findViewById(R.id.expand_text_view);
        mProfilePicture = (SimpleDraweeView) view.findViewById(R.id.profile_image);
        mCoverPhoto = (SimpleDraweeView) view.findViewById(R.id.cover_photo);
        mActionButton = (FloatingActionButton) view.findViewById(R.id.edit_profile_button);
        mDisplayName = (TextView) view.findViewById(R.id.display_name);
        mProgressBar = (ProgressBar) view.findViewById(R.id.edit_profile_progress);

        topFishingRegionsTitle.setText(getResources().getString(R.string.regions));
        mostChasedSpeciesTitle.setText(getResources().getString(R.string.chased_species));
        fishingMethodsTitle.setText(getResources().getString(R.string.fishing_methods));

        mFishbookUser = FishbookUser.getCurrentUser();

        initUserProfile();

        mFishbookUser.addUserValueEventListener(new FishbookUser.UserValueEventListener() {

            @Override
            public void onDataChange(User user) {

                mUserData = user;

                initUserProfile();

                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                mProgressBar.setVisibility(View.GONE);
            }
        });

        initPopupMenu();

        return view;
    }

    void initUserProfile() {

        mDisplayName.setText(mUserData.getDisplayName());
        mTopFishingRegions.setText(mUserData.getTopFishingRegionsAsString());
        mMostChasedSpecies.setText(mUserData.getMostChasedSpeciesAsStringAsString());
        mFishingMethods.setText(mUserData.getFishingMethodsAsString());

        if( mUserData.profilePictures != null && mUserData.profilePictures.size() > 0 ) {

            if( mProfilePictures == null )
                mProfilePictures = new ArrayList<String>();

            mProfilePictures.clear();

            for( int index = 0; index < mUserData.profilePictures.size(); index++ ) {

                ProfilePicture profilePicture = mUserData.profilePictures.get(index);

                mProfilePictures.add(profilePicture.highResUri);
            }

            ProfilePicture actualProfilePicture = mUserData.profilePictures.get(0);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setLowResImageRequest(ImageRequest.fromUri(actualProfilePicture.lowResUri))
                    .setImageRequest(ImageRequest.fromUri(actualProfilePicture.highResUri))
                    .setOldController(mProfilePicture.getController())
                    .build();

            mProfilePicture.setController(controller);
            mProfilePicture.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    showImages(0, mProfilePictures);
                }
            });
        }

        if( mUserData.coverPhotos != null && mUserData.coverPhotos.size() > 0 ) {

            if( mCoverPhotos == null )
                mCoverPhotos = new ArrayList<String>();

            mCoverPhotos.clear();

            for( int index = 0; index < mUserData.coverPhotos.size(); index++ ) {

                CoverPhoto coverPhoto = mUserData.coverPhotos.get(index);

                mCoverPhotos.add(coverPhoto.highResUri);
            }

            CoverPhoto actualCoverPhoto = mUserData.coverPhotos.get(0);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setLowResImageRequest(ImageRequest.fromUri(actualCoverPhoto.lowResUri))
                    .setImageRequest(ImageRequest.fromUri(actualCoverPhoto.highResUri))
                    .setOldController(mCoverPhoto.getController())
                    .build();

            mCoverPhoto.setController(controller);
            mCoverPhoto.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    showImages(0, mCoverPhotos);
                }
            });
        }
    }

    void showImages(int nStartPosition, ArrayList<String> images) {

        Activity activity = getActivity();

        if( activity != null && activity instanceof MainActivity ) {

            MainActivity mainActivity = (MainActivity) activity;

            mainActivity.showImages(nStartPosition, images);
        }
    }

    void initPopupMenu() {

        mPopupMenu = new PopupMenu( EditProfileFragment.this.getContext(), mActionButton );
        mPopupMenu.getMenuInflater().inflate(R.menu.menu_edit_profile, mPopupMenu.getMenu());
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                boolean result = true;

                switch (item.getItemId()) {

                    case R.id.action_edit_cover_photo:
                        startSingleImagePicker( REQUEST_CODE_COVER_PHOTO_PICKER );
                        break;

                    case R.id.action_edit_profile_picture:
                        startSingleImagePicker( REQUEST_CODE_PROFILE_PICTURE_PICKER );
                        break;

                    case R.id.action_settings:
                        editSettings();
                        break;

                    case R.id.action_sign_out:
                        signOut();
                        break;

                    default:
                        result = false;
                        break;
                }

                return result;
            }
        });

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( mProgressBar.getVisibility() != View.GONE )
                    return;

                mPopupMenu.show();
            }
        });
    }

    protected void editSettings() {

        Activity activity = getActivity();
        Intent intent = new Intent(activity, SettingsActivity.class);

        activity.startActivity(intent);
    }

    protected void signOut() {

        FishbookUser fishbookUser = FishbookUser.getCurrentUser();

        if( fishbookUser != null ) {

            fishbookUser.signOut();
        }

        Activity activity = getActivity();

        Intent intent = new Intent(activity, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivity(intent);
        activity.finish();
    }

    protected void startSingleImagePicker(int requestCode) {

        ImagePicker.create(this)
                .single()
                .start(requestCode);
    }

    protected Image getFirstSelectedImage(Intent data) {

        ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

        return images.get(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( resultCode == RESULT_OK && data != null ) {

            switch ( requestCode ) {

                case REQUEST_CODE_PROFILE_PICTURE_PICKER:
                    mFishbookUser.setProfilePicture(getFirstSelectedImage(data));
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;

                case REQUEST_CODE_COVER_PHOTO_PICKER:
                    mFishbookUser.setCoverPhoto(getFirstSelectedImage(data));
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
