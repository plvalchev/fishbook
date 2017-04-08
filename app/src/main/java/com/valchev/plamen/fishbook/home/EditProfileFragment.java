package com.valchev.plamen.fishbook.home;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.valchev.plamen.fishbook.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditProfileFragment extends Fragment {

    protected ExpandableTextView mTopFishingRegions;
    protected ExpandableTextView mMostChasedSpecies;
    protected ExpandableTextView mFishingMethods;
    protected SimpleDraweeView mProfilePicture;
    protected SimpleDraweeView mCoverPhoto;

    static String[] test = new String[] { "https://wallpaperscraft.com/image/fishing_gear_shoes_64538_1024x768.jpg", "https://scontent-frt3-1.xx.fbcdn.net/v/l/t1.0-9/14484832_10207954542601473_5993629062988162629_n.jpg?oh=3447efedcb5bdef8e55627513838e225&oe=5953DE1D" };

    public EditProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_profile, container, false);

        View regions = view.findViewById(R.id.regions);
        View species = view.findViewById(R.id.species);
        View methods = view.findViewById(R.id.methods);
        TextView topFishingRegionsTitle = (TextView) regions.findViewById(R.id.title);
        TextView mostChasedSpeciesTitle = (TextView) species.findViewById(R.id.title);
        TextView fishingMethodsTitle = (TextView) methods.findViewById(R.id.title);

        mTopFishingRegions = (ExpandableTextView) regions.findViewById(R.id.expand_text_view);
        mMostChasedSpecies = (ExpandableTextView) species.findViewById(R.id.expand_text_view);
        mFishingMethods = (ExpandableTextView) methods.findViewById(R.id.expand_text_view);
        mProfilePicture = (SimpleDraweeView) view.findViewById(R.id.profile_image);
        mCoverPhoto = (SimpleDraweeView) view.findViewById(R.id.cover_photo);

        topFishingRegionsTitle.setText(getResources().getString(R.string.regions));
        mostChasedSpeciesTitle.setText(getResources().getString(R.string.chased_species));
        fishingMethodsTitle.setText(getResources().getString(R.string.fishing_methods));

        mTopFishingRegions.setText("Straldja, Jambol, Varna, Sofia");
        mMostChasedSpecies.setText("Pike, Carp, Largemouth Bass, Garfish, Shark");
        mFishingMethods.setText("Fly fishing, Spinning, Coarse, Match");

        Uri uri = Uri.parse(test[1]);
        mProfilePicture.setImageURI(uri);

        uri = Uri.parse(test[0]);

        mCoverPhoto.setImageURI(uri);

        mProfilePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new ImageViewer.Builder(EditProfileFragment.this.getContext(), test)
                        .setStartPosition(1)
                        .setImageMarginPx(20)
                        .show();
            }
        });

        mCoverPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new ImageViewer.Builder(EditProfileFragment.this.getContext(), test)
                        .setStartPosition(0)
                        .setImageMarginPx(20)
                        .show();
            }
        });

        return view;
    }
}
