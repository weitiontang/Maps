package com.Tang.Maps;
import java.util.ArrayList;
import java.util.List;

import com.Tang.Maps.DBAdapter;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;  
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
 
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
 
public class MapsActivity extends MapActivity 
{    
    public static Drawable mDrawable;
	//map member
	private MapView mapView; 
    private MapController mc;
    
    private final String scriptName = "script2";
    
    //gps member
    private GeoPoint p;
    private newGpsPosition gps;
    
    //first access gprs
    boolean isFirst = true;
    
    
    //layer member
    private MyLocationOverlay mylayer;
    private LandMarkOverlay marklayer;
    
    //database member
    private DBAdapter myDB;
    private Cursor cursor;
    
    //sensor member
    //private SensorManager manager;
	//private Sensor sensor;
	//private SensorEventListener listener;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        Log.e("maps","oncreate");
        setContentView(R.layout.main);

//write inner data
        SharedData.load(this,"script1");
        SharedData.load(this,"script2"); 
        
        
//get reference
        mapView = (MapView) findViewById(R.id.mapView);
        mc = mapView.getController();

        
//set database
        myDB = new DBAdapter(MapsActivity.this);
		myDB.selectTable(scriptName);
		
		
//set zoom view       
        LinearLayout zoomLayout = (LinearLayout)findViewById(R.id.zoom);  
        View zoomView = mapView.getZoomControls(); 
 
        zoomLayout.addView(zoomView, 
            new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT)); 
        mapView.displayZoomControls(true);

        
        
        
//gps start
        
        gps = new newGpsPosition(this);
              
        if( !gps.gpsStart() )
        {
        	Toast.makeText(getBaseContext(),"GPS cannot find!!!",Toast.LENGTH_SHORT).show();
        }
                
        
//set to last position
        gps.onLocationChanged(null);

//sensor init
        //manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //ContainerBox.topManager = manager;
        //ContainerBox.topSensor = sensor;
        
        
        
        
        
}
    
    
    
 
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
    
 private void setupMap() {
          List<Overlay> overlays = mapView.getOverlays();
        
           mylayer = new MyLocationOverlay(this, mapView);
           
           mylayer.runOnFirstFix(new Runnable() {
               public void run() {
                   // Zoom in to current location
            	   mapView.setTraffic(true);
                   mc.setZoom(12);
                   mc.animateTo(mylayer.getMyLocation());
               } 
           });
           
           overlays.clear();
           overlays.add(mylayer);
           
           Drawable pin=getResources().getDrawable(android.R.drawable.btn_star_big_on);
           pin.setBounds(0, 0, pin.getMinimumWidth(), pin.getMinimumHeight());
              
           marklayer = new LandMarkOverlay(pin);
           //overlays.clear();
           overlays.add(marklayer);
           mapView.invalidate();
        }
    
public void setOnCenter(GeoPoint center)
{
	mc.setCenter(center);
	mc.setZoom(14); 
	mapView.invalidate();
}
 
 
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_I) 
    {
        // Zooming In
        mc.setZoom(mapView.getZoomLevel() + 1);
        return true;
    } 
    else if (keyCode == KeyEvent.KEYCODE_O) 
    {
        // Zooming Out
        mc.setZoom(mapView.getZoomLevel() - 1);
        return true;
    } 
    else if (keyCode == KeyEvent.KEYCODE_S) 
    {
        // Switch to satellite view
        mapView.setSatellite(true) ;
        mapView.setTraffic(false);
        return true;
    } 
    else if (keyCode == KeyEvent.KEYCODE_T) 
    {
        // Switch on traffic overlays
        mapView.setSatellite(false) ;
        mapView.setTraffic(true);
        return true;
    }
    else if (keyCode == KeyEvent.KEYCODE_R) 
    {
        // reset games
        SharedData.newProgress(this, scriptName);
        
        gps.onLocationChanged(null);
        return true;
    }
    
    
    
    return false;
}  


private class newGpsPosition extends GpsPosition {
	
	newGpsPosition(Context ctx)
	{
		super(ctx);
	}
	
