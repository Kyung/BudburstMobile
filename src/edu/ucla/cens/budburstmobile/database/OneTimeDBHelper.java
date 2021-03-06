package edu.ucla.cens.budburstmobile.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.ucla.cens.budburstmobile.floracaching.FloracacheItem;
import edu.ucla.cens.budburstmobile.helper.HelperLocalPlantListItem;
import edu.ucla.cens.budburstmobile.helper.HelperPlantItem;
import edu.ucla.cens.budburstmobile.helper.HelperValues;
import edu.ucla.cens.budburstmobile.lists.ListGroupItem;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class OneTimeDBHelper extends SQLiteOpenHelper {

	public OneTimeDBHelper(Context context){
		super(context, "onetimeBudburst.db", null, 59);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		Log.i("K", "make a oneTimePlant table");
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE oneTimePlant (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"plant_id NUMERIC," +
				"species_id NUMERIC," +
				"site_id NUMERIC," +
				"protocol_id NUMERIC," +
				"cname TEXT," +
				"sname TEXT," +
				"active NUMERIC," + // deleted(0) or not(1)
				"category NUMERIC, " +
				"synced NUMERIC, " +
				"is_floracache NUMERIC DEFAULT 0, " +
				"floracache_id NUMERIC DEFAULT -1" +
				");");
		
		db.execSQL("CREATE TABLE oneTimeObservation (" +
				"plant_id NUMERIC," +
				"phenophase_id NUMERIC," +
				"lat NUMERIC," +
				"lng NUMERIC," +
				"accuracy NUMERIC," +
				"image_id TEXT," +
				"dt_taken TEXT," +
				"notes TEXT," +
				"synced NUMERIC);"
				);
		
		db.execSQL("CREATE TABLE speciesLists (" +
				"id NUMERIC, " +
				"cname TEXT, " +
				"sname TEXT, " +
				"text TEXT, " + 
				"image_name TEXT, " +
				"image_url TEXT);");
		
		db.execSQL("CREATE TABLE pbbFlickrLists (" +
				"common_name TEXT, " +
				"science_name TEXT, " +
				"phenophase TEXT, " + 
				"dt_taken TEXT, " +
				"lat TEXT, " + 
				"lon TEXT, " + 
				"distance NUMERIC);");
		
		db.execSQL("CREATE TABLE userDefineLists (" + 
				"id INTEGER PRIMARY KEY, " +
				"common_name TEXT, " +
				"science_name TEXT, " +
				"credit TEXT, " +
				"category INTEGER, " +
				"protocol_id INTEGER, " +
				"description TEXT" +
				"); ");
		
		db.execSQL("CREATE TABLE localPlantLists (" +
				"category INTEGER, " +
				"common_name TEXT, " +
				"science_name TEXT, " +
				"county TEXT, " +
				"state TEXT, " +
				"usda_url TEXT, " +
				"photo_url TEXT, " +
				"copy_right TEXT, " +
				"image_id NUMERIC); ");
		
		db.execSQL("CREATE TABLE userDefinedGroup (" +
				"category_id INTEGER, " +
				"category_name TEXT, " +
				"user_admin_id TEXT, " +
				"latitude NUMERIC, " +
				"longitude NUMERIC, " +
				"description TEXT, " +
				"long_description TEXT, " +
				"icon_url TEXT, " +
				"distance NUMERIC); ");
		
		db.execSQL("CREATE TABLE floracacheLists (" +
				"id INTEGER, " +
				"user_name TEXT, " +
				"species_id INTEGER, " +
				"user_plant_id INTEGER, " + // temporarily
				"category INTEGER, " +
				"station INTEGER, " +
				"notes TEXT, " +
				"date TEXT, " +
				"rank INTEGER, " +
				"latitude NUMERIC, " +
				"longitude NUMERIC, " + 
				"common_name TEXT, " +
				"science_name TEXT, " +
				"protocol_id INTEGER, " + 
				"group_id INTEGER, " +
				"image_id TEXT, " +
				"observed_date TEXT" + "); ");
		
		db.execSQL("CREATE TABLE floracacheGroups (" +
				"id INTEGER, " +
				"name TEXT, " +
				"date_created TEXT, " +
				"latitude NUMERIC, " +
				"longitude NUMERIC, " +
				"radius INTEGER, " +
				"description TEXT, " +
				"icon_url TEXT" + "); ");
		
		/*
		 * Category for localPlantLists
		// type=1 BudBurst  
		// type=2 WhatsInvasive
		// type=3 WhatsNative
		// type=3 WhatsPoisonous
		// type=4 WhatsEndangered
		 */
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS oneTimePlant");
		db.execSQL("DROP TABLE IF EXISTS oneTimeObservation");
		db.execSQL("DROP TABLE IF EXISTS speciesLists");
		db.execSQL("DROP TABLE IF EXISTS pbbFlickrLists");
		db.execSQL("DROP TABLE IF EXISTS userDefineLists");
		db.execSQL("DROP TABLE IF EXISTS userDefinedGroup");
		db.execSQL("DROP TABLE IF EXISTS localPlantLists");
		db.execSQL("DROP TABLE IF EXISTS floracacheLists");
		db.execSQL("DROP TABLE IF EXISTS floracacheGroups");
		
		
		onCreate(db);
	}
	
	public void clearAllTable(Context cont){
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		db.execSQL("DELETE FROM oneTimePlant;");
		db.execSQL("DELETE FROM speciesLists;");
		db.execSQL("DELETE FROM oneTimeObservation;");
		
		db.close();
	 	dbhelper.close();
 	}
	
	public void clearUserDefineList(Context cont) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		db.execSQL("DELETE FROM userDefineLists;");
		
		db.close();
	 	dbhelper.close();
	}
	
	public void clearUserDefineListByCategory(Context cont, int category) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		db.execSQL("DELETE FROM userDefineLists WHERE category=" + category + ";");
		
		db.close();
	 	dbhelper.close();
	}
	
	public void clearLocalListAll(Context cont) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		db.execSQL("DELETE FROM localPlantLists;");
	
		db.close();
	 	dbhelper.close();
	}
	
	public void clearFloracacheList(Context cont) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		db.execSQL("DELETE FROM floracacheLists;");
		db.execSQL("DELETE FROM floracacheGroups;");
		
		db.close();
	 	dbhelper.close();
	}
	
	public void clearLocalListsByCategory(Context cont, int category) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		db.execSQL("DELETE FROM localPlantLists WHERE category=" + category + ";");
		
		db.close();
	 	dbhelper.close();
	}
	
	public void clearUserDefinedGroup(Context cont) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		db.execSQL("DELETE FROM userDefinedGroup;");
		
		db.close();
	 	dbhelper.close();
	}
	
	public int getTotalUnsynced(Context cont) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM oneTimeObservation " +
									"WHERE synced=" + SyncDBHelper.SYNCED_NO + " " +
									"GROUP BY plant_id", null);
		int count = cursor.getCount();
		
		db.close();
		cursor.close();
		
		return count;
	}
	
	public int getTotalNumberOfFloracache(Context cont) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM oneTimePlant " +
											"WHERE synced=" + SyncDBHelper.SYNCED_YES + " " +
													"AND is_floracache=1", null);
		int count = cursor.getCount();
		
		db.close();
		cursor.close();
		
		return count;
	}
	
	public int getTotalNumberOfQCOPlants(Context cont) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM oneTimePlant WHERE synced=" + SyncDBHelper.SYNCED_YES, null);
		int count = cursor.getCount();
		
		db.close();
		cursor.close();
		
		return count;
	}
	
	public int getTotalNumberOfQCObservations(Context cont) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM oneTimeObservation WHERE synced=" + SyncDBHelper.SYNCED_YES, null);
		int count = cursor.getCount();
		
		db.close();
		cursor.close();
		
		return count;
	}
	
	public ArrayList<HelperPlantItem> getAllMyListInformation(Context cont) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		ArrayList<HelperPlantItem> plantList = new ArrayList<HelperPlantItem>();
		
		HelperPlantItem pi = null;
		
		Cursor cursor = db.rawQuery("SELECT _id, species_id, plant_id, protocol_id, cname, sname, category FROM oneTimePlant;", null);
		while(cursor.moveToNext()) {
			
			 Cursor cursor2 = db.rawQuery("SELECT plant_id, phenophase_id, lat, lng, image_id, dt_taken, notes FROM oneTimeObservation WHERE plant_id = " + cursor.getInt(2) + " ORDER BY dt_taken;", null);
			 
			 int speciesID = cursor.getInt(1);
			 int protocolID = cursor.getInt(3);
			 int category = cursor.getInt(6);
			 String commonName = cursor.getString(4);
			 String scienceName = cursor.getString(5);
			 String imageName = cursor.getString(6);
			 
			 
			 while(cursor2.moveToNext()) {
				 int plantID = cursor2.getInt(0);
				 int phenophaseID = cursor2.getInt(1);
				 double latitude = cursor2.getDouble(2);
				 double longitude = cursor2.getDouble(3);
				 //String imageName = cursor2.getString(4);
				 String dtTaken = cursor2.getString(5);
				 String notes = cursor2.getString(6);
				 
				 //int aWhichList, int aWhere, int aSpeciesID, int aPlantID, int aCategory, String aUserName, String aCommonName, String aScienceName, int aPhenoID, int aProtocolID, double aLatitude, double aLongitude, String aImageName, String aDate, String aNotes
				 
				 pi = new HelperPlantItem();
				 pi.setWhichList(HelperValues.MY_PLANT_LIST);
				 pi.setWhere(HelperValues.FROM_QUICK_CAPTURE);
				 pi.setSpeciesID(speciesID);
				 pi.setPlantID(plantID);
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
		}
		
		db.close();
		cursor.close();
		
		
		return plantList;
	}
	
	public void clearLocalLists(Context cont) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		db.execSQL("DELETE FROM localPlantLists;");
		
		db.close();
	 	dbhelper.close();
	}
	
	public int getImageID(Context cont, String ScienceName, int Category) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		int imageID = 0;
		
		Cursor getImageID = db.rawQuery("SELECT Image_id FROM localPlantLists WHERE " +
				"category=" + Category + " AND science_name=\"" + ScienceName + "\"", null);
		
		while(getImageID.moveToNext()) {
			imageID = getImageID.getInt(0);
		}
		
		getImageID.close();
		db.close();
		
		return imageID;
	}
	
	public int getImageIDByCName(Context cont, String CommonName, int Category) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		int imageID = 0;
		
		Cursor getImageID = db.rawQuery("SELECT Image_id FROM localPlantLists WHERE " +
				"category=" + Category + " AND common_name=\"" + CommonName + "\"", null);
		
		while(getImageID.moveToNext()) {
			imageID = getImageID.getInt(0);
		}
		
		getImageID.close();
		db.close();
		
		return imageID;
	}
	
	public int getUserListsImageID(Context cont, String scienceName, int category) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		int imageID = 0;
		
		Cursor getImageID = db.rawQuery("SELECT id FROM userDefineLists WHERE " +
				"category=" + category + " AND science_name=\"" + scienceName + "\"", null);
		
		while(getImageID.moveToNext()) {
			imageID = getImageID.getInt(0);
		}
		
		getImageID.close();
		db.close();
		
		return imageID;
	}
	
	public String getScienceName(Context cont, String CommonName, int Category) {
		
		
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		String scienceName = "";
		
		Cursor getScienceName = db.rawQuery("SELECT science_name FROM localPlantLists WHERE " +
				"category=" + Category + " AND common_name=\"" + CommonName + "\"", null);
		
		while(getScienceName.moveToNext()) {
			scienceName = getScienceName.getString(0);
		}
		
		getScienceName.close();
		db.close();
		
		return scienceName; 
	}
	
	public String getUserListsScienceName(Context cont, String CommonName, int Category) {
		
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		String scienceName = "";
		
		Cursor getScienceName = db.rawQuery("SELECT science_name FROM userDefineLists WHERE " +
				"category=" + Category + " AND common_name=\"" + CommonName + "\"", null);
		
		while(getScienceName.moveToNext()) {
			scienceName = getScienceName.getString(0);
		}
		
		getScienceName.close();
		db.close();
		
		return scienceName; 
	}
	
	public int getUserDefinedImageID(Context cont, String ScienceName, int Category) {
		//userDefineLists
		
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		int ImageID = 999;
		
		Cursor getImageID = db.rawQuery("SELECT id FROM userDefineLists WHERE " +
				"category=" + Category + " AND science_name=\"" + ScienceName + "\"", null);
		
		while(getImageID.moveToNext()) {
			ImageID = getImageID.getInt(0);
		}
		
		getImageID.close();
		db.close();
		
		return ImageID;
	}
	
	/*
	 * 
		db.execSQL("CREATE TABLE localPlantLists (" +
				"category INTEGER, " +
				"common_name TEXT, " +
				"science_name TEXT, " +
				"county TEXT, " +
				"state TEXT, " +
				"usda_url TEXT, " +
				"photo_url TEXT, " +
				"copy_right TEXT, " +
				"image_id NUMERIC); ");
	 */
	
	public HelperLocalPlantListItem getLocalPlantList(Context cont, String CommonName, int Category) {
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		String scienceName = "";
		
		Cursor getImageID = db.rawQuery("SELECT category, common_name, science_name, " +
				"county, state, usda_url, copy_right, image_id FROM localPlantLists WHERE " +
				"category=" + Category + " AND common_name=\"" + CommonName + "\"", null);
		
		HelperLocalPlantListItem pItem = null;
		/*
		 * public HelperLocalPlantListItem(int Category, String CommonName, String ScienceName, String County,
			String State, String UsdaUrl, String CopyRight) {
		 */
		if(getImageID.getCount() == 0) {
			getImageID.close();
			db.close();
			return null;
		}
		
		while(getImageID.moveToNext()) {
			pItem = new HelperLocalPlantListItem(getImageID.getInt(0), getImageID.getString(1), 
					getImageID.getString(2), getImageID.getString(3), getImageID.getString(4),
					getImageID.getString(5), getImageID.getString(6), getImageID.getInt(7));
		}
		
		getImageID.close();
		db.close();
		
		return pItem; 
	}
	
	public ArrayList<ListGroupItem> getListGroupItem(Context cont) {
		
		ArrayList<ListGroupItem> fArr = new ArrayList<ListGroupItem>();
		
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor getGroupItem = db.rawQuery("SELECT " +
				"category_id, category_name, user_admin_id, latitude, " +
				"longitude, description, long_description," +
				"icon_url, distance " +
				"FROM userDefinedGroup ORDER BY distance ASC", null);
		
		while(getGroupItem.moveToNext()) {
			ListGroupItem gItem = new ListGroupItem();
			
			gItem.setCategoryID(getGroupItem.getInt(0));
			gItem.setCategoryName(getGroupItem.getString(1));
			gItem.setUserAdminID(getGroupItem.getInt(2));
			gItem.setLatitude(getGroupItem.getDouble(3));
			gItem.setLongitude(getGroupItem.getDouble(4));
			gItem.setDescription(getGroupItem.getString(5));
			gItem.setLongDescription(getGroupItem.getString(6));
			gItem.setIconURL(getGroupItem.getString(7));
			gItem.setDistance(getGroupItem.getFloat(8));
			
			fArr.add(gItem);
		}
		
		getGroupItem.close();
		db.close();
		
		return fArr;
	}
	
	public ArrayList<FloracacheItem> getFloracacheLists(Context cont, int rank, int groupID, double latitude, double longitude) {
		
		ArrayList<FloracacheItem> fArr = new ArrayList<FloracacheItem>();
		
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor getLists = db.rawQuery("SELECT id, user_name, species_id, " +
				"category, station, notes, date, latitude, longitude, common_name, " +
				"science_name, protocol_id, user_plant_id, image_id " +
				"FROM floracacheLists " +
				"WHERE rank=" + rank + " " + 
					"AND group_id=" + groupID + ";", null);
				
		
		FloracacheItem fItem = null;
		
		while(getLists.moveToNext()) {
			
			Cursor ifFloracache = db.rawQuery("SELECT floracache_id " +
					"FROM oneTimePlant " +
					"WHERE is_floracache=1;", null);
			
			boolean isFloracache = false;
			
			while(ifFloracache.moveToNext()) {
				if(ifFloracache.getInt(0) == getLists.getInt(0)) {
					isFloracache = true;
					break;
				}
			}
			
			ifFloracache.close();
			
			if(!isFloracache) {
				fItem = new FloracacheItem();
				
				float dist[] = new float[1];
				Location.distanceBetween(latitude, longitude, getLists.getDouble(7), getLists.getDouble(8), dist);
				
				
				fItem.setFloracacheID(getLists.getInt(0));
				fItem.setUserName(getLists.getString(1));
				fItem.setUserSpeciesID(getLists.getInt(2));
				fItem.setUserSpeciesCategoryID(getLists.getInt(3));
				fItem.setUserStationID(getLists.getInt(4));
				fItem.setLatitude(getLists.getDouble(7));
				fItem.setLongitude(getLists.getDouble(8));
				fItem.setFloracacheNotes(getLists.getString(5));
				fItem.setDate(getLists.getString(6));
				fItem.setCommonName(getLists.getString(9));
				fItem.setScienceName(getLists.getString(10));
				fItem.setProtocolID(getLists.getInt(11));
				fItem.setImageID(getLists.getInt(13));
				fItem.setDistance(dist[0]);
				
				fArr.add(fItem);
			}
		}
		
		getLists.close();
		db.close();

		
		
		
		// sorting
		Comparator<FloracacheItem> compare = new Comparator<FloracacheItem>() {

			@Override
			public int compare(FloracacheItem obj1, FloracacheItem obj2) {
				// TODO Auto-generated method stub
				return String.valueOf(obj1.getDistance()).compareToIgnoreCase(String.valueOf(obj2.getDistance()));
			}
		};
		
		Collections.sort(fArr, compare);
		
		ArrayList<FloracacheItem> mNewArr = new ArrayList<FloracacheItem>();
		
		int count = 0;
		for(FloracacheItem item: fArr) {
			
			if(count++ < 20) {
				try {
					mNewArr.add(item.clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		Log.i("K", "Length : " + mNewArr.size());
		
		return mNewArr;
		
	}
	
	public FloracacheItem getFloracacheInfo(Context cont, int floracacheID) {
		
		OneTimeDBHelper dbhelper = new OneTimeDBHelper(cont);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor getLists = db.rawQuery("SELECT id, user_name, species_id, " +
				"category, station, notes, date, latitude, longitude, common_name, " +
				"science_name, protocol_id, user_plant_id, image_id, observed_date " +
				"FROM floracacheLists " +
				"WHERE id=" + floracacheID + ";", null);
				
		
		FloracacheItem fItem = null;
		
		while(getLists.moveToNext()) {
			
			fItem = new FloracacheItem();
			fItem.setFloracacheID(getLists.getInt(0));
			fItem.setUserName(getLists.getString(1));
			fItem.setUserSpeciesID(getLists.getInt(2));
			fItem.setUserSpeciesCategoryID(getLists.getInt(3));
			fItem.setUserStationID(getLists.getInt(4));
			fItem.setLatitude(getLists.getDouble(7));
			fItem.setLongitude(getLists.getDouble(8));
			fItem.setFloracacheNotes(getLists.getString(5));
			fItem.setDate(getLists.getString(6));
			fItem.setCommonName(getLists.getString(9));
			fItem.setScienceName(getLists.getString(10));
			fItem.setProtocolID(getLists.getInt(11));
			fItem.setImageID(getLists.getInt(13));
			fItem.setObservedDate(getLists.getString(14));
		}
		
		getLists.close();
		db.close();
		
		return fItem;
	}
}
