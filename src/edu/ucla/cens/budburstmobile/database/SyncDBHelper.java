package edu.ucla.cens.budburstmobile.database;

import java.util.ArrayList;

import edu.ucla.cens.budburstmobile.helper.HelperPlantItem;
import edu.ucla.cens.budburstmobile.helper.HelperValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SyncDBHelper extends SQLiteOpenHelper{
	
	//Constants for 'synced'
	public static final int IMG_FILE_NEED_TO_BE_DOWNLOADED = 0;
	public static final int SYNCED_YES = 5;
	public static final int SYNCED_NO = 9;
	
	public SyncDBHelper(Context context){
		super(context, "syncBudburst.db", null, 34);
	}
	
	public void onCreate(SQLiteDatabase db){
		
		//This table stores plant list of user.
		db.execSQL("CREATE TABLE my_plants (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"species_id NUMERIC," +
				"site_id NUMERIC," +
				"site_name TEXT," +
				"protocol_id NUMERIC," +
				"common_name TEXT," +
				"active NUMERIC," + // active 0(need to be removed), 1(nothing), 2(update the species)
				"synced NUMERIC, " + 
				"category NUMERIC DEFAULT 1);");
				// category 1 : budburst
				// category 2 : invasive
				// category 3 : poisonous
				// category 4 : endangered
				// category 10 : treelists
				// category 11 : blooming
				
				
				
		//If synced is 
		// : 5, then this plant is synced with server
		// : 9, then this plant is new added plant
		//This table stores observation.
		db.execSQL("CREATE TABLE my_observation (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"species_id NUMERIC," +
				"site_id NUMERIC," +
				"phenophase_id NUMERIC," +
				"image_id TEXT," +
				"time TEXT," +
				"note TEXT," +
				"synced NUMERIC);");
		//If syned is
		// : 0, then observation image file has not been downloaded. 
		//		So the img files need to be downloaded.
		// : 5, then observation image file has been downloaded.
		// : 9, then this observation is new, and needs to upload for SYNC.
		
		//This table stores site list of user.
		db.execSQL("CREATE TABLE my_sites (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"site_id TEXT," +
				"site_name TEXT," +
				"latitude TEXT," +
				"longitude TEXT," +
				"accuracy TEXT," +
				"state TEXT," +
				"comments TEXT, " +
				"hdistance TEXT, " +
				"shading TEXT, " +
				"irrigation TEXT, " +
				"habitat TEXT, " +
				"official NUMERIC, " +
				"synced NUMERIC);");
		//If syned is
		// : 5, then this site is synced with server
		// : 9, then this site is new, and needs to upload for SYNC.
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS my_plants;");
		db.execSQL("DROP TABLE IF EXISTS my_observation;");
		db.execSQL("DROP TABLE IF EXISTS my_sites;");
		
		onCreate(db);
	}
	
	public int getTotalNumberOfPlants(Context cont) {
		SyncDBHelper dbhelper = new SyncDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM my_plants WHERE synced=" + this.SYNCED_YES, null);
		
		int count = cursor.getCount();
		
		cursor.close();
		dbhelper.close();
		
		return count;
	}
	
	public int getTotalNumberOfUnSynced(Context cont) {
		SyncDBHelper dbhelper = new SyncDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM my_observation " +
											"WHERE synced=" + this.SYNCED_NO + " " +
											"GROUP BY species_id AND site_id", null);
		
		int count = cursor.getCount();
		
		cursor.close();
		dbhelper.close();
		
		return count;
	}
	
	
	public int getTotalNumberOfObservations(Context cont) {
		SyncDBHelper dbhelper = new SyncDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM my_observation WHERE synced=" + this.SYNCED_YES, null);
		
		int count = cursor.getCount();
		
		cursor.close();
		dbhelper.close();
		
		return count;
	}
	
	/*
	public int getSpeciesInfoByName(Context cont, String name) {
		SyncDBHelper dbhelper = new SyncDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT species_id, protocol_id FROM my_plants WHERE synced=" + this.SYNCED_YES, null);
		
		int count = cursor.getCount();
		
		db.close();
		cursor.close();
		
		return count;
	}
	*/
	
	
	public ArrayList<HelperPlantItem> getAllMyListInformation(Context cont) {
		SyncDBHelper dbhelper = new SyncDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		ArrayList<HelperPlantItem> plantList = new ArrayList<HelperPlantItem>();
		
		HelperPlantItem pi;
		
		Cursor cursor = db.rawQuery("SELECT _id, species_id, site_id, site_name, protocol_id, common_name, category FROM my_plants WHERE synced=" + this.SYNCED_YES, null);
		
		while(cursor.moveToNext()) {
			 Cursor cursor2 = db.rawQuery("SELECT species_id, site_id, phenophase_id, image_id, time, note FROM my_observation WHERE species_id = " + cursor.getInt(1) + " LIMIT 1", null);
			 
			 int speciesID = cursor.getInt(1);
			 int protocolID = cursor.getInt(4);
			 int category = cursor.getInt(6);
			 String commonName = cursor.getString(5);
			 String scienceName = "";
			 double latitude = 0.0;
			 double longitude = 0.0;
			 int siteID = cursor.getInt(2);
			 
			 Cursor cursor3 = db.rawQuery("SELECT latitude, longitude FROM my_sites WHERE site_id=" + siteID, null);
			 while(cursor3.moveToNext()) {
				 latitude = cursor3.getDouble(0);
				 longitude = cursor3.getDouble(1);
			 }
			 
			 while(cursor2.moveToNext()) {
				 int phenophaseID = cursor2.getInt(2);
				 int plantID = 0; // we don't have a plantID for monitored plants, so just put 0.
				 
				 String imageName = cursor2.getString(3);
				 String dtTaken = cursor2.getString(4);
				 String notes = cursor2.getString(5);
				 
				 Log.i("K", "Lat: " + latitude + ", Lng: " + longitude);
				 
				 
				 pi = new HelperPlantItem();
				 pi.setWhichList(HelperValues.MY_PLANT_LIST);
				 pi.setWhere(HelperValues.FROM_PLANT_LIST);
				 pi.setSpeciesID(speciesID);
				 pi.setPlantID(siteID);
				 pi.setCategory(category);
				 pi.setUserName("");
				 pi.setCommonName(commonName);
				 pi.setSpeciesName(scienceName);
				 pi.setPhenoID(phenophaseID);
				 pi.setProtocolID(protocolID);
				 pi.setLatitude(latitude);
				 pi.setLongitude(longitude);
				 pi.setImageName(imageName);
				 pi.setDate(dtTaken);
				 pi.setNote(notes);
				 
				 plantList.add(pi);
			 }
			 cursor2.close();
			 cursor3.close();
		}
		
		dbhelper.close();		
		cursor.close();
		
		return plantList;
	}
	
	
	public String getScienceName(Context context, int speciesID, int siteID) {
	
		return null;
	}
	
	public String getCommonName() {
		
		return null;
	}
	
	public String getImageName(Context context, int speciesID, int siteID) {
		
		SyncDBHelper dbhelper = new SyncDBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT image_id FROM my_observation WHERE species_id=" + 
				speciesID + " AND site_id=" + siteID, null);
		
		String getImageName = "";
		if(cursor.getCount() > 0) {
			cursor.moveToNext();
			getImageName = cursor.getString(0);
		}

		cursor.close();
		db.close();
		return getImageName;
	}
	
	public String getNote(Context context, int speciesID, int siteID) {
		SyncDBHelper dbhelper = new SyncDBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT note FROM my_observation WHERE species_id=" + 
				speciesID + " AND site_id=" + siteID, null);
		
		String getNote = "";
		if(cursor.getCount() > 0) {
			cursor.moveToNext();
			getNote = cursor.getString(0);
		}
		
		cursor.close();
		db.close();
		
		return getNote;
	}
	
	public int getProtocolID() {
		
		return 0; 
	}
	
	
		
	public void clearAllTable(Context cont){
		SyncDBHelper dbhelper = new SyncDBHelper(cont);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		db.execSQL("DELETE FROM my_plants;");
		
		/*
		Cursor c = db.rawQuery("SELECT species_id FROM my_observation;", null);
		while(c.moveToNext()) {
			db.execSQL("DELETE FROM my_observation WHERE species_id=" + c.getInt(0) + ";");
		}
		c.close();
		*/
		db.execSQL("DELETE FROM my_observation;");
		db.execSQL("DELETE FROM my_sites;");
		
	 	dbhelper.close();
 	}
}