	@Override
    public void onLocationChanged(Location location) {
		
		super.onLocationChanged(location);
		setupMap();
		GeoPoint center = new GeoPoint( (int) (this.latitude * 1E6),(int) (this.longitude * 1E6));
		
		if(isFirst)
		{
		  setOnCenter(center);
		  isFirst = false;
		}
	}
	
	
}



private class LandMarkOverlay extends ItemizedOverlay<OverlayItem> 
{
	
    private List<OverlayItem> items = new ArrayList<OverlayItem>();
    
	private int numOfElement;
	private long lastTime = -1;
	
    
	
    
    public LandMarkOverlay(Drawable defaultMarker) {
            super(mDrawable = boundCenterBottom(defaultMarker));
            /*-------LOAD DATA FROM DATABASE-----------------*/
            Log.e("maps","draw");
            cursor = myDB.getAllTitles();
            cursor.moveToFirst();
            numOfElement = cursor.getCount();
            
            /*-------MARK CURRENT POSITION-----------------*/
           
            
            if(gps.latitude != 0 && gps.longitude != 0)
            {
               	GeoPoint current_gp = new GeoPoint( (int)(gps.latitude*1E6) ,(int)(gps.longitude*1E6));
               	String text =   
                "id: " + cursor.getString(0) + "\n" +
                "NAME: " + cursor.getString(1) + "\n" +
                "LATITUDE: " + cursor.getString(2) + "\n" +
                "LONGITUDE:  " + cursor.getString(3) + "\n" +
                "DATE: " + cursor.getString(4) + "\n" +
                "CONTENT: " + cursor.getString(5);
               	
               	
               	
               	OverlayItem overlayitem = new OverlayItem(current_gp,cursor.getString(0),text);
               	mDrawable =boundCenterBottom( getResources().getDrawable(android.R.drawable.btn_star_big_off));
                           
               	overlayitem.setMarker(mDrawable);
               	items.add(overlayitem);   
               	
               	
               	populate();        	
            	     
            }
            
            
            /*-------CHECK IF APPROACH TO NEXT POSITION-----------------*/
            if(SharedData.recentProgress(MapsActivity.this,scriptName) != numOfElement)
            {
             //does not finish all stages
            	cursor = myDB.getTitle(SharedData.recentProgress(MapsActivity.this,scriptName)+1);
            	
            	double d = SharedData.calcDistance(
            			               Double.parseDouble(cursor.getString(2)), 
            			               Double.parseDouble(cursor.getString(3)), 
            						   gps.latitude, 
            						   gps.longitude);
            
            	if(d <= 200)
            	{ SharedData.addProgress(MapsActivity.this,scriptName); }
            }
            else 
            {
              //finished all stage
                  	
            }
            
            cursor = myDB.getAllTitles();
            cursor.moveToFirst();
            /*--------MARK FINISHED POSITION----------------*/
            for(int i = 0; i < numOfElement && i < SharedData.recentProgress(MapsActivity.this,scriptName)+1; i++)
            {
            	GeoPoint gp = new GeoPoint( (int)( Double.parseDouble(cursor.getString(2))*1E6) ,
            			                    (int)( Double.parseDouble(cursor.getString(3))*1E6));
            	String text =   
                        "id: " + cursor.getString(0) + "\n" +
                        "NAME: " + cursor.getString(1) + "\n" +
                        "LATITUDE: " + cursor.getString(2) + "\n" +
                        "LONGITUDE:  " + cursor.getString(3) + "\n" +
                        "DATE: " + cursor.getString(4) + "\n" +
                        "CONTENT: " + cursor.getString(5);
            	
            	items.add(new OverlayItem(gp,cursor.getString(0),text));
              	populate();
              	cursor.moveToNext();
            }

                        
                
    }

 
    @Override
    protected OverlayItem createItem(int i) {
            // TODO Auto-generated method stub
            Log.e("MAPS", String.valueOf(i));
    		return items.get(i);
    }

    @Override
    public int size() {
            // TODO Auto-generated method stub
            return items.size();
    }

