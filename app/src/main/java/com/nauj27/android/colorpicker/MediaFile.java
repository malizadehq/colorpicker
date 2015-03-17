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

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Juan Martín
 *
 */
class MediaFile {
    private static final String APPLICATION_NAME = "ColorPicker";

    /** Create a file Uri for saving an image */
    public static Uri getOutputMediaFileUri(){
        try {
            return Uri.fromFile(getOutputMediaFile());
        } catch(NullPointerException e) {
            return null;
        }
    }

    /** Create a File for saving an image */
    private static File getOutputMediaFile(){
        // To be safe, we should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaFile;
        File mediaStorageDir;

        mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), APPLICATION_NAME);

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.e(APPLICATION_NAME, "failed to create directory");
                return null;
            }
        }

        // Create a media file pseudo random name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).
                format(new Date());

        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }
}
