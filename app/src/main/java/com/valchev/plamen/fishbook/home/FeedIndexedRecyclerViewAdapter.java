package com.valchev.plamen.fishbook.home;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.Query;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.models.Post;

/**
 * Created by admin on 24.5.2017 г..
 */

public class FeedIndexedRecyclerViewAdapter extends FirebaseIndexRecyclerAdapter<Post, FeedViewHolder> {

    private FishbookActivity mActivity;

    public FeedIndexedRecyclerViewAdapter(Query key, Query data, FishbookActivity activity) {

        super(Post.class, R.layout.feed_view, FeedViewHolder.class, key, data);

        mActivity = activity;
    }

    @Override
    protected void populateViewHolder(FeedViewHolder viewHolder, Post model, int position) {

        viewHolder.mActivity = mActivity;

        viewHolder.bindPost(model, getRef(position).getKey());
    }
}