    @Override
    protected boolean onTap(int pIndex) 
    {
       Toast.makeText(MapsActivity.this,items.get(pIndex).getSnippet(),Toast.LENGTH_SHORT)
       .show();
       return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) 
    {   
        //---when user lifts his finger---
        if (event.getAction() == MotionEvent.ACTION_DOWN) 
        {                
            GeoPoint p = mapView.getProjection().fromPixels(
                (int) event.getX(),
                (int) event.getY()  );
            Toast.makeText(getBaseContext(), 
            	p.getLatitudeE6() / 1E6 + "," + 
                p.getLongitudeE6() /1E6 , 
                Toast.LENGTH_SHORT).show();
        }

        
        if (event.getAction() == MotionEvent.ACTION_DOWN){

            if(event.getEventTime()-lastTime<2000)
            {
              GeoPoint center = new GeoPoint( (int) (gps.latitude * 1E6),(int) (gps.longitude * 1E6));
             // setOnCenter(center);
            }
        }       
        lastTime=event.getEventTime();    
        
     
        return false;
    
    }        

}    
    
    
/*  
@Override
public void onResume(){
	super.onResume();
	if(sensor!=null){
    	listener = new SensorEventListener(){

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				//Log.e("========= Sensor Listener","Pitch = "+event.values[2]);
				if(Math.abs(event.values[2])>45){
					Intent intent = new Intent();
					intent.setClass(MapsActivity.this, CameraMode.class);
					startActivity(intent);
				}
			}
    		
    	};
    	manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }
}

@Override
public void onPause(){
	super.onPause();
	manager.unregisterListener(listener);
}


*/



    
    
    
    
    
    
        
    
    
    class MapOverlay extends com.google.android.maps.Overlay
    {
    	private DBAdapter myDB;
        private Cursor cursor;
    	private String script;
        private double[] latitudes;
        private double[] longitudes;
    	private int numOfElement;
    	private boolean isFirstCenterSet = false;
    	private long lastTime = -1;
        public MapOverlay(String script_name)
    	{
    		//Log.e("Map", "MAP CREATE");
    		script = script_name;
    		
    		myDB = new DBAdapter(MapsActivity.this);
    		myDB.newTable(script);
    		myDB.selectTable(script);
            cursor = myDB.getAllTitles();
            cursor.moveToFirst();
            numOfElement = cursor.getCount();
            latitudes = new double[numOfElement];
            longitudes = new double[numOfElement];
            
            for(int i = 0; i < numOfElement; i++)
            {
            	
            	latitudes[i] = Double.parseDouble(cursor.getString(2));
            	longitudes[i] = Double.parseDouble(cursor.getString(3));
            	cursor.moveToNext();
            	
            }
            myDB.close();
            
                         	
    	}
        
    	
    	@Override
        public boolean draw(Canvas canvas, MapView mapView,boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow,when);                   
            //Log.e("Map","DRAW MAP!!!!");
            
            Point screenPts = new Point();
        	Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.pushpin);
                     
            
            
            /*-------MARK CURRENT POSITION-----------------*/
            if(gps.latitude != 0 && gps.longitude != 0)
            {
            
            //get position
            	GeoPoint current_gp = new GeoPoint( (int)(gps.latitude*1E6) ,(int)(gps.longitude*1E6));
            //translate to pixel
            	mapView.getProjection().toPixels(current_gp, screenPts);
            //---add the marker---
            	canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);         
                    	
            	if(!isFirstCenterSet)
            	{
            		//set first center	
            		toCurrentPosition();
                    isFirstCenterSet = true;
            	}
                      
            }
            
            
            /*-------CHECK IF APPROACH TO NEXT POSITION-----------------*/
            if(SharedData.recentProgress(MapsActivity.this,script) != numOfElement)
            {
             //does not finish all stages
            	double d = SharedData.calcDistance(
            			               latitudes[SharedData.recentProgress(MapsActivity.this,script)], 
            						   longitudes[SharedData.recentProgress(MapsActivity.this,script)], 
            						   gps.latitude, 
            						   gps.longitude);
            
            	if(d <= 200)
            	{ SharedData.addProgress(MapsActivity.this,script); }
            }
            else 
            {
              //finished all stage
            
            	
            }
            
            
            /*--------MARK FINISHED POSITION----------------*/
            for(int i = 0; i < numOfElement && i < SharedData.recentProgress(MapsActivity.this,script)+1; i++)
            {
            	//get position
                GeoPoint gp = new GeoPoint( (int)(latitudes[i]*1E6) ,(int)(longitudes[i]*1E6));
              	//translate to pixel
            	mapView.getProjection().toPixels(gp, screenPts);
             	//---add the marker---
                canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);         
               
            }
            
            
            return true;
        }
   
        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView) 
        {   
            //---when user lifts his finger---
            if (event.getAction() == MotionEvent.ACTION_DOWN) 
            {                
                GeoPoint p = mapView.getProjection().fromPixels(
                    (int) event.getX(),
                    (int) event.getY()  );
                Toast.makeText(getBaseContext(), 
                	p.getLatitudeE6() / 1E6 + "," + 
                    p.getLongitudeE6() /1E6 , 
                    Toast.LENGTH_SHORT).show();
            }
               
            if (event.getAction() == MotionEvent.ACTION_DOWN){

                if(event.getEventTime()-lastTime<2000)
                {
                	toCurrentPosition();
                }
            }       
            lastTime=event.getEventTime();    
            
        
            
            
            return false;
        }        
    
        
        public void toCurrentPosition()
        {
        	GeoPoint center = new GeoPoint( (int) (gps.latitude * 1E6),(int) (gps.longitude * 1E6));
    		mc.setCenter(center);
    		mc.setZoom(14); 
    		mapView.invalidate();
        }
    
    
    
    };


    
    public void DisplayTitle(Cursor c)
    {
		Toast.makeText(this, 
				"id: " + c.getString(0) + "\n" +
                "NAME: " + c.getString(1) + "\n" +
                "LATITUDE: " + c.getString(2) + "\n" +
                "LONGITUDE:  " + c.getString(3) + "\n" +
                "DATE:" + c.getString(4) + "\n" +
                "CONTENT" + c.getString(5),
        Toast.LENGTH_LONG).show();   
       
    } 

