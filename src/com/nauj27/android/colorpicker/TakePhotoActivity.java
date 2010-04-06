/**
 * Activity to make photo.
 */
package com.nauj27.android.colorpicker;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author nauj27
 *
 */
public class TakePhotoActivity extends Activity {
	private static final String TAG = "TakePhotoActivity";
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
		
		// No title, no name: Fullscreen
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.take_photo_layout);
		
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceViewCamera);
		surfaceView.setOnClickListener(onClickListener);
		
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceCallback);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				
	}
	
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
	
	
	private PictureCallback jpegCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] jpegPicture, Camera camera) {
			final String JPEG_PICTURE = "JPEG_PICTURE";
			
			Intent photoIntent = new Intent();			
			photoIntent.putExtra(JPEG_PICTURE , jpegPicture);
			setResult(RESULT_OK, photoIntent);
			
			finish();
		}
	};
	
	
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
			
			// Show a bit of help :)
			Context context = getApplicationContext();
	    	CharSequence charSequence = getString(R.string.take_photo_help);
	    	int duration = Toast.LENGTH_LONG;
	    	Toast toast = Toast.makeText(context, charSequence, duration);
	    	toast.show();
			
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
}
