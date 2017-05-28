package com.valchev.plamen.fishbook.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;

/**
 * Created by admin on 28.5.2017 г..
 */

public class EventFragment extends Fragment {

    private EventRecyclerViewAdapter eventRecyclerViewAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        FishbookActivity activity = (FishbookActivity)getActivity();

        if( activity instanceof MainActivity ) {

            ((MainActivity) activity).showFAB(false);
        }

        eventRecyclerViewAdapter = new EventRecyclerViewAdapter(activity);

        recyclerView.setAdapter(eventRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if( eventRecyclerViewAdapter != null )
            eventRecyclerViewAdapter.cleanup();
    }
}
