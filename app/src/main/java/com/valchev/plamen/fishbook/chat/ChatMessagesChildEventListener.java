package com.valchev.plamen.fishbook.chat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.valchev.plamen.fishbook.global.FishbookChildEventListener;
import com.valchev.plamen.fishbook.global.FishbookValueEventListener;
import com.valchev.plamen.fishbook.global.ValueChangeListener;
import com.valchev.plamen.fishbook.models.Message;

import java.util.Collection;

/**
 * Created by admin on 24.5.2017 г..
 */

public class ChatMessagesChildEventListener extends FishbookChildEventListener<Message> {

    public ChatMessagesChildEventListener(Query query, ValueChangeListener<Collection<FishbookValueEventListener<Message>>> valueChangeListener) {

        super(query, valueChangeListener);
    }

    @Override
    protected FishbookValueEventListener<Message> createValueEventListenerInstance(DatabaseReference databaseReference) {

        return new ChatMessage(databaseReference);
    }
}
