package com.nauj27.android.coloroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ColoroidActivity extends Activity {
	static final int BITMAP_FROM_CAMERA = 0;
	static final Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        imageCaptureIntent.putExtra("return-data", true);
		startActivityForResult(imageCaptureIntent, BITMAP_FROM_CAMERA);

		Button button =   (Button)findViewById(R.id.bHacerFoto);
        button.setOnClickListener(buttonOnClickListener);
    }
    
    private OnClickListener buttonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			startActivityForResult(imageCaptureIntent, BITMAP_FROM_CAMERA);
		}
    };
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == BITMAP_FROM_CAMERA) {
    		if (resultCode == RESULT_OK) {
    			// Se recibe una foto devuelta por la aplicaci—n de la c‡mara.
    			// TODO: Mostrar la foto y un bot—n abajo para hacer una nueva foto
    			//       En la foto al tocar sale un mensaje con el c—digo de color!!
    			Log.d("ColoroidActivity", "Datos recibidos");
    		} else {
    			// TODO: Mostrar mensajito toast de "No se hizo ninguna foto" o similar.
    			Log.e("ColoroidActivity", "Hubo un problema al recibir los datos");
    		}
    	}
    }
}