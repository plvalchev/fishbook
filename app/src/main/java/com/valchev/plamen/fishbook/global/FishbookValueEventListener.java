package com.valchev.plamen.fishbook.global;

import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.valchev.plamen.fishbook.models.Post;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

/**
 *
 */

public class FishbookValueEventListener<T> implements ValueEventListener {

    protected ArrayList<ValueChangeListener<T>> valueChangeListeners;
    protected Query query;
    protected T value;

    protected FishbookValueEventListener() {

    }

    public FishbookValueEventListener(Query query) {

        this(query, null);
    }

    public FishbookValueEventListener(Query query, ValueChangeListener<T> valueChangeListener) {

        this(query, null, valueChangeListener);
    }

    public FishbookValueEventListener(Query query, T value, ValueChangeListener<T> valueChangeListener) {

        this.value = value;

        if( valueChangeListener != null ) {

            valueChangeListeners = new ArrayList<>();

            valueChangeListeners.add(valueChangeListener);

            if( this.value != null ) {

                valueChangeListener.onChange(value);
            }
        }

        this.query = query;
        this.query.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        if( this.getClass().getGenericSuperclass() instanceof ParameterizedType ) {

            value = dataSnapshot.getValue( (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0] );

        } else {

            value = (T) dataSnapshot.getValue();
        }

        triggerChange();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void cleanUp() {

        query.removeEventListener(this);

        if( valueChangeListeners != null )
            valueChangeListeners.clear();
    }

    public void addValueChangeListener(ValueChangeListener<T> valueChangeListener) {

        if( valueChangeListeners == null ) {

            valueChangeListeners = new ArrayList<>();
        }

        if( !valueChangeListeners.contains(valueChangeListener) ) {

            valueChangeListeners.add(valueChangeListener);

            valueChangeListener.onChange(value);
        }
    }

    public String getKey() {

        return query.getRef().getKey();
    }

    public T getValue() {

        return value;
    }

    public void delete() {

        query.getRef().setValue(null);
    }

    public void update(T value) {

        setValue(value);

        query.getRef().setValue(this.value);
    }

    protected void triggerChange() {

        if( valueChangeListeners != null ) {

            for (ValueChangeListener<T> valueChangeListener : valueChangeListeners ) {

                valueChangeListener.onChange(value);
            }
        }
    }

    protected void setValue(T value) {

        this.value = value;
    }
}
