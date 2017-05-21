package com.valchev.plamen.fishbook.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.stfalcon.frescoimageviewer.ImageViewer;
import com.valchev.plamen.fishbook.models.Image;

import java.util.ArrayList;

/**
 * Created by admin on 14.5.2017 г..
 */

public class FishbookActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_POSITION = "CURRENT_POSITION";
    private static final String KEY_IMAGES = "IMAGES";

    protected ArrayList<Image> mImages;
    protected int mCurrentPosition = -1;
    protected ImageOverlayView mImageOverlayView;

    public void showImages(int startPosition, ArrayList<Image> images) {

        mImageOverlayView = new ImageOverlayView(this);
        mImages = images;
        mCurrentPosition = startPosition;

        new ImageViewer.Builder(this, mImages)
                .setStartPosition(mCurrentPosition)
                .setImageMarginPx(20)
                .setOverlayView(mImageOverlayView)
                .hideStatusBar(false)
//                .setContainerPaddingPx(0, 0, 0, 0)
                .setImageChangeListener(new ImageViewer.OnImageChangeListener() {

                    @Override
                    public void onImageChange(int position) {

                        mCurrentPosition = position;

                        Image image = mImages.get(position);

                        mImageOverlayView.bindImage(image);

                    }

                })
                .setOnDismissListener(new ImageViewer.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mCurrentPosition = -1;
                    }
                })
                .setFormatter(new ImageViewer.Formatter<Image>() {
                    @Override
                    public String format(Image image) {
                        return image.highResUri;
                    }
                })
                .show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {

            mCurrentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION);
            mImages = (ArrayList<Image>)savedInstanceState.getSerializable(KEY_IMAGES);

            if( mCurrentPosition >= 0 && mImages != null ) {

                showImages(mCurrentPosition, mImages);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(KEY_CURRENT_POSITION, mCurrentPosition);
        outState.putSerializable(KEY_IMAGES, mImages);

        super.onSaveInstanceState(outState);
    }
}
