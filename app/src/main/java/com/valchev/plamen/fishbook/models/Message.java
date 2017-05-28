package com.valchev.plamen.fishbook.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 21.5.2017 г..
 */

public class Message implements Serializable {

    public String content;
    public String userID;
    public Long invertedDateTime;

    public Message() {

        Date date = new Date();

        invertedDateTime = new Long(-date.getTime());
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("content", content);
        result.put("userID", userID);
        result.put("invertedDateTime", invertedDateTime);

        return result;
    }
}
