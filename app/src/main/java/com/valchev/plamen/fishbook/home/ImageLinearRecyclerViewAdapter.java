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

        public class CustomClickListener implements View.OnClickListener {

            public Image mImage;

            @Override
            public void onClick(View v) {

                if( mImage == null )
                    return;

                int index = ImageLinearRecyclerViewAdapter.this.mImageList.indexOf(mImage);

                if( index >= 0 ) {

                    mImageList.remove(index);

                    ImageLinearRecyclerViewAdapter.this.notifyItemRemoved(index);
                }

                if( ImageLinearRecyclerViewAdapter.this.mOriginImages != null ) {

                    int originSize = ImageLinearRecyclerViewAdapter.this.mOriginImages.size();

                    for( index = 0; index < originSize; index++  ) {

                        com.nguyenhoanglam.imagepicker.model.Image image =
                                ImageLinearRecyclerViewAdapter.this.mOriginImages.get(index);

                        File file = new File(image.getPath());
                        Uri uri = Uri.fromFile(file);
                        String uriString = uri.toString();

                        if( mImage.highResUri.compareToIgnoreCase(uriString) == 0 &&
                                mImage.lowResUri.compareToIgnoreCase(uriString) == 0 ) {

                            ImageLinearRecyclerViewAdapter.this.mOriginImages.remove(index);
                            break;
                        }
                    }
                }

                mImage = null;
            }
        }

        public EditText mCaption;
        public SimpleDraweeView mImage;
        public CustomTextWatcher mTextWatcher;
        public ImageButton mDeleteButton;
        public CustomClickListener mCustomClickListener;

        public ImageViewHolder(View itemView) {

            super(itemView);

            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mCaption = (EditText) itemView.findViewById(R.id.caption);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);

            mTextWatcher = new CustomTextWatcher();
            mCustomClickListener = new CustomClickListener();

            mCaption.addTextChangedListener(mTextWatcher);
            mDeleteButton.setOnClickListener(mCustomClickListener);
        }
    }

    private ArrayList<com.nguyenhoanglam.imagepicker.model.Image> mOriginImages;
    private ArrayList<Image> mImageList;

    public ImageLinearRecyclerViewAdapter(ArrayList<com.nguyenhoanglam.imagepicker.model.Image> originImages,
                                          ArrayList<Image> imageList) {

        mOriginImages = originImages;
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
        viewHolder.mCustomClickListener.mImage = image;

        caption.setText(image.caption);
    }

    @Override
    public int getItemCount() {

        return mImageList.size();
    }

    public void setImageList(ArrayList<com.nguyenhoanglam.imagepicker.model.Image> originImages, ArrayList<Image> imageList) {

        mOriginImages = originImages;
        mImageList = imageList;

        if( mImageList == null ) {

            mImageList = new ArrayList<>();
        }

        notifyDataSetChanged();
    }
}
