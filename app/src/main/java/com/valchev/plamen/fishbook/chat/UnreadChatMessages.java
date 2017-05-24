package com.valchev.plamen.fishbook.chat;

import com.google.firebase.database.Query;
import com.valchev.plamen.fishbook.global.FishbookValueEventListener;

/**
 * Created by admin on 24.5.2017 г..
 */

public class UnreadChatMessages extends FishbookValueEventListener<Long> {

    public UnreadChatMessages(Query query) {
        super(query);
    }

    public UnreadChatMessages(Query query, ValueChangeListener<Long> valueChangeListener) {
        super(query, valueChangeListener);
    }

    public UnreadChatMessages(Query query, Long value, ValueChangeListener<Long> valueChangeListener) {
        super(query, value, valueChangeListener);
    }
}
