package com.valchev.plamen.fishbook.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.internal.Objects;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.home.FishbookActivity;
import com.valchev.plamen.fishbook.models.Chat;
import com.valchev.plamen.fishbook.models.Message;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatActivity extends FishbookActivity
        implements ValueEventListener, ChildEventListener, ValueChangeListener<ChatMessage>, MessageInput.InputListener {

    private MessagesListAdapter<ChatMessage> messagesListAdapter;
    private MessagesList messagesList;
    private MessageInput messageInput;
    private ChatUser sender;
    private ChatUser receiver;
    private DatabaseReference messagesDatabaseReference;
    private DatabaseReference chatDatabaseReference;
    private FishbookUser currentUser;
    private ArrayList<ChatMessage> chatMessages;
    private Chat chat;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        chatMessages = new ArrayList<>();

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        messageInput = (MessageInput) findViewById(R.id.input);

        messageInput.setInputListener(this);

        currentUser = FishbookUser.getCurrentUser();

        sender = new ChatUser(currentUser.getUid());

        messagesListAdapter = new MessagesListAdapter<>(currentUser.getUid(), new ImageLoader() {

            @Override
            public void loadImage(final ImageView imageView, String url) {

                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(url))
                        .build();

                DataSource<CloseableReference<CloseableImage>> dataSource =
                        imagePipeline.fetchDecodedImage(imageRequest, this);

                try {

                    dataSource.subscribe(new BaseBitmapDataSubscriber() {

                        @Override
                        public void onNewResultImpl(@Nullable Bitmap bitmap) {

                            imageView.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onFailureImpl(DataSource dataSource) {


                        }

                    }, CallerThreadExecutor.getInstance());

                } finally {

                    if (dataSource != null) {
                        dataSource.close();
                    }
                }
            }
        });

        messagesList.setAdapter(messagesListAdapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String receiverUid = bundle.getString("receiver");
        receiver = new ChatUser(receiverUid);

        String chatKey = getChatKey();

        messagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("messages").child(chatKey);
        chatDatabaseReference = FirebaseDatabase.getInstance().getReference().child("chats").child(chatKey);

        messagesDatabaseReference.addChildEventListener(this);
        chatDatabaseReference.addValueEventListener(this);
    }

    public String getChatKey() {

        ArrayList<String> userIDArrayList = new ArrayList<>();

        userIDArrayList.add(currentUser.getUid());
        userIDArrayList.add(receiver.getId());

        Collections.sort(userIDArrayList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        String chatKey = new String();

        for (String uid : userIDArrayList) {

            if( !chatKey.isEmpty() )
                chatKey += "|";

            chatKey += uid.hashCode();
        }

        return chatKey;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        Message message = dataSnapshot.getValue(Message.class);
        ChatUser chatUser = message.userID.equals(receiver.getId()) ? receiver : sender;

        ChatMessage chatMessage = new ChatMessage( getChatKey(), dataSnapshot.getKey(), chatUser, message );

        chatMessages.add(chatMessage);
        messagesListAdapter.addToStart(chatMessage, true);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        int size = chatMessages.size();

        for( int index = 0; index < size; index++ ) {

            ChatMessage chatMessage = chatMessages.get(index);

            if( chatMessage.getId().equals(dataSnapshot.getKey()) ) {

                chatMessage.cleanUp();

                chatMessages.remove(index);
                messagesListAdapter.deleteById(dataSnapshot.getKey());

                break;
            }
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onChange(ChatMessage newData) {

        messagesListAdapter.update(newData);
    }

    @Override
    public boolean onSubmit(CharSequence input) {

        Message message = new Message();

        message.content = String.valueOf(input);
        message.userID = sender.getId();

        Map<String, Object> messageMap = message.toMap();

        if( chat == null ) {

            chat = new Chat();

            chat.users = new HashMap<>();
            chat.userUnreadMessages = new HashMap<>();
            chat.users.put(receiver.getId(), true);
            chat.users.put(sender.getId(), true);
            chat.userUnreadMessages.put(receiver.getId(), 1);
        }
        else {

            Integer unreadMessages = chat.userUnreadMessages.get(receiver.getId());

            if( unreadMessages == null ) {

                unreadMessages = new Integer(0);
            }

            chat.userUnreadMessages.put(receiver.getId(), unreadMessages + 1);
        }

        Map<String, Object> chatMap = chat.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        String chatKey = getChatKey();

        childUpdates.put("/messages/" + chatKey + "/" + messagesDatabaseReference.push().getKey(), messageMap);
        childUpdates.put("/chats/" + chatKey, chatMap);
        childUpdates.put("/user-chats/" + receiver.getId() + "/" + chatKey, chatMap);
        childUpdates.put("/user-chats/" + sender.getId() + "/" + chatKey, chatMap);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.updateChildren(childUpdates);

        return true;
    }
}
