package com.valchev.plamen.fishbook.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 21.5.2017 г..
 */

public class Chat implements Serializable {

    public Map<String, Object> users;
    public Map<String, Integer> userUnreadMessages;

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("users", users);
        result.put("userUnreadMessages", userUnreadMessages);

        return result;
    }
}
