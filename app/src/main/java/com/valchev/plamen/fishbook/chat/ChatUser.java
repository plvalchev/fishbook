package com.valchev.plamen.fishbook.chat;

import com.stfalcon.chatkit.commons.models.IUser;
import com.valchev.plamen.fishbook.global.FishbookValueEventListener;
import com.valchev.plamen.fishbook.global.ValueChangeListener;
import com.valchev.plamen.fishbook.models.Image;
import com.valchev.plamen.fishbook.models.User;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatUser extends FishbookValueEventListener<User> implements IUser {

    public ChatUser(String key) {

        super(FirebaseDatabaseUtils.getUserDatabaseReference(key));
    }

    public ChatUser(String key, ValueChangeListener<User> valueChangeListener) {

        super(FirebaseDatabaseUtils.getUserDatabaseReference(key), valueChangeListener);
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

        if( getValue() != null && getValue().profilePicture != null ) {

            avatar = getValue().profilePicture.midResUri;
        }

        return avatar;
    }
}
