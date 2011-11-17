package com.Tang.Maps;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class CameraMode extends Activity {
	
	/** Members */
	private DrawingSurface mSurface;
	
	private SensorManager manager = ContainerBox.topManager;
	private Sensor sensor = ContainerBox.topSensor;
	private SensorEventListener listener;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurface = new DrawingSurface(this);
        setContentView(mSurface);
        
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	listener = new SensorEventListener(){

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				mSurface.setCurrent(event.values);
				Log.e("========== Sensors","roll = "+event.values[2]);
				if(Math.abs(event.values[2])<30)
					CameraMode.this.finish();
			}
    		
    	};
    	
    	manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	manager.unregisterListener(listener);
    }
    
   
}
