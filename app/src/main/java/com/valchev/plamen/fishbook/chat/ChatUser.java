package com.valchev.plamen.fishbook.chat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.models.IUser;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.Chat;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatUser implements IUser, ValueEventListener {

    private DatabaseReference mUserDatabaseReference;
    private String id;
    private User user;
    private ArrayList<ValueChangeListener<ChatUser>> valueChangeListeners;

    public ChatUser(String id) {

        this.id = id;
        this.user = new User();

        mUserDatabaseReference = FishbookUser.getUserDatabaseReference(id);
        mUserDatabaseReference.addValueEventListener(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {

        return user.getDisplayName();
    }

    @Override
    public String getAvatar() {

        String avatar = null;

        if( user.profilePictures != null ) {

            Image image = user.profilePictures.get(0);

            avatar = image.midResUri;
        }

        return avatar;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        user = dataSnapshot.getValue(User.class);

        triggerChange();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void cleanUp() {

        mUserDatabaseReference.removeEventListener(this);
        valueChangeListeners.clear();
    }

    public void addValueChangeListener(ValueChangeListener<ChatUser> valueChangeListener) {

        if( valueChangeListeners == null ) {

            valueChangeListeners = new ArrayList<>();
        }

        if( !valueChangeListeners.contains(valueChangeListener) ) {

            valueChangeListeners.add(valueChangeListener);

            valueChangeListener.onChange(this);
        }
    }

    private void triggerChange() {

        if( valueChangeListeners != null ) {

            for (ValueChangeListener<ChatUser> valueChangeListener : valueChangeListeners ) {

                valueChangeListener.onChange(this);
            }
        }
    }
}
