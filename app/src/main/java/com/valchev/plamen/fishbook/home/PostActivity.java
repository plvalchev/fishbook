package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_CODE_PHOTOS = 2000;
    private final static int REQUEST_CODE_PREVIEW_IMAGES = 2001;

    private final static String KEY_ORIGIN_IMAGES = "ORIGIN_IMAGES";
    private final static String KEY_IMAGE_LIST = "IMAGE_LIST";

    protected FishbookUser mFishbookUser;
    protected SimpleDraweeView mProfilePicture;
    protected TextView mDisplayName;
    protected EditText mDescription;
    protected LinearLayout mRecyclerViewLayout;
    protected RecyclerView mMainRecyclerView;
    protected RecyclerView mSubRecyclerView;
    protected ImageGridRecyclerViewAdapter mMainRecyclerViewAdapter;
    protected ImageGridRecyclerViewAdapter mSubRecyclerViewAdapter;
    protected StaggeredGridLayoutManager mMainStaggeredGridLayoutManager;
    protected StaggeredGridLayoutManager mSubStaggeredGridLayoutManager;
    protected SpaceNavigationView mSpaceNavigationView;
    protected ArrayList<com.nguyenhoanglam.imagepicker.model.Image> mOriginImages;
    protected ArrayList<Image> mImageList;

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
        mImageList = new ArrayList<>();

        if( savedInstanceState != null ) {

            mOriginImages = savedInstanceState.getParcelableArrayList(KEY_ORIGIN_IMAGES);
            mImageList = (ArrayList<Image>) savedInstanceState.getSerializable(KEY_IMAGE_LIST);

            setImageListToRecyclerViews(mImageList);
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

    protected void startImagesActivity() {

        Intent intent = new Intent(PostActivity.this, ImagesActivity.class);
        Bundle bundle = new Bundle();

        bundle.putParcelableArrayList(KEY_ORIGIN_IMAGES, mOriginImages);
        bundle.putSerializable(KEY_IMAGE_LIST, mImageList);

        intent.putExtras(bundle);

        startActivityForResult(intent, REQUEST_CODE_PREVIEW_IMAGES);
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
        outState.putSerializable(KEY_IMAGE_LIST, mImageList);
    }

    protected void initRecyclerView() {

        mMainRecyclerViewAdapter = new ImageGridRecyclerViewAdapter(this, ImageGridRecyclerViewAdapter.INFINITY);
        mSubRecyclerViewAdapter = new ImageGridRecyclerViewAdapter(this, 3);
        mMainStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mSubStaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);

        mMainRecyclerView.setLayoutManager(mMainStaggeredGridLayoutManager);
        mSubRecyclerView.setLayoutManager(mSubStaggeredGridLayoutManager);
        mMainRecyclerView.setAdapter(mMainRecyclerViewAdapter);
        mSubRecyclerView.setAdapter(mSubRecyclerViewAdapter);
        mMainRecyclerView.setOnClickListener(this);
        mSubRecyclerView.setOnClickListener(this);
    }

    protected void setImageListToRecyclerViews(ArrayList<Image> imageArrayList) {

        mImageList = imageArrayList;

        int hint = R.string.hint_whats_on_your_mind;

        if( imageArrayList == null ) {

            mMainRecyclerViewAdapter.setImageList(null);
            mSubRecyclerViewAdapter.setImageList(null);

        } else {

            int size = imageArrayList.size();

            ArrayList<Image> mainImageArrayList = new ArrayList<>();
            ArrayList<Image> subImageArrayList = null;

            mainImageArrayList.add(imageArrayList.get(0));

            int subRecyclerViewIndex = 1;
            int mainStaggeredGridLayoutManagerOrientation = StaggeredGridLayoutManager.VERTICAL;

            if( size > 1 && size < 4 ) {

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
            mSubRecyclerView.setVisibility(subImageArrayList != null ? View.VISIBLE : View.GONE);

            hint = size > 1 ? R.string.hint_say_something_about_these_photos : R.string.hint_say_something_about_this_photo;
        }

        String description = mDescription.getText().toString();

        if( description == null || description.isEmpty() ) {

            mDescription.setHint(hint);
        }
    }

    protected void setSelectedImageListToRecyclerViews(ArrayList<com.nguyenhoanglam.imagepicker.model.Image> imageArrayList) {

        ArrayList<Image> imageModelArrayList = new ArrayList<>();

        imageModelArrayList.addAll(mImageList);

        if( imageModelArrayList == null )
            imageModelArrayList = new ArrayList<>();

        for (com.nguyenhoanglam.imagepicker.model.Image image : mOriginImages) {

            File file = new File(image.getPath());
            Uri uri = Uri.fromFile(file);
            String uriString = uri.toString();
            Image imageOriginModel = new Image(uriString, uriString);
            int index = imageModelArrayList.indexOf(imageOriginModel);

            if( index >= 0 ) {

                imageModelArrayList.remove(index);
            }
        }

        if (imageArrayList != null) {

            mOriginImages = imageArrayList;
        }
        else {

            mOriginImages = new ArrayList<>();
        }

        for (com.nguyenhoanglam.imagepicker.model.Image image : mOriginImages) {

            File file = new File(image.getPath());
            Uri uri = Uri.fromFile(file);
            String uriString = uri.toString();
            Image imageModel = new Image(uriString, uriString);

            int indexOf = mImageList.indexOf(imageModel);

            if( indexOf >= 0 ) {

                imageModel = mImageList.get(indexOf);
            }

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

                case REQUEST_CODE_PREVIEW_IMAGES: {

                    Bundle bundle = data.getExtras();

                    mOriginImages = bundle.getParcelableArrayList(KEY_ORIGIN_IMAGES);
                    mImageList = (ArrayList<Image>) bundle.getSerializable(KEY_IMAGE_LIST);

                    setImageListToRecyclerViews(mImageList);
                }
                break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {

        startImagesActivity();
    }
}
