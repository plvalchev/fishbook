package com.valchev.plamen.fishbook.global;


import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.shamanland.fonticon.FontIconTypefaceHolder;

public class FishbookApplication extends Application {

    private static Context mContext;

    public static Context getContext() {

        return mContext;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        //
        mContext = this;

        FontIconTypefaceHolder.init(getAssets(), "fontawesome.ttf");
        Fresco.initialize(this);
    }
}
