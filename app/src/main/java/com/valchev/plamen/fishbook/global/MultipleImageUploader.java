package com.valchev.plamen.fishbook.global;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.utils.FirebaseStorageUtils;
import com.valchev.plamen.fishbook.utils.FishbookUtils;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by admin on 23.4.2017 г..
 */

public class MultipleImageUploader extends AsyncTask<MultipleImageUploader.MultipleImage, Void, ArrayList<Image>> {

    private enum URL_TYPE {

        LOW_RES,
        MID_RES,
        HIGH_RES
    }

    private class OnImageUploadSuccessListener implements OnSuccessListener<UploadTask.TaskSnapshot> {

        private Image mImage;
        private URL_TYPE mUrlType;

        public OnImageUploadSuccessListener(Image image, URL_TYPE urlType) {

            mImage = image;
            mUrlType = urlType;
        }

        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            Uri downloadUri = taskSnapshot.getDownloadUrl();
            String uri = downloadUri.toString();

            switch (mUrlType) {

                case LOW_RES:
                    mImage.lowResUri = uri;
                    break;
                case MID_RES:
                    mImage.midResUri = uri;
                    break;
                case HIGH_RES:
                    mImage.highResUri = uri;
                    break;
            }
        }
    }

    public static class MultipleImage {

        public Image image;
        public StorageReference storageReference;

        public MultipleImage(Image image, StorageReference storageReference) {

            this.image = image;
            this.storageReference = storageReference;
        }
    }

    OnFailureListener mOnFailureListener;
    OnSuccessListener<ArrayList<Image>> mImageOnSuccessListener;

    public MultipleImageUploader(OnSuccessListener<ArrayList<Image>> onSuccessListener) {

        mImageOnSuccessListener = onSuccessListener;
    }

    public MultipleImageUploader(OnSuccessListener<ArrayList<Image>> onSuccessListener, OnFailureListener onFailureListener) {

        mImageOnSuccessListener = onSuccessListener;
        mOnFailureListener = onFailureListener;
    }

    @Override
    protected ArrayList<Image> doInBackground(MultipleImage... params) {

        ArrayList<UploadTask> uploadTasks = new ArrayList<>();
        final ArrayList<Image> result = new ArrayList<>();

        for (MultipleImage multipleImage : params) {

            Image image = multipleImage.image;

            result.add(image);

            byte[] lowResResByteArray = FishbookUtils.resizeAndCompressImage(image.path, 64, 64);
            byte[] midResResByteArray = FishbookUtils.resizeAndCompressImage(image.path, 768, 768);
            byte[] highResByteArray = FishbookUtils.resizeAndCompressImage(image.path, 1280, 1280);

            String imageName = UUID.randomUUID().toString();

            StorageReference lowResStorageReference =
                    FirebaseStorageUtils.getLowResStorageReference(multipleImage.storageReference).child(imageName);
            StorageReference midResStorageReference =
                    FirebaseStorageUtils.getMidResStorageReference(multipleImage.storageReference).child(imageName);
            StorageReference highResStorageReference =
                    FirebaseStorageUtils.getHighResStorageReference(multipleImage.storageReference).child(imageName);

            UploadTask lowResUploadTask = lowResStorageReference.putBytes(lowResResByteArray);
            UploadTask midResUploadTask = midResStorageReference.putBytes(midResResByteArray);
            UploadTask highResUploadTask = highResStorageReference.putBytes(highResByteArray);

            lowResUploadTask.addOnSuccessListener(new OnImageUploadSuccessListener(image, URL_TYPE.LOW_RES));
            midResUploadTask.addOnSuccessListener(new OnImageUploadSuccessListener(image, URL_TYPE.MID_RES));
            highResUploadTask.addOnSuccessListener(new OnImageUploadSuccessListener(image, URL_TYPE.HIGH_RES));

            uploadTasks.add(lowResUploadTask);
            uploadTasks.add(midResUploadTask);
            uploadTasks.add(highResUploadTask);
        }

        try {

            Task<Void> uploadTask = Tasks.whenAll(uploadTasks);

            uploadTask.addOnFailureListener(mOnFailureListener);

            Log.d("Multi image upload", "Awaiting started");

            Tasks.await(uploadTask, 1, TimeUnit.MINUTES);

            Log.d("Multi image upload", "Awaiting finished");

        } catch (ExecutionException e) {

            e.printStackTrace();

        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (TimeoutException e) {

            Log.d("Multi image upload", "Time out");

            e.printStackTrace();
        }

        for (Image image : result) {

            if( image.lowResUri.startsWith("http") &&
                    image.midResUri.startsWith("http") &&
                    image.highResUri.startsWith("http") ) {

                image.path = null;
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Image> images) {

        for (Image image : images) {

            if( !image.isUploaded() ) {

                if( image.lowResUri.startsWith("http") &&
                        image.midResUri.startsWith("http") &&
                        image.highResUri.startsWith("http") ) {

                    image.path = null;
                }
                else {

                    Log.d("Multi image upload", "Image is not uploaded");
                    Log.d("Multi image upload", "Low res uri:" + image.lowResUri);
                    Log.d("Multi image upload", "Mid res uri:" + image.midResUri);
                    Log.d("Multi image upload", "High res uri:" + image.highResUri);
                    Log.d("Multi image upload", "Path:" + image.path);
                    return;
                }
            }
        }

        mImageOnSuccessListener.onSuccess(images);
    }
}
