package com.valchev.plamen.fishbook.models;

import java.io.Serializable;

/**
 * Created by admin on 9.4.2017 г..
 */

public class FishingMethod implements Serializable {

    public String name;

    public FishingMethod() {

    }

    public FishingMethod(String name) {
        this.name = name;
    }
}
