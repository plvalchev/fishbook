package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.widget.IconButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.Post;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;

/**
 * Created by admin on 24.5.2017 г..
 */

public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private class AuthorValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if( mPost.userID.compareToIgnoreCase(dataSnapshot.getKey()) != 0 ) {
                return;
            }

            mUserData = dataSnapshot.getValue(User.class);

            if( mUserData == null )
                mUserData = new User();

            bindUserData();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    protected LinearLayout mRecyclerViewLayout;
    protected RecyclerView mMainRecyclerView;
    protected RecyclerView mSubRecyclerView;
    protected ImageGridRecyclerViewAdapter mMainRecyclerViewAdapter;
    protected ImageGridRecyclerViewAdapter mSubRecyclerViewAdapter;
    protected StaggeredGridLayoutManager mMainStaggeredGridLayoutManager;
    protected StaggeredGridLayoutManager mSubStaggeredGridLayoutManager;
    protected SimpleDraweeView mProfilePicture;
    protected TextView mDisplayName;
    protected TextView mPostDate;
    protected ExpandableTextView mDescription;
    protected Post mPost;
    protected DatabaseReference mUserDatabaseReference;
    protected User mUserData;
    protected ImageButton mBottomSheetButton;
    protected LinearLayout mUserInfoLayout;
    protected AuthorValueEventListener mAuthorValueEventListener;
    protected SocialPaneController mSocialPaneController;
    protected String key;
    public PostBottomSheetDialogFragment mPostBottomSheetDialogFragment;
    public FishbookActivity mActivity;

    public FeedViewHolder(View itemView) {

        super(itemView);

        mMainRecyclerView = (RecyclerView) itemView.findViewById(R.id.main_recycler_view);
        mSubRecyclerView = (RecyclerView) itemView.findViewById(R.id.sub_recycler_view);
        mProfilePicture = (SimpleDraweeView) itemView.findViewById(R.id.profile_picture);
        mDisplayName = (TextView) itemView.findViewById(R.id.display_name);
        mPostDate = (TextView) itemView.findViewById(R.id.post_date);
        mDescription = (ExpandableTextView) itemView.findViewById(R.id.post_description).findViewById(R.id.expand_text_view);
        mRecyclerViewLayout = (LinearLayout) itemView.findViewById(R.id.recycler_view_layout);
        mBottomSheetButton =  (ImageButton) itemView.findViewById(R.id.bottom_sheet);
        mUserInfoLayout = (LinearLayout) itemView.findViewById(R.id.user_info_layout);

        IconButton likeButton = (IconButton) itemView.findViewById(R.id.like_button);
        IconButton commentButton = (IconButton) itemView.findViewById(R.id.comment_button);
        LinearLayout likeCommentsLayout = (LinearLayout) itemView.findViewById(R.id.like_comments_layout);
        TextView likes = (TextView) itemView.findViewById(R.id.likes);
        TextView comments = (TextView) itemView.findViewById(R.id.comments);

        mSocialPaneController = new SocialPaneController(likeCommentsLayout, likeButton, commentButton, likes, comments, mActivity);

        mMainRecyclerViewAdapter = new ImageGridRecyclerViewAdapter(this, ImageGridRecyclerViewAdapter.INFINITY);
        mSubRecyclerViewAdapter = new ImageGridRecyclerViewAdapter(this, 3);
        mMainStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mSubStaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);

        mMainRecyclerView.setLayoutManager(mMainStaggeredGridLayoutManager);
        mSubRecyclerView.setLayoutManager(mSubStaggeredGridLayoutManager);
        mMainRecyclerView.setAdapter(mMainRecyclerViewAdapter);
        mSubRecyclerView.setAdapter(mSubRecyclerViewAdapter);

        itemView.setOnClickListener(this);
        mRecyclerViewLayout.setOnClickListener(this);
        mMainRecyclerView.setOnClickListener(this);
        mSubRecyclerView.setOnClickListener(this);
        mBottomSheetButton.setOnClickListener(this);
        mUserInfoLayout.setOnClickListener(this);
        mProfilePicture.setOnClickListener(this);

        mAuthorValueEventListener = new AuthorValueEventListener();
    }

    public void bindPost(Post post, String key) {

        this.key = key;

        boolean loadUser = mPost == null || mPost.userID.compareToIgnoreCase(post.userID) != 0;

        mPost = post;

        if( loadUser ) {

            mUserData = new User();

            bindUserData();

            if( mUserDatabaseReference != null ) {

                mUserDatabaseReference.removeEventListener(mAuthorValueEventListener);
            }

            mUserDatabaseReference = FirebaseDatabaseUtils.getUserDatabaseReference(mPost.userID);

            mUserDatabaseReference.addValueEventListener(mAuthorValueEventListener);
        }
        else {

            bindUserData();
        }

        boolean isUserPost = mPost.userID.compareToIgnoreCase(FishbookUser.getCurrentUser().getUid()) == 0;

        mBottomSheetButton.setVisibility(isUserPost ? View.VISIBLE : View.GONE);

        mPostDate.setText(mPost.dateTime);
        mDescription.setText(mPost.description);

        DatabaseReference commentsDatabaseReference = FirebaseDatabaseUtils.getPostCommentsDatabaseReference(this.key);
        DatabaseReference likesDatabaseReference = FirebaseDatabaseUtils.getPostLikesDatabaseReference(this.key);

        mSocialPaneController.setDatabaseReferences(commentsDatabaseReference, likesDatabaseReference, mPost.userID);
        mSocialPaneController.setActivity(mActivity);

        if( mPost.images == null || mPost.images.size() == 0 ) {

            mMainRecyclerViewAdapter.setImageList(null, mPost.images);
            mSubRecyclerViewAdapter.setImageList(null, mPost.images);

            mRecyclerViewLayout.setVisibility(View.GONE);

        } else {

            int size = mPost.images.size();

            ArrayList<Image> mainImageArrayList = new ArrayList<>();
            ArrayList<Image> subImageArrayList = null;

            mainImageArrayList.add(mPost.images.get(0));

            int subRecyclerViewIndex = 1;
            int mainStaggeredGridLayoutManagerOrientation = StaggeredGridLayoutManager.VERTICAL;

            if( size > 1 && size < 4 ) {

                mainImageArrayList.add(mPost.images.get(1));

                subRecyclerViewIndex = 2;
                mainStaggeredGridLayoutManagerOrientation = StaggeredGridLayoutManager.HORIZONTAL;

            }

            mMainStaggeredGridLayoutManager.setOrientation(mainStaggeredGridLayoutManagerOrientation);
            mMainRecyclerViewAdapter.setImageList(null, mainImageArrayList);

            for( int index = subRecyclerViewIndex; index < size; index++ ) {

                if( subImageArrayList == null ) {

                    subImageArrayList = new ArrayList<>();
                }

                subImageArrayList.add(mPost.images.get(index));
            }

            if( mPost.imagesCount != null) {

                for (int index = size; index < mPost.imagesCount; index++) {

                    subImageArrayList.add(null);
                }
            }

            mSubRecyclerViewAdapter.setImageList(null, subImageArrayList);
            mSubRecyclerView.setVisibility(subImageArrayList != null ? View.VISIBLE : View.GONE);

            mMainRecyclerViewAdapter.setShowDeleteButton(false);
            mSubRecyclerViewAdapter.setShowDeleteButton(false);

            mRecyclerViewLayout.setVisibility(View.VISIBLE);
        }
    }

    private void bindUserData() {

        String displayName = null;
        Image profilePicture = new Image();

        if( mUserData != null ) {

            displayName = mUserData.getDisplayName();

            if( mUserData.profilePicture != null ) {

                profilePicture = mUserData.profilePicture;
            }
        }

        mDisplayName.setText(displayName);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(profilePicture.lowResUri))
                .setImageRequest(ImageRequest.fromUri(profilePicture.midResUri))
                .setOldController(mProfilePicture.getController())
                .build();

        mProfilePicture.setController(controller);
    }

    @Override
    public void onClick(View v) {

        if( mPost == null ) {

            return;
        }

        if( v == mBottomSheetButton ) {

            if( mPostBottomSheetDialogFragment == null ) {

                mPostBottomSheetDialogFragment = new PostBottomSheetDialogFragment();
            }

            mPostBottomSheetDialogFragment.setPost(key);

            mPostBottomSheetDialogFragment.show(mActivity.getSupportFragmentManager(), mPostBottomSheetDialogFragment.getTag());

        } else if( v == mUserInfoLayout || v == mProfilePicture ) {

            Intent intent = new Intent(mActivity, UserProfileActivity.class);

            if( mPost.userID.compareToIgnoreCase(FishbookUser.getCurrentUser().getUid()) != 0 ) {

                Bundle bundle = new Bundle();

                bundle.putSerializable("userData", mUserData);
                bundle.putString("uid", mPost.userID);

                intent.putExtras(bundle);
            }

            mActivity.startActivity(intent);

        } else {

            if( mPost.images == null || mPost.images.size() <= 0 ) {

                return;
            }

            mActivity.showImages(FirebaseDatabaseUtils.getPostImagesDatabaseReference(key));
        }
    }
}