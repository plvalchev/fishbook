package com.valchev.plamen.fishbook.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.beloo.widget.chipslayoutmanager.util.log.Log;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseError;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.chat.ChatActivity;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.User;

import java.io.File;
import java.io.Serializable;
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
    protected TextView mTopFishingRegions;
    protected TextView mMostChasedSpecies;
    protected TextView mFishingMethods;
    protected SimpleDraweeView mProfilePicture;
    protected SimpleDraweeView mCoverPhoto;
    protected PopupMenu mPopupMenu;
    protected Button mSendMessageButton;
    protected LinearLayout mLayoutUserInfo;
    protected RecyclerView mRecyclerView;
    protected FeedRecyclerViewAdapter mFeedRecyclerViewAdapter;
    protected Button mSettingsButton;

    public EditProfileFragment() {

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

        mTopFishingRegions = (TextView) regions.findViewById(R.id.expandable_text);
        mMostChasedSpecies = (TextView) species.findViewById(R.id.expandable_text);
        mFishingMethods = (TextView) methods.findViewById(R.id.expandable_text);
        mProfilePicture = (SimpleDraweeView) view.findViewById(R.id.profile_image);
        mCoverPhoto = (SimpleDraweeView) view.findViewById(R.id.cover_photo);
        mDisplayName = (TextView) view.findViewById(R.id.display_name);
        mSendMessageButton = (Button) view.findViewById(R.id.send_message);
        mLayoutUserInfo = (LinearLayout) view.findViewById(R.id.layout_user_info);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mSettingsButton = (Button) view.findViewById(R.id.settings);

        FishbookActivity activity = (FishbookActivity)getActivity();

        if( activity instanceof MainActivity ) {

            ((MainActivity) activity).showFAB(false);
        }

        topFishingRegionsTitle.setText(getResources().getString(R.string.regions));
        mostChasedSpeciesTitle.setText(getResources().getString(R.string.chased_species));
        fishingMethodsTitle.setText(getResources().getString(R.string.fishing_methods));

        Bundle bundle = getActivity().getIntent().getExtras();

        if( bundle != null ) {

            Serializable user = bundle.getSerializable("userData");
            String uid = bundle.getString("uid");

            if( user != null ) {

                mUserData = (User) user;

                mFishbookUser = new FishbookUser(uid, mUserData);

                mFishbookUser.reloadUserInBackground();
            }
            else
                mFishbookUser = FishbookUser.getCurrentUser();
        }
        else {

            mFishbookUser = FishbookUser.getCurrentUser();
        }

        mFeedRecyclerViewAdapter = new FeedRecyclerViewAdapter(FirebaseDatabaseUtils.getUserPostsDatabaseReference(mFishbookUser.getUid()), activity);
        mRecyclerView.setAdapter(mFeedRecyclerViewAdapter);

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

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity activity = getActivity();
                Intent intent = new Intent(activity, ChatActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("receiver", mFishbookUser.getUid());

                intent.putExtras(bundle);

                activity.startActivity(intent);
            }
        });

        mLayoutUserInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                editSettings();
            }
        });


        return view;
    }

    void initUserProfile() {

        if( getActivity() instanceof UserProfileActivity ) {

            ((UserProfileActivity) getActivity()).getSupportActionBar().setTitle(mUserData.getDisplayName());
        }

        mDisplayName.setText(mUserData.getDisplayName());
        mTopFishingRegions.setText(mUserData.getTopFishingRegionsAsString());
        mMostChasedSpecies.setText(mUserData.getMostChasedSpeciesAsStringAsString());
        mFishingMethods.setText(mUserData.getFishingMethodsAsString());

        if( mUserData.profilePicture != null ) {

            com.valchev.plamen.fishbook.models.Image actualProfilePicture = mUserData.profilePicture;

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setLowResImageRequest(ImageRequest.fromUri(actualProfilePicture.lowResUri))
                    .setImageRequest(ImageRequest.fromUri(actualProfilePicture.midResUri))
                    .setOldController(mProfilePicture.getController())
                    .build();

            mProfilePicture.setController(controller);
            mProfilePicture.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Activity activity = getActivity();

                    if( activity != null && activity instanceof FishbookActivity ) {

                        FishbookActivity fishbookActivity = (FishbookActivity) activity;

                        fishbookActivity.showImages(FirebaseDatabaseUtils.getUserProfileImagesDatabaseReference(mFishbookUser.getUid()));
                    }
                }
            });
        }

        if( mUserData.coverPhoto != null ) {

            com.valchev.plamen.fishbook.models.Image actualCoverPhoto = mUserData.coverPhoto;

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setLowResImageRequest(ImageRequest.fromUri(actualCoverPhoto.lowResUri))
                    .setImageRequest(ImageRequest.fromUri(actualCoverPhoto.midResUri))
                    .setOldController(mCoverPhoto.getController())
                    .build();

            mCoverPhoto.setController(controller);
            mCoverPhoto.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Activity activity = getActivity();

                    if( activity != null && activity instanceof FishbookActivity ) {

                        FishbookActivity fishbookActivity = (FishbookActivity) activity;

                        fishbookActivity.showImages(FirebaseDatabaseUtils.getUserCoverImagesDatabaseReference(mFishbookUser.getUid()));
                    }
                }
            });
        }
    }

    void initPopupMenu() {

        mPopupMenu = new PopupMenu( EditProfileFragment.this.getContext(), mSettingsButton );
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

        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPopupMenu.show();
            }
        });

//        boolean isCurrentUser = mFishbookUser.getUid().compareToIgnoreCase(FishbookUser.getCurrentUser().getUid()) == 0;
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
                    setProfilePicture(getFirstSelectedImage(data));
                    break;

                case REQUEST_CODE_COVER_PHOTO_PICKER:
                    setCoverPhoto(getFirstSelectedImage(data));
                    break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void setProfilePicture(Image profilePicture) {

        File file = new File(profilePicture.getPath());
        Uri uri = Uri.fromFile(file);
        String uriString = uri.toString();
        com.valchev.plamen.fishbook.models.Image imageModel =
                new com.valchev.plamen.fishbook.models.Image(uriString, uriString, uriString, profilePicture.getPath());

        mFishbookUser.setProfilePicture(imageModel, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("setProfilePicture", "Unable to set profile picture");

                e.printStackTrace();
            }
        });
    }

    protected void setCoverPhoto(Image coverPhoto) {

        File file = new File(coverPhoto.getPath());
        Uri uri = Uri.fromFile(file);
        String uriString = uri.toString();
        com.valchev.plamen.fishbook.models.Image imageModel =
                new com.valchev.plamen.fishbook.models.Image(uriString, uriString, uriString, coverPhoto.getPath());

        mFishbookUser.setCoverPhoto(imageModel, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("setCoverPhoto", "Unable to set cover photo");

                e.printStackTrace();
            }
        });
    }
}
