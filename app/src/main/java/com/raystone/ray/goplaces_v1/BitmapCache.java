package com.raystone.ray.goplaces_v1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel1.ImageBucketLevel1Fragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

public class BitmapCache extends Activity {

	public Handler handler = new Handler();
	public final String TAG = getClass().getSimpleName();
	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	public void put(String path, Bitmap bmp) {
		if (!TextUtils.isEmpty(path) && bmp != null) {
			imageCache.put(path, new SoftReference<Bitmap>(bmp));
		}
	}

	public void displayBmp(final ImageView iv, final String thumbPath,
			final String sourcePath, final ImageCallback callback) {
		if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath)) {
			Log.e(TAG, "no paths pass in");
			return;
		}

		final String path;
		final boolean isThumbPath;
		if (!TextUtils.isEmpty(thumbPath)) {
			path = thumbPath;
			isThumbPath = true;
		} else if (!TextUtils.isEmpty(sourcePath)) {
			path = sourcePath;
			isThumbPath = false;
		} else {
			// iv.setImageBitmap(null);
			return;
		}

		if (imageCache.containsKey(path)) {
			SoftReference<Bitmap> reference = imageCache.get(path);
			Bitmap bmp = reference.get();
			if (bmp != null) {
				if (callback != null) {
					callback.imageLoad(iv, bmp, sourcePath);
				}
				iv.setImageBitmap(bmp);
				Log.d(TAG, "hit cache");
				return;
			}
		}
		iv.setImageBitmap(null);

		new Thread() {
			Bitmap thumb;

			public void run() {

				try {
					if (isThumbPath) {
						thumb = BitmapFactory.decodeFile(thumbPath);
						if (thumb == null) {
							thumb = zipImage(sourcePath);
						}						
					} else {
						thumb = zipImage(sourcePath);
					}
				} catch (Exception e) {	
					
				}
				if (thumb == null) {
					thumb = ImageBucketLevel1Fragment.bitmap;
				}
				Log.e(TAG, "-------thumb------"+thumb);
				put(path, thumb);

				if (callback != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							callback.imageLoad(iv, thumb, sourcePath);
						}
					});
				}
			}
		}.start();

	}

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

	public interface ImageCallback {
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params);
	}
}
