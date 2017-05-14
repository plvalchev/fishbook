package com.valchev.plamen.fishbook.models;

import java.io.Serializable;

/**
 * Created by admin on 9.4.2017 г..
 */

public class Specie implements Serializable {

    public String name;

    public Specie() {

    }

    public Specie(String name) {
        this.name = name;
    }
}
