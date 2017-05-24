package com.valchev.plamen.fishbook.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 29.4.2017 г..
 */

public class Post implements Serializable {

    public String key;
    public String userID;
    public String dateTime;
    public String description;
    public ArrayList<FishingRegion> fishingRegions;
    public ArrayList<Specie> species;
    public ArrayList<FishingMethod> fishingMethods;
    public ArrayList<Image> images;

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("key", key);
        result.put("userID", userID);
        result.put("dateTime", dateTime);
        result.put("description", description);
        result.put("fishingRegions", fishingRegions);
        result.put("species", species);
        result.put("fishingMethods", fishingMethods);
        result.put("images", images);

        return result;
    }

}
