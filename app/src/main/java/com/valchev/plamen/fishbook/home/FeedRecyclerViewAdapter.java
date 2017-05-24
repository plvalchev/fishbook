package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.widget.IconButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookComment;
import com.valchev.plamen.fishbook.global.FishbookLike;
import com.valchev.plamen.fishbook.global.FishbookPost;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.Post;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;

/**
 * Created by admin on 7.5.2017 г..
 */

public class FeedRecyclerViewAdapter extends FirebaseRecyclerAdapter<Post, FeedViewHolder> {

    private FishbookActivity mActivity;

    public FeedRecyclerViewAdapter(Query query, FishbookActivity activity) {
        super(Post.class, R.layout.feed_view, FeedViewHolder.class, query);

        mActivity = activity;
    }

    @Override
    protected void populateViewHolder(FeedViewHolder viewHolder, Post model, int position) {

        viewHolder.mActivity = mActivity;

        viewHolder.bindPost(model);

    }
}
