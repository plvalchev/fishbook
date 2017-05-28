package com.valchev.plamen.fishbook.global;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.valchev.plamen.fishbook.models.FishingMethod;
import com.valchev.plamen.fishbook.models.FishingRegion;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.Post;
import com.valchev.plamen.fishbook.models.Specie;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;
import com.valchev.plamen.fishbook.utils.FirebaseStorageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 7.5.2017 г..
 */

public class FishbookPost extends FishbookValueEventListener<Post>
        implements OnSuccessListener<ArrayList<Image>>, ValueChangeListener<ArrayList<Image>> {

    private FishbookImagesEventListener imagesEventListener;

    public FishbookPost(Query query) {

        this(query, null);
    }

    public FishbookPost(Query query, ValueChangeListener<Post> valueChangeListener) {

        super(query, valueChangeListener);

        String postKey = query.getRef().getKey();

        imagesEventListener =
                new FishbookImagesEventListener(FirebaseDatabaseUtils.getPostImagesDatabaseReference(postKey), this);
    }

//    public ArrayList<Image> getImages() {
//
//        ArrayList<Image> images = null;
//
//        HashMap<String, FishbookValueEventListener<Image>> fishbookValueEventListenerHashMap = imageChildEventListener.getFishbookValueEventListeners();
//
//        if( fishbookValueEventListenerHashMap != null ) {
//
//            for (FishbookValueEventListener<Image> imageListener: fishbookValueEventListenerHashMap.values()) {
//
//                if( imageListener.getValue() == null )
//                    continue;
//
//                if( images == null ) {
//
//                    images = new ArrayList<>();
//                }
//
//                images.add(imageListener.getValue());
//            }
//        }
//
//        return images;
//    }

    @Override
    public void update(Post post) {

        setValue(post);

        ArrayList<MultipleImageUploader.MultipleImage> multipleImages = null;

        if( post.images != null ) {

            for (Image image : post.images) {

                if( image.isUploaded() ) {

                    continue;
                }

                if( multipleImages == null ) {

                    multipleImages = new ArrayList<>();
                }

                StorageReference storageReference = FirebaseStorageUtils.getUserPostsImagesStorageReference(post.userID);
                MultipleImageUploader.MultipleImage multipleImage = new MultipleImageUploader.MultipleImage(image, storageReference);

                multipleImages.add(multipleImage);
            }
        }

        if( multipleImages != null ) {

            MultipleImageUploader multipleImageUploader = new MultipleImageUploader(this);

            multipleImageUploader.execute(multipleImages.toArray(new MultipleImageUploader.MultipleImage[multipleImages.size()]));

        }
        else {

            Map<String, Object> postImages = new HashMap<>();

            if( post.images != null ) {

                int size = post.images.size();

                for( int index = size - 1; index >= 0; index-- ) {

                    Image image = post.images.get(index);

                    if (image.id == null || image.id.isEmpty()) {

                        image.id = FirebaseDatabaseUtils.getPostImagesDatabaseReference(getKey()).push().getKey();
                    }

                    postImages.put(image.id, image.toMap());

                    if( index > 3 )
                        post.images.set(index, null);
                }
            }

            post.imagesCount = postImages.size();

            Map<String, Object> postValues = post.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put("/images/posts/" + getKey(), postImages);
            childUpdates.put("/posts/" + getKey(), postValues);
            childUpdates.put("/user-posts/" + post.userID + "/" + getKey(), postValues);

            if( post.species != null ) {

                for (Specie specie : post.species) {

                    childUpdates.put("/post-search-index/" + specie.name.toLowerCase() + "/" + getKey(), true);
                }
            }

            if( post.fishingMethods != null ) {

                for (FishingMethod method : post.fishingMethods) {

                    childUpdates.put("/post-search-index/" + method.name.toLowerCase() + "/" + getKey(), true);
                }
            }

            if( post.fishingRegions != null ) {

                for (FishingRegion region : post.fishingRegions) {

                    childUpdates.put("/post-search-index/" + region.name.toLowerCase() + "/" + getKey(), true);
                }
            }

            FirebaseDatabaseUtils.getDatabaseReference().updateChildren(childUpdates);
        }
    }

    @Override
    public void onSuccess(ArrayList<Image> images) {

        update(getValue());
    }

    public static void deleteCurrentUserPost(String postKey) {

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/posts/" + postKey, null);
        childUpdates.put("/user-posts/" + FishbookUser.getCurrentUser().getUid() + "/" + postKey, null);
        childUpdates.put("/likes/posts/" + postKey, null);

        FirebaseDatabaseUtils.getDatabaseReference().updateChildren(childUpdates);
    }

    @Override
    public void delete() {

        Post post = getValue();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/posts/" + getKey(), null);
        childUpdates.put("/user-posts/" + post.userID + "/" + getKey(), null);
        childUpdates.put("/likes/posts/" + getKey(), null);

        FirebaseDatabaseUtils.getDatabaseReference().updateChildren(childUpdates);
    }

    @Override
    public void onChange(ArrayList<Image> newData) {

        Post post = getValue();

        if( post == null ) {

            return;
        }

        post.images = newData;

        triggerChange();
    }

    public void cleanUp() {

        super.cleanUp();

        imagesEventListener.cleanUp();
    }
}
