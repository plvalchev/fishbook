package com.valchev.plamen.fishbook.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 20.5.2017 г..
 */

public class Comment implements Serializable {

    public String id;
    public String userID;
    public String dateTime;
    public String content;

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("userID", userID);
        result.put("dateTime", dateTime);
        result.put("content", content);

        return result;
    }

}
