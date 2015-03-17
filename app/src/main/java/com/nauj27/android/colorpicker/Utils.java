/**
 *  Color Picker by Juan Martín
 *  Copyright (C) 2012 - 2015 nauj27.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.nauj27.android.colorpicker;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

//import android.util.Log;

/**
 * @author Juan Martín
 *
 */
public class Utils {
    private static final int X_SCALE    = 0;
    private static final int X_POSITION = 2;
    private static final int Y_SCALE    = 4;
    private static final int Y_POSITION = 5;

    private static final int AVERAGED_MATRIX_3x3 = 1;

	/**
	 * Fill hex string with "0" when hexString minor than F.
	 * @param hexString Hexadecimal string value
	 * @return hexadecimal value with two digits
	 */
	public static String beautyHexString(String hexString) {
		if (hexString.length() < 2) {
			return "0".concat(hexString);
		} else {
			return hexString;
		}
	}
	
	/**
     * Find components of color of the bitmap at x, y. 
     * @param iTouchedX Distance from left border of the View
     * @param iTouchedY Distance from top of the View
     * @param view Touched surface on screen
     */
	public static int findColor(View view, int iTouchedX, int iTouchedY)
	    throws NullPointerException, OutOfBitmapException {
		
		int iRed = 0;
    	int iGreen = 0;
    	int iBlue = 0;
    	int iColorRGB;

        // iAverageOffset is used to compute average color
        // information, moving from touched point as much as
        // this value indicates.
    	int iAverageOffset = AVERAGED_MATRIX_3x3;
    	int pixelsNumber = 0;
    	
    	// Get the bitmap from the view.
    	ImageView imageView = (ImageView)view;
    	BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
    	Bitmap imageBitmap = bitmapDrawable.getBitmap();

        // Get position and size of the bitmap
        Matrix imageMatrix = imageView.getImageMatrix();
        float[] fValues;
        fValues = new float[9];
        imageMatrix.getValues(fValues);

        float bitmapPositionX = fValues[X_POSITION];
        float bitmapPositionY = fValues[Y_POSITION];
        float bitmapWidth = imageBitmap.getWidth() * fValues[X_SCALE];
        float bitmapHeight = imageBitmap.getHeight() * fValues[Y_SCALE];

        // Check if the touched point is inside the bitmap
        if (iTouchedX < bitmapPositionX || iTouchedX > (bitmapPositionX + bitmapWidth) ||
                iTouchedY < bitmapPositionY || iTouchedY > (bitmapPositionY + bitmapHeight)) {
            //Log.d(Utils.class.getSimpleName(), "Touched point is out of the bitmap");
            throw new OutOfBitmapException();
        }

        //Log.d(Utils.class.getSimpleName(), "Touched point is in the bitmap");
        int xImage = (int) (iTouchedX - bitmapPositionX);
        int yImage = (int) (iTouchedY - bitmapPositionY);

        // Calculate the target in the bitmap.
        xImage = (int)(xImage * ((double)imageBitmap.getWidth() / (double)bitmapWidth));
        yImage = (int)(yImage * ((double)imageBitmap.getHeight() / (double)bitmapHeight));
    	
        // Average of pixels color around the center of the touch.
    	for (int i = xImage - iAverageOffset; i <= xImage + iAverageOffset; i++) {
    		for (int j = yImage - iAverageOffset; j <= yImage + iAverageOffset; j++) {
    			try {
        			iColorRGB = imageBitmap.getPixel(i, j);
        			iRed += Color.red(iColorRGB);
        			iGreen += Color.green(iColorRGB);
        			iBlue += Color.blue(iColorRGB);
        			pixelsNumber += 1;

        		} catch(Exception e) {
                    throw new NullPointerException();
        		}	
    		}
    	}
    	iRed = iRed / pixelsNumber;
    	iGreen = iGreen / pixelsNumber;
    	iBlue = iBlue / pixelsNumber;
    	
    	return Color.rgb(iRed, iGreen, iBlue);
	}
}
