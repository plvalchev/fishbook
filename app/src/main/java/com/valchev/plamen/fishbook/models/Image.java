package com.valchev.plamen.fishbook.models;

/**
 * Created by admin on 23.4.2017 г..
 */

public class Image {

    public String lowResUri;
    public String highResUri;

    public Image() {

    }

    public Image(String lowResUri, String highResUri) {

        this.lowResUri = lowResUri;
        this.highResUri = highResUri;
    }
}
