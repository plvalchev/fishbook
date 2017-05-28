package com.valchev.plamen.fishbook.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valchev.plamen.fishbook.global.FishbookUser;

/**
 * Created by admin on 27.5.2017 г..
 */

public class FirebaseDatabaseUtils {

    public static DatabaseReference getDatabaseReference() {

        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getPostLikesDatabaseReference(String postKey) {

        DatabaseReference postsLikesDatabaseReference = getPostsLikesDatabaseReference();

        return postsLikesDatabaseReference.child(postKey);
    }

    public static DatabaseReference getPostsLikesDatabaseReference() {

        DatabaseReference likesDatabaseReference = getLikesDatabaseReference();

        return likesDatabaseReference.child("posts");
    }

    public static DatabaseReference getImageLikesDatabaseReference(String imageKey) {

        DatabaseReference imagesLikesDatabaseReference = getImagesLikesDatabaseReference();

        return imagesLikesDatabaseReference.child(imageKey);
    }

    public static DatabaseReference getImagesLikesDatabaseReference() {

        DatabaseReference likesDatabaseReference = getLikesDatabaseReference();

        return likesDatabaseReference.child("images");
    }

    public static DatabaseReference getLikesDatabaseReference() {

        DatabaseReference databaseReference = getDatabaseReference();

        return databaseReference.child("likes");
    }

    public static DatabaseReference getPostCommentsDatabaseReference(String postKey) {

        DatabaseReference postsCommentsDatabaseReference = getPostsCommentsDatabaseReference();

        return postsCommentsDatabaseReference.child(postKey);
    }

    public static DatabaseReference getPostsCommentsDatabaseReference() {

        DatabaseReference commentsDatabaseReference = getCommentsDatabaseReference();

        return commentsDatabaseReference.child("posts");
    }

    public static DatabaseReference getImageCommentsDatabaseReference(String imageKey) {

        DatabaseReference imagesCommentsDatabaseReference = getImagesCommentsDatabaseReference();

        return imagesCommentsDatabaseReference.child(imageKey);
    }

    public static DatabaseReference getImagesCommentsDatabaseReference() {

        DatabaseReference commentsDatabaseReference = getCommentsDatabaseReference();

        return commentsDatabaseReference.child("images");
    }

    public static DatabaseReference getCommentsDatabaseReference() {

        DatabaseReference databaseReference = getDatabaseReference();

        return databaseReference.child("comments");
    }

    public static DatabaseReference getCurrentUserUnreadMessagesDatabaseReference(String chatKey) {

        FishbookUser fishbookUser = FishbookUser.getCurrentUser();

        return getUserUnreadMessagesDatabaseReference(chatKey, fishbookUser.getUid());
    }

    public static DatabaseReference getUserUnreadMessagesDatabaseReference(String chatKey, String userKey) {

        DatabaseReference chatUnreadMessagesDatabaseReference = getChatUnreadMessagesDatabaseReference(chatKey);

        return chatUnreadMessagesDatabaseReference.child(userKey);
    }

    public static DatabaseReference getChatUnreadMessagesDatabaseReference(String chatKey) {

        DatabaseReference unreadMessagesDatabaseReference = getUnreadMessagesDatabaseReference();

        return unreadMessagesDatabaseReference.child(chatKey);
    }

    public static DatabaseReference getUnreadMessagesDatabaseReference() {

        DatabaseReference databaseReference = getDatabaseReference();

        return databaseReference.child("unread-messages");
    }

    public static DatabaseReference getChatMessagesDatabaseReference(String chatKey) {

        DatabaseReference messagesDatabaseReference = getMessagesDatabaseReference();

        return messagesDatabaseReference.child(chatKey);
    }

    public static DatabaseReference getMessagesDatabaseReference() {

        DatabaseReference databaseReference = getDatabaseReference();

        return databaseReference.child("messages");
    }

    public static DatabaseReference getCurrentUserChatsDatabaseReference() {

        FishbookUser fishbookUser = FishbookUser.getCurrentUser();

        return getUserChatsDatabaseReference(fishbookUser.getUid());
    }

    public static DatabaseReference getUserChatsDatabaseReference(String userKey) {

        DatabaseReference userChatsDatabaseReference = getUserChatsDatabaseReference();

        return userChatsDatabaseReference.child(userKey);
    }

    public static DatabaseReference getUserChatsDatabaseReference() {

        DatabaseReference databaseReference = getDatabaseReference();

        return databaseReference.child("user-chats");
    }

    public static DatabaseReference getPostDatabaseReference(String postKey) {

        DatabaseReference postsDatabaseReference = getPostsDatabaseReference();

        return postsDatabaseReference.child(postKey);
    }

    public static DatabaseReference getPostsDatabaseReference() {

        DatabaseReference databaseReference = getDatabaseReference();

        return databaseReference.child("posts");
    }

    public static DatabaseReference getUserPostsDatabaseReference(String userKey) {

        DatabaseReference userPostsDatabaseReference = getUserPostsDatabaseReference();

        return userPostsDatabaseReference.child(userKey);
    }

    public static DatabaseReference getUserPostsDatabaseReference() {

        DatabaseReference databaseReference = getDatabaseReference();

        return databaseReference.child("user-posts");
    }

    public static DatabaseReference getUserDatabaseReference(String userKey) {

        DatabaseReference userDatabaseReference = getUserDatabaseReference();

        return userDatabaseReference.child(userKey);
    }

    public static DatabaseReference getUserDatabaseReference() {

        DatabaseReference databaseReference = getDatabaseReference();

        return databaseReference.child("user");
    }

    public static DatabaseReference getPostImagesDatabaseReference(String postKey) {

        DatabaseReference postsImagesDatabaseReference = getPostsImagesDatabaseReference();

        return postsImagesDatabaseReference.child(postKey);
    }

    public static DatabaseReference getPostsImagesDatabaseReference() {

        DatabaseReference imagesDatabaseReference = getImagesDatabaseReference();

        return imagesDatabaseReference.child("posts");
    }

    public static DatabaseReference getUserProfileImagesDatabaseReference(String userKey) {

        DatabaseReference profileImagesDatabaseReference = getProfileImagesDatabaseReference();

        return profileImagesDatabaseReference.child(userKey);
    }

    public static DatabaseReference getProfileImagesDatabaseReference() {

        DatabaseReference imagesDatabaseReference = getImagesDatabaseReference();

        return imagesDatabaseReference.child("profile-pictures");
    }

    public static DatabaseReference getUserCoverImagesDatabaseReference(String userKey) {

        DatabaseReference coverImagesDatabaseReference = getCoverImagesDatabaseReference();

        return coverImagesDatabaseReference.child(userKey);
    }

    public static DatabaseReference getCoverImagesDatabaseReference() {

        DatabaseReference imagesDatabaseReference = getImagesDatabaseReference();

        return imagesDatabaseReference.child("cover-photos");
    }

    public static DatabaseReference getImagesDatabaseReference() {

        DatabaseReference databaseReference = getDatabaseReference();

        return databaseReference.child("images");
    }

    public static DatabaseReference getCurrentUserEventsDatabaseReference() {

        FishbookUser fishbookUser = FishbookUser.getCurrentUser();

        return getUserEventsDatabaseReference(fishbookUser.getUid());
    }

    public static DatabaseReference getUserEventsDatabaseReference(String userKey) {

        DatabaseReference eventsDatabaseReference = getEventsDatabaseReference();

        return eventsDatabaseReference.child(userKey);
    }

    public static DatabaseReference getEventsDatabaseReference() {

        DatabaseReference databaseReference = getDatabaseReference();

        return databaseReference.child("user-events");
    }
}
