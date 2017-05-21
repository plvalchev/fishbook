package com.valchev.plamen.fishbook.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.FishbookUser;

import java.util.ArrayList;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatFragment extends Fragment implements ChildEventListener, ValueChangeListener<ChatDialog> {

    private ArrayList<ChatDialog> adapterChats;
    private ArrayList<ChatDialog> loadedChats;
    private DialogsList dialogsList;
    private DialogsListAdapter<ChatDialog> dialogsListAdapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        loadedChats = new ArrayList<>();
        dialogsList = (DialogsList) view.findViewById(R.id.dialogsList);

        dialogsListAdapter = new DialogsListAdapter<>(R.layout.chat_item_dialog, new ImageLoader() {

            @Override
            public void loadImage(ImageView imageView, String url) {

                SimpleDraweeView simpleDraweeView = (SimpleDraweeView) imageView;

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(ImageRequest.fromUri(url))
                        .setOldController(simpleDraweeView.getController())
                        .build();

                simpleDraweeView.setController(controller);
            }
        });

        dialogsList.setAdapter(dialogsListAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("user-chats").child(FishbookUser.getCurrentUser().getUid());

        databaseReference.addChildEventListener(this);

        return view;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        ChatDialog chatDialog = new ChatDialog(dataSnapshot.getKey());

        loadedChats.add(chatDialog);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

//        ChatDialog chatDialog = new ChatDialog(dataSnapshot.getKey());
//
//        int size = loadedChats.size();
//
//        for (int index = 0; index < size; index++ ) {
//
//            ChatDialog oldChatDialog = loadedChats.get(index);
//
//            if( oldChatDialog.getId().equals(chatDialog.getId()) ) {
//
//                oldChatDialog.cleanUp();
//                loadedChats.set(index, chatDialog);
//                break;
//            }
//        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        int size = loadedChats.size();

        for (int index = 0; index < size; index++ ) {

            ChatDialog oldChatDialog = loadedChats.get(index);

            if( oldChatDialog.getId().equals(dataSnapshot.getKey()) ) {

                oldChatDialog.cleanUp();
                loadedChats.remove(index);
                adapterChats.remove(oldChatDialog);
                dialogsListAdapter.deleteById(dataSnapshot.getKey());
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
    public void onChange(ChatDialog newData) {

        // Ще ги обновяаваме само, след като заредим последното съобщение
        if( newData.getLastMessage() != null ) {

            int index = 0;
            int size = adapterChats.size();

            for ( index = 0; index < size; index++ ) {

                ChatDialog oldChatDialog = adapterChats.get(index);

                if( oldChatDialog.getId().equals(newData.getId()) ) {

                    adapterChats.set(index, newData);
                    dialogsListAdapter.updateItemById(newData);
                    break;
                }
            }

            if( index >= size ) {
                adapterChats.add(newData);
                dialogsListAdapter.addItem(newData);
            }
        }
    }
}
