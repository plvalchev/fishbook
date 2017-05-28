package com.valchev.plamen.fishbook.global;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.valchev.plamen.fishbook.models.Image;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by admin on 28.5.2017 г..
 */

public class FishbookImagesEventListener extends FishbookValueEventListener<ArrayList<Image>> {

    public FishbookImagesEventListener(Query query, ValueChangeListener<ArrayList<Image>> valueChangeListener) {

        this.query = query;

        if( valueChangeListener != null ) {

            valueChangeListeners = new ArrayList<>();

            valueChangeListeners.add(valueChangeListener);
        }

        query.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        value = new ArrayList<>();

        for (DataSnapshot children:  dataSnapshot.getChildren()) {

            Image image = children.getValue(Image.class);

            value.add(image);
        }

        triggerChange();
    }
}
