package com.valchev.plamen.fishbook.home;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookPost;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.Post;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;

/**
 * Created by admin on 7.5.2017 г..
 */

public class FeedRecyclerViewAdapter extends FirebaseRecyclerAdapter<Post, FeedRecyclerViewAdapter.FeedViewHolder> {

    public static class FeedViewHolder extends RecyclerView.ViewHolder implements ValueEventListener {

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


        public FeedViewHolder(View itemView) {

            super(itemView);

            mMainRecyclerView = (RecyclerView) itemView.findViewById(R.id.main_recycler_view);
            mSubRecyclerView = (RecyclerView) itemView.findViewById(R.id.sub_recycler_view);
            mProfilePicture = (SimpleDraweeView) itemView.findViewById(R.id.profile_picture);
            mDisplayName = (TextView) itemView.findViewById(R.id.display_name);
            mPostDate = (TextView) itemView.findViewById(R.id.post_date);
            mDescription = (ExpandableTextView) itemView.findViewById(R.id.post_description).findViewById(R.id.expand_text_view);

            mMainRecyclerViewAdapter = new ImageGridRecyclerViewAdapter(null, ImageGridRecyclerViewAdapter.INFINITY);
            mSubRecyclerViewAdapter = new ImageGridRecyclerViewAdapter(null, 3);
            mMainStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mSubStaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);

            mMainRecyclerView.setLayoutManager(mMainStaggeredGridLayoutManager);
            mSubRecyclerView.setLayoutManager(mSubStaggeredGridLayoutManager);
            mMainRecyclerView.setAdapter(mMainRecyclerViewAdapter);
            mSubRecyclerView.setAdapter(mSubRecyclerViewAdapter);
        }

        public void bindPost(Post post) {

            boolean loadUser = mPost == null || mPost.userID.compareToIgnoreCase(post.userID) != 0;

            mPost = post;

            if( loadUser ) {

                if( mUserDatabaseReference != null ) {

                    mUserDatabaseReference.removeEventListener(this);
                }

                mUserDatabaseReference = FishbookUser.getUserDatabaseReference(mPost.userID);

                mUserDatabaseReference.addValueEventListener(this);
            }
            else {

                bindUserData();
            }

            mPostDate.setText(mPost.dateTime);
            mDescription.setText(mPost.description);

            if( mPost.images == null || mPost.images.size() == 0 ) {

                mMainRecyclerViewAdapter.setImageList(null, mPost.images);
                mSubRecyclerViewAdapter.setImageList(null, mPost.images);

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

                mSubRecyclerViewAdapter.setImageList(null, subImageArrayList);
                mSubRecyclerView.setVisibility(subImageArrayList != null ? View.VISIBLE : View.GONE);

                mMainRecyclerViewAdapter.setShowDeleteButton(false);
                mSubRecyclerViewAdapter.setShowDeleteButton(false);
            }
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if( mPost.userID.compareToIgnoreCase(dataSnapshot.getKey()) != 0 ) {
                return;
            }

            mUserData = dataSnapshot.getValue(User.class);

            bindUserData();
        }

        private void bindUserData() {

            mDisplayName.setText(mUserData.getDisplayName());

            if( mUserData.profilePictures != null && mUserData.profilePictures.size() > 0 ) {

                Image profilePicture = mUserData.profilePictures.get(0);

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setLowResImageRequest(ImageRequest.fromUri(profilePicture.lowResUri))
                        .setImageRequest(ImageRequest.fromUri(profilePicture.midResUri))
                        .setOldController(mProfilePicture.getController())
                        .build();

                mProfilePicture.setController(controller);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public FeedRecyclerViewAdapter() {
        super(Post.class, R.layout.post_view, FeedViewHolder.class, FishbookPost.getPostsDatabaseReference());
    }

    @Override
    protected void populateViewHolder(FeedViewHolder viewHolder, Post model, int position) {

        viewHolder.bindPost(model);

    }

}
