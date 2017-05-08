package com.valchev.plamen.fishbook.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 23.4.2017 г..
 */

public class Image implements Serializable {

    public String caption;
    public String lowResUri;
    public String midResUri;
    public String highResUri;

    @Exclude
    public String path;

    public Image() {

    }

    public Image(String lowResUri, String midResUri, String highResUri) {

        this.lowResUri = lowResUri;
        this.midResUri = midResUri;
        this.highResUri = highResUri;
    }

    public Image(String lowResUri, String midResUri, String highResUri, String path) {

        this.lowResUri = lowResUri;
        this.midResUri = midResUri;
        this.highResUri = highResUri;
        this.path = path;
    }

    @Exclude
    public boolean isUploaded() {

        return path == null || path.isEmpty();
    }

    @Exclude
    @Override
    public boolean equals(Object object) {

        if (this == object)
            return true;

        if (object == null || getClass() != object.getClass())
            return false;

        Image image = (Image) object;

        if (lowResUri != null ? !lowResUri.equals(image.lowResUri) : image.lowResUri != null)
            return false;

        if (highResUri != null ? !highResUri.equals(image.highResUri) : image.highResUri != null)
            return false;

        return true;
    }
}
