package com.valchev.plamen.fishbook.chat;

import android.app.Activity;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.valchev.plamen.fishbook.R;
import com.valchev.plamen.fishbook.global.ValueChangeListener;
import com.valchev.plamen.fishbook.utils.FirebaseDatabaseUtils;
import com.valchev.plamen.fishbook.global.FishbookUser;
import com.valchev.plamen.fishbook.global.FishbookValueEventListener;
import com.valchev.plamen.fishbook.home.FishbookActivity;
import com.valchev.plamen.fishbook.home.MainActivity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by admin on 21.5.2017 г..
 */

public class ChatFragment extends Fragment implements ValueChangeListener<Collection<FishbookValueEventListener<String>>> {

    private DialogsList dialogsList;
    private DialogsListAdapter<ChatDialog> dialogsListAdapter;
    private ChatDialogChildEventListener chatDialogChildEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

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

        dialogsListAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener<ChatDialog>() {

            @Override
            public void onDialogClick(ChatDialog dialog) {

                ArrayList<ChatUser> users = dialog.getUsers();

                if( users != null ) {

                    String receiverID = FishbookUser.getCurrentUser().getUid();

                    for (ChatUser user: users) {

                        if( !user.getId().equals(receiverID) ) {

                            receiverID = user.getId();
                            break;
                        }
                    }

                    Activity activity = getActivity();
                    Intent intent = new Intent(activity, ChatActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("receiver", receiverID);

                    intent.putExtras(bundle);

                    activity.startActivity(intent);
                }
            }
        });

        dialogsList.setAdapter(dialogsListAdapter);

        DatabaseReference databaseReference = FirebaseDatabaseUtils.getCurrentUserChatsDatabaseReference();

        chatDialogChildEventListener = new ChatDialogChildEventListener(databaseReference, this);

        FishbookActivity activity = (FishbookActivity)getActivity();

        if( activity instanceof MainActivity) {

            ((MainActivity) activity).showFAB(false);
        }

        return view;
    }

    @Override
    public void onChange(Collection<FishbookValueEventListener<String>> newData) {

        dialogsListAdapter.clear();

        if( newData != null ) {

            for (FishbookValueEventListener<String> fishbookValueEventListener : newData) {

                ChatDialog chatDialog = (ChatDialog) fishbookValueEventListener;

                // Ще ги обновяаваме само, след като заредим последното съобщение и потребителя
                if( chatDialog.getUsers() == null ||
                        chatDialog.getLastMessage() == null ||
                        chatDialog.getLastMessage().getUser() == null ) {

                    continue;
                }

                dialogsListAdapter.addItem(chatDialog);
            }
        }

        dialogsListAdapter.sortByLastMessageDate();
    }
}
