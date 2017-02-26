package com.valchev.plamen.fishbook.Global;


import android.app.Application;
import android.content.Context;

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
    }
}
