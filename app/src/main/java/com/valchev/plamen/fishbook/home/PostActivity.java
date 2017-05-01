package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.firebase.database.DatabaseError;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.ProfilePicture;
import com.valchev.plamen.fishbook.models.User;

import org.geonames.PostalCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class PostActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_PHOTOS = 2000;

    private final static String KEY_ORIGIN_IMAGES = "ORIGIN_IMAGES";

    protected FishbookUser mFishbookUser;
    protected SimpleDraweeView mProfilePicture;
    protected TextView mDisplayName;
    protected EditText mDescription;
    protected LinearLayout mRecyclerViewLayout;
    protected RecyclerView mMainRecyclerView;
    protected RecyclerView mSubRecyclerView;
    protected ImageRecyclerViewAdapter mMainRecyclerViewAdapter;
    protected ImageRecyclerViewAdapter mSubRecyclerViewAdapter;
    protected StaggeredGridLayoutManager mMainStaggeredGridLayoutManager;
    protected StaggeredGridLayoutManager mSubStaggeredGridLayoutManager;
    protected SpaceNavigationView mSpaceNavigationView;
    protected ArrayList<com.nguyenhoanglam.imagepicker.model.Image> mOriginImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mProfilePicture = (SimpleDraweeView) findViewById(R.id.profile_picture);
        mDisplayName = (TextView) findViewById(R.id.display_name);
        mDescription = (EditText) findViewById(R.id.post_description);
        mRecyclerViewLayout = (LinearLayout)findViewById(R.id.recycler_view_layout);
        mMainRecyclerView = (RecyclerView)findViewById(R.id.main_recycler_view);
        mSubRecyclerView = (RecyclerView)findViewById(R.id.sub_recycler_view);
        mSpaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);

        mSpaceNavigationView.initWithSaveInstanceState(savedInstanceState);

        loadUserInBackground();

        initSpaceNavigationView();
        initRecyclerView();

        mOriginImages = new ArrayList<>();

        if( savedInstanceState != null ) {

            mOriginImages = savedInstanceState.getParcelableArrayList(KEY_ORIGIN_IMAGES);

            setSelectedImageListToRecyclerViews(mOriginImages);
        }
    }

    protected void initSpaceNavigationView() {

        mSpaceNavigationView.setCentreButtonRippleColor(getResources().getColor(R.color.grey, null));
        mSpaceNavigationView.setCentreButtonSelectable(true);
        mSpaceNavigationView.setCentreButtonSelected();
        mSpaceNavigationView.addSpaceItem(new SpaceItem("Discard", R.mipmap.ic_arrow_back_white_18dp));
        mSpaceNavigationView.addSpaceItem(new SpaceItem("Post", R.mipmap.ic_check_white_18dp));

        mSpaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {

                ImagePicker.create(PostActivity.this)
                        .multi()
                        .origin(mOriginImages)
                        .start(REQUEST_CODE_PHOTOS);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {

                switch (itemIndex) {

                    case 0:
                        PostActivity.this.onBackPressed();
                        break;

                    case 1:
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });
    }

    protected void loadUserInBackground() {

        mFishbookUser = FishbookUser.getCurrentUser();

        mFishbookUser.addUserValueEventListener(new FishbookUser.UserValueEventListener() {

            @Override
            public void onDataChange(User user) {

                if( user != null ) {

                    if( user.profilePictures != null ) {

                        ProfilePicture actualProfilePicture = user.profilePictures.get(0);

                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setLowResImageRequest(ImageRequest.fromUri(actualProfilePicture.lowResUri))
                                .setImageRequest(ImageRequest.fromUri(actualProfilePicture.highResUri))
                                .setOldController(mProfilePicture.getController())
                                .build();

                        mProfilePicture.setController(controller);
                    }

                    mDisplayName.setText(user.getDisplayName());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSpaceNavigationView.onSaveInstanceState(outState);

        outState.putParcelableArrayList(KEY_ORIGIN_IMAGES, mOriginImages);
    }

    protected void initRecyclerView() {

        mMainRecyclerViewAdapter = new ImageRecyclerViewAdapter(ImageRecyclerViewAdapter.INFINITY);
        mSubRecyclerViewAdapter = new ImageRecyclerViewAdapter(3);
        mMainStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mSubStaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);

        mMainRecyclerView.setLayoutManager(mMainStaggeredGridLayoutManager);
        mSubRecyclerView.setLayoutManager(mSubStaggeredGridLayoutManager);
        mMainRecyclerView.setAdapter(mMainRecyclerViewAdapter);
        mSubRecyclerView.setAdapter(mSubRecyclerViewAdapter);
    }

    protected void setImageListToRecyclerViews(ArrayList<Image> imageArrayList) {

        String description = mDescription.getText().toString();

        if( imageArrayList == null ) {

            mMainRecyclerViewAdapter.setImageList(null);
            mSubRecyclerViewAdapter.setImageList(null);

            if( description == null || description.isEmpty() ) {

                mDescription.setHint(R.string.hint_whats_on_your_mind);
            }

        } else {

            int size = imageArrayList.size();

            if( size == 1 ) {

                mMainRecyclerViewAdapter.setImageList(imageArrayList);
                mSubRecyclerViewAdapter.setImageList(null);
                mSubRecyclerView.setVisibility(View.GONE);

            } else {

                ArrayList<Image> mainImageArrayList = new ArrayList<>();
                ArrayList<Image> subImageArrayList = null;

                mainImageArrayList.add(imageArrayList.get(0));

                int subRecyclerViewIndex = 1;
                int mainStaggeredGridLayoutManagerOrientation = StaggeredGridLayoutManager.VERTICAL;

                if( size < 4 ) {

                    mainImageArrayList.add(imageArrayList.get(1));

                    subRecyclerViewIndex = 2;
                    mainStaggeredGridLayoutManagerOrientation = StaggeredGridLayoutManager.HORIZONTAL;
                }

                mMainStaggeredGridLayoutManager.setOrientation(mainStaggeredGridLayoutManagerOrientation);
                mMainRecyclerViewAdapter.setImageList(mainImageArrayList);

                for( int index = subRecyclerViewIndex; index < size; index++ ) {

                    if( subImageArrayList == null ) {

                        subImageArrayList = new ArrayList<>();
                    }

                    subImageArrayList.add(imageArrayList.get(index));
                }

                mSubRecyclerViewAdapter.setImageList(subImageArrayList);
                mSubRecyclerView.setVisibility( subImageArrayList != null ? View.VISIBLE : View.GONE);
            }

            if( description == null || description.isEmpty() ) {

                mDescription.setHint(R.string.hint_say_something_about_these_photos);
            }
        }
    }

    protected void setSelectedImageListToRecyclerViews(ArrayList<com.nguyenhoanglam.imagepicker.model.Image> imageArrayList) {

        if (imageArrayList != null) {

            mOriginImages = imageArrayList;
        }
        else {

            mOriginImages = new ArrayList<>();
        }

        ArrayList<Image> imageModelArrayList = null;

        for (com.nguyenhoanglam.imagepicker.model.Image image : mOriginImages) {

            if( imageModelArrayList == null ) {

                imageModelArrayList = new ArrayList<>();
            }

            File file = new File(image.getPath());
            Uri uri = Uri.fromFile(file);
            String uriString = uri.toString();
            Image imageModel = new Image(uriString, uriString);

            imageModelArrayList.add(imageModel);
        }

        setImageListToRecyclerViews(imageModelArrayList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( resultCode == RESULT_OK && data != null ) {

            switch ( requestCode ) {

                case REQUEST_CODE_PHOTOS: {
                    ArrayList<com.nguyenhoanglam.imagepicker.model.Image> images =
                            data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

                    setSelectedImageListToRecyclerViews(images);
                }
                break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
