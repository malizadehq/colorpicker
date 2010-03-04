package com.nauj27.android.colorpicker;

import com.nauj27.android.colorpicker.ral.RalColor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Main activity for color picker.
 * @author nauj27
 * Extends Activity
 */
public class ColorPickerActivity extends Activity {
	// Private constants.
	private static final String TAG = "ColorPickerActivity";
	private static final int BITMAP_FROM_CAMERA = 0;
	private static final int MENU_TAKE_PHOTO_ITEM = Menu.FIRST;	
	
	// Private variables.
	private boolean photoTaken = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        
    	// From http://www.designerandroid.com/?p=73
    	// This is the only way camera preview work on all android devices
    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
    	// No title, no name: Fullscreen
    	getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.color_picker_layout);
        
        ImageView imageView = (ImageView)findViewById(R.id.ivPicture);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        
        // Background splash image
        imageView.setBackgroundResource(R.drawable.splash);
        
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
						if (photoTaken) {
							int color = Utils.findColor(view, x, y);
							RalColor ralColor = new RalColor(color);
							
							CharSequence msg = "Color: (" 
								+ Color.red(color) + ", "
								+ Color.green(color) + ", "
								+ Color.blue(color) + ") "
								+ ralColor.getName();
							Context context = getApplicationContext();
					    	int duration = Toast.LENGTH_SHORT;
					    	Toast toast = Toast.makeText(context, msg, duration);
					    	toast.show();
						} else {
					    	Context context = getApplicationContext();
					    	CharSequence charSequence = getString(R.string.help_message);
					    	int duration = Toast.LENGTH_SHORT;
					    	Toast toast = Toast.makeText(context, charSequence, duration);
					    	toast.show();
						}
				}
				return false;
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	int groupId = 0;
    	int menuItemId = MENU_TAKE_PHOTO_ITEM;
    	int menuItemOrder = Menu.NONE;
    	int menuItemText = R.string.menu_take_photo_item;
    	
    	// Create the menu item and keep a reference to it.
    	//MenuItem menuItem = menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
    	menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
    	//menuItem.setIcon(R.drawable.menu_item_icon);
    	return true;
    }
    
    @Override
    /**
     * What to do when a option menu is selected.
     * @param menuItem Item selected from menu.
     */
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	super.onOptionsItemSelected(menuItem);
    	
    	// Search for menu item.
    	switch(menuItem.getItemId()) {
	    	case(MENU_TAKE_PHOTO_ITEM):
	    		Intent takePhotoIntent = new Intent(this, TakePhotoActivity.class);
	    		startActivityForResult(takePhotoIntent, BITMAP_FROM_CAMERA);	    	
	    		return true;
    	}
    	
    	// Menu item does not exist.
    	return false;
    }
    
    @Override
    /**
     * Overrides actions on activity result from other activity.
     * @param requestCode The request code
     * @param resultCode The result code
     * @param data Data returned from activity
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	final String JPEG_PICTURE = "JPEG_PICTURE";
    	
    	switch(requestCode) {
    	case BITMAP_FROM_CAMERA:
    		if (resultCode == RESULT_OK) {
    			
    			Bundle bundle = data.getExtras();
    			if (bundle.containsKey(JPEG_PICTURE)) {
    				
    				byte[] jpegPicture = bundle.getByteArray(JPEG_PICTURE);    			
    			
	    			int offset = 0;
	    			int length = jpegPicture.length;
	    			Bitmap bitmap = BitmapFactory
	    				.decodeByteArray(jpegPicture, offset, length);
	    			
	    			ImageView imageView = (ImageView)findViewById(R.id.ivPicture);
	    			imageView.setImageBitmap(bitmap);
	    			
	    			// Show a bit of help :)
	    			Context context = getApplicationContext();
	    	    	CharSequence charSequence = getString(R.string.color_picker_photo_help);
	    	    	int duration = Toast.LENGTH_SHORT;
	    	    	Toast toast = Toast.makeText(context, charSequence, duration);
	    	    	toast.show();
	    			
	    			photoTaken = true;
	    			
    			} else {
    				// pass
    			}
    			
    		}
    		break;
    	}
    }
}
