package com.valchev.plamen.fishbook.home;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.rohitarya.fresco.facedetection.processor.FaceCenterCrop;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.models.Image;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by admin on 7.5.2017 г..
 */

public abstract class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder> {

    public interface OnImageDeleteListener {

        void onDelete(Image image);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public class DeleteButtonClickListener implements View.OnClickListener {

            public Image mImage;

            @Override
            public void onClick(View v) {

                if( mImage == null )
                    return;

                ImageRecyclerViewAdapter.this.deleteImage(mImage);

                mImage = null;
            }
        }

        public SimpleDraweeView mImage;
        public ImageButton mDeleteButton;
        public DeleteButtonClickListener mCustomClickListener;

        public ImageViewHolder(View itemView) {

            super(itemView);

            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);

            if( mDeleteButton != null ) {

                mCustomClickListener = new DeleteButtonClickListener();

                mDeleteButton.setOnClickListener(mCustomClickListener);
            }
        }
    }

    protected ArrayList<com.nguyenhoanglam.imagepicker.model.Image> mOriginImages;
    protected ArrayList<Image> mImageList;
    protected boolean mShowDeleteButton;
    protected OnImageDeleteListener mOnImageDeleteListener;

    public ImageRecyclerViewAdapter() {

        mImageList = new ArrayList<>();
        mOriginImages = new ArrayList<>();

        mShowDeleteButton = true;
    }

    public ImageRecyclerViewAdapter(ArrayList<com.nguyenhoanglam.imagepicker.model.Image> originImages, ArrayList<Image> imageList) {

        mOriginImages = originImages;
        mImageList = imageList;

        mShowDeleteButton = true;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder viewHolder, final int position) {

        SimpleDraweeView simpleDraweeView = viewHolder.mImage;

        Image image = mImageList.get(position);
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(image.midResUri))
                .setPostprocessor(new FaceCenterCrop(512, 512))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(image.lowResUri))
                .setImageRequest(imageRequest)
                .setOldController(simpleDraweeView.getController())
                .build();

        simpleDraweeView.setController(controller);

        if( viewHolder.mCustomClickListener != null ) {

            viewHolder.mCustomClickListener.mImage = image;
        }

        viewHolder.mDeleteButton.setVisibility(mShowDeleteButton ? View.VISIBLE : View.GONE);
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

    public void setShowDeleteButton(boolean showDeleteButton) {

        if( showDeleteButton == mShowDeleteButton )
            return;

        mShowDeleteButton = showDeleteButton;

        notifyDataSetChanged();
    }

    public void setOnImageDeleteListener(OnImageDeleteListener onImageDeleteListener) {

        mOnImageDeleteListener = onImageDeleteListener;
    }

    protected void deleteImage(Image image) {

        if( mOriginImages != null ) {

            int originSize = mOriginImages.size();

            for( int index = 0; index < originSize; index++  ) {

                com.nguyenhoanglam.imagepicker.model.Image originImage = mOriginImages.get(index);

                File file = new File(originImage.getPath());
                Uri uri = Uri.fromFile(file);
                String uriString = uri.toString();

                if( image.highResUri.compareToIgnoreCase(uriString) == 0 &&
                        image.midResUri.compareToIgnoreCase(uriString) == 0 &&
                        image.lowResUri.compareToIgnoreCase(uriString) == 0 ) {

                    mOriginImages.remove(index);
                    break;
                }
            }
        }

        int index = mImageList.indexOf(image);

        if( index >= 0 ) {

            mImageList.remove(index);

            notifyItemRemoved(index);
        }

        if( mOnImageDeleteListener != null ) {

            mOnImageDeleteListener.onDelete(image);
        }
    }
}