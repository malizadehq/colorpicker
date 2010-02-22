/**
 * Activity to make photo.
 */
package com.nauj27.android.colorpicker;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

/**
 * @author nauj27
 *
 */
public class TakePhotoActivity extends Activity {
	private static final String TAG = "TakePhotoActivity";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.takephotolayout);
		
		Camera camera = Camera.open();
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceViewCamera);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		try {
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			Log.e(TAG, "Error setting preview holder for camera object");
		}
		
        surfaceView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				Log.d(TAG, "Screen touched");
				
				int action = motionEvent.getAction();
				
				switch(action) {
					case(MotionEvent.ACTION_DOWN):
						//camera.autoFocus(autoFocusCallback);
				}
				return false;
			}
		});
	}
}
