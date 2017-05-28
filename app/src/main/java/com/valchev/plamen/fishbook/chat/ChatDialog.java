package com.valchev.plamen.fishbook.chat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.valchev.plamen.fishbook.global.ValueChangeListener;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.global.FishbookValueEventListener;
import com.valchev.plamen.fishbook.models.Message;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatDialog extends FishbookValueEventListener<String> implements IDialog {

    private ArrayList<ChatUser> chatUsers;
    private ChatMessage lastMessage;
    private int unreadMessagesCount;
    private ChatMessagesChildEventListener chatMessagesChildEventListener;
    private UnreadChatMessages unreadMessagesValueEventListener;

    public ChatDialog(Query query) {

        super(query);

        this.unreadMessagesCount = 0;
        this.chatUsers = new ArrayList<>();

        String currentUserID = FishbookUser.getCurrentUser().getUid();

        chatUsers.add(new ChatUser(currentUserID, new ValueChangeListener<User>() {

            @Override
            public void onChange(User newData) {

                triggerChange();
            }
        }));

        Query lastMessageQuery = FirebaseDatabaseUtils.getChatMessagesDatabaseReference(getKey())
                .orderByChild("invertedDateTime").limitToFirst(1);

        chatMessagesChildEventListener = new ChatMessagesChildEventListener(lastMessageQuery, new ValueChangeListener<Collection<FishbookValueEventListener<Message>>>() {

            @Override
            public void onChange(Collection<FishbookValueEventListener<Message>> newData) {

                if( newData != null ) {

                    if( newData.iterator().hasNext() ) {

                        ChatMessage message = (ChatMessage) newData.iterator().next();

                        setLastMessage(message);

                        triggerChange();
                    }
                }
            }
        });

        DatabaseReference unreadMessagesDatabaseReference = FirebaseDatabaseUtils.getCurrentUserUnreadMessagesDatabaseReference(getKey());

        unreadMessagesValueEventListener = new UnreadChatMessages(
                unreadMessagesDatabaseReference,
                new ValueChangeListener<Long>() {

            @Override
            public void onChange(Long newData) {

                if( newData == null )
                    unreadMessagesCount = 0;
                else
                    unreadMessagesCount = (int) newData.longValue();

                triggerChange();
            }
        });
    }

    @Override
    public String getId() {

        return getKey();
    }

    @Override
    public String getDialogPhoto() {

        if( chatUsers == null ) {

            return null;
        }

        String dialogPhoto = new String();
        FishbookUser fishbookUser = FishbookUser.getCurrentUser();
        String currentUserId = fishbookUser != null ? fishbookUser.getUid() : null;

        for (ChatUser chatUser : chatUsers) {

            if( chatUser.getValue() == null )
                continue;

            dialogPhoto = chatUser.getAvatar();

            if( chatUsers.size() > 1 && currentUserId != null && chatUser.getId().compareToIgnoreCase(currentUserId) == 0 )
                continue;
            else
                break;
        }

        return dialogPhoto;
    }

    @Override
    public String getDialogName() {

        if( chatUsers == null ) {

            return null;
        }

        String dialogName = new String();
        String userName = new String();
        FishbookUser fishbookUser = FishbookUser.getCurrentUser();
        String currentUserId = fishbookUser != null ? fishbookUser.getUid() : null;

        for (ChatUser chatUser : chatUsers) {

            if( chatUser.getValue() == null )
                continue;

            userName = chatUser.getName();

            if( chatUsers.size() > 1 && currentUserId != null && chatUser.getId().compareToIgnoreCase(currentUserId) == 0 )
                continue;

            if( !dialogName.isEmpty() )
                dialogName += ", ";

            dialogName += userName;
        }

        return dialogName.isEmpty() ? userName : dialogName;
    }

    @Override
    public ArrayList<ChatUser> getUsers() {

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

        return unreadMessagesCount;
    }

    public void cleanUp() {

        super.cleanUp();

        if( chatUsers != null ) {

            for (ChatUser chatUser: chatUsers) {

                chatUser.cleanUp();
            }
        }

        if( unreadMessagesValueEventListener != null ) {

            unreadMessagesValueEventListener.cleanUp();
        }
    }

    @Override
    protected void triggerChange() {

        super.triggerChange();

        if( getValue() == null ) {
            return;
        }

        if( chatUsers != null ) {

            int index = 0;
            int size = chatUsers.size();

            for (index = 0; index < size; index++) {

                ChatUser user = chatUsers.get(index);

                if (getValue().equals(user.getId())) {

                    break;
                }
            }

            if( index >= size ) {

                chatUsers.add(new ChatUser(getValue()));
            }
        }
    }
}
