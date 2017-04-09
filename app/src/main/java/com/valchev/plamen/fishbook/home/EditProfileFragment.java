package com.valchev.plamen.fishbook.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookApplication;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.CoverPhoto;
import com.valchev.plamen.fishbook.models.FishingMethod;
import com.valchev.plamen.fishbook.models.FishingRegion;
import com.valchev.plamen.fishbook.models.PersonalInformation;
import com.valchev.plamen.fishbook.models.ProfilePicture;
import com.valchev.plamen.fishbook.models.Specie;
import com.valchev.plamen.fishbook.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditProfileFragment extends Fragment {

    private final static int REQUEST_CODE_COVER_PHOTO_PICKER = 2000;
    private final static int REQUEST_CODE_PROFILE_PICTURE_PICKER = 2001;
    private static final String KEY_PROFILE_PICTURES_POSITION = "PROFILE_PICTURES_POSITION";
    private static final String KEY_COVER_PHOTOS_PISITON = "COVER_PHOTOS_POSITION";
    private static final String KEY_USER_DATA = "USER_DATA";

    protected FishbookUser mFishbookUser;
    protected User mUserData;
    protected TextView mDisplayName;
    protected ExpandableTextView mTopFishingRegions;
    protected ExpandableTextView mMostChasedSpecies;
    protected ExpandableTextView mFishingMethods;
    protected SimpleDraweeView mProfilePicture;
    protected SimpleDraweeView mCoverPhoto;
    protected Button mActionButton;
    protected PopupMenu mPopupMenu;
    protected ArrayList<String> mProfilePictures;
    protected ArrayList<String> mCoverPhotos;
    protected int mProfilePicturesCurrentPosition;
    protected int mCoverPhotosCurrentPosition;


    static String[] test = new String[] { "https://wallpaperscraft.com/image/fishing_gear_shoes_64538_1024x768.jpg", "https://scontent-frt3-1.xx.fbcdn.net/v/l/t1.0-9/14484832_10207954542601473_5993629062988162629_n.jpg?oh=3447efedcb5bdef8e55627513838e225&oe=5953DE1D" };

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
        mActionButton = (Button) view.findViewById(R.id.edit_profile_button);
        mDisplayName = (TextView) view.findViewById(R.id.display_name);

        topFishingRegionsTitle.setText(getResources().getString(R.string.regions));
        mostChasedSpeciesTitle.setText(getResources().getString(R.string.chased_species));
        fishingMethodsTitle.setText(getResources().getString(R.string.fishing_methods));

        mFishbookUser = FishbookUser.getCurrentUser();

        if (savedInstanceState != null) {
            mProfilePicturesCurrentPosition = savedInstanceState.getInt(KEY_PROFILE_PICTURES_POSITION);
            mCoverPhotosCurrentPosition = savedInstanceState.getInt(KEY_COVER_PHOTOS_PISITON);
        }

        initUserProfile();

        mFishbookUser.addUserValueEventListener(new FishbookUser.UserValueEventListener() {

            @Override
            public void onDataChange(User user) {

                mUserData = user;

                initUserProfile();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        initPopupMenu();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(KEY_PROFILE_PICTURES_POSITION, mProfilePicturesCurrentPosition);
        outState.putInt(KEY_COVER_PHOTOS_PISITON, mCoverPhotosCurrentPosition);
        super.onSaveInstanceState(outState);
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

                mProfilePictures.add(profilePicture.uri);
            }

            String actualProfilePictureUri = mProfilePictures.get(0);

            mProfilePicture.setImageURI(Uri.parse(actualProfilePictureUri));
            mProfilePicture.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    showProfilePicturesImageViewer(0);
                }
            });
        }

        if( mUserData.coverPhotos != null && mUserData.coverPhotos.size() > 0 ) {

            if( mCoverPhotos == null )
                mCoverPhotos = new ArrayList<String>();

            mCoverPhotos.clear();

            for( int index = 0; index < mUserData.coverPhotos.size(); index++ ) {

                CoverPhoto coverPhoto = mUserData.coverPhotos.get(index);

                mCoverPhotos.add(coverPhoto.uri);
            }

            String actualCoverPhotoUri = mCoverPhotos.get(0);

            mCoverPhoto.setImageURI(Uri.parse(actualCoverPhotoUri));
            mCoverPhoto.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    showCoverPhotosImageViewer(0);
                }
            });
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (mProfilePicturesCurrentPosition >= 0
                && mProfilePictures != null
                && mProfilePicturesCurrentPosition < mProfilePictures.size() - 1 ) {

            showProfilePicturesImageViewer(mProfilePicturesCurrentPosition);

        } else if( mCoverPhotosCurrentPosition >= 0
                && mCoverPhotos != null
                && mCoverPhotosCurrentPosition < mCoverPhotos.size() - 1 ) {

            showCoverPhotosImageViewer(mCoverPhotosCurrentPosition);
        }
    }

    void showCoverPhotosImageViewer(int nStartPosition) {

        mCoverPhotosCurrentPosition = nStartPosition;

        new ImageViewer.Builder(getActivity(), mCoverPhotos)
                .setStartPosition(nStartPosition)
                .setImageMarginPx(20)
                .setImageChangeListener(new ImageViewer.OnImageChangeListener() {
                    @Override
                    public void onImageChange(int position) {
                        mCoverPhotosCurrentPosition = position;
                    }
                })
                .setOnDismissListener(new ImageViewer.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mCoverPhotosCurrentPosition = -1;
                    }
                })
                .show();
    }

    void showProfilePicturesImageViewer(int nStartPosition) {

        mProfilePicturesCurrentPosition = nStartPosition;

        new ImageViewer.Builder(getActivity(), mProfilePictures)
                .setStartPosition(nStartPosition)
                .setImageMarginPx(20)
                .setImageChangeListener(new ImageViewer.OnImageChangeListener() {
                    @Override
                    public void onImageChange(int position) {
                        mProfilePicturesCurrentPosition = position;
                    }
                })
                .setOnDismissListener(new ImageViewer.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mProfilePicturesCurrentPosition = -1;
                    }
                })
                .show();

    }

    void initPopupMenu() {

        mPopupMenu = new PopupMenu( EditProfileFragment.this.getContext(), mActionButton );
        mPopupMenu.getMenuInflater().inflate(R.menu.menu_edit_profile, mPopupMenu.getMenu());
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                boolean result = true;

                switch (item.getItemId()) {

                    case R.id.action_edit_personal_information:
                        editPersonalInformation();
                        break;

                    case R.id.action_edit_cover_photo:
                        startSingleImagePicker( REQUEST_CODE_COVER_PHOTO_PICKER );
                        break;

                    case R.id.action_edit_profile_picture:
                        startSingleImagePicker( REQUEST_CODE_PROFILE_PICTURE_PICKER );
                        break;

                    case R.id.action_settings:
                        editSettings();
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

                mPopupMenu.show();
            }
        });
    }

    protected void editPersonalInformation() {


    }

    protected void editSettings() {

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
                    break;

                case REQUEST_CODE_COVER_PHOTO_PICKER:
                    mFishbookUser.setCoverPhoto(getFirstSelectedImage(data));
                    break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
