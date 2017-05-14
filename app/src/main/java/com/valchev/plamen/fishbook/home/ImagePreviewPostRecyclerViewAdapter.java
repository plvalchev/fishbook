package com.valchev.plamen.fishbook.home;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.models.Image;

import java.util.ArrayList;

/**
 * Created by admin on 14.5.2017 г..
 */

public class ImagePreviewPostRecyclerViewAdapter extends ImageRecyclerViewAdapter {

    public class ImageViewHolder extends ImageRecyclerViewAdapter.ImageViewHolder {

        protected ExpandableTextView mDescription;

        public ImageViewHolder(View itemView) {

            super(itemView);

            mDescription = (ExpandableTextView) itemView.findViewById(R.id.post_description).findViewById(R.id.expand_text_view);


            mImageView.setOnClickListener(this);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_preview_layout, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(layoutView);

        return imageViewHolder;
    }

    public ImagePreviewPostRecyclerViewAdapter(FishbookActivity fishbookActivity) {

        super(fishbookActivity);
    }

    @Override
    public void onBindViewHolder(ImageRecyclerViewAdapter.ImageViewHolder viewHolder, final int position) {

        super.onBindViewHolder(viewHolder, position);

        Image image = mImageList.get(position);
        ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;

        imageViewHolder.mDescription.setText(image.caption);
    }
}
