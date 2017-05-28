package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.firebase.database.DatabaseError;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookPost;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.global.ValueChangeListener;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.Post;
import com.valchev.plamen.fishbook.models.User;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;

public class PostPreviewActivity extends FishbookActivity implements ValueChangeListener<Post> {

    protected RecyclerView mRecyclerView;
    protected FishbookUser mFishbookUser;
    protected FishbookPost mFishbookPost;
    protected SimpleDraweeView mProfilePicture;
    protected TextView mDisplayName;
    protected TextView mPostDate;
    protected ExpandableTextView mDescription;
    protected ImagePreviewPostRecyclerViewAdapter mImageLinearRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_preview);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mProfilePicture = (SimpleDraweeView) findViewById(R.id.profile_picture);
        mDisplayName = (TextView) findViewById(R.id.display_name);
        mPostDate = (TextView) findViewById(R.id.post_date);
        mDescription = (ExpandableTextView) findViewById(R.id.post_description).findViewById(R.id.expand_text_view);

        mImageLinearRecyclerViewAdapter = new ImagePreviewPostRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mImageLinearRecyclerViewAdapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String postKey = bundle.getString("key");

        bindPost(new Post());
        bindUser(new User());

        mFishbookPost = new FishbookPost(FirebaseDatabaseUtils.getPostDatabaseReference(postKey), this);
    }

    private void bindPost(Post post) {

        mPostDate.setText(post.dateTime);
        mDescription.setText(post.description);

        mImageLinearRecyclerViewAdapter.setImageList(null, post.images);
    }

    private void bindUser(User userData) {

        String displayName = null;
        Image profilePicture = new Image();

        if( userData != null ) {

            displayName = userData.getDisplayName();

            if( userData.profilePicture != null ) {

                profilePicture = userData.profilePicture;
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
    public void onChange(Post newData) {

        if( (mFishbookUser == null && newData != null && newData.userID != null) ||
                mFishbookUser.getUid() != newData.userID ) {

            FishbookUser currentUser = FishbookUser.getCurrentUser();

            if( currentUser.getUid().compareToIgnoreCase(newData.userID) == 0 ) {

                mFishbookUser = currentUser;
            }
            else {

                mFishbookUser = new FishbookUser(newData.userID);

                mFishbookUser.reloadUserInBackground();
            }

            mFishbookUser.addUserValueEventListener(new FishbookUser.UserValueEventListener() {

                @Override
                public void onDataChange(User user) {

                    bindUser(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        bindPost(newData);
    }
}
