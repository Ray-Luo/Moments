package com.raystone.ray.goplaces.Helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 11/24/2015.
 */
public class MyBitMap {

    public static int max = 0;
    public static List<Bitmap> bmp = new ArrayList<>();
    public static List<String> dir = new ArrayList<>();
    public static boolean act_bool = true;

    public static Bitmap zipImage(String path) throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap;
        while (true) {
            if ((options.outWidth  >= 540)
                    || (options.outHeight  >= 720)) {
                in = new BufferedInputStream(new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
            }else
            {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                in.close();
                break;
            }
            i += 1;
        }
        return bitmap;
    }

    public static Bitmap zipSmallImage(String path) throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap;
        while (true) {
            if ((options.outWidth  >= 256)
                    || (options.outHeight  >= 256)) {
                in = new BufferedInputStream(new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
            }else
            {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                in.close();
                break;
            }
            i += 1;
        }
        return bitmap;
    }

}
