package com.valchev.plamen.fishbook.models;

import java.io.Serializable;

/**
 * Created by admin on 9.4.2017 г..
 */

public class FishingRegion implements Serializable {

    public String name;

    public FishingRegion() {

    }

    public FishingRegion(String name) {
        this.name = name;
    }
}
