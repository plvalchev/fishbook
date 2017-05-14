package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joanzapata.iconify.widget.IconButton;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookPost;
import com.valchev.plamen.fishbook.models.Post;

/**
 * Created by admin on 14.5.2017 г..
 */

public class PostBottomSheetDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    protected IconButton mEditButton;
    protected IconButton mDeleteButton;
    protected Post mPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.post_bottom_sheet, container, false);

        mEditButton = (IconButton) view.findViewById(R.id.edit_post);
        mDeleteButton = (IconButton) view.findViewById(R.id.delete_post);

        mEditButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);

        return view;
    }

    void setPost(Post post) {

        mPost = post;
    }

    @Override
    public void onClick(View v) {

        if( mPost == null )
            return;

        if( v == mDeleteButton ) {

            FishbookPost fishbookPost = new FishbookPost(mPost);

            fishbookPost.deletePost();

        } else {

            Intent intent = new Intent(getActivity(), PostActivity.class);
            Bundle bundle = new Bundle();

            bundle.putSerializable("Post", mPost);
            intent.putExtras(bundle);


            getActivity().startActivity(intent);
        }

        dismiss();
    }
}
