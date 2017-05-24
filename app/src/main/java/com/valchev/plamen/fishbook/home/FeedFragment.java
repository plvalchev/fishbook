package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookPost;

/**
 * Created by admin on 29.4.2017 г..
 */

public class FeedFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    protected RecyclerView mRecyclerView;
    protected FeedRecyclerViewAdapter mFeedRecyclerViewAdapter;
    protected FeedIndexedRecyclerViewAdapter mFeedIndexedRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        FishbookActivity activity = (FishbookActivity)getActivity();

        if( activity instanceof MainActivity ) {

            ((MainActivity) activity).showFAB(true);
        }

        mFeedRecyclerViewAdapter = new FeedRecyclerViewAdapter(FishbookPost.getPostsDatabaseReference(), activity);
        mRecyclerView.setAdapter(mFeedRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if( mFeedRecyclerViewAdapter != null )
            mFeedRecyclerViewAdapter.cleanup();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if( mFeedIndexedRecyclerViewAdapter != null ) {

            mFeedIndexedRecyclerViewAdapter.cleanup();
        }

        String lowerCaseQuery = query.toLowerCase();
        Query keyQuery = FirebaseDatabase.getInstance().getReference()
                .child("post-search-index").child(lowerCaseQuery);
        FishbookActivity activity = (FishbookActivity)getActivity();

        mFeedIndexedRecyclerViewAdapter = new FeedIndexedRecyclerViewAdapter(keyQuery, FishbookPost.getPostsDatabaseReference(), activity);
        mRecyclerView.setAdapter(mFeedIndexedRecyclerViewAdapter);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    @Override
    public boolean onClose() {

        if( mFeedIndexedRecyclerViewAdapter != null ) {

            mFeedIndexedRecyclerViewAdapter.cleanup();
        }

        mRecyclerView.setAdapter(mFeedRecyclerViewAdapter);

        return false;
    }
}
