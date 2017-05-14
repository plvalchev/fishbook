package com.valchev.plamen.fishbook.home;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.models.Image;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by admin on 1.5.2017 г..
 */

public class ImageLinearRecyclerViewAdapter extends ImageRecyclerViewAdapter {

    public class ImageViewHolder extends ImageRecyclerViewAdapter.ImageViewHolder {

        public class CustomTextWatcher implements TextWatcher {

            public Image mImage;

            public CustomTextWatcher() {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if( mImage == null )
                    return;

                String caption = s.toString();

                if( caption == null || caption.isEmpty() )
                    mImage.caption = null;
                else
                    mImage.caption = caption;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }

        public EditText mCaption;
        public CustomTextWatcher mTextWatcher;

        public ImageViewHolder(View itemView) {

            super(itemView);

            mCaption = (EditText) itemView.findViewById(R.id.caption);

            mTextWatcher = new CustomTextWatcher();

            mCaption.addTextChangedListener(mTextWatcher);

            mImageView.setOnClickListener(this);
        }
    }

    public ImageLinearRecyclerViewAdapter(FishbookActivity fishbookActivity) {

        super(fishbookActivity);
    }

    public ImageLinearRecyclerViewAdapter(FishbookActivity fishbookActivity,
                                          ArrayList<com.nguyenhoanglam.imagepicker.model.Image> originImages,
                                          ArrayList<Image> imageList) {

        super(fishbookActivity, originImages, imageList);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_linear_layout, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(layoutView);

        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageRecyclerViewAdapter.ImageViewHolder viewHolder, int position) {

        super.onBindViewHolder(viewHolder, position);

        ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;
        Image image = mImageList.get(position);

        EditText caption = imageViewHolder.mCaption;

        imageViewHolder.mTextWatcher.mImage = image;

        caption.setText(image.caption);
    }
}
