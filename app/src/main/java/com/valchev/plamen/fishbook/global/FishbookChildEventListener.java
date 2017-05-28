package com.valchev.plamen.fishbook.global;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by admin on 24.5.2017 г..
 */

public class FishbookChildEventListener<T> implements ChildEventListener, ValueChangeListener<T> {

    private HashMap<String, FishbookValueEventListener<T>> fishbookValueEventListeners;
    private ArrayList<ValueChangeListener<Collection<FishbookValueEventListener<T>>>> valuesChangeListeners;
    private Query query;

    public FishbookChildEventListener(Query query) {

        this(query, null);
    }

    public FishbookChildEventListener(Query query, ValueChangeListener<Collection<FishbookValueEventListener<T>>> valueChangeListener) {

        this.query = query;
        this.query.addChildEventListener(this);

        if( valueChangeListener != null ) {

            addValueChangeListener(valueChangeListener);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        String childKey = dataSnapshot.getKey();

        if( fishbookValueEventListeners == null ) {

            fishbookValueEventListeners = new HashMap<>();
        }

        FishbookValueEventListener<T> fishbookValueEventListener = createValueEventListenerInstance(dataSnapshot.getRef());

        fishbookValueEventListeners.put(childKey, fishbookValueEventListener);

        fishbookValueEventListener.addValueChangeListener(this);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        FishbookValueEventListener<T> fishbookValueEventListener = fishbookValueEventListeners.get(dataSnapshot.getKey());

        if( fishbookValueEventListener != null ) {

            fishbookValueEventListener.cleanUp();

            fishbookValueEventListeners.remove(dataSnapshot.getKey());
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onChange(T newData) {

        triggerChange();
    }

    public HashMap<String, FishbookValueEventListener<T>> getFishbookValueEventListeners() {

        return fishbookValueEventListeners;
    }

    public void cleanUp() {

        query.removeEventListener(this);

        if( valuesChangeListeners != null )
            valuesChangeListeners.clear();

        if( fishbookValueEventListeners != null ) {

            for( FishbookValueEventListener<T> fishbookValueEventListener : fishbookValueEventListeners.values() ) {

                fishbookValueEventListener.cleanUp();
            }
        }
    }

    public void addValueChangeListener(ValueChangeListener<Collection<FishbookValueEventListener<T>>> valueChangeListener) {

        if( valuesChangeListeners == null ) {

            valuesChangeListeners = new ArrayList<>();
        }

        if( !valuesChangeListeners.contains(valueChangeListener) ) {

            valuesChangeListeners.add(valueChangeListener);

            if( fishbookValueEventListeners != null ) {

                valueChangeListener.onChange(fishbookValueEventListeners.values());
            }
        }
    }

    protected void triggerChange() {

        if( valuesChangeListeners != null && fishbookValueEventListeners != null ) {

            for (ValueChangeListener<Collection<FishbookValueEventListener<T>>> valueChangeListener : valuesChangeListeners ) {

                valueChangeListener.onChange(fishbookValueEventListeners.values());
            }
        }
    }

    protected FishbookValueEventListener<T> createValueEventListenerInstance(DatabaseReference databaseReference) {

        return new FishbookValueEventListener<T>(databaseReference);
    }
}
