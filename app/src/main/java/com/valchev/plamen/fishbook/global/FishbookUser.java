package com.valchev.plamen.fishbook.global;

import android.support.annotation.NonNull;

import com.beloo.widget.chipslayoutmanager.util.log.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.User;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;
import com.valchev.plamen.fishbook.utils.FirebaseStorageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by admin on 9.4.2017 г..
 */

public class FishbookUser implements ValueEventListener, OnSuccessListener<ArrayList<Image>> {

    public interface UserValueEventListener {

        void onDataChange(User user);
        void onCancelled(DatabaseError databaseError);
    }

    public interface UserAuthStateListener {

        void onAuthStateChanged(FishbookUser fishbookUser);
    }

    protected String mUid;
    protected User mUserData;
    protected ArrayList<UserValueEventListener> mUserValueEventListeners;

    protected static FishbookUser mCurrentUser;
    protected static FirebaseAuth.AuthStateListener mFireBaseAuthStateListener;

    public FishbookUser(String uid) {

        mUid = uid;
    }

    public FishbookUser(String uid, User userData) {

        mUserData = userData;
        mUid = uid;
    }

    public static void loadCurrentUserInBackground(final UserAuthStateListener userAuthStateListener) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if( mFireBaseAuthStateListener != null )
            firebaseAuth.removeAuthStateListener(mFireBaseAuthStateListener);

        mFireBaseAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if( firebaseUser != null ) {

                    mCurrentUser = new FishbookUser(firebaseUser.getUid());
                    mCurrentUser.reloadUserInBackground();
                }
                else {

                    mCurrentUser = null;
                }

                if( userAuthStateListener != null )
                    userAuthStateListener.onAuthStateChanged(mCurrentUser);
            }
        };

        firebaseAuth.addAuthStateListener(mFireBaseAuthStateListener);
    }

    public static FishbookUser getCurrentUser() {

        return mCurrentUser;
    }

    public String getUid() {

        return mUid;
    }

    public void saveUserData() {

        Log.d( "saveUserData", "Save user data begin" );

        if( mUserData.profilePicture != null ) {

            if( mUserData.profilePicture.id == null || mUserData.profilePicture.id.isEmpty() )
                mUserData.profilePicture.id = FirebaseDatabaseUtils.getProfileImagesDatabaseReference().push().getKey();
        }

        if( mUserData.coverPhoto != null ) {

            if( mUserData.coverPhoto.id == null || mUserData.coverPhoto.id.isEmpty() )
                mUserData.coverPhoto.id = FirebaseDatabaseUtils.getCoverImagesDatabaseReference().push().getKey();
        }

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/user/" + getUid(), mUserData);

        if( mUserData.profilePicture != null ) {

            childUpdates.put("/images/profile-pictures/" + getUid() + "/" + mUserData.profilePicture.id, mUserData.profilePicture);
        }

        if( mUserData.coverPhoto != null ) {

            childUpdates.put("/images/cover-photos/" + getUid() + "/" + mUserData.coverPhoto.id, mUserData.coverPhoto);
        }

        FirebaseDatabaseUtils.getDatabaseReference().updateChildren(childUpdates);

        Log.d( "saveUserData", "Save user data end" );
    }

    @Override
    public void onSuccess(ArrayList<Image> images) {

        Log.d( "onSuccess", "On success called" );

        saveUserData();
    }

    public void setCoverPhoto(Image newImage, OnFailureListener onFailureListener) {

        mUserData.coverPhoto = newImage;

        if( !newImage.isUploaded() ) {

            StorageReference storageReference = FirebaseStorageUtils.getUserCoverImagesStorageReference(getUid());
            MultipleImageUploader.MultipleImage multipleImage = new MultipleImageUploader.MultipleImage(newImage, storageReference);
            MultipleImageUploader multipleImageUploader = new MultipleImageUploader(this, onFailureListener);

            multipleImageUploader.execute(multipleImage);
        }
        else {

            saveUserData();
        }
    }

    public void setProfilePicture(Image newImage, OnFailureListener onFailureListener) {

        mUserData.profilePicture = newImage;

        if( !newImage.isUploaded() ) {

            StorageReference storageReference = FirebaseStorageUtils.getUserProfileImagesStorageReference(getUid());
            MultipleImageUploader.MultipleImage multipleImage = new MultipleImageUploader.MultipleImage(newImage, storageReference);
            MultipleImageUploader multipleImageUploader = new MultipleImageUploader(this, onFailureListener);

            multipleImageUploader.execute(multipleImage);
        }
        else {

            saveUserData();
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        mUserData = dataSnapshot.getValue(User.class);

        if( mUserData == null )
            mUserData = new User();

        if( mUserValueEventListeners != null ) {

            for (UserValueEventListener valueEventListener: mUserValueEventListeners) {

                valueEventListener.onDataChange(mUserData);
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

        if( mUserValueEventListeners != null ) {

            for (UserValueEventListener valueEventListener: mUserValueEventListeners) {

                valueEventListener.onCancelled(databaseError);
            }
        }
    }

    public void reloadUserInBackground() {

        DatabaseReference userDatabaseReference = FirebaseDatabaseUtils.getUserDatabaseReference(getUid());
        userDatabaseReference.removeEventListener(this);
        userDatabaseReference.addValueEventListener(this);
    }

    public void signOut() {

        FirebaseAuth.getInstance().signOut();
    }

    public void addUserValueEventListener(UserValueEventListener valueEventListener) {

        if( mUserValueEventListeners == null ) {

            mUserValueEventListeners = new ArrayList<>();
        }
        else if( mUserValueEventListeners.contains(valueEventListener) ) {

            return;
        }

        mUserValueEventListeners.add(valueEventListener);

        if( mUserData != null )
            valueEventListener.onDataChange(mUserData);
    }
}
