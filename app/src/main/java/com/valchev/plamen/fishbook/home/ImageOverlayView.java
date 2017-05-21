package com.valchev.plamen.fishbook.home;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DatabaseReference;
import com.joanzapata.iconify.widget.IconButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookComment;
import com.valchev.plamen.fishbook.global.FishbookLike;
import com.valchev.plamen.fishbook.models.Image;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ImageOverlayView extends RelativeLayout {

    protected ExpandableTextView mDescription;
    protected SocialPaneController mSocialPaneController;

    public ImageOverlayView(Context context) {
        super(context);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void bindImage(Image image) {

        DatabaseReference commentsDatabaseReference = FishbookComment.getImageCommentsDatabaseReference(image.id);
        DatabaseReference likesDatabaseReference = FishbookLike.getImageLikesDatabaseReference(image.id);

        mSocialPaneController.setDatabaseReferences(commentsDatabaseReference, likesDatabaseReference);

        mDescription.setText(image.caption);
    }

    private void init() {

        View view = inflate(getContext(), R.layout.image_overlay, this);

        mDescription = (ExpandableTextView) view.findViewById(R.id.description).findViewById(R.id.expand_text_view);

        TextView textView = (TextView) mDescription.findViewById(R.id.expandable_text);

        textView.setTextColor(getResources().getColor(R.color.white));

        IconButton likeButton = (IconButton) view.findViewById(R.id.like_button);
        IconButton commentButton = (IconButton) view.findViewById(R.id.comment_button);
        LinearLayout likeCommentsLayout = (LinearLayout) view.findViewById(R.id.like_comments_layout);
        TextView likes = (TextView) view.findViewById(R.id.likes);
        TextView comments = (TextView) view.findViewById(R.id.comments);

        AppCompatActivity activity = null;
        Context context = getContext();

        if( context instanceof AppCompatActivity )
            activity = (AppCompatActivity) context;

        mSocialPaneController = new SocialPaneController(likeCommentsLayout, likeButton, commentButton, likes, comments, activity);
    }
}
