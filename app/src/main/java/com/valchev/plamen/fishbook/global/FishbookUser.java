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

        byte[] byteArray = FishbookUtils.resizeAndCompressImage(newImage.getPath(), 1560, 1560);
        String coverPhotoName = "cover_photo_" + UUID.randomUUID().toString();
        StorageReference coverPhotosStorageReference = getCoverPhotosStorageReference(coverPhotoName);
        UploadTask uploadTask = coverPhotosStorageReference.putBytes(byteArray);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // TODO
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                DatabaseReference userDatabaseReference = getUserDatabaseReference();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                CoverPhoto coverPhoto = new CoverPhoto(downloadUrl.toString());

                if( mUserData.coverPhotos == null ) {

                    mUserData.coverPhotos = new ArrayList<CoverPhoto>();
                }

                mUserData.coverPhotos.add(0, coverPhoto);

                userDatabaseReference.setValue(mUserData);
            }
        });
    }

    public void setProfilePicture(Image newImage) {

        byte[] byteArray = FishbookUtils.resizeAndCompressImage(newImage.getPath(), 1560, 1560);
        String profilePictureName = "profile_picture" + UUID.randomUUID().toString();
        StorageReference profilePictureStorageReference = getProfilePicturesStorageReference(profilePictureName);
        UploadTask uploadTask = profilePictureStorageReference.putBytes(byteArray);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // TODO
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                DatabaseReference userDatabaseReference = getUserDatabaseReference();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                ProfilePicture profilePicture = new ProfilePicture(downloadUrl.toString());

                if( mUserData.profilePictures == null ) {

                    mUserData.profilePictures = new ArrayList<ProfilePicture>();
                }

                mUserData.profilePictures.add(0, profilePicture);

                userDatabaseReference.setValue(mUserData);
            }
        });
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

    protected StorageReference getCoverPhotosStorageReference(String coverPhotoName) {

        StorageReference storageReference = mStorageReference.child("images/" + getUid() + "/cover_photos/" + coverPhotoName);

        return storageReference;
    }

    protected StorageReference getProfilePicturesStorageReference(String profilePictureName) {

        StorageReference storageReference = mStorageReference.child("images/" + getUid() + "/profile_pictures/" + profilePictureName);

        return storageReference;
    }
}
