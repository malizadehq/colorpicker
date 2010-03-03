/**
 * 
 */
package com.nauj27.android.colorpicker;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * @author nauj27
 *
 */
public class Utils {
	// Private constants.
	private static final String TAG = "Utils";
	
	/**
     * Find components of color of the bitmap at x, y. 
     * @param x Distance from left border of the View
     * @param y Distance from top of the View
     * @param view Touched surface on screen
     */
	public static int findColor(View view, int x, int y) {
		
		int red = 0;
    	int green = 0;
    	int blue = 0;
    	int color = 0;
    	
    	int offset = 1; // 3x3 Matrix
    	int pixelsNumber = 0;
    	
    	int xImage = 0;
    	int yImage = 0;
    	
    	ImageView imageView = (ImageView)view;
    	Log.d(TAG, "View size: " + imageView.getWidth() + "x" + imageView.getHeight());
    	BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
    	Bitmap imageBitmap = bitmapDrawable.getBitmap();
    	Log.d(TAG, "Bitmap size: " + imageBitmap.getWidth() + "x" + imageBitmap.getHeight());

        // Calculate the target in the bitmap.
    	xImage = (int)(x * ((double)imageBitmap.getWidth() / (double)imageView.getWidth()));
    	yImage = (int)(y * ((double)imageBitmap.getHeight() / (double)imageView.getHeight()));
        Log.d(TAG, "Transformation: " + x + "x" + y + " => " + xImage + "x" + yImage);
    	
        // Average of pixels around the touched one.
    	for (int i = xImage - offset; i <= xImage + offset; i++) {
    		for (int j = yImage - offset; j <= yImage + offset; j++) {
    			try {
        			color = imageBitmap.getPixel(i, j);
        			red += Color.red(color);
        			green += Color.green(color);
        			blue += Color.blue(color);
        			pixelsNumber += 1;
        			Log.d(TAG, "Color del punto " + i + " :(" + 
        					Color.red(color) + ", " + Color.green(color) + 
        					", " + Color.blue(color) + ")");
        		} catch(Exception e) {
        			Log.w(TAG, "Error picking color!");
        		}	
    		}
    	}
    	red = red / pixelsNumber;
    	green = green / pixelsNumber;
    	blue = blue / pixelsNumber;
    	
    	CharSequence msg = "Color: (" + red + ", " + green + ", " + blue + ")"; 
    	Log.d(TAG, (String)msg);
    	
    	return Color.rgb(red, green, blue); 
    	
	}

}
