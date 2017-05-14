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
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;
import com.pchmn.materialchips.model.ChipInterface;
import com.rohitarya.fresco.facedetection.processor.core.FrescoFaceDetector;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookPost;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.global.FishbookUtils;
import com.valchev.plamen.fishbook.models.FishingMethod;
import com.valchev.plamen.fishbook.models.FishingRegion;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.Post;
import com.valchev.plamen.fishbook.models.Specie;
import com.valchev.plamen.fishbook.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity implements View.OnClickListener, ImageRecyclerViewAdapter.OnImageDeleteListener {

    private final static int REQUEST_CODE_PHOTOS = 2000;
    private final static int REQUEST_CODE_PREVIEW_IMAGES = 2001;

    private final static String KEY_ORIGIN_IMAGES = "ORIGIN_IMAGES";
    private final static String KEY_IMAGE_LIST = "IMAGE_LIST";
    private final static String KEY_SELECTED_REGION_CHIPS = "SELECTED_REGION_CHIPS";
    private final static String KEY_SELECTED_METHOD_CHIPS = "SELECTED_METHOD_CHIPS";
    private final static String KEY_SELECTED_SPECIE_CHIPS = "SELECTED_SPECIE_CHIPS";

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
    protected ChipsInput mFishingRegionChipsInput;
    protected ChipsInput mSpeciesChipsInput;
    protected ChipsInput mFishingMethodChipsInput;
    protected FishbookPost mFishbookPost;
    protected Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        FrescoFaceDetector.initialize(this);

        mProfilePicture = (SimpleDraweeView) findViewById(R.id.profile_picture);
        mDisplayName = (TextView) findViewById(R.id.display_name);
        mDescription = (EditText) findViewById(R.id.post_description);
        mRecyclerViewLayout = (LinearLayout)findViewById(R.id.recycler_view_layout);
        mMainRecyclerView = (RecyclerView)findViewById(R.id.main_recycler_view);
        mSubRecyclerView = (RecyclerView)findViewById(R.id.sub_recycler_view);
        mSpaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        mFishingRegionChipsInput = (ChipsInput) findViewById(R.id.fishing_chips_input);
        mSpeciesChipsInput = (ChipsInput) findViewById(R.id.species_chips_input);
        mFishingMethodChipsInput = (ChipsInput) findViewById(R.id.method_chips_input);

        initChipsFilterableLists();

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

            restoreChipsState(savedInstanceState, mFishingRegionChipsInput, KEY_SELECTED_REGION_CHIPS);
            restoreChipsState(savedInstanceState, mFishingMethodChipsInput, KEY_SELECTED_METHOD_CHIPS);
            restoreChipsState(savedInstanceState, mSpeciesChipsInput, KEY_SELECTED_SPECIE_CHIPS);
        }
        else {

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            if( bundle != null ) {

                mPost = (Post) bundle.getSerializable("Post");
            }

            if (mPost != null) {

                loadPost();
            }
        }
    }

    protected void loadPost() {

        setImageListToRecyclerViews(mPost.images);

        mDescription.setText(mPost.description);

        if( mPost.species != null ) {

            for (Specie specie : mPost.species) {

                mSpeciesChipsInput.addChip(new Chip(specie.name, null));
            }
        }

        if( mPost.fishingRegions != null ) {

            for (FishingRegion region : mPost.fishingRegions) {

                mFishingRegionChipsInput.addChip(new Chip(region.name, null));
            }
        }

        if( mPost.fishingMethods != null ) {

            for (FishingMethod method : mPost.fishingMethods) {

                mFishingMethodChipsInput.addChip(new Chip(method.name, null));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FrescoFaceDetector.releaseDetector();
    }

    protected void initChipsFilterableLists() {

        initChipFilterableList(mSpeciesChipsInput, R.array.species);
        initChipFilterableList(mFishingRegionChipsInput, R.array.regions);
        initChipFilterableList(mFishingMethodChipsInput, R.array.fishing_methods);
    }

    protected void initChipFilterableList(ChipsInput chipsInput, int arrayResource) {

        CharSequence[] strings = getResources().getTextArray(arrayResource);
        ArrayList<Chip> chipArrayList = new ArrayList<>();

        if( strings != null ) {

            for(int index = 0; index < strings.length; index++ ) {

                CharSequence charSequence = strings[index];

                chipArrayList.add(new Chip(charSequence.toString(), null));
            }
        }

        chipsInput.setFilterableList(chipArrayList);
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
                        post();
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

                        com.valchev.plamen.fishbook.models.Image actualProfilePicture = user.profilePictures.get(0);

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

        saveChipsState(outState, mFishingRegionChipsInput, KEY_SELECTED_REGION_CHIPS);
        saveChipsState(outState, mFishingMethodChipsInput, KEY_SELECTED_METHOD_CHIPS);
        saveChipsState(outState, mSpeciesChipsInput, KEY_SELECTED_SPECIE_CHIPS);
    }

    protected void restoreChipsState(Bundle savedInstanceState, ChipsInput chipsInput, String key) {

        ArrayList<String> selectedChipStringList = savedInstanceState.getStringArrayList(key);

        if( selectedChipStringList != null ) {

            for (String chip: selectedChipStringList) {

                chipsInput.addChip( new Chip(chip, null) );
            }
        }
    }

    protected void saveChipsState(Bundle outState, ChipsInput chipsInput, String key) {

        List<? extends ChipInterface> selectedChipList = chipsInput.getSelectedChipList();

        if( selectedChipList != null ) {

            ArrayList<String> selectedChipStringList = null;

            for (ChipInterface chipInterface: selectedChipList) {

                if( selectedChipStringList == null ) {

                    selectedChipStringList = new ArrayList<>();
                }

                selectedChipStringList.add(chipInterface.getLabel());
            }

            if( selectedChipStringList != null ) {

                outState.putStringArrayList(key, selectedChipStringList);
            }
        }
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
        mMainRecyclerViewAdapter.setOnImageDeleteListener(this);
    }

    protected void setImageListToRecyclerViews(ArrayList<Image> imageArrayList) {

        mImageList = imageArrayList;

        int hint = R.string.hint_whats_on_your_mind;

        if( imageArrayList == null || imageArrayList.size() == 0 ) {

            mMainRecyclerViewAdapter.setImageList(mOriginImages, mImageList);
            mSubRecyclerViewAdapter.setImageList(mOriginImages, mImageList);

            mFishingRegionChipsInput.setVisibility(View.GONE);
            mFishingMethodChipsInput.setVisibility(View.GONE);
            mSpeciesChipsInput.setVisibility(View.GONE);

        } else {

            mFishingRegionChipsInput.setVisibility(View.VISIBLE);
            mFishingMethodChipsInput.setVisibility(View.VISIBLE);
            mSpeciesChipsInput.setVisibility(View.VISIBLE);

            mDescription.requestFocus();

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
            mMainRecyclerViewAdapter.setImageList(mOriginImages, size <= 1 ? mImageList : mainImageArrayList);

            for( int index = subRecyclerViewIndex; index < size; index++ ) {

                if( subImageArrayList == null ) {

                    subImageArrayList = new ArrayList<>();
                }

                subImageArrayList.add(imageArrayList.get(index));
            }

            mSubRecyclerViewAdapter.setImageList(mOriginImages, subImageArrayList);
            mSubRecyclerView.setVisibility(subImageArrayList != null ? View.VISIBLE : View.GONE);

            mMainRecyclerViewAdapter.setShowDeleteButton(size == 1);
            mSubRecyclerViewAdapter.setShowDeleteButton(false);

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
            Image imageOriginModel = new Image(uriString, uriString, uriString, image.getPath());
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
            Image imageModel = new Image(uriString, uriString, uriString, image.getPath());

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

        if( mImageList.size() <= 1 )
            return;

        startImagesActivity();
    }

    @Override
    public void onDelete(Image image) {

        int size = mImageList.size();
        boolean showChips = size > 0;

        mFishingRegionChipsInput.setVisibility(showChips ? View.VISIBLE : View.GONE);
        mFishingMethodChipsInput.setVisibility(showChips ? View.VISIBLE : View.GONE);
        mSpeciesChipsInput.setVisibility(showChips ? View.VISIBLE : View.GONE);

        int hint = R.string.hint_whats_on_your_mind;

        if( size > 0 ) {

            hint = size > 1 ? R.string.hint_say_something_about_these_photos : R.string.hint_say_something_about_this_photo;
        }

        String description = mDescription.getText().toString();

        if( description == null || description.isEmpty() ) {

            mDescription.setHint(hint);
        }
    }

    protected void post() {

        if( mPost == null ) {

            mPost = new Post();
        }

        mPost.userID = mFishbookUser.getUid();

        String description = mDescription.getText().toString();

        if( !description.isEmpty() )
            mPost.description = description;
        else
            mPost.description = null;

        if( mPost.dateTime == null ) {

            mPost.dateTime = FishbookUtils.getCurrentDateTime();
        }

        mPost.images = mImageList;

        List<? extends ChipInterface> selectedChipList = mFishingRegionChipsInput.getSelectedChipList();

        for (ChipInterface chip: selectedChipList) {

            if( mPost.fishingRegions == null ) {

                mPost.fishingRegions = new ArrayList<>();
            }

            mPost.fishingRegions.clear();

            mPost.fishingRegions.add(new FishingRegion(chip.getLabel()));
        }

        selectedChipList = mFishingMethodChipsInput.getSelectedChipList();

        for (ChipInterface chip: selectedChipList) {

            if( mPost.fishingMethods == null ) {

                mPost.fishingMethods = new ArrayList<>();
            }

            mPost.fishingMethods.clear();

            mPost.fishingMethods.add(new FishingMethod(chip.getLabel()));
        }

        selectedChipList = mSpeciesChipsInput.getSelectedChipList();

        for (ChipInterface chip: selectedChipList) {

            if( mPost.species == null ) {

                mPost.species = new ArrayList<>();
            }

            mPost.species.clear();

            mPost.species.add(new Specie(chip.getLabel()));
        }

        if( mFishbookPost == null ) {

            mFishbookPost = new FishbookPost(mPost);
        }

        mFishbookPost.savePost();

        onBackPressed();
    }
}
