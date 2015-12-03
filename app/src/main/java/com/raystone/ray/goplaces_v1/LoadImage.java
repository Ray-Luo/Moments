package com.raystone.ray.goplaces_v1;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by Ray on 11/25/2015.
 */
public class LoadImage extends AsyncTask<Bitmap, Void,Bitmap> {

    public static ImageView imageView;
    public static Bitmap bitmap;
    @Override
    protected Bitmap doInBackground(Bitmap... bitmaps)
    {
        bitmap = bitmaps[0];
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        ((ImageView)imageView).setImageBitmap(bitmap);
    }
}
