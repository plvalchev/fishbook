package com.valchev.plamen.fishbook.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.joanzapata.iconify.widget.IconButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookComment;
import com.valchev.plamen.fishbook.global.FishbookLike;
import com.valchev.plamen.fishbook.models.Image;

/**
 * Created by admin on 14.5.2017 г..
 */

public class ImagePreviewPostRecyclerViewAdapter extends ImageRecyclerViewAdapter {

    public class ImageViewHolder extends ImageRecyclerViewAdapter.ImageViewHolder {

        protected ExpandableTextView mDescription;
        protected SocialPaneController mSocialPaneController;

        public ImageViewHolder(View itemView) {

            super(itemView);

            mDescription = (ExpandableTextView) itemView.findViewById(R.id.post_description).findViewById(R.id.expand_text_view);

            mImageView.setOnClickListener(this);

            IconButton likeButton = (IconButton) itemView.findViewById(R.id.like_button);
            IconButton commentButton = (IconButton) itemView.findViewById(R.id.comment_button);
            LinearLayout likeCommentsLayout = (LinearLayout) itemView.findViewById(R.id.like_comments_layout);
            TextView likes = (TextView) itemView.findViewById(R.id.likes);
            TextView comments = (TextView) itemView.findViewById(R.id.comments);

            mSocialPaneController = new SocialPaneController(likeCommentsLayout, likeButton, commentButton, likes, comments, mFishbookActivity);
        }

        public void bindImage(Image image) {

            mDescription.setText(image.caption);

            DatabaseReference commentsDatabaseReference = FishbookComment.getImageCommentsDatabaseReference(image.id);
            DatabaseReference likesDatabaseReference = FishbookLike.getImageLikesDatabaseReference(image.id);

            mSocialPaneController.setDatabaseReferences(commentsDatabaseReference, likesDatabaseReference);
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

        imageViewHolder.bindImage(image);
    }
}
