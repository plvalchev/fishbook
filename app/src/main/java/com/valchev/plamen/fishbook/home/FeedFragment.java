package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valchev.plamen.fishbook.R;

/**
 * Created by admin on 29.4.2017 г..
 */

public class FeedFragment extends Fragment implements  View.OnClickListener {

    FloatingActionButton mAddNewPostFAB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);

        mAddNewPostFAB = (FloatingActionButton) view.findViewById( R.id.add_new_post );

        mAddNewPostFAB.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        if( v == mAddNewPostFAB ) {

            Intent intent = new Intent(getActivity(), PostActivity.class);

            getActivity().startActivity(intent);
        }
    }
}
