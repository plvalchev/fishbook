package com.valchev.plamen.fishbook.utils;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by admin on 27.5.2017 г..
 */

public class FirebaseStorageUtils {

    public static StorageReference getStorageReference() {

        return FirebaseStorage.getInstance().getReference();
    }

    public static StorageReference getImagesStorageReference() {

        StorageReference storageReference = getStorageReference();

        return storageReference.child("images");
    }

    public static StorageReference getUserImagesStorageReference(String userKey) {

        StorageReference imagesStorageReference = getImagesStorageReference();

        return imagesStorageReference.child(userKey);
    }

    public static StorageReference getUserPostsImagesStorageReference(String userKey) {

        StorageReference userImagesStorageReference = getUserImagesStorageReference(userKey);

        return userImagesStorageReference.child("posts");
    }

    public static StorageReference getUserProfileImagesStorageReference(String userKey) {

        StorageReference userImagesStorageReference = getUserImagesStorageReference(userKey);

        return userImagesStorageReference.child("profile_pictures");
    }

    public static StorageReference getUserCoverImagesStorageReference(String userKey) {

        StorageReference userImagesStorageReference = getUserImagesStorageReference(userKey);

        return userImagesStorageReference.child("cover_photos");
    }

    public static StorageReference getLowResStorageReference(StorageReference storageReference) {

        return storageReference.child("low_res");
    }

    public static StorageReference getMidResStorageReference(StorageReference storageReference) {

        return storageReference.child("mid_res");
    }

    public static StorageReference getHighResStorageReference(StorageReference storageReference) {

        return storageReference.child("high_res");
    }
}
