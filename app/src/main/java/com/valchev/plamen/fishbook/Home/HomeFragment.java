package com.valchev.plamen.fishbook.Home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valchev.plamen.fishbook.Global.FishbookApplication;
import com.valchev.plamen.fishbook.R;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    static ArrayList<View> imageViews = new ArrayList<View>() {{

        addView(R.string.log_your_catches);
        addView(R.string.capture_and_share_snapshots);
        addView(R.string.explore_pictures_and_catches);
        addView(R.string.discover_interesting_anglers);
    }

        void addView(int stringID) {

            Context context = FishbookApplication.getContext();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_home, null);
            TextView textView	= (TextView)view.findViewById(R.id.fragment_home_text);

            textView.setText(stringID);

            add(view);
        }

    };

    public HomeFragment() {

    }

    public static int getPagesCount() {

        return imageViews.size();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View result = null;

        for( View cacheImageView : imageViews ) {

            if( cacheImageView.getParent() == null )
                result = cacheImageView;
        }

        if( result == null )
            result = new View(FishbookApplication.getContext());

        return result;
    }

}
