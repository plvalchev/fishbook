package com.valchev.plamen.fishbook.chat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.models.IUser;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.global.FishbookValueEventListener;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatUser extends FishbookValueEventListener<User> implements IUser {

    public ChatUser(String key) {

        super( FishbookUser.getUserDatabaseReference(key) );
    }

    public ChatUser(String key, ValueChangeListener<User> valueChangeListener) {

        super( FishbookUser.getUserDatabaseReference(key), valueChangeListener );
    }

    @Override
    public String getId() {

        return getKey();
    }

    @Override
    public String getName() {

        String name = null;

        if( getValue() != null ) {

            name = getValue().getDisplayName();
        }

        return name;
    }

    @Override
    public String getAvatar() {

        String avatar = null;

        if( getValue() != null && getValue().profilePictures != null ) {

            Image image = getValue().profilePictures.get(0);

            avatar = image.midResUri;
        }

        return avatar;
    }
}
