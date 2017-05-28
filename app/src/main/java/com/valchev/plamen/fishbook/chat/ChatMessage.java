package com.valchev.plamen.fishbook.chat;

import com.google.firebase.database.Query;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.valchev.plamen.fishbook.global.FishbookValueEventListener;
import com.valchev.plamen.fishbook.global.ValueChangeListener;
import com.valchev.plamen.fishbook.models.Message;
import com.valchev.plamen.fishbook.models.User;

import java.util.Date;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatMessage extends FishbookValueEventListener<Message> implements IMessage, ValueChangeListener<User> {

    private ChatUser chatUser;

    public ChatMessage(Query query) {

        super(query);
    }

    @Override
    public String getId() {

        return getKey();
    }

    @Override
    public String getText() {

        String content = null;

        if ( getValue() != null ) {

            content = getValue().content;
        }

        return content;
    }

    @Override
    public IUser getUser() {

        return chatUser;
    }

    @Override
    public Date getCreatedAt() {

        Date createdAt = new Date();

        if( getValue() != null ) {

            createdAt = new Date(-getValue().invertedDateTime);
        }

        return createdAt;
    }

    @Override
    public void onChange(User newData) {

        triggerChange();
    }

    @Override
    protected void triggerChange() {

        super.triggerChange();

        if( chatUser == null && getValue() != null ) {

            chatUser = new ChatUser(getValue().userID, this);
        }
    }

    @Override
    public void cleanUp() {

        super.cleanUp();

        if( chatUser != null ) {

            chatUser.cleanUp();
        }
    }
}
