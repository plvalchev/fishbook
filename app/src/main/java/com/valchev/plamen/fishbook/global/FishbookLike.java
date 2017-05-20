package com.valchev.plamen.fishbook.global;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valchev.plamen.fishbook.models.Post;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 20.5.2017 г..
 */

public class FishbookLike  {

    protected DatabaseReference mDatabaseReference;

    public FishbookLike() {

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void like(DatabaseReference databaseReference) {

        HashMap<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(FishbookUser.getCurrentUser().getUid(), true);

        databaseReference.updateChildren(childUpdates);
    }

    public void dislike(DatabaseReference databaseReference) {

        HashMap<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(FishbookUser.getCurrentUser().getUid(), null);

        databaseReference.updateChildren(childUpdates);
    }

    public void likePost(Post post) {

        DatabaseReference postLikesDatabaseReference = getPostLikesDatabaseReference().child(post.key);

        HashMap<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(FishbookUser.getCurrentUser().getUid(), true);

        postLikesDatabaseReference.updateChildren(childUpdates);
    }

    public void dislikePost(Post post) {

        DatabaseReference postLikesDatabaseReference = getPostLikesDatabaseReference().child(post.key);

        HashMap<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(FishbookUser.getCurrentUser().getUid(), null);

        postLikesDatabaseReference.updateChildren(childUpdates);
    }

    public static DatabaseReference getPostLikesDatabaseReference(String key) {

        return FirebaseDatabase.getInstance().getReference().child("likes").child("posts").child(key);
    }

    public static DatabaseReference getImageLikesDatabaseReference(String key) {

        return FirebaseDatabase.getInstance().getReference().child("likes").child("images").child(key);
    }

    private DatabaseReference getLikesDatabaseReference() {

        return mDatabaseReference.child("likes");
    }

    private DatabaseReference getPostLikesDatabaseReference() {

        DatabaseReference likesDatabaseReference = getLikesDatabaseReference();

        return likesDatabaseReference.child("posts");
    }


}
