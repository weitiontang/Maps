package com.Tang.Maps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter extends SQLiteOpenHelper
{
	 public static final String KEY_ROWID = "_id";
	 public static final String KEY_NAME = "name";
	 public static final String KEY_LATITUDE = "latitude";
	 public static final String KEY_LONGITUDE= "longitude";    
	 public static final String KEY_DATE = "date";
	 public static final String KEY_CONTENT = "content";
	 private static final String TAG = "DBAdapter";

	 private static final String DATABASE_NAME = "scriptDataBase";
	 private String DATABASE_TABLE;
	 private static final int DATABASE_VERSION = 2;

	 //     
	   
	 
	 private SQLiteDatabase db;

	 public DBAdapter(Context context) 
	 {
	   
	   super(context, DATABASE_NAME, null, DATABASE_VERSION);
	   Log.e(TAG,"constructor!!");
       db = this.getWritableDatabase();
	   
	 }
	 
	 @Override
     public void onCreate(SQLiteDatabase db) 
     {
     
     }
     
     
     
     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                           int newVersion) 
     {
         /*
     	Log.w(TAG, "Upgrading database from version " + oldVersion 
               + " to "
               + newVersion + ", which will destroy all old data");
         //db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
         onCreate(db);
         */
     }       
	 	 
     //---new table---
	 public DBAdapter newTable(String table_name) throws SQLException 
	 {
		   DATABASE_TABLE = table_name;
	       String DATABASE_CREATE =
		        	"create table if not exists " + DATABASE_TABLE +" (_id integer primary key autoincrement, "
		        	 + "name text not null, latitude text not null, " 
		        	 + "longitude text not null, date text not null, content text not null);";
	       db.execSQL(DATABASE_CREATE);
	       Log.e(TAG,"new Table: " + table_name);    
	       return this;
	  }

	//---select table---
		 public DBAdapter selectTable(String table_name) throws SQLException 
		 {
			   DATABASE_TABLE = table_name;
		       Log.e(TAG,"select Table: " + table_name);    
		       return this;
		  }

	    //---closes the database---    
	  public void close() 
	  {
	     db.close();
	     db = null;
	  }
	    
	  //---insert a title into the database---
	  public long insertTitle(String name, String latitude, String longitude, String date, String content) 
	  {
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_NAME, name);
	        initialValues.put(KEY_LATITUDE, latitude);
	        initialValues.put(KEY_LONGITUDE, longitude);
	        initialValues.put(KEY_DATE, date);
	        initialValues.put(KEY_CONTENT, content);
	        Log.e(TAG,"insert!!!!");
	        return db.insert(DATABASE_TABLE, null, initialValues);
	        
	  }

	  //---deletes a particular title---
	  public boolean deleteTitle(long rowId) 
	  {
	        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	  }

	    //---retrieves all the titles---
	  public Cursor getAllTitles() 
	  {
	      return db.query(DATABASE_TABLE, new String[] {
	    		    KEY_ROWID, KEY_NAME, KEY_LATITUDE,KEY_LONGITUDE,KEY_DATE,KEY_CONTENT}, 
	                null, 
	                null, 
	                null, 
	                null, 
	                null);
	  }

	    //---retrieves a particular title---
	  public Cursor getTitle(long rowId) throws SQLException 
	  {
	        Cursor mCursor =
	                db.query(true, DATABASE_TABLE, new String[] {
	                		KEY_ROWID, KEY_NAME,KEY_LATITUDE,KEY_LONGITUDE,KEY_DATE,KEY_CONTENT}, 
	                		KEY_ROWID + "=" + rowId, 
	                		null,
	                		null, 
	                		null, 
	                		null, 
	                		null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;
	   }

	    //---updates a title---
	  public boolean updateTitle(long rowId, String name, 
	                 String latitude, String longitude, String date, String content) 
	  {
	        ContentValues args = new ContentValues();
	        args.put(KEY_NAME, name);
	        args.put(KEY_LATITUDE, latitude);
	        args.put(KEY_LONGITUDE, longitude);
	        args.put(KEY_DATE, date);
	        args.put(KEY_CONTENT, content);
	        return db.update(DATABASE_TABLE, args, 
	                         KEY_ROWID + "=" + rowId, null) > 0;
	  }

	  public void dropTable(String table_name)
	  {
	    	Log.e(TAG,"drop database "+ DATABASE_TABLE);
	    	db.execSQL("DROP TABLE IF EXISTS "+ table_name);	
	    	
	  }
	    
	    
}