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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.FileNotFoundException;

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

	private static final int LIMIT_MB = 20000000;
	private static final int SAMPLE_HALF = 2;
	
	private Uri photoUri;
	private ImageView imageView;
	private RalColor ralColor = null;

    // These matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private float oldDist = 1f;

	OnTouchListener onTouchListener = new OnTouchListener() {

		// We can be in one of these 3 states
		static final int NONE = 0;
		static final int DRAG = 1;
		static final int ZOOM = 2;

		int mode = NONE;

		// Remember some things for zooming
		PointF start = new PointF();
		PointF mid = new PointF();

		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {

			int action = motionEvent.getAction();
			switch(action & MotionEvent.ACTION_MASK) {
				case(MotionEvent.ACTION_DOWN):

					int x = (int)motionEvent.getX();
					int y = (int)motionEvent.getY();
					int color;

					// Must check for null pointer cause "Droid" report
					// this error once in the market developer website
					try {
						color = Utils.findColor(view, x, y);

					} catch (NullPointerException nullPointerException) {
						return false;

					} catch (OutOfBitmapException outOfBitmapException) {
                        savedMatrix.set(matrix);
                        start.set(x, y);
                        mode = DRAG;
                        break;

                    }

					// Check if ralColor already exist as RalColor
					if (ralColor == null) {
						ralColor = new RalColor(color);
					} else {
						ralColor.setColor(color);
					}

					updateResultData();

					savedMatrix.set(matrix);
					start.set(x, y);
					mode = DRAG;
					break;

				case MotionEvent.ACTION_POINTER_DOWN:

					oldDist = spacing(motionEvent);

					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, motionEvent);
						mode = ZOOM;
					}
					break;

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:

					mode = NONE;
					break;

				case MotionEvent.ACTION_MOVE:

					if (mode == DRAG) {

						matrix.set(savedMatrix);
						matrix.postTranslate(motionEvent.getX() - start.x, motionEvent.getY() - start.y);

					} else if (mode == ZOOM) {

						float newDist = spacing(motionEvent);
						if (newDist > 5f) {
							matrix.set(savedMatrix);
							float  scale = newDist / oldDist; // setting the scaling of the
							// matrix...if scale > 1 means
							// zoom in...if scale < 1 means
							// zoom out
							matrix.postScale(scale, scale, mid.x, mid.y);
						}

					}
					break;

			}

			((ImageView)view).setImageMatrix(matrix);
			return true;
		}

		/** Determine the space between the first two fingers */
		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return (float) Math.sqrt(x * x + y * y);
		}

		/** Calculate the mid point of the first two fingers */
		private void midPoint(PointF point, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}
	};
	
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
								if (ralColor != null) updateResultData();
							} catch (FileNotFoundException e) {
								Log.e(APPLICATION_NAME, "File ".
										concat(photoUri.getPath()).concat(" not found!"));
							} catch (Exception e) {
                                Log.e(APPLICATION_NAME, e.getMessage());
							}
						}

                        imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				});
			}
		}

		if (savedInstanceState != null) {
            Log.d(this.getClass().getSimpleName(), "Restoring saved instance");
			String photoUriPath = savedInstanceState.getString(KEY_PHOTO_PATH);
			if (photoUriPath != null) {
                photoUri = Uri.parse(photoUriPath);
            }

			if (savedInstanceState.containsKey(KEY_COLOR_COMPONENTS)) {
				ralColor = new RalColor(
						savedInstanceState.getInt(KEY_COLOR_COMPONENTS));
			}
		}
	}
	
	protected void updateResultData() {
		int index;
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
            //
            //FIXME: something is wrong with indexes. Maybe there is one more color than names
            //Maybe the user can see odd color names!! Please test!!
            //
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
                            if (ralColor != null) updateResultData();
						} catch (FileNotFoundException e) {
							Log.e(APPLICATION_NAME, "File ".
									concat(photoUri.getPath()).concat(" not found!"));
						} catch (Exception e) {
                            Log.e(APPLICATION_NAME, e.getMessage());
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
			String realPath = photoUri.toString();
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

			photoUri = MediaFile.getOutputMediaFileUri();
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
	
	@Override
	protected void onActivityResult(
		int requestCode,
		int resultCode,
		Intent data) {

		if ((requestCode == SELECT_ACTIVITY_REQUEST_CODE) && (resultCode == RESULT_OK)) {
				photoUri = data.getData();
		}

		if (resultCode == RESULT_OK) {
            try {
                showCapturedImage();

            } catch (FileNotFoundException fileNotFoundException) {
                Log.e(this.getClass().getSimpleName(), fileNotFoundException.getMessage());

            } catch (NullPointerException nullPointerException) {
                imageView = (ImageView)findViewById(R.id.imageView);
                try {
                    showCapturedImage();
                } catch (Exception exception) {
                    Log.e(this.getClass().getSimpleName(), exception.getMessage());
                }

            }

            // Reset zoom and positioning
            oldDist = 1f;
            matrix.reset();
            savedMatrix.reset();
            imageView.setImageMatrix(new Matrix());

			imageView.setOnTouchListener(onTouchListener);
		}
	}

	/**
	 * Shows the image captured by the camera into the image view
	 * @throws FileNotFoundException
	 */
	private void showCapturedImage()
        throws FileNotFoundException, NullPointerException {

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

	    try {
		    Bitmap bitmap = BitmapFactory.decodeStream(
		    		this.getContentResolver().openInputStream(photoUri),
		    		null, bmOptions);

		    //int bitmapSize = bitmap.getByteCount(); // Reserved for API >= 12
		    int bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
		    if (bitmapSize > LIMIT_MB) {
		    	scaleFactor = SAMPLE_HALF;
		    	bmOptions.inSampleSize = scaleFactor;
		    	bitmap = BitmapFactory.decodeStream(
			    		this.getContentResolver().openInputStream(photoUri),
			    		null, bmOptions);
		    }

		    imageView.setImageBitmap(bitmap);
	    } catch (OutOfMemoryError e) {
	    	Log.e(APPLICATION_NAME, e.getMessage());
	    }

	}
}
