package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.models.Image;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class ImagesActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_PHOTOS = 2000;
    private final static int REQUEST_CODE_PREVIEW_IMAGES = 2001;

    private final static String KEY_ORIGIN_IMAGES = "ORIGIN_IMAGES";
    private final static String KEY_IMAGE_LIST = "IMAGE_LIST";

    protected SpaceNavigationView mSpaceNavigationView;
    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected ImageLinearRecyclerViewAdapter mImageLinearRecyclerViewAdapter;
    protected ArrayList<com.nguyenhoanglam.imagepicker.model.Image> mOriginImages;
    protected ArrayList<Image> mImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSpaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);

        mSpaceNavigationView.initWithSaveInstanceState(savedInstanceState);

        if( savedInstanceState != null ) {

            mOriginImages = savedInstanceState.getParcelableArrayList(KEY_ORIGIN_IMAGES);
            mImageList = (ArrayList<Image>) savedInstanceState.getSerializable(KEY_IMAGE_LIST);
        }
        else {

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            if( bundle != null ) {

                mOriginImages = bundle.getParcelableArrayList(KEY_ORIGIN_IMAGES);
                mImageList = (ArrayList<Image>) bundle.getSerializable(KEY_IMAGE_LIST);
            }
        }

        initSpaceNavigationView();
        initRecyclerView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSpaceNavigationView.onSaveInstanceState(outState);

        outState.putParcelableArrayList(KEY_ORIGIN_IMAGES, mOriginImages);
        outState.putSerializable(KEY_IMAGE_LIST, mImageList);
    }

    protected void initRecyclerView() {

        mLinearLayoutManager = new LinearLayoutManager(this);
        mImageLinearRecyclerViewAdapter = new ImageLinearRecyclerViewAdapter(mOriginImages, mImageList);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mImageLinearRecyclerViewAdapter);
    }

    protected void initSpaceNavigationView() {

        mSpaceNavigationView.setCentreButtonRippleColor(getResources().getColor(R.color.grey, null));
        mSpaceNavigationView.setCentreButtonSelectable(true);
        mSpaceNavigationView.setCentreButtonSelected();
        mSpaceNavigationView.addSpaceItem(new SpaceItem("Back", R.mipmap.ic_arrow_back_white_18dp));
        mSpaceNavigationView.addSpaceItem(new SpaceItem("Done", R.mipmap.ic_check_white_18dp));

        mSpaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {

                ImagePicker.create(ImagesActivity.this)
                        .multi()
                        .origin(mOriginImages)
                        .start(REQUEST_CODE_PHOTOS);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {

                switch (itemIndex) {

                    case 0:
                        ImagesActivity.this.onBackPressed();
                        break;

                    case 1:
                        finishActivityWithResult();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });
    }

    protected void finishActivityWithResult() {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putParcelableArrayList(KEY_ORIGIN_IMAGES, mOriginImages);
        bundle.putSerializable(KEY_IMAGE_LIST, mImageList);

        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        finish();
    }

    protected void setSelectedImageListToRecyclerView(ArrayList<com.nguyenhoanglam.imagepicker.model.Image> imageArrayList) {

        ArrayList<Image> imageModelArrayList = new ArrayList<>();

        imageModelArrayList.addAll(mImageList);

        if( imageModelArrayList == null )
            imageModelArrayList = new ArrayList<>();

        for (com.nguyenhoanglam.imagepicker.model.Image image : mOriginImages) {

            File file = new File(image.getPath());
            Uri uri = Uri.fromFile(file);
            String uriString = uri.toString();
            Image imageOriginModel = new Image(uriString, uriString, image.getPath());
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
            Image imageModel = new Image(uriString, uriString, image.getPath());

            int indexOf = mImageList.indexOf(imageModel);

            if( indexOf >= 0 ) {

                imageModel = mImageList.get(indexOf);
            }

            imageModelArrayList.add(imageModel);
        }

        mImageList = imageModelArrayList;
        mImageLinearRecyclerViewAdapter.setImageList(mOriginImages, mImageList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( resultCode == RESULT_OK && data != null ) {

            switch ( requestCode ) {

                case REQUEST_CODE_PHOTOS: {
                    ArrayList<com.nguyenhoanglam.imagepicker.model.Image> images =
                            data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

                    setSelectedImageListToRecyclerView(images);
                }
                break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
