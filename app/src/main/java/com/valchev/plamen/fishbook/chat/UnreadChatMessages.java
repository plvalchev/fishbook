package com.valchev.plamen.fishbook.chat;

import com.google.firebase.database.Query;
import com.valchev.plamen.fishbook.global.FishbookValueEventListener;
import com.valchev.plamen.fishbook.global.ValueChangeListener;

/**
 * Created by admin on 24.5.2017 г..
 */

public class UnreadChatMessages extends FishbookValueEventListener<Long> {

    public UnreadChatMessages(Query query, ValueChangeListener<Long> valueChangeListener) {

        super(query, valueChangeListener);
    }
}
