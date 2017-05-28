package com.valchev.plamen.fishbook.home;

import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valchev.plamen.fishbook.R;

/**
 * Created by admin on 30.4.2017 г..
 */

public class ImageGridRecyclerViewAdapter extends ImageRecyclerViewAdapter {

    public static int INFINITY = 0;

    private int mMaxImages;
    private View.OnClickListener mOnClickListener;

    public class ImageViewHolder extends ImageRecyclerViewAdapter.ImageViewHolder {

        public TextView mOverFlowText;

        public ImageViewHolder(View itemView) {

            super(itemView);

            mOverFlowText = (TextView) itemView.findViewById(R.id.overflow_text);
        }
    }

    public ImageGridRecyclerViewAdapter(View.OnClickListener onClickListener, int maxImages) {

        mMaxImages = maxImages;
        mOnClickListener = onClickListener;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_grid_layout, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(layoutView);

        if( mOnClickListener != null ) {

            layoutView.setOnClickListener(ImageGridRecyclerViewAdapter.this.mOnClickListener);
        }

        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageRecyclerViewAdapter.ImageViewHolder viewHolder, int position) {

        super.onBindViewHolder(viewHolder, position);

        ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;

        TextView overflowText = imageViewHolder.mOverFlowText;

        overflowText.setVisibility(View.GONE);

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

    @Override
    public int getItemCount() {

        int itemCount = mMaxImages > INFINITY ? Math.min(mImageList.size(), mMaxImages) : mImageList.size();

        return itemCount;
    }
}
