package com.valchev.plamen.fishbook.home;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.models.Image;

import java.util.ArrayList;

/**
 * Created by admin on 1.5.2017 г..
 */

public class ImageLinearRecyclerViewAdapter extends RecyclerView.Adapter<ImageLinearRecyclerViewAdapter.ImageViewHolder> {

    public class ImageViewHolder extends RecyclerView.ViewHolder {

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

                mImage.caption = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }


        public EditText mCaption;
        public SimpleDraweeView mImage;
        public CustomTextWatcher mTextWatcher;

        public ImageViewHolder(View itemView) {

            super(itemView);

            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mCaption = (EditText) itemView.findViewById(R.id.caption);

            mTextWatcher = new CustomTextWatcher();

            mCaption.addTextChangedListener(mTextWatcher);
        }
    }

    private ArrayList<Image> mImageList;

    public ImageLinearRecyclerViewAdapter(ArrayList<Image> imageList) {

        mImageList = imageList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_linear_layout, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(layoutView);

        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder viewHolder, final int position) {

        SimpleDraweeView simpleDraweeView = viewHolder.mImage;
        EditText caption = viewHolder.mCaption;

        Image image = mImageList.get(position);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(image.lowResUri))
                .setImageRequest(ImageRequest.fromUri(image.highResUri))
                .setOldController(simpleDraweeView.getController())
                .build();

        simpleDraweeView.setController(controller);

        viewHolder.mTextWatcher.mImage = image;
        caption.setText(image.caption);
    }

    @Override
    public int getItemCount() {

        return mImageList.size();
    }

    public void setImageList(ArrayList<Image> imageList) {

        mImageList = imageList;

        if( mImageList == null ) {

            mImageList = new ArrayList<>();
        }

        notifyDataSetChanged();
    }
}
