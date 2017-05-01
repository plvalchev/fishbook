package com.valchev.plamen.fishbook.home;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.models.Image;

import java.util.ArrayList;

/**
 * Created by admin on 30.4.2017 г..
 */

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder> {

    public static int INFINITY = 0;

    private ArrayList<Image> mImageList;
    private int mMaxImages;

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView mOverFlowText;
        public SimpleDraweeView mImage;

        public ImageViewHolder(View itemView) {

            super(itemView);

            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mOverFlowText = (TextView) itemView.findViewById(R.id.overflow_text);
        }
    }

    public ImageRecyclerViewAdapter(int maxImages) {

        mImageList = new ArrayList<>();
        mMaxImages = maxImages;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(layoutView);

        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder viewHolder, int position) {

        SimpleDraweeView simpleDraweeView = viewHolder.mImage;
        TextView overflowText = viewHolder.mOverFlowText;

        overflowText.setVisibility(View.GONE);

        Image image = mImageList.get(position);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(image.lowResUri))
                .setImageRequest(ImageRequest.fromUri(image.highResUri))
                .setOldController(simpleDraweeView.getController())
                .build();

        simpleDraweeView.setController(controller);

        int itemCount = getItemCount();

        final ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();

        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {

            StaggeredGridLayoutManager.LayoutParams staggeredGridLayoutParams = (StaggeredGridLayoutManager.LayoutParams) layoutParams;

            staggeredGridLayoutParams.setFullSpan(itemCount == 1 && position == 0);
        }

        if( mMaxImages > INFINITY && mImageList.size() > mMaxImages && position == mMaxImages - 1 ) {

            int overflow = mImageList.size() - mMaxImages;

            overflowText.setText("+" + String.valueOf(overflow));
            overflowText.setVisibility(View.VISIBLE);
        }
    }

    public void setImageList(ArrayList<Image> imageList) {

        mImageList = imageList;

        if( mImageList == null ) {

            mImageList = new ArrayList<>();
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        int itemCount = mMaxImages > INFINITY ? Math.min(mImageList.size(), mMaxImages) : mImageList.size();

        return itemCount;
    }
}
