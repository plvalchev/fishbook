package com.valchev.plamen.fishbook.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.ValueChangeListener;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.global.FishbookValueEventListener;
import com.valchev.plamen.fishbook.home.FishbookActivity;
import com.valchev.plamen.fishbook.models.Message;
import com.valchev.plamen.fishbook.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatActivity extends FishbookActivity
        implements ValueChangeListener<Collection<FishbookValueEventListener<Message>>>,
        ImageLoader, MessageInput.InputListener {

    private MessagesListAdapter<ChatMessage> messagesListAdapter;
    private MessagesList messagesList;
    private MessageInput messageInput;
    private ChatUser sender;
    private ChatUser receiver;
    private FishbookUser currentUser;
    private ChatMessagesChildEventListener chatMessagesChildEventListener;
    private UnreadChatMessages unreadChatMessages;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        messageInput = (MessageInput) findViewById(R.id.input);

        messageInput.setInputListener(this);

        currentUser = FishbookUser.getCurrentUser();

        sender = new ChatUser(currentUser.getUid());

        messagesListAdapter = new MessagesListAdapter<>(currentUser.getUid(), new ImageLoader() {

            @Override
            public void loadImage(final ImageView imageView, String url) {


            }
        });

        messagesList.setAdapter(messagesListAdapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String receiverUid = bundle.getString("receiver");
        receiver = new ChatUser(receiverUid);

        receiver.addValueChangeListener(new ValueChangeListener<User>() {

            @Override
            public void onChange(User newData) {

                getSupportActionBar().setTitle(receiver.getName());
            }
        });

        String chatKey = getChatKey();

        DatabaseReference chatMessagesDatabaseReference = FirebaseDatabaseUtils.getChatMessagesDatabaseReference(chatKey);
        DatabaseReference unreadMessagesDatabaseReference = FirebaseDatabaseUtils.getCurrentUserUnreadMessagesDatabaseReference(chatKey);

        chatMessagesChildEventListener = new ChatMessagesChildEventListener(chatMessagesDatabaseReference, this);
        unreadChatMessages = new UnreadChatMessages(unreadMessagesDatabaseReference, new ValueChangeListener<Long>() {

            @Override
            public void onChange(Long newData) {

                unreadChatMessages.delete();
            }
        });
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
    public void onChange(Collection<FishbookValueEventListener<Message>> newData) {

        messagesListAdapter.clear();

        if( newData != null ) {

            ArrayList<FishbookValueEventListener<Message>> arrayList = new ArrayList<>(newData);

            Collections.sort(arrayList, new Comparator<FishbookValueEventListener<Message>>() {

                @Override
                public int compare(FishbookValueEventListener<Message> valueListener1, FishbookValueEventListener<Message> valueListener2) {

                    ChatMessage chatMessage1 = (ChatMessage) valueListener1;
                    ChatMessage chatMessage2 = (ChatMessage) valueListener2;
                    Message message1 = chatMessage1.getValue();
                    Message message2 = chatMessage2.getValue();

                    if (message1 == message2)
                        return 0;

                    if (message1 == null && message2 != null)
                        return 1;

                    if (message1 != null && message2 == null)
                        return -1;

                    Long invertedDateTime1 = message1.invertedDateTime;
                    Long invertedDateTime2 = message2.invertedDateTime;

                    return invertedDateTime2.compareTo(invertedDateTime1);
                }

            });

            for (FishbookValueEventListener<Message> fishbookValueEventListener : arrayList) {

                ChatMessage chatMessage = (ChatMessage) fishbookValueEventListener;

                if (chatMessage == null ||
                        chatMessage.getUser() == null ||
                        chatMessage.getValue() == null) {

                    continue;
                }

                messagesListAdapter.addToStart(chatMessage, true);
            }
        }

        messagesList.setAdapter(messagesListAdapter);
        messagesListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSubmit(CharSequence input) {

        Message message = new Message();

        message.content = String.valueOf(input);
        message.userID = sender.getId();

        Map<String, Object> messageMap = message.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        String chatKey = getChatKey();

        String messageKey = FirebaseDatabaseUtils.getChatMessagesDatabaseReference(chatKey).push().getKey();

        childUpdates.put("/messages/" + chatKey + "/" + messageKey, messageMap);
        childUpdates.put("/user-chats/" + sender.getId() + "/" + chatKey, receiver.getId());
        childUpdates.put("/user-chats/" + receiver.getId() + "/" + chatKey, sender.getId());

        DatabaseReference databaseReference = FirebaseDatabaseUtils.getDatabaseReference();
        databaseReference.updateChildren(childUpdates);

        DatabaseReference receiverUnreadMessages = FirebaseDatabaseUtils.getUserUnreadMessagesDatabaseReference(chatKey, receiver.getId());

        receiverUnreadMessages.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Integer unreadMessages = mutableData.getValue(Integer.class);

                if (unreadMessages == null) {

                    unreadMessages = new Integer(0);
                }

                unreadMessages++;

                mutableData.setValue(unreadMessages);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

        return true;
    }

    @Override
    public void loadImage(final ImageView imageView, String url) {

        class BitmapDataSubscriber extends BaseBitmapDataSubscriber {

            private ImageView imageView;

            private BitmapDataSubscriber(ImageView imageView) {

                this.imageView = imageView;
            }

            @Override
            protected void onNewResultImpl(@javax.annotation.Nullable Bitmap bitmap) {

                imageView.setImageBitmap(bitmap);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .build();

        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, this);

        try {

            dataSource.subscribe(new BitmapDataSubscriber(imageView), CallerThreadExecutor.getInstance());

        } finally {

            if (dataSource != null) {
                dataSource.close();
            }
        }
    }
}