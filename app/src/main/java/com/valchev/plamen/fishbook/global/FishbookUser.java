package com.valchev.plamen.fishbook.global;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

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
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.valchev.plamen.fishbook.models.CoverPhoto;
import com.valchev.plamen.fishbook.models.FishingRegion;
import com.valchev.plamen.fishbook.models.PersonalInformation;
import com.valchev.plamen.fishbook.models.ProfilePicture;
import com.valchev.plamen.fishbook.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

import id.zelory.compressor.Compressor;

/**
 * Created by admin on 9.4.2017 г..
 */

public class FishbookUser implements ValueEventListener {

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

    public void setCoverPhoto(Image newImage) {

        String coverPhotoName = "cover_photo_" + UUID.randomUUID().toString();
        StorageReference lowResCoverPhotosStorageReference = getLowResCoverPhotosStorageReference(coverPhotoName);
        StorageReference highResCoverPhotosStorageReference = getHighResCoverPhotosStorageReference(coverPhotoName);

        MultipleImagesUploader multipleImagesUploader = new MultipleImagesUploader();
        multipleImagesUploader.addOnSuccessListener(new OnSuccessListener<ArrayList<com.valchev.plamen.fishbook.models.Image>>() {

            @Override
            public void onSuccess(ArrayList<com.valchev.plamen.fishbook.models.Image> images) {

                com.valchev.plamen.fishbook.models.Image coverPhotoImage = images.get(0);
                CoverPhoto coverPhoto = new CoverPhoto(coverPhotoImage.lowResUri, coverPhotoImage.highResUri);

                if( mUserData.coverPhotos == null ) {

                    mUserData.coverPhotos = new ArrayList<>();
                }

                mUserData.coverPhotos.add(0, coverPhoto);

                DatabaseReference userDatabaseReference = getUserDatabaseReference();
                userDatabaseReference.setValue(mUserData);
            }
        });

        ArrayList<Image> images = new ArrayList<>();

        images.add(newImage);

        multipleImagesUploader.uploadImages(images, lowResCoverPhotosStorageReference, highResCoverPhotosStorageReference);
    }

    public void setProfilePicture(Image newImage) {

        String profilePictureName = "profile_picture" + UUID.randomUUID().toString();
        StorageReference lowResProfilePictureStorageReference = getLowResProfilePicturesStorageReference(profilePictureName);
        StorageReference highResProfilePictureStorageReference = getHighResProfilePicturesStorageReference(profilePictureName);

        MultipleImagesUploader multipleImagesUploader = new MultipleImagesUploader();
        multipleImagesUploader.addOnSuccessListener(new OnSuccessListener<ArrayList<com.valchev.plamen.fishbook.models.Image>>() {

            @Override
            public void onSuccess(ArrayList<com.valchev.plamen.fishbook.models.Image> images) {

                com.valchev.plamen.fishbook.models.Image profilePictureImage = images.get(0);
                ProfilePicture profilePicture = new ProfilePicture(profilePictureImage.lowResUri, profilePictureImage.highResUri);

                if( mUserData.profilePictures == null ) {

                    mUserData.profilePictures = new ArrayList<>();
                }

                mUserData.profilePictures.add(0, profilePicture);

                DatabaseReference userDatabaseReference = getUserDatabaseReference();
                userDatabaseReference.setValue(mUserData);
            }
        });

        ArrayList<Image> images = new ArrayList<>();

        images.add(newImage);

        multipleImagesUploader.uploadImages(images, lowResProfilePictureStorageReference, highResProfilePictureStorageReference);
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
