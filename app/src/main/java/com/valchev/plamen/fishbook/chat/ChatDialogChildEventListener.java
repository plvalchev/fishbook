package com.valchev.plamen.fishbook.chat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.valchev.plamen.fishbook.global.FishbookChildEventListener;
import com.valchev.plamen.fishbook.global.FishbookValueEventListener;
import com.valchev.plamen.fishbook.global.ValueChangeListener;

import java.util.Collection;

/**
 * Created by admin on 24.5.2017 г..
 */

public class ChatDialogChildEventListener extends FishbookChildEventListener<String> {

    public ChatDialogChildEventListener(Query query, ValueChangeListener<Collection<FishbookValueEventListener<String>>> valueChangeListener) {

        super(query, valueChangeListener);
    }

    @Override
    protected FishbookValueEventListener<String> createValueEventListenerInstance(DatabaseReference databaseReference) {

        return new ChatDialog(databaseReference);
    }
}
