package com.valchev.plamen.fishbook.models;

import java.util.Date;

/**
 * Created by admin on 28.5.2017 г..
 */

public class Event {

    public String eventType;
    public String objectType;
    public String objectKey;
    public Long invertedDateTime;
    public String userID;
    public Integer eventsCount;

    public Event() {

        Date date = new Date();

        invertedDateTime = new Long(-date.getTime());
    }
}
