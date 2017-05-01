package com.valchev.plamen.fishbook.global;

import android.graphics.Bitmap;

import com.google.firebase.storage.StorageReference;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import id.zelory.compressor.Compressor;

/**
 * Created by admin on 22.4.2017 г..
 */

public class FishbookUtils {

    public static byte[] resizeAndCompressImage(String path, float maxWidth, float maxHeight) {

        File file = new File(path);

        return resizeAndCompressImage(file, maxWidth, maxHeight);
    }

    public static byte[] resizeAndCompressImage(File file, float maxWidth, float maxHeight) {

        Bitmap bitmap = resizeImage(file, maxWidth, maxHeight);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if( !compressImage( bitmap, byteArrayOutputStream ) )
            return null;

        return byteArrayOutputStream.toByteArray();
    }

    public static boolean compressImage(Bitmap bitmap, OutputStream stream) {

        return bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);
    }

    public static Bitmap resizeImage(File file, float maxWidth, float maxHeight) {

        return new Compressor.Builder(FishbookApplication.getContext())
                .setMaxWidth(maxWidth)
                .setMaxHeight(maxHeight)
                .build()
                .compressToBitmap(file);
    }

    public static Date stringToDate(String date) {

        Calendar calendar = Calendar.getInstance();
        Date result = calendar.getTime();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {

            if( date != null ) {

                result = simpleDateFormat.parse(date);
            }

        } catch (ParseException e) {

        }

        return result;
    }

    public static String dateToString(Date date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        return simpleDateFormat.format(date);
    }
}
