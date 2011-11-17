package com.Tang.Maps;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.Tang.Maps.DBAdapter;
import java.lang.Math;
import java.util.Hashtable;




public class SharedData 
{
	
	//public static int script1_progress = 1;
	public static Hashtable<String, Integer> progress = null;
	public static final String Stage= "Stage";
	
	
	public static void newProgress(Context context, String scriptName)
	{
		SharedPreferences setting = context.getSharedPreferences(Stage,0);
		Editor edit = setting.edit();
		edit.putInt(scriptName, 0);
		edit.commit();
		
    }
	
	public static int recentProgress(Context context, String scriptName)
	{
        SharedPreferences setting = context.getSharedPreferences(Stage,0);
		int progress = setting.getInt(scriptName, -1);
		return progress;
	}
	
	public static void addProgress(Context context, String scriptName)
	{
		int tmp = recentProgress(context,scriptName);
		SharedPreferences setting = context.getSharedPreferences(Stage,0);
		Editor edit = setting.edit();
		edit.putInt(scriptName, tmp+1);
	    edit.commit();
		
	}
	
	
	
	
	public static void load(Context context, String table_name)
	{
		DBAdapter myDB = new DBAdapter(context);
		if(table_name.equals("script1"))
		{
		  myDB.dropTable("script1");
          myDB.newTable(table_name);
          myDB.selectTable(table_name);
          myDB.insertTitle("system2", "25.0153", "121.533","date","tag1");
          myDB.insertTitle("system2", "25.027807", "121.521522","date","tag2");
          myDB.insertTitle("system2", "25.050047", "121.543524","date","tag3");
          myDB.insertTitle("system2", "25.005251", "121.55554","date","tag4");
          myDB.close();
          newProgress(context,"script1");
          
          
          
		}
		
		if(table_name.equals("script2"))
		{
		  myDB.dropTable("script2");
	      myDB.newTable(table_name);
	      myDB.selectTable(table_name);
	      myDB.insertTitle("system2", "25.020811", "121.541066","date","tag1");
          myDB.insertTitle("system2", "25.004473", "121.521893","date","tag2");
          myDB.close();
          newProgress(context,"script2");
		}
		
	}
	
	
	public static double calcDistance(double lat1, double lon1, double lat2, double lon2)
	{
		lat1 = lat1/180 * Math.PI;
		lon1 = lon1/180 * Math.PI;
		lat2 = lat2/180 * Math.PI;
		lon2 = lon2/180 * Math.PI;
		
		//earth radius
		double r = 6371000;
		//calculation
		double d = r*Math.acos(Math.sin(lat1)*Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon1-lon2));
		return d;
	}
	
};