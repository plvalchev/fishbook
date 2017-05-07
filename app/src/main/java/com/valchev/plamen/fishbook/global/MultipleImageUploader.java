package com.valchev.plamen.fishbook.global;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valchev.plamen.fishbook.models.Image;

import java.util.ArrayList;

/**
 * Created by admin on 23.4.2017 г..
 */

public class MultipleImageUploader {

    private class OnImageUploadSuccessListener implements OnSuccessListener<UploadTask.TaskSnapshot> {

        private Image mImage;
        private boolean mLowResUrl;

        public OnImageUploadSuccessListener(Image image, boolean lowResUrl) {

            mImage = image;
            mLowResUrl = lowResUrl;
        }

        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            Uri downloadUri = taskSnapshot.getDownloadUrl();
            String uri = downloadUri.toString();

            if( mLowResUrl ) {

                mImage.lowResUri = uri;
            }
            else {

                mImage.highResUri = uri;
            }

            if( mImage.lowResUri.startsWith("http") && mImage.highResUri.startsWith("http") )
                mImage.path = null;

            for (MultipleImage multipleImage: mMultipleImages) {

                if( multipleImage == null )
                    return;

                if( !multipleImage.image.isUploaded() )
                    return;
            }

            if( mSuccessListeners != null ) {

                for( OnSuccessListener<ArrayList<MultipleImage>> successListener : mSuccessListeners ) {

                    successListener.onSuccess(mMultipleImages);
                }
            }
        }
    }

    public static class MultipleImage {

        public Image image;
        public StorageReference lowResStorageReference;
        public StorageReference highResStorageReference;

        public MultipleImage(Image image, StorageReference lowResStorageReference, StorageReference highResStorageReference) {
            this.image = image;
            this.lowResStorageReference = lowResStorageReference;
            this.highResStorageReference = highResStorageReference;
        }
    }

    private ArrayList<OnFailureListener> mFailureListeners;
    private ArrayList<OnSuccessListener<ArrayList<MultipleImage>>> mSuccessListeners;
    private ArrayList<MultipleImage> mMultipleImages;

    public MultipleImageUploader(MultipleImage multipleImage) {

        mMultipleImages = new ArrayList<>();

        mMultipleImages.add(multipleImage);
    }

    public MultipleImageUploader(ArrayList<MultipleImage> multipleImages) {

        mMultipleImages = multipleImages;
    }

    public MultipleImageUploader addOnFailureListener(OnFailureListener onFailureListener) {

        if( mFailureListeners == null ) {

            mFailureListeners = new ArrayList<>();
        }

        mFailureListeners.add(onFailureListener);
        return this;
    }

    public MultipleImageUploader addOnSuccessListener(OnSuccessListener<ArrayList<MultipleImage>> onSuccessListener) {

        if( mSuccessListeners == null ) {

            mSuccessListeners = new ArrayList<>();
        }

        mSuccessListeners.add(onSuccessListener);
        return this;
    }

    public void uploadImages() {

        for (MultipleImage multipleImage : mMultipleImages) {

            Image image = multipleImage.image;

            byte[] lowResResByteArray = FishbookUtils.resizeAndCompressImage(image.path, 15, 15);
            byte[] highResByteArray = FishbookUtils.resizeAndCompressImage(image.path, 1560, 1560);

            StorageReference lowResStorageReference = multipleImage.lowResStorageReference;
            StorageReference highResStorageReference = multipleImage.highResStorageReference;
            UploadTask lowResUploadTask = lowResStorageReference.putBytes(lowResResByteArray);
            UploadTask highResUploadTask = highResStorageReference.putBytes(highResByteArray);

            if (mFailureListeners != null) {

                for (OnFailureListener failureListener : mFailureListeners) {

                    lowResUploadTask.addOnFailureListener(failureListener);
                    highResUploadTask.addOnFailureListener(failureListener);
                }
            }

            lowResUploadTask.addOnSuccessListener(new OnImageUploadSuccessListener(image, true));
            highResUploadTask.addOnSuccessListener(new OnImageUploadSuccessListener(image, false));
        }
    }
}
