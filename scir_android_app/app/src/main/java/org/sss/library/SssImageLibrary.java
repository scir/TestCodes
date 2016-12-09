package org.sss.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by khelender on 07-12-2016.
 */

public class SssImageLibrary {
    public static byte[] resizeImage(byte[] input, int PhotoWidth, int PhotoHeight) {
        Bitmap original = BitmapFactory.decodeByteArray(input , 0, input.length);
        Bitmap resized = Bitmap.createScaledBitmap(original, PhotoWidth, PhotoHeight, true);

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);

        return blob.toByteArray();
    }

}
