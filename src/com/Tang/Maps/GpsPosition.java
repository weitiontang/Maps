package com.Tang.Maps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


public class GpsPosition implements LocationListener
{
	private LocationManager locationManager;
	private boolean wirelessEnable = false;
	private boolean gpsEnable = false;
	private Context context;
	
	public double latitude;
	public double longitude;
	
	public GpsPosition (Context ctx)
	{
		
		context = ctx;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		wirelessEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		Location location = locationManager.getLastKnownLocation("gps");
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		
		//latitude = 0;
		//longitude = 0;
	}
	
	
	public boolean gpsStart()
	{
		if(!wirelessEnable && !gpsEnable)
		{
			return false;
		}
		else if(!gpsEnable && wirelessEnable)
		{
		    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		    return true;
		}
		else
		{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			return true;
		}
	}



	@Override
	public void onLocationChanged(Location location) {
		
		Log.e("GPS","LOCATION CHANGE");
		if(location != null)
		{
		  latitude = (double) (location.getLatitude());
		  longitude = (double) (location.getLongitude());
		  Log.e("GPS","LOCATION CHANG:"+String.valueOf(latitude)+","+String.valueOf(longitude));
		 }
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}





}
	
	
	
	
	
	
	