/*   
    public void setInitialPos()
    {
    //	mc = mapView.getController();
        p = new GeoPoint( (int) (init_latitude * 1E6),(int) (init_longitude * 1E6));
        mc.animateTo(p);
        mc.setZoom(14); 
        mapView.invalidate();
    }

/*    
    protected static final int MENU_TAIPEI = Menu.FIRST;
    protected static final int MENU_TAICHUNG = Menu.FIRST+1;
    protected static final int MENU_KAOSHONG = Menu.FIRST+2;
    protected static final int MENU_RTK   = Menu.FIRST+3;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            // TODO Auto-generated method stub
            menu.add(0, MENU_TAIPEI, 0, "台北");
            menu.add(0, MENU_TAICHUNG, 0, "台中");
            menu.add(0, MENU_KAOSHONG, 0, "高雄");
            menu.add(0, MENU_RTK, 0, "RTK");
            return super.onCreateOptionsMenu(menu);
    }
    
GeoPoint station_taipei = new GeoPoint((int) (25.047192 * 1000000),(int) (121.516981 * 1000000));
GeoPoint station_taichung = new GeoPoint((int) (24.136895 * 1000000),(int) (120.684975 * 1000000));
GeoPoint station_kaoshong = new GeoPoint((int) (22.639359 * 1000000),(int) (120.302628 * 1000000));
GeoPoint station_rtk = new GeoPoint((int) (0 * 1000000),(int) (0 * 1000000));
    
public boolean onOptionsItemSelected(MenuItem item) {
    // TODO Auto-generated method stub
    super.onOptionsItemSelected(item);
    switch(item.getItemId()) {
            case MENU_TAIPEI:
                    mc.animateTo(station_taipei);
                    break;
            case MENU_TAICHUNG:
                    mc.animateTo(station_taichung);
                    break;
            case MENU_KAOSHONG:
                    mc.animateTo(station_kaoshong);
                    break;
            case MENU_RTK:
                    mc.animateTo(station_rtk);
                    break;                            
    }
    return super.onOptionsItemSelected(item);
}   
*/




}