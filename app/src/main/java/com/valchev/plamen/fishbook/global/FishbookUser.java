package com.valchev.plamen.fishbook.global;

import android.support.annotation.NonNull;

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

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by admin on 9.4.2017 г..
 */

public class FishbookUser implements ValueEventListener, OnSuccessListener<ArrayList<MultipleImageUploader.MultipleImage>> {

    public interface UserValueEventListener {

        void onDataChange(User user);
        void onCancelled(DatabaseError databaseError);
    }

    public interface UserAuthStateListener {

        void onAuthStateChanged(FishbookUser fishbookUser);
    }

    protected String mUid;
    protected DatabaseReference mDatabaseReference;
    protected StorageReference mStorageReference;
    protected User mUserData;
    protected ArrayList<UserValueEventListener> mUserValueEventListeners;

    protected static FishbookUser mCurrentUser;
    protected static FirebaseAuth.AuthStateListener mFireBaseAuthStateListener;

    public FishbookUser(String uid) {

        mUid = uid;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
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

        DatabaseReference userDatabaseReference = getUserDatabaseReference();

        userDatabaseReference.setValue(mUserData);
    }

    @Override
    public void onSuccess(ArrayList<MultipleImageUploader.MultipleImage> images) {

        saveUserData();
    }

    public void setCoverPhoto(Image newImage) {

        if (mUserData.coverPhotos == null) {

            mUserData.coverPhotos = new ArrayList<>();
        }

        mUserData.coverPhotos.remove(newImage);
        mUserData.coverPhotos.add(0, newImage);

        if( !newImage.isUploaded() ) {

            String coverPhotoName = "cover_photo_" + UUID.randomUUID().toString();
            StorageReference lowResCoverPhotosStorageReference = getLowResCoverPhotosStorageReference(coverPhotoName);
            StorageReference highResCoverPhotosStorageReference = getHighResCoverPhotosStorageReference(coverPhotoName);
            MultipleImageUploader.MultipleImage multipleImage = new MultipleImageUploader.MultipleImage(newImage, lowResCoverPhotosStorageReference, highResCoverPhotosStorageReference);
            MultipleImageUploader multipleImageUploader = new MultipleImageUploader(multipleImage);

            multipleImageUploader.addOnSuccessListener(this);
            multipleImageUploader.uploadImages();
        }
        else {

            saveUserData();
        }
    }

    public void setProfilePicture(Image newImage) {

        if( mUserData.profilePictures == null ) {

            mUserData.profilePictures = new ArrayList<>();
        }

        mUserData.profilePictures.remove(newImage);
        mUserData.profilePictures.add(0, newImage);

        if( !newImage.isUploaded() ) {

            String profilePictureName = "profile_picture" + UUID.randomUUID().toString();
            StorageReference lowResProfilePictureStorageReference = getLowResProfilePicturesStorageReference(profilePictureName);
            StorageReference highResProfilePictureStorageReference = getHighResProfilePicturesStorageReference(profilePictureName);
            MultipleImageUploader.MultipleImage multipleImage = new MultipleImageUploader.MultipleImage(newImage, lowResProfilePictureStorageReference, highResProfilePictureStorageReference);
            MultipleImageUploader multipleImageUploader = new MultipleImageUploader(multipleImage);

            multipleImageUploader.addOnSuccessListener(this);
            multipleImageUploader.uploadImages();
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

        DatabaseReference userDatabaseReference = getUserDatabaseReference();
        userDatabaseReference.removeEventListener(this);
        userDatabaseReference.addValueEventListener(this);
    }

    public void signOut() {

        FirebaseAuth.getInstance().signOut();
    }

    public void addUserValueEventListener(UserValueEventListener valueEventListener) {

        if( mUserValueEventListeners == null ) {

            mUserValueEventListeners = new ArrayList<UserValueEventListener>();
        }
        else if( mUserValueEventListeners.contains(valueEventListener) ) {

            return;
        }

        mUserValueEventListeners.add(valueEventListener);

        if( mUserData != null )
            valueEventListener.onDataChange(mUserData);
    }

    protected DatabaseReference getCoverPhotosDatabaseReference() {

        DatabaseReference userDatabaseReference = getUserDatabaseReference();
        DatabaseReference databaseReference = userDatabaseReference.child("cover_photos");

        return databaseReference;
    }

    protected DatabaseReference getProfilePicturesDatabaseReference() {

        DatabaseReference userDatabaseReference = getUserDatabaseReference();
        DatabaseReference databaseReference = userDatabaseReference.child("profile_pictures");

        return databaseReference;
    }

    protected DatabaseReference getUserDatabaseReference() {

        DatabaseReference databaseReference = mDatabaseReference.child("user").child(getUid());

        return databaseReference;
    }

    public static DatabaseReference getUserDatabaseReference(String uid) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(uid);

        return databaseReference;
    }

    protected StorageReference getHighResCoverPhotosStorageReference(String coverPhotoName) {

        StorageReference storageReference = mStorageReference.child("images/" + getUid() + "/cover_photos/high_res/" + coverPhotoName);

        return storageReference;
    }

    protected StorageReference getHighResProfilePicturesStorageReference(String profilePictureName) {

        StorageReference storageReference = mStorageReference.child("images/" + getUid() + "/profile_pictures/high_res/" + profilePictureName);

        return storageReference;
    }

    protected StorageReference getLowResCoverPhotosStorageReference(String coverPhotoName) {

        StorageReference storageReference = mStorageReference.child("images/" + getUid() + "/cover_photos/low_res/" + coverPhotoName);

        return storageReference;
    }

    protected StorageReference getLowResProfilePicturesStorageReference(String profilePictureName) {

        StorageReference storageReference = mStorageReference.child("images/" + getUid() + "/profile_pictures/low_res/" + profilePictureName);

        return storageReference;
    }
}
