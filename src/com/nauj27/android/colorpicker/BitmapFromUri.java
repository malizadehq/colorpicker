/**
 * 
 */
package com.nauj27.android.colorpicker;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * @author nauj27
 *
 */
public class BitmapFromUri {
	private Bitmap bitmap;
	
	public BitmapFromUri(ContentResolver contentResolver, Uri uri) {
		try {
			bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}

}