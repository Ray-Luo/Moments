package com.raystone.ray.goplaces.Helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel1.ImageBucketLevel1Fragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/*
 This class is used to show thumbnails
 */

public class BitmapCache extends Activity {

	public Handler handler = new Handler();
	public final String TAG = getClass().getSimpleName();
	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	//  if the thumbnail path and the image are not null, put them in a hashmap for future use
	public void put(String path, Bitmap bmp) {
		if (!TextUtils.isEmpty(path) && bmp != null) {
			imageCache.put(path, new SoftReference<>(bmp));
		}
	}


	//  display the picture. If there is a thumbnail, display the thumbnail, otherwise display the picture itself
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
			return;
		}

/*
In the Adapter's code, every item in the GridView will execute the getView() function. The imageCache will first be empty. After running through one item in the GridView, the item's path and the image will be added into the imageCache. If looping through all the items in the GridView, all the items' path and image will be added in the imageCache. So next time when needing to show the picture, it can just retrieve contents in the imageCache.
 */
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

		//  If the imageCache doesn't have the contents yet, add the item's path and image in the imageCache for future reuse and display the image.
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
					thumb = ImageBucketLevel1Fragment.mBitmap;
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

	//  This function is used to decrease the size of images showing in the GridView, so using less memory
	public static Bitmap zipImage(String path) throws IOException
	{
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		//  retrieve the size of the image when setting this to be true
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap;
		//  resize the image if necessary
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

	//  This interface will be implemented by a specific GridView's Adapter when needing to show pictures. Every item in the GridView will call the imageLoad function to show the image.
	public interface ImageCallback {
		void imageLoad(ImageView imageView, Bitmap bitmap, Object... params);
	}
}
