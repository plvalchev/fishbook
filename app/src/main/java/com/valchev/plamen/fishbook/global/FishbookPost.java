package com.valchev.plamen.fishbook.global;

import android.provider.ContactsContract;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.valchev.plamen.fishbook.models.FishingMethod;
import com.valchev.plamen.fishbook.models.FishingRegion;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.Post;
import com.valchev.plamen.fishbook.models.Specie;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by admin on 7.5.2017 г..
 */

public class FishbookPost implements OnSuccessListener<ArrayList<Image>>, ValueEventListener {

    public interface PostValueEventListener {

        void onDataChange(Post post);
        void onCancelled(DatabaseError databaseError);
    }

    protected Post mPost;
    protected DatabaseReference mDatabaseReference;
    protected StorageReference mStorageReference;
    protected ArrayList<PostValueEventListener> mPostValueEventListeners;

    public FishbookPost(Post post) {

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mPost = post;
    }

    public void savePost() {

        ArrayList<MultipleImageUploader.MultipleImage> multipleImages = null;

        if( mPost.images != null ) {

            for (Image image : mPost.images ) {

                if( image.isUploaded() ) {

                    continue;
                }

                if( multipleImages == null ) {

                    multipleImages = new ArrayList<>();
                }

                String imageName = "post_" + UUID.randomUUID().toString();
                StorageReference lowResImageStorageReference = getLowResImageStorageReference(imageName, mPost.userID);
                StorageReference midResImageStorageReference = getMidResImageStorageReference(imageName, mPost.userID);
                StorageReference highResImageStorageReference = getHighResImageStorageReference(imageName, mPost.userID);
                MultipleImageUploader.MultipleImage multipleImage =
                        new MultipleImageUploader.MultipleImage(image, lowResImageStorageReference, midResImageStorageReference, highResImageStorageReference);

                multipleImages.add(multipleImage);
            }
        }

        if( multipleImages != null ) {

            MultipleImageUploader multipleImageUploader = new MultipleImageUploader(this);

            multipleImageUploader.execute(multipleImages.toArray(new MultipleImageUploader.MultipleImage[multipleImages.size()]));

        }
        else {

            savePostInFirebase();
        }
    }

    @Override
    public void onSuccess(ArrayList<Image> images) {

        savePostInFirebase();
    }

    public void deletePost() {

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/posts/" + mPost.key, null);
        childUpdates.put("/user-posts/" + mPost.userID + "/" + mPost.key, null);
        childUpdates.put("/likes/posts/" + mPost.key, null);

        mDatabaseReference.updateChildren(childUpdates);
    }

    protected void savePostInFirebase() {

        if( mPost.key == null ) {

            mPost.key = mDatabaseReference.child("posts").push().getKey();
        }

        if( mPost.images != null ) {

            int size = mPost.images.size();

            for( int index = 0; index < size; index++ ) {

                Image image = mPost.images.get(index);

                if (image.id == null || image.id.isEmpty()) {

                    image.id = mDatabaseReference.child("posts").child(mPost.key).child("images").push().getKey();
                }
            }
        }

        Map<String, Object> postValues = mPost.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/posts/" + mPost.key, postValues);
        childUpdates.put("/user-posts/" + mPost.userID + "/" + mPost.key, postValues);

        if( mPost.species != null ) {

            for (Specie specie : mPost.species) {

                childUpdates.put("/post-search-index/" + specie.name.toLowerCase() + "/" + mPost.key, true);
            }
        }

        if( mPost.fishingMethods != null ) {

            for (FishingMethod method : mPost.fishingMethods) {

                childUpdates.put("/post-search-index/" + method.name.toLowerCase() + "/" + mPost.key, true);
            }
        }

        if( mPost.fishingRegions != null ) {

            for (FishingRegion region : mPost.fishingRegions) {

                childUpdates.put("/post-search-index/" + region.name.toLowerCase() + "/" + mPost.key, true);
            }
        }

        mDatabaseReference.updateChildren(childUpdates);
    }

    public static DatabaseReference getPostsDatabaseReference() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");

        return databaseReference;
    }

    public static DatabaseReference getUserPostsDatabaseReference(String uid) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user-posts").child(uid);

        return databaseReference;
    }

    protected StorageReference getHighResImageStorageReference(String imageName, String uid) {

        StorageReference storageReference = mStorageReference.child("images/" + uid + "/posts/high_res/" + imageName);

        return storageReference;
    }

    protected StorageReference getLowResImageStorageReference(String imageName, String uid) {

        StorageReference storageReference = mStorageReference.child("images/" + uid + "/posts/low_res/" + imageName);

        return storageReference;
    }

    protected StorageReference getMidResImageStorageReference(String imageName, String uid) {

        StorageReference storageReference = mStorageReference.child("images/" + uid + "/posts/mid_res/" + imageName);

        return storageReference;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        mPost = dataSnapshot.getValue(Post.class);

        if( mPost == null )
            mPost = new Post();

        if( mPostValueEventListeners != null ) {

            for (PostValueEventListener valueEventListener: mPostValueEventListeners) {

                valueEventListener.onDataChange(mPost);
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void addPostValueEventListener(PostValueEventListener valueEventListener) {

        if( mPostValueEventListeners == null ) {

            mPostValueEventListeners = new ArrayList<>();
        }
        else if( mPostValueEventListeners.contains(valueEventListener) ) {

            return;
        }

        mPostValueEventListeners.add(valueEventListener);

        if( mPost != null )
            valueEventListener.onDataChange(mPost);
    }
}
