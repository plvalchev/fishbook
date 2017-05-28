package com.valchev.plamen.fishbook.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.joanzapata.iconify.widget.IconButton;
import com.joanzapata.iconify.widget.IconTextView;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.Event;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;
import com.valchev.plamen.fishbook.utils.FishbookUtils;
import com.valchev.plamen.fishbook.models.Comment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 20.5.2017 г..
 */

public class CommentsBottomSheetDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private IconTextView likes;
    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    private EditText writeComment;
    private DatabaseReference commentsDatabaseReference;
    private DatabaseReference likesDatabaseReference;
    private IconButton writeCommentButton;
    private SocialPaneController socialPaneController;
    private String ownerKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.comments_layout, container, false);

        likes = (IconTextView) view.findViewById(R.id.likes);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        writeComment = (EditText) view.findViewById(R.id.write_comment);
        writeCommentButton = (IconButton) view.findViewById(R.id.write_comment_button);

        FishbookActivity fishbookActivity = (FishbookActivity)getActivity();

        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(commentsDatabaseReference, fishbookActivity);

        recyclerView.setAdapter(commentRecyclerViewAdapter);

        writeCommentButton.setOnClickListener(this);

        socialPaneController = new SocialPaneController(null, null, null, likes, null, null);

        socialPaneController.setDatabaseReferences(null, likesDatabaseReference, ownerKey);

        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(android.support.design.R.id.design_bottom_sheet);
                final BottomSheetBehavior bottomSheetBehavior =  BottomSheetBehavior.from(bottomSheetInternal);

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {

                        if( newState == BottomSheetBehavior.STATE_COLLAPSED )
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                        if( newState == BottomSheetBehavior.STATE_HIDDEN ) {

                            dismiss();
                        }

                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });

        return view;
    }

    public void setDatabaseReferences(DatabaseReference commentsDatabaseReference,
                                      DatabaseReference likesDatabaseReference,
                                      String ownerKey) {

        this.commentsDatabaseReference = commentsDatabaseReference;
        this.likesDatabaseReference = likesDatabaseReference;
        this.ownerKey = ownerKey;
    }

    @Override
    public void onClick(View v) {

        if( writeCommentButton == v ) {

            Comment comment = new Comment();

            comment.id = commentsDatabaseReference.push().getKey();
            comment.userID = FishbookUser.getCurrentUser().getUid();
            comment.content = writeComment.getText().toString();

            if( comment.dateTime == null ) {

                comment.dateTime = FishbookUtils.getCurrentDateTime();
            }

            Event event = new Event();

            event.eventType = commentsDatabaseReference.getParent().getParent().getKey();
            event.objectType = commentsDatabaseReference.getParent().getKey();
            event.objectKey = commentsDatabaseReference.getKey();
            event.userID = FishbookUser.getCurrentUser().getUid();
            event.eventsCount = commentRecyclerViewAdapter.getItemCount() + 1;

            DatabaseReference databaseReference = FirebaseDatabaseUtils.getDatabaseReference();
            Map<String, Object> childUpdates = new HashMap<>();

            String commentChild = event.eventType + "/" + event.objectType + "/" + event.objectKey + "/" + comment.id;
            String eventChild = "user-events/" + ownerKey + "/" + event.eventType + "|" + event.objectType + "|" + event.objectKey;

            childUpdates.put(commentChild, comment);
            childUpdates.put(eventChild, event);

            databaseReference.updateChildren(childUpdates);

            writeComment.setText(null);

            recyclerView.scrollToPosition(commentRecyclerViewAdapter.getItemCount() - 1);
        }
    }
}
