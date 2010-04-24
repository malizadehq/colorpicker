/**
 * Activity to make photo.
 */
package com.nauj27.android.colorpicker;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

/**
 * This activity make focus and take a photo.
 * @author nauj27
 * Extends Activity
 */
public class TakePhotoActivity extends Activity {
	private static final String TAG = "TakePhotoActivity";
	private static final int MENU_TAKE_PHOTO_ITEM = Menu.FIRST;
	private static final int MENU_TAKE_PHOTO_EXIT = 10; // The last one.
	
	private Camera camera = null;
	
	// FIXME: size is not standard, but only can use getValid in 2.1 :(
	private static final int PICTURE_SIZE_WIDTH = 512; //352;
	private static final int PICTURE_SIZE_HEIGHT = 384; //288;
	private static final int PREVIEW_SIZE_WIDTH = 352;
	private static final int PREVIEW_SIZE_HEIGHT = 288;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// From http://www.designerandroid.com/?p=73
    	// This is the only way camera preview work on all android devices
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		// No title, no name: Full screen
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.take_photo_layout);
		
		ImageButton imageCameraButton = (ImageButton)findViewById(R.id.ImageCameraButton);
		imageCameraButton.setOnClickListener(onClickListener);
		
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceViewCamera);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceCallback);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				
	}
	
	/**
	 * Create the on click listener for the button.
	 */
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			camera.autoFocus(autoFocusCallback);
		}
	};

	
	private AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean arg0, Camera camera) {
	        camera.takePicture(null, null, jpegCallback);			
		}
	};
	
	/**
	 * JPEG data is available after take picture.
	 * Put data into extras of Intent and launch new intent for ColorPicker.
	 */
	private PictureCallback jpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] jpegPicture, Camera camera) {
			final String JPEG_PICTURE = "JPEG_PICTURE";
			
			Intent colorPickerIntent = new Intent(getApplicationContext(), ColorPickerActivity.class);
			colorPickerIntent.putExtra(JPEG_PICTURE, jpegPicture);
			startActivity(colorPickerIntent);
		}
	};
	
	/**
	 * Create the surface callback for 
	 */
	private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}

		@Override
		public void surfaceChanged(
				SurfaceHolder surfaceHolder, int format, int width, int height) {
			
			Camera.Parameters cameraParameters = camera.getParameters();
			
			cameraParameters.setPictureSize(PICTURE_SIZE_WIDTH, PICTURE_SIZE_HEIGHT);
			cameraParameters.setPreviewSize(PREVIEW_SIZE_WIDTH, PREVIEW_SIZE_HEIGHT);
			cameraParameters.setPictureFormat(PixelFormat.JPEG);
			
			camera.setParameters(cameraParameters);
			camera.startPreview();
		}

		@Override
		public void surfaceCreated(SurfaceHolder surfaceHolder) {		
			camera = Camera.open();
			
			try {
				camera.setPreviewDisplay(surfaceHolder);
			} catch (IOException ioException) {
				Log.e(TAG, "Error setting preview display");
				camera.release();
				camera = null;
			}
		}
	};
	
    @Override
    /**
     * Add items to the menu for this activity.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	final int groupId = 0;
    	int menuItemId;
    	int menuItemOrder;
    	int menuItemText;
    	MenuItem menuItem;

    	// Button to take picture
    	menuItemId = MENU_TAKE_PHOTO_ITEM;
    	menuItemOrder = MENU_TAKE_PHOTO_ITEM;
    	menuItemText = R.string.menu_take_photo_item;
    	menuItem = menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
    	menuItem.setIcon(android.R.drawable.ic_menu_camera);
    	
    	// Button to exit the application
    	menuItemId = MENU_TAKE_PHOTO_EXIT;
    	menuItemOrder = MENU_TAKE_PHOTO_EXIT;
    	menuItemText = R.string.menu_take_photo_exit;
    	menuItem = menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
    	menuItem.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	
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
	    		camera.autoFocus(autoFocusCallback);	    	
	    		return true;
	    	case(MENU_TAKE_PHOTO_EXIT):
	    		finish();
	    		return true;
    	}
    	
    	// Menu item does not exist.
    	return false;
    }
}
