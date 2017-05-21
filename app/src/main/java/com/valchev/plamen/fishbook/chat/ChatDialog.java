package com.valchev.plamen.fishbook.chat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.models.Chat;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatDialog implements IDialog, ValueEventListener, ValueChangeListener<ChatUser> {

    private String id;
    private Chat chat;
    private ArrayList<ChatUser> chatUsers;
    private ChatMessage lastMessage;
    private ArrayList<ValueChangeListener<ChatDialog>> valueChangeListeners;
    private DatabaseReference chatDatabaseReference;
    private DatabaseReference messagesDatabaseReference;

    public ChatDialog(String id) {

        this.id = id;
        this.chat = new Chat();
        this.lastMessage = new ChatMessage();
        this.lastMessage.addValueChangeListener(new ValueChangeListener<ChatMessage>() {
            @Override
            public void onChange(ChatMessage newData) {
                triggerChange();
                setLastMessage(lastMessage);
            }
        });

        chatDatabaseReference = FirebaseDatabase.getInstance().getReference().child("chats").child(id);
        chatDatabaseReference.addValueEventListener(this);

        messagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("messages").child(id);
        messagesDatabaseReference.orderByChild("invertedDateTime").limitToFirst(1).addValueEventListener(lastMessage);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {

        String dialogPhoto = new String();
        FishbookUser fishbookUser = FishbookUser.getCurrentUser();
        String currentUserId = fishbookUser != null ? fishbookUser.getUid() : null;

        for (ChatUser chatUser : chatUsers) {

            // TODO Uncomment
//            if( currentUserId != null && chatUser.getId().compareToIgnoreCase(currentUserId) == 0 )
//                continue;

            dialogPhoto = chatUser.getAvatar();
            break;
        }

        return dialogPhoto;
    }

    @Override
    public String getDialogName() {

        String dialogName = new String();
        FishbookUser fishbookUser = FishbookUser.getCurrentUser();
        String currentUserId = fishbookUser != null ? fishbookUser.getUid() : null;

        for (ChatUser chatUser : chatUsers) {

            // TODO Uncomment
//            if( currentUserId != null && chatUser.getId().compareToIgnoreCase(currentUserId) == 0 )
//                continue;

            if( !dialogName.isEmpty() )
                dialogName += ", ";

            dialogName += chatUser.getName();
        }

        return dialogName;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return chatUsers;
    }

    @Override
    public IMessage getLastMessage() {

        return lastMessage;
    }

    @Override
    public void setLastMessage(IMessage message) {

        lastMessage = (ChatMessage) message;
    }

    @Override
    public int getUnreadCount() {

        int unreadCount = 0;

        FishbookUser fishbookUser = FishbookUser.getCurrentUser();

        if( fishbookUser != null && chat.userUnreadMessages.containsKey(fishbookUser.getUid()) ) {

            Integer integer = chat.userUnreadMessages.get(fishbookUser.getUid());

            if( integer != null ) {

                unreadCount = integer.intValue();
            }
        }

        return unreadCount;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatDialog that = (ChatDialog) o;

        if (chat != null ? !chat.equals(that.chat) : that.chat != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = chat != null ? chat.hashCode() : 0;
        result = 31 * result;
        return result;
    }

    public void cleanUp() {

        chatDatabaseReference.removeEventListener(this);

        valueChangeListeners.clear();

        if( chatUsers != null ) {

            for (ChatUser chatUser: chatUsers) {

                chatUser.cleanUp();
            }
        }
    }

    private void loadChatUsers() {

        cleanUp();

        chatUsers = new ArrayList<>();

        for(String userID : chat.users.keySet()) {

            User user = new User();
            ChatUser chatUser = new ChatUser(userID);

            chatUser.addValueChangeListener(this);

            chatUsers.add(chatUser);
        }

        triggerChange();
    }

    @Override
    public void onChange(ChatUser newData) {

        triggerChange();
    }

    public void addValueChangeListener(ValueChangeListener<ChatDialog> valueChangeListener) {

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

            for (ValueChangeListener<ChatDialog> valueChangeListener : valueChangeListeners ) {

                valueChangeListener.onChange(this);
            }
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        chat = dataSnapshot.getValue(Chat.class);

        triggerChange();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
