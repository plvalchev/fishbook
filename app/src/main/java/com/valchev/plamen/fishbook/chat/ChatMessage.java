package com.valchev.plamen.fishbook.chat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.valchev.plamen.fishbook.models.Message;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatMessage implements IMessage, ValueChangeListener<ChatUser>, ValueEventListener {

    private String id;
    private Message message;
    private ChatUser chatUser;
    private ArrayList<ValueChangeListener<ChatMessage>> valueChangeListeners;
    private DatabaseReference messagesDatabaseReference;

    public ChatMessage() {

    }

    public ChatMessage(String chatId, String id) {

        this(chatId, id, null, new Message());
    }

    public ChatMessage(String chatId, String id, ChatUser chatUser, Message message) {

        this.id = id;
        this.message = message;
        this.chatUser = chatUser;

        if( this.chatUser != null ) {

            this.chatUser.addValueChangeListener(this);
        }

        messagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("messages").child(chatId).child(id);
        messagesDatabaseReference.addValueEventListener(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return message.content;
    }

    @Override
    public IUser getUser() {
        return chatUser;
    }

    @Override
    public Date getCreatedAt() {

        return new Date();
    }

    @Override
    public void onChange(ChatUser newData) {

        triggerChange();
    }

    public void addValueChangeListener(ValueChangeListener<ChatMessage> valueChangeListener) {

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

            for (ValueChangeListener<ChatMessage> valueChangeListener : valueChangeListeners ) {

                valueChangeListener.onChange(this);
            }
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        id = dataSnapshot.getKey();
        message = dataSnapshot.getValue(Message.class);

        // TODO Error Binding

        if( chatUser == null && message.userID != null ) {

            chatUser = new ChatUser(message.userID);

            chatUser.addValueChangeListener(this);
        }

        triggerChange();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void cleanUp() {

        messagesDatabaseReference.removeEventListener(this);

        valueChangeListeners.clear();

        chatUser.cleanUp();
    }
}
