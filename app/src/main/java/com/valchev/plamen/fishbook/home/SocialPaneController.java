package com.valchev.plamen.fishbook.home;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.widget.IconButton;
import com.joanzapata.iconify.widget.IconTextView;
import com.valchev.plamen.fishbook.models.Event;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.PersonalInformation;
import com.valchev.plamen.fishbook.models.User;

import java.util.HashMap;

/**
 * Created by admin on 20.5.2017 г..
 */

public class SocialPaneController implements  View.OnClickListener {

    private class CommentsChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            mCommentsCount++;

            setCommentsText();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

            mCommentsCount--;

            setCommentsText();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class LikesChildEventListener implements ChildEventListener {

        private class UserNameChildEventListener implements ValueEventListener {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                PersonalInformation personalInformation = dataSnapshot.getValue(PersonalInformation.class);
                User user = new User();

                user.personalInformation = personalInformation;

                String userName = user.getDisplayName();

                setLikeText(userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }

        private UserNameChildEventListener userNameChildEventListener;
        private DatabaseReference userNameDatabaseReference;

        public LikesChildEventListener() {

            userNameChildEventListener = new UserNameChildEventListener();
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            mLikesCount++;

            String userID = dataSnapshot.getKey();

            if( userNameDatabaseReference != null ) {

                userNameDatabaseReference.removeEventListener(userNameChildEventListener);
            }

            if( userID.compareToIgnoreCase(FishbookUser.getCurrentUser().getUid()) == 0 ) {

                isLikedByCurrentUser = true;

                if( mLikeButton != null )
                    mLikeButton.setText("{fa-thumbs-down} Dislike");

                setLikeText("You");

            } else {

                setLikeText("John doe");

                userNameDatabaseReference = FirebaseDatabaseUtils.getUserDatabaseReference(userID).child("personalInformation");

                userNameDatabaseReference.addValueEventListener(userNameChildEventListener);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

            String userID = dataSnapshot.getKey();

            mLikesCount--;

            if( userID.compareToIgnoreCase(FishbookUser.getCurrentUser().getUid()) == 0 ) {

                if( mLikeButton != null )
                    mLikeButton.setText("{fa-thumbs-up} Like");

                isLikedByCurrentUser = false;
            }

            setLikeText(mPreviousLikedUserName);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private LinearLayout mLikeCommentsLayout;
    private IconButton mLikeButton;
    private IconButton mCommentButton;
    private TextView mLikes;
    private TextView mComments;
    private LikesChildEventListener mLikesChildEventListener;
    private int mLikesCount;
    private String mPreviousLikedUserName;
    private String mLastLikedUserName;
    private boolean isLikedByCurrentUser;
    private CommentsChildEventListener mCommentsChildEventListener;
    private int mCommentsCount;
    private DatabaseReference mLikesDatabaseReference;
    private DatabaseReference mCommentsDatabaseReference;
    private String ownerKey;
    private CommentsBottomSheetDialogFragment mCommentsBottomSheetDialogFragment;
    private AppCompatActivity mActivity;

    public SocialPaneController(LinearLayout likeCommentsLayout, IconButton likeButton,
                                IconButton commentButton, TextView likes, TextView comments, AppCompatActivity activity) {

        mLikesCount = 0;
        mCommentsCount = 0;
        isLikedByCurrentUser = false;

        mLikeCommentsLayout = likeCommentsLayout;
        mLikeButton = likeButton;
        mCommentButton = commentButton;
        mLikes = likes;
        mComments = comments;
        mActivity = activity;

        if( mLikeButton != null ) {

            mLikeButton.setOnClickListener(this);
        }

        if( mCommentButton != null ) {

            mCommentButton.setOnClickListener(this);
        }

        mLikesChildEventListener = new LikesChildEventListener();
        mCommentsChildEventListener = new CommentsChildEventListener();
    }

    @Override
    public void onClick(View v) {

        if( v == mLikeButton ) {

            Event event = new Event();

            event.eventType = mLikesDatabaseReference.getParent().getParent().getKey();
            event.objectType = mLikesDatabaseReference.getParent().getKey();
            event.objectKey = mLikesDatabaseReference.getKey();
            event.userID = FishbookUser.getCurrentUser().getUid();
            event.eventsCount = mLikesCount + 1;

            DatabaseReference databaseReference = FirebaseDatabaseUtils.getDatabaseReference();
            HashMap<String, Object> childUpdates = new HashMap<>();

            String likeChild = event.eventType + "/" + event.objectType + "/" + event.objectKey + "/" + event.userID;
            String eventChild = "user-events/" + ownerKey + "/" + event.eventType + "|" + event.objectType + "|" + event.objectKey;

            childUpdates.put(likeChild, isLikedByCurrentUser ? null : true);
            childUpdates.put(eventChild, isLikedByCurrentUser ? null : event);

            databaseReference.updateChildren(childUpdates);

        } else if( v == mCommentButton  ) {

            if( mCommentsBottomSheetDialogFragment == null )
                mCommentsBottomSheetDialogFragment = new CommentsBottomSheetDialogFragment();

            mCommentsBottomSheetDialogFragment.setDatabaseReferences(mCommentsDatabaseReference, mLikesDatabaseReference, ownerKey);

            mCommentsBottomSheetDialogFragment.show(mActivity.getSupportFragmentManager(), mCommentsBottomSheetDialogFragment.getTag());
        }
    }

    public void setDatabaseReferences(DatabaseReference commentsDatabaseReference,
                                      DatabaseReference likesDatabaseReference,
                                      String ownerKey) {

        this.ownerKey = ownerKey;

        mCommentsCount = 0;

        setCommentsText();

        if( mCommentsDatabaseReference != null ) {

            mCommentsDatabaseReference.removeEventListener(mCommentsChildEventListener);
        }

        mCommentsDatabaseReference = commentsDatabaseReference;

        if( mCommentsDatabaseReference != null ) {

            mCommentsDatabaseReference.addChildEventListener(mCommentsChildEventListener);
        }

        if( mLikesDatabaseReference != null ) {

            mLikesDatabaseReference.removeEventListener(mLikesChildEventListener);
        }

        mLikesDatabaseReference = likesDatabaseReference;

        mLikesCount = 0;

        if( mLikeButton != null )
            mLikeButton.setText("{fa-thumbs-up} Like");

        setLikeText("John doe");

        if( mLikesDatabaseReference != null )
            mLikesDatabaseReference.addChildEventListener(mLikesChildEventListener);
    }

    public void setActivity(AppCompatActivity activity) {

        this.mActivity = activity;
    }

    private void setLikeText(String userName) {

        mPreviousLikedUserName = mLastLikedUserName;
        mLastLikedUserName = userName;

        if( mLikes == null )
            return;

        if( isLikedByCurrentUser )
            userName = "You";

        boolean isIconTextView = mLikes instanceof IconTextView;
        String prefix = isIconTextView ? "{fa-thumbs-up} " : "";

        switch (mLikesCount) {
            case 0:
                mLikes.setText("Be the first to like this");
                break;

            case 1:
                mLikes.setText(prefix + userName + " liked this");
                break;

            case 2:
                mLikes.setText(prefix + userName + " and 1 others");
                break;

            default:
                mLikes.setText(prefix + userName + " and " + String.valueOf(mLikesCount - 1) + " others liked this");
                break;
        }
    }

    private void setCommentsText() {

        if( mComments == null )
            return;

        switch (mCommentsCount) {

            case 0:
                mComments.setText(null);
                break;

            case 1:
                mComments.setText("1 Comment");
                break;

            default:
                mComments.setText(mCommentsCount + " Comments");
                break;
        }
    }
}
