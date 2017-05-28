package com.valchev.plamen.fishbook.home;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.models.Post;

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

        viewHolder.bindPost(model, getRef(position).getKey());

    }
}
