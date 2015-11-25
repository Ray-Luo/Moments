package com.raystone.ray.goplaces_v1;

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

    public static Bitmap zipImage(String path) throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap;
        while(true)
        {
            in = new BufferedInputStream(new FileInputStream(new File(path)));
            if((options.outWidth >> i >= 1000) || (options.outHeight >> i >= 1000) )
            {
                options.inSampleSize = (int)Math.pow(2.0D,i);
            }
            else
            {
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i = i + 1;
        }
        in.close();
        return bitmap;
    }

}
