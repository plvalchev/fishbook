package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.valchev.plamen.fishbook.R;

/**
 * Created by admin on 29.4.2017 г..
 */

public class FeedFragment extends Fragment {

    protected RecyclerView mRecyclerView;
    protected FeedRecyclerViewAdapter mFeedRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        FishbookActivity activity = (FishbookActivity)getActivity();

        mFeedRecyclerViewAdapter = new FeedRecyclerViewAdapter(activity);
        mRecyclerView.setAdapter(mFeedRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if( mFeedRecyclerViewAdapter != null )
            mFeedRecyclerViewAdapter.cleanup();
    }
}
