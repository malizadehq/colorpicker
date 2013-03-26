/**
 *  Color Picker by Juan Martín
 *  Copyright (C) 2012 nauj27.com
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nauj27.android.colorpicker.ral.RalColor;

/**
 * @author Juan Martín
 *
 */
public class ColorPickerActivity extends Activity {
	private static final String APPLICATION_NAME = "ColorPicker";
	
	private static final int CAPTURE_ACTIVITY_REQUEST_CODE = 100;
	private static final int SELECT_ACTIVITY_REQUEST_CODE = 200;
	
	private static final String KEY_PHOTO_PATH = "photoUri";
	private static final String KEY_COLOR_COMPONENTS = "rgb";
	
	private Uri photoUri;
	private ImageView imageView;
	private RalColor ralColor = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(APPLICATION_NAME, "onCreate method entered");
		setContentView(R.layout.activity_color_picker);
		
		if (imageView == null) {
			imageView = (ImageView)findViewById(R.id.imageView);
			if (imageView != null) {
				ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
				viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						if (photoUri != null) {
							try {
								showCapturedImage();
								updateResultData();
							} catch (FileNotFoundException e) {
								Log.e(APPLICATION_NAME, "File ".
										concat(photoUri.getPath()).concat(" not found!"));
							} catch (Exception e) {
								// Ignore
							}
						}
						
						/**
				         * Set the listener for touch event into the image view.
				         */
				        //imageView.setOnTouchListener(onTouchListener);
						imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				});
			}
		}
		
		if (savedInstanceState != null) {
			String photoUriPath = savedInstanceState.getString(KEY_PHOTO_PATH);
			if (photoUriPath != null) {
				photoUri = Uri.fromFile(new File(photoUriPath));
			}
			
			if (savedInstanceState.containsKey(KEY_COLOR_COMPONENTS)) {
				ralColor = new RalColor(
						savedInstanceState.getInt(KEY_COLOR_COMPONENTS));
			}
		}
		
		
	}
	
	protected void updateResultData() {
		int index = 0;
		int red = Color.red(ralColor.getColor());
		int green = Color.green(ralColor.getColor());
		int blue = Color.blue(ralColor.getColor());
		
		// Set the color name from localized resource
		index = ralColor.getIndex();
		String[] colorNames = getResources().getStringArray(R.array.color_names);
		
		TextView textViewColorName = (TextView) findViewById(R.id.textViewColorName);
		try {
			textViewColorName.setText(colorNames[index]);
		} catch (ArrayIndexOutOfBoundsException e) {
			// FIXME: something was wrong with indexes. Maybe there is one
			// more color than color names :-?
			// Maybe the user can see odd color names!! Please test!!
			textViewColorName.setText(colorNames[colorNames.length-1]);
		}
		
		
		ImageView imageViewColor = (ImageView)findViewById(R.id.imageViewColor);
		imageViewColor.setBackgroundColor(ralColor.getColor());
		
		TextView textViewRal = (TextView)findViewById(R.id.textViewRal);
		textViewRal.setText(
			"RAL: ".concat(Integer.toString(ralColor.getCode(), 10)));
		
		TextView textViewRgb = (TextView)findViewById(R.id.textViewRgb);
		textViewRgb.setText(
			"RGB: ".concat(Integer.toString(red , 10)).
			concat(", ").concat(Integer.toString(green, 10)).
			concat(", ").concat(Integer.toString(blue, 10)));
			
		TextView textViewHex = (TextView)findViewById(R.id.textViewHex);
		textViewHex.setText(
			"HEX: #".concat(Utils.beautyHexString(Integer.toHexString(red))).
			concat(Utils.beautyHexString(Integer.toHexString(green))).
			concat(Utils.beautyHexString(Integer.toHexString(blue))));
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(APPLICATION_NAME, "onResume method entered");
		
		imageView = (ImageView) findViewById(R.id.imageView);
		if (imageView != null) {
			ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
			viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					if (photoUri != null) {
						try {
							showCapturedImage();
							updateResultData();
						} catch (FileNotFoundException e) {
							Log.e(APPLICATION_NAME, "File ".
									concat(photoUri.getPath()).concat(" not found!"));
						} catch (Exception e) {
							// Ignore
						}
					}
					
					/**
			         * Set the listener for touch event into the image view.
			         */
			        imageView.setOnTouchListener(onTouchListener);
					imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			});
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(APPLICATION_NAME, "onStop method entered");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(APPLICATION_NAME, "onDestroy method entered");
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(APPLICATION_NAME, "onSaveInstanceState");
		
		if (photoUri != null) {
			String realPath;
			try {
				realPath = getRealPathFromURI(photoUri);
			} catch (UnsupportedEncodingException e) {
				realPath = null;
			}
			outState.putString(KEY_PHOTO_PATH, realPath);
		}
		
		if (ralColor != null) {
			outState.putInt(KEY_COLOR_COMPONENTS, ralColor.getColor());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_color_picker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		switch (menuItem.getItemId()) {
		case R.id.picture_from_camera:
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			
			photoUri = getOutputMediaFileUri();
			if (photoUri == null) {
				Toast.makeText(
						this, R.string.cant_write_external_storage, 
						Toast.LENGTH_LONG).show();
				return true;
			}
			
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, CAPTURE_ACTIVITY_REQUEST_CODE);
			
			return true;
			
		case R.id.picture_from_gallery:
			Intent intentGallery = new Intent();
			intentGallery.setType("image/*");
			intentGallery.setAction(Intent.ACTION_GET_CONTENT);
			
			startActivityForResult(
				Intent.createChooser(
						intentGallery, 
						getString(R.string.select_picture)),
				SELECT_ACTIVITY_REQUEST_CODE);
			
			return true;
			
		default:
			return super.onOptionsItemSelected(menuItem);
		}
	}
	
	OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			
			int action = motionEvent.getAction();
			switch(action) {
				case(MotionEvent.ACTION_DOWN):
					int x = (int)motionEvent.getX();
					int y = (int)motionEvent.getY();
					int color;

					// Must check for null pointer cause "Droid" report
					// this error once in the market developer website
					try {
						color = Utils.findColor(view, x, y);
					} catch (NullPointerException e) {
						return false;
					}
					
					// Check if ralColor already exist as RalColor
					if (ralColor == null) { 
						ralColor = new RalColor(color);
					} else {
						ralColor.setColor(color);
					}
					
					updateResultData();
			}
			return false;
		}
	};
	
	@Override
	protected void onActivityResult(
		int requestCode, 
		int resultCode, 
		Intent data) {
		if (requestCode == CAPTURE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.i(APPLICATION_NAME, "Capture result OK");
				try {
					showCapturedImage();
				} catch (FileNotFoundException fileNotFoundException) {
					// Image decode failed, advise user
				} catch (NullPointerException nullPointerException) {
					imageView = (ImageView)findViewById(R.id.imageView);
					try {
						showCapturedImage();
					} catch (Exception exception) {
						// Do nothing
					}
				}
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
				Toast.makeText(this, R.string.action_canceled,
						Toast.LENGTH_SHORT).show();
			} else {
				// Image capture failed, advise user
			}
		}
		
		if (requestCode == SELECT_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.i(APPLICATION_NAME, "Select result OK");
				try {
					photoUri = data.getData();
					showCapturedImage();
				} catch (FileNotFoundException e) {
					// Image decode failed, advise user
				} catch (NullPointerException e) {
					imageView = (ImageView)findViewById(R.id.imageView);
					try {
						showCapturedImage();
					} catch (Exception exception) {
						// Do nothing
					}
				}
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
				Toast.makeText(this, R.string.action_canceled,
						Toast.LENGTH_SHORT).show();
			} else {
				// Image capture failed, advise user
			}
		}
		
		if (resultCode == RESULT_OK) {
			imageView.setOnTouchListener(onTouchListener);
		}
	}

	/**
	 * Shows the image captured by the camera into the image view
	 * @param data
	 * @throws FileNotFoundException 
	 */
	private void showCapturedImage() throws 
	FileNotFoundException, NullPointerException {
		//ImageView imageView = (ImageView)findViewById(R.id.imageView);
		if (imageView == null) {
			throw new NullPointerException();
		}
		
		// Get the dimensions of the container
		FrameLayout frameLayoutImage = (FrameLayout)findViewById(R.id.frameLayoutImage);
		int targetW = frameLayoutImage.getWidth();
	    int targetH = frameLayoutImage.getHeight();
	  
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(
	    		this.getContentResolver().openInputStream(photoUri), 
	    		null, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	  
	    // Determine how much to scale down the image
	    int scaleFactor = 1;
	    try {
	    	scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	    } catch (ArithmeticException arithmeticException) {
	    	Log.w(APPLICATION_NAME, "frameLayout not yet inflated, no scaling");
	    }
	    
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	  
	    try {
		    Bitmap bitmap = BitmapFactory.decodeStream(
		    		this.getContentResolver().openInputStream(photoUri), 
		    		null, bmOptions);
		    
		    int bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
		    //int bitmapSize = bitmap.getByteCount(); // API 12
		    
		    if ( bitmapSize > 20000000) {
		    	bmOptions.inSampleSize = 2;
		    	bitmap = BitmapFactory.decodeStream(
			    		this.getContentResolver().openInputStream(photoUri), 
			    		null, bmOptions);
		    }
		    
		    imageView.setImageBitmap(bitmap);
	    } catch (OutOfMemoryError e) {
	    	Log.e(APPLICATION_NAME, e.getLocalizedMessage());
	    }
	    
	}
	
	/** Create a file Uri for saving an image */
	private static Uri getOutputMediaFileUri(){
		try {
			return Uri.fromFile(getOutputMediaFile());
		} catch(NullPointerException e) {
			return null;
		}
	}

	/** Create a File for saving an image */
	private static File getOutputMediaFile(){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(
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
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
        "IMG_"+ timeStamp + ".jpg");

	    return mediaFile;
	}
	
	/**
	 * Get the real path from URI.
	 * @param contentUri
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings("deprecation")
	private String getRealPathFromURI(Uri contentUri) throws UnsupportedEncodingException {
		String realPath;
		
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        if (cursor == null) {
        	realPath = contentUri.toString();
        } else {
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        realPath = cursor.getString(column_index);
        }
        
        // Hack to avoid file:// repetition
        if (realPath.startsWith("file://")) {
        	realPath = realPath.substring("file://".length());
        }
        return URLDecoder.decode(realPath, "UTF-8");
    }
	
	/*
	 * Not deprecated.
	 * 
	private String getRealPathFromURI(Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
	    Cursor cursor = loader.loadInBackground();
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	*/
}
