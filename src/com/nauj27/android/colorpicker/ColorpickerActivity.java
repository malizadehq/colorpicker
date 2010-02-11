package com.nauj27.android.colorpicker;

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

public class ColorpickerActivity extends Activity {
	private static final String TAG = "ColorpickerActivity";
	static final int BITMAP_FROM_CAMERA = 0;
	static final Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	static final private int MENU_TAKE_PHOTO_ITEM = Menu.FIRST;
	private boolean photoTaken = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photolayout);        
        imageCaptureIntent.putExtra("return-data", true);
        
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
     * @param x
     * @param y
     */
    private void findColor(View view, int x, int y) {
    	int offset = 1; // 3x3 Matrix
    	int red = 0;
    	int green = 0;
    	int blue = 0;
    	int color = 0;
    	int pixelsNumber = 0;
    	
    	ImageView imageView = (ImageView)view;
    	Log.d(TAG, "View size: " + imageView.getWidth() + "x" + imageView.getHeight());
    	BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
    	Bitmap imageBitmap = bitmapDrawable.getBitmap();
    	Log.d(TAG, "Bitmap size: " + imageBitmap.getWidth() + "x" + imageBitmap.getHeight());
    	    	
    	for (int i = x - offset; i <= x + offset; i++) {
    		for (int j = y - offset; j <= y + offset; j++) {
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
    	// colorInt = bitmap.getPixel(x, y);
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
    
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	super.onOptionsItemSelected(menuItem);
    	
    	// Search for menu item.
    	switch(menuItem.getItemId()) {
	    	case(MENU_TAKE_PHOTO_ITEM):
	    		startActivityForResult(imageCaptureIntent, BITMAP_FROM_CAMERA);
	    		return true;
    	}
    	
    	// Menu item does not exist.
    	return false;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == BITMAP_FROM_CAMERA) {
    		if (resultCode == RESULT_OK) {
    			Log.d(TAG, "Received Uri: " + data.toURI());
    			
    			Uri uri = Uri.parse(data.toURI());
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