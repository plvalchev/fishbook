package com.valchev.plamen.fishbook.global;

import android.graphics.Bitmap;

import com.google.firebase.storage.StorageReference;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;

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

}
