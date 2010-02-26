package com.nauj27.android.colorpicker;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Main activity for colorpicker.
 * @author jmartin
 * Extends Activity
 */
public class ColorPickerActivity extends Activity {
	// Private constants.
	private static final String TAG = "ColorpickerActivity";
	private static final String IMAGE_CAPTURE_FILENAME = "ColorPicker.jpg";
	private static final int BITMAP_FROM_CAMERA = 0;
	private static final int MENU_TAKE_PHOTO_ITEM = Menu.FIRST;	
	
	// Private variables.
	private boolean photoTaken = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photolayout);        
        //imageCaptureIntent.putExtra("return-data", true);
        //imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
        //		Uri.fromFile(new File(IMAGE_CAPTURE_FILENAME)));
        
        ImageView imageView = (ImageView)findViewById(R.id.ivPicture);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        
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
							findColor(view, x, y);
						} else {
					    	Context context = getApplicationContext();
					    	CharSequence charSequence = "Haga una foto usando la tecla menu";
					    	int duration = Toast.LENGTH_SHORT;
					    	Toast toast = Toast.makeText(context, charSequence, duration);
					    	toast.show();
						}
				}
				return false;
			}
		});
    }
    
    /**
     * Find components of color of the bitmap at x, y. 
     * @param x Distance from left border of the View
     * @param y Distance from top of the View
     * @param view Touched surface on screen
     */
    private void findColor(View view, int x, int y) {
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
    	
    	Context context = getApplicationContext();
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(context, msg, duration);
    	toast.show();
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
    	if (requestCode == BITMAP_FROM_CAMERA) {
    		if (resultCode == RESULT_OK) {
    			Log.d(TAG, "Received Uri: " + data.toURI());
    			
    			Uri uri = Uri.parse(data.toURI());
    			if (uri.toString().equals("")) {
    				Uri.fromFile(new File(IMAGE_CAPTURE_FILENAME));
    			}
    			BitmapFromUri bitmapFromUri = new BitmapFromUri(getContentResolver(), uri);
    			Bitmap bitmap = bitmapFromUri.getBitmap();
    			
    			ImageView imageView = (ImageView)findViewById(R.id.ivPicture);
    			imageView.setImageBitmap(bitmap);
    			photoTaken = true;
    		} else {
    			Log.w(TAG, "No photo taken.");
    		}
    	}
    }
}
