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

public class MultipleImagesUploader {

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

            for (Image image: mResult) {

                if( image == null )
                    return;

                if( image.lowResUri == null || image.lowResUri.isEmpty() )
                    return;

                if( image.highResUri == null || image.highResUri.isEmpty() )
                    return;
            }

            if( mSuccesListeners != null ) {

                for( OnSuccessListener<ArrayList<Image>> successListener : mSuccesListeners ) {

                    successListener.onSuccess(mResult);
                }
            }
        }
    }

    private ArrayList<OnFailureListener> mFailureListeners;
    private ArrayList<OnSuccessListener<ArrayList<Image>>> mSuccesListeners;
    private ArrayList<Image> mResult;

    public MultipleImagesUploader addOnFailureListener(OnFailureListener onFailureListener) {

        if( mFailureListeners == null ) {

            mFailureListeners = new ArrayList<OnFailureListener>();
        }

        mFailureListeners.add(onFailureListener);
        return this;
    }

    public MultipleImagesUploader addOnSuccessListener(OnSuccessListener<ArrayList<Image>> onSuccessListener) {

        if( mSuccesListeners == null ) {

            mSuccesListeners = new ArrayList<OnSuccessListener<ArrayList<Image>>>();
        }

        mSuccesListeners.add(onSuccessListener);
        return this;
    }

    public void uploadImages(ArrayList<com.nguyenhoanglam.imagepicker.model.Image> images,
                             StorageReference lowResStorageReference,
                             StorageReference highResStorageReference) {



        for (com.nguyenhoanglam.imagepicker.model.Image image: images ) {

            if( mResult == null ) {

                mResult = new ArrayList<Image>();
            }

            Image result = new Image();

            mResult.add(result);

            byte[] lowResResByteArray = FishbookUtils.resizeAndCompressImage(image.getPath(), 15, 15);
            byte[] highResByteArray = FishbookUtils.resizeAndCompressImage(image.getPath(), 1560, 1560);

            UploadTask lowResUploadTask = lowResStorageReference.putBytes(lowResResByteArray);
            UploadTask highResUploadTask = highResStorageReference.putBytes(highResByteArray);

            if( mFailureListeners != null ) {

                for (OnFailureListener failureListener : mFailureListeners) {

                    lowResUploadTask.addOnFailureListener(failureListener);
                    highResUploadTask.addOnFailureListener(failureListener);
                }
            }

            lowResUploadTask.addOnSuccessListener( new OnImageUploadSuccessListener(result, true) );
            highResUploadTask.addOnSuccessListener( new OnImageUploadSuccessListener(result, false) );
        }
    }
}
