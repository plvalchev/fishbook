package com.valchev.plamen.fishbook.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.Comment;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.User;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;

/**
 * Created by admin on 20.5.2017 г..
 */

public class CommentRecyclerViewAdapter extends FirebaseRecyclerAdapter<Comment, CommentRecyclerViewAdapter.CommentViewHolder> {

    public static class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        private class AuthorValueEventListener implements ValueEventListener {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( mComment.userID.compareToIgnoreCase(dataSnapshot.getKey()) != 0 ) {
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

        protected Comment mComment;
        protected SimpleDraweeView mProfilePicture;
        protected TextView mDisplayName;
        protected TextView mCommentDate;
        protected ExpandableTextView mContent;
        protected ImageButton mBottomSheetButton;
        protected LinearLayout mUserInfoLayout;
        protected DatabaseReference mUserDatabaseReference;
        protected User mUserData;
        protected AuthorValueEventListener mAuthorValueEventListener;
        public FishbookActivity mActivity;

        public CommentViewHolder(View itemView) {
            super(itemView);

            mProfilePicture = (SimpleDraweeView) itemView.findViewById(R.id.profile_picture);
            mDisplayName = (TextView) itemView.findViewById(R.id.display_name);
            mCommentDate = (TextView) itemView.findViewById(R.id.comment_date);
            mBottomSheetButton =  (ImageButton) itemView.findViewById(R.id.bottom_sheet);
            mUserInfoLayout = (LinearLayout) itemView.findViewById(R.id.user_info_layout);
            mContent = (ExpandableTextView) itemView.findViewById(R.id.content).findViewById(R.id.expand_text_view);

            mAuthorValueEventListener = new AuthorValueEventListener();
        }

        public void bindComment(Comment comment) {

            boolean loadUser = mComment == null || mComment.userID.compareToIgnoreCase(comment.userID) != 0;

            mComment = comment;

            if( loadUser ) {

                mUserData = new User();

                bindUserData();

                if( mUserDatabaseReference != null ) {

                    mUserDatabaseReference.removeEventListener(mAuthorValueEventListener);
                }

                mUserDatabaseReference = FirebaseDatabaseUtils.getUserDatabaseReference(mComment.userID);

                mUserDatabaseReference.addValueEventListener(mAuthorValueEventListener);
            }
            else {

                bindUserData();
            }

            boolean isUserPost = mComment.userID.compareToIgnoreCase(FishbookUser.getCurrentUser().getUid()) == 0;

            mBottomSheetButton.setVisibility(isUserPost ? View.VISIBLE : View.GONE);

            mCommentDate.setText(mComment.dateTime);
            mContent.setText(mComment.content);
        }

        public void bindUserData() {

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

        }
    }

    private FishbookActivity mActivity;

    public CommentRecyclerViewAdapter(Query query, FishbookActivity activity) {

        super(Comment.class, R.layout.comment_viewholder, CommentViewHolder.class, query);

        mActivity = activity;
    }

    @Override
    protected void populateViewHolder(CommentViewHolder viewHolder, Comment model, int position) {

        viewHolder.mActivity = mActivity;
        viewHolder.bindComment(model);
    }

}
