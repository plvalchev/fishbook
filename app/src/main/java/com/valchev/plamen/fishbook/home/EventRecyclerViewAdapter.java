package com.valchev.plamen.fishbook.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.models.Event;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.Post;
import com.valchev.plamen.fishbook.models.User;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;
import com.valchev.plamen.fishbook.utils.FishbookUtils;

import java.util.Date;

/**
 * Created by admin on 28.5.2017 г..
 */

public class EventRecyclerViewAdapter extends FirebaseRecyclerAdapter<Event, EventRecyclerViewAdapter.EventViewHolder> {

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private class AuthorValueEventListener implements ValueEventListener {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( event.userID.compareToIgnoreCase(dataSnapshot.getKey()) != 0 ) {
                    return;
                }

                userData = dataSnapshot.getValue(User.class);

                if( userData == null )
                    userData = new User();

                bindUserData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }

        private User userData;
        private FrameLayout frameLayout;
        private SimpleDraweeView profilePicture;
        private TextView description;
        private TextView date;
        private Event event;
        private DatabaseReference userDatabaseReference;
        private AuthorValueEventListener authorValueEventListener;
        public FishbookActivity activity;

        public EventViewHolder(View itemView) {

            super(itemView);

            frameLayout = (FrameLayout) itemView.findViewById(R.id.layout);
            profilePicture = (SimpleDraweeView) itemView.findViewById(R.id.profile_picture);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.post_date);

            authorValueEventListener = new AuthorValueEventListener();

            frameLayout.setOnClickListener(this);
        }

        public void bindEvent(Event event) {

            boolean loadUser = this.event == null || this.event.userID.compareToIgnoreCase(event.userID) != 0;

            this.event = event;

            if( loadUser ) {

                userData = new User();

                bindUserData();

                if( userDatabaseReference != null ) {

                    userDatabaseReference.removeEventListener(authorValueEventListener);
                }

                userDatabaseReference = FirebaseDatabaseUtils.getUserDatabaseReference(this.event.userID);

                userDatabaseReference.addValueEventListener(authorValueEventListener);
            }
            else {

                bindUserData();
            }

            Date eventDate = new Date(-event.invertedDateTime);

            date.setText(FishbookUtils.dateTimeToString(eventDate));

            setDescription();
        }

        private void bindUserData() {

            Image image = new Image();

            if( userData.profilePicture != null ) {

                image = userData.profilePicture;
            }

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setLowResImageRequest(ImageRequest.fromUri(image.lowResUri))
                    .setImageRequest(ImageRequest.fromUri(image.midResUri))
                    .setOldController(profilePicture.getController())
                    .build();

            profilePicture.setController(controller);

            setDescription();
        }

        private void setDescription() {

            String displayName = userData.getDisplayName();
            String objectName = event.objectType.equals("posts") ? "post" : "image";

            if( event.eventsCount > 1 ) {

                String eventType = event.eventType;

                description.setText(displayName + " and " + (event.eventsCount - 1) + " " + eventType + " your " + objectName);

            } else {

                String eventType = event.eventType.equals("likes") ? "like" : "comment";

                description.setText(displayName + " " + eventType + " your " + objectName);
            }
        }

        @Override
        public void onClick(View v) {

            if( event.objectType.equals("posts") ) {

                Intent intent = new Intent(activity, PostPreviewActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("key", event.objectKey);
                intent.putExtras(bundle);

                activity.startActivity(intent);

            } else {

//                activity.showImages(FirebaseDatabaseUtils.getImagesDatabaseReference());
            }
        }
    }

    public FishbookActivity activity;

    public EventRecyclerViewAdapter(FishbookActivity activity) {

        super(Event.class,
                R.layout.event_layout,
                EventViewHolder.class,
                FirebaseDatabaseUtils.getCurrentUserEventsDatabaseReference().orderByChild("invertedDateTime"));

        this.activity = activity;
    }

    @Override
    protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {

        viewHolder.activity = this.activity;
        viewHolder.bindEvent(model);
    }
}
