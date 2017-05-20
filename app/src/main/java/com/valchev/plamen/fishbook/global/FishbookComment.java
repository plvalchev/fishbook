package com.valchev.plamen.fishbook.global;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by admin on 20.5.2017 г..
 */

public class FishbookComment {

    public static DatabaseReference getPostCommentsDatabaseReference(String key) {

        return FirebaseDatabase.getInstance().getReference().child("comments").child("posts").child(key);
    }

    public static DatabaseReference getImageCommentsDatabaseReference(String key) {

        return FirebaseDatabase.getInstance().getReference().child("comments").child("images").child(key);
    }
}
