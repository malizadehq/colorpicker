package com.nauj27.android.colorpicker;

import com.nauj27.android.colorpicker.ral.RalColor;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This activity receive a picture and show touched color.
 * @author nauj27
 * Extends Activity
 */
public class ColorPickerActivity extends Activity {
	// Private constants.
	private static final String TAG = "ColorPickerActivity";
	private static final String JPEG_PICTURE = "JPEG_PICTURE";
	private static final int DIALOG_COLOR_ID = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        
    	// From http://www.designerandroid.com/?p=73
    	// This is the only way camera preview work on all android devices at
    	// full screen
    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	// No title, no name: Full screen
    	getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		// Set the default layout for this activity
        setContentView(R.layout.color_picker_layout);
        
        // Get context and ImageView view for later usage
        Context context = getApplicationContext();
        ImageView imageView = (ImageView)findViewById(R.id.ivPicture);
        
        // Get JPEG image from extras from the Intent that launch this activity
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras.containsKey(JPEG_PICTURE)) {
        	byte[] jpegPicture = bundleExtras.getByteArray(JPEG_PICTURE);    			
			
			int offset = 0;
			int length = jpegPicture.length;
			
			// Obtain bitmap from the JPEG data object
			Bitmap bitmap = BitmapFactory
				.decodeByteArray(jpegPicture, offset, length);
			
			// Set the bitmap as background image of the image view
	        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setImageBitmap(bitmap);
			
			// Show a bit of help to the user :)
	    	CharSequence charSequence = getString(R.string.color_picker_photo_help);
	    	int duration = Toast.LENGTH_SHORT;
	    	Toast toast = Toast.makeText(context, charSequence, duration);
	    	toast.show();
        } else {
        	// JPEG data is not in the bundle extra received
	    	finishActivity(RESULT_CANCELED);
        }
        
        /**
         * Set the listener for touch event into the image view.
         */
        imageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				Log.d(TAG, "Screen touched");
				
				int action = motionEvent.getAction();
				
				switch(action) {
					case(MotionEvent.ACTION_DOWN):
						int x = (int)motionEvent.getX();
						int y = (int)motionEvent.getY();
						Log.d(TAG, "Position: " + x + ", " + y);
						
						int color = Utils.findColor(view, x, y);
						RalColor ralColor = new RalColor(color);
						
						// This is how to show color with dialog
						/*protected Dialog onCreateDialog(int id) {
						    Dialog dialog;
						    switch(id) {
						    case DIALOG_PAUSED_ID:
						        // do the work to define the pause Dialog
						        break;
						    case DIALOG_GAMEOVER_ID:
						        // do the work to define the game over Dialog
						        break;
						    default:
						        dialog = null;
						    }
						    return dialog;
						}*/
						// And then:
						// showDialog(DIALOG_PAUSED_ID);
						
						// This is how to show color with toast
						CharSequence msg = "Color: (" 
							+ Color.red(color) + ", "
							+ Color.green(color) + ", "
							+ Color.blue(color) + ") "
							+ ralColor.getName();
						Context context = getApplicationContext();
				    	int duration = Toast.LENGTH_SHORT;
				    	Toast toast = Toast.makeText(context, msg, duration);
				    	toast.show();
				}
				return false;
			}
		});
    }
}
