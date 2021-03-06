package edu.ucla.cens.budburstmobile.onetime;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucla.cens.budburstmobile.R;
import edu.ucla.cens.budburstmobile.adapter.MyListAdapter;
import edu.ucla.cens.budburstmobile.database.OneTimeDBHelper;
import edu.ucla.cens.budburstmobile.database.StaticDBHelper;
import edu.ucla.cens.budburstmobile.database.SyncDBHelper;
import edu.ucla.cens.budburstmobile.helper.HelperPlantItem;
import edu.ucla.cens.budburstmobile.helper.HelperValues;
import edu.ucla.cens.budburstmobile.myplants.PBBAddNotes;
import edu.ucla.cens.budburstmobile.myplants.PBBAddPlant;
import edu.ucla.cens.budburstmobile.myplants.PBBAddSite;
import edu.ucla.cens.budburstmobile.utils.PBBItems;

public class OneTimePBBLists extends ListActivity{
	private ArrayList<HelperPlantItem> arPlantList;
	
	private StaticDBHelper staticDBHelper = null;
	private SQLiteDatabase staticDB = null;
	private MyListAdapter mylistapdater = null;
	private ListView MyList = null;
	
	private Button topBtn1 = null;
	private Button topBtn2 = null;
	private Button topBtn3 = null;
	private Button topBtn4 = null;
	private EditText et1 = null;
	private Dialog dialog = null;
	
	//private TextView header = null;
	private TextView myTitleText = null;
	
	private int mCurrentPosition = 0;
	private int mPhenoID = 0;
	private int mPreviousActivity;
	private int mProtocolID = 0;
	
	
	private String mCommonName = "Unknown/Other";
	
	private PBBItems pbbItem;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.flora_observer);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.pbb_title);
		
		ViewGroup v = (ViewGroup) findViewById(R.id.title_bar).getParent().getParent();
		v = (ViewGroup)v.getChildAt(0);
		v.setPadding(0, 0, 0, 0);

		myTitleText = (TextView) findViewById(R.id.my_title);
		myTitleText.setText(" One Time Observation > Top 10");
		
		Bundle bundle = getIntent().getExtras();
		pbbItem = bundle.getParcelable("pbbItem");
		mPhenoID = pbbItem.getPhenophaseID();
		mPreviousActivity = bundle.getInt("from");
		
		topBtn1 = (Button)findViewById(R.id.option1);
		topBtn2 = (Button)findViewById(R.id.option2);
		topBtn3 = (Button)findViewById(R.id.option3);
		topBtn4 = (Button)findViewById(R.id.option4);
		
		topBtn1.setOnClickListener(radio_listener);
		topBtn2.setOnClickListener(radio_listener);
		topBtn3.setOnClickListener(radio_listener);
		topBtn4.setOnClickListener(radio_listener);
		
		//Check if site table is empty
		staticDBHelper = new StaticDBHelper(OneTimePBBLists.this);
		staticDB = staticDBHelper.getReadableDatabase();
		 
		arPlantList = new ArrayList<HelperPlantItem>();
 		Cursor cursor = staticDB.rawQuery("SELECT _id, species_name, common_name, protocol_id FROM species ORDER BY common_name;",null);
 		while(cursor.moveToNext()){
			Integer id = cursor.getInt(0);
			if(id == 70 || id == 69 || id == 45 || id == 59 || id == 60 || id == 19 || id == 32 || id == 34 || id == 24) {
				String species_name = cursor.getString(1);
				String common_name = cursor.getString(2);
				int protocol_id = cursor.getInt(3);
				int resID = getResources().getIdentifier("edu.ucla.cens.budburstmobile:drawable/s"+id, null, null);
				
				HelperPlantItem pi;
				//pi = aPicture, String aCommonName, String aSpeciesName, int aSpeciesID
				pi = new HelperPlantItem();
				pi.setPicture(resID);
				pi.setCommonName(common_name);
				pi.setSpeciesName(species_name);
				pi.setSpeciesID(id);
				pi.setProtocolID(protocol_id);
				arPlantList.add(pi);
			}
		}
 		
		// add plant at the last.
		HelperPlantItem pi = new HelperPlantItem();
		pi.setPicture(getResources().getIdentifier("edu.ucla.cens.budburstmobile:drawable/pbb_icon_main2", null, null));
		pi.setCommonName("Unknown/Other");
		pi.setSpeciesName("Unknown/Other");
		pi.setSpeciesID(999);
		arPlantList.add(pi);
		
		mylistapdater = new MyListAdapter(OneTimePBBLists.this, R.layout.plantlist_item2, arPlantList);
		MyList = getListView(); 
		MyList.setAdapter(mylistapdater);
		
		//Close DB and cursor
		staticDB.close();
		cursor.close();
		
	}

	private OnClickListener radio_listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			staticDBHelper = new StaticDBHelper(OneTimePBBLists.this);
			staticDB = staticDBHelper.getReadableDatabase();
			
			if(v == topBtn1) {
				//header.setText("'TOP 10' list of the plants.");
				myTitleText.setText(" One Time Observation > Top 10");
				arPlantList = new ArrayList<HelperPlantItem>();
		 		Cursor cursor = staticDB.rawQuery("SELECT _id, species_name, common_name, protocol_id FROM species ORDER BY common_name;", null);
				while(cursor.moveToNext()){
					Integer id = cursor.getInt(0);
					if(id == 70 || id == 69 || id == 45 || id == 59 || id == 60 || id == 19 || id == 32 || id == 34 || id == 24) {
						String species_name = cursor.getString(1);
						String common_name = cursor.getString(2);
						int protocol_id = cursor.getInt(3);
									
						int resID = getResources().getIdentifier("edu.ucla.cens.budburstmobile:drawable/s"+id, null, null);
						
						HelperPlantItem pi;
						//pi = aPicture, String aCommonName, String aSpeciesName, int aSpeciesID
						pi = new HelperPlantItem();
						pi.setPicture(resID);
						pi.setCommonName(common_name);
						pi.setSpeciesName(species_name);
						pi.setSpeciesID(id);
						pi.setProtocolID(protocol_id);
						arPlantList.add(pi);
					}
				}
				
				// add plant at the last.
				HelperPlantItem pi = new HelperPlantItem();
				pi.setPicture(getResources().getIdentifier("edu.ucla.cens.budburstmobile:drawable/pbb_icon_main2", null, null));
				pi.setCommonName("Unknown/Other");
				pi.setSpeciesName("Unknown/Other");
				pi.setSpeciesID(999);
				arPlantList.add(pi);
				
				mylistapdater = new MyListAdapter(OneTimePBBLists.this, R.layout.plantlist_item2, arPlantList);
				MyList = getListView(); 
				MyList.setAdapter(mylistapdater);
				cursor.close();
				
			}
			else if (v == topBtn2) {
				//header.setText("'ALL' list of the plants.");
				myTitleText.setText(" One Time Observation > All");
				//Rereive syncDB and add them to arUserPlatList arraylist
				arPlantList = new ArrayList<HelperPlantItem>();
		 		Cursor cursor = staticDB.rawQuery("SELECT _id, species_name, common_name, protocol_id FROM species ORDER BY common_name;", null);
				while(cursor.moveToNext()){
					Integer id = cursor.getInt(0);
				
					String species_name = cursor.getString(1);
					String common_name = cursor.getString(2);
					int protocol_id = cursor.getInt(3);
									
					int resID = getResources().getIdentifier("edu.ucla.cens.budburstmobile:drawable/s"+id, null, null);
						
					HelperPlantItem pi;
					//pi = aPicture, String aCommonName, String aSpeciesName, int aSpeciesID
					pi = new HelperPlantItem();
					pi.setPicture(resID);
					pi.setCommonName(common_name);
					pi.setSpeciesName(species_name);
					pi.setSpeciesID(id);
					pi.setProtocolID(protocol_id);
					arPlantList.add(pi);
				}
				
				// add plant at the last.
				HelperPlantItem pi = new HelperPlantItem();
				pi.setPicture(getResources().getIdentifier("edu.ucla.cens.budburstmobile:drawable/pbb_icon_main2", null, null));
				pi.setCommonName("Unknown/Other");
				pi.setSpeciesName("Unknown/Other");
				pi.setSpeciesID(999);
				arPlantList.add(pi);
				
				mylistapdater = new MyListAdapter(OneTimePBBLists.this, R.layout.plantlist_item2, arPlantList);
				MyList = getListView(); 
				MyList.setAdapter(mylistapdater);
				cursor.close();
			}
			else if(v == topBtn3){
				//header.setText("By Group.");
				new AlertDialog.Builder(OneTimePBBLists.this)
				.setTitle("Select Category")
				.setIcon(android.R.drawable.ic_menu_more)
				.setItems(R.array.category, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String[] category = getResources().getStringArray(R.array.category);
						StaticDBHelper staticDBHelper = new StaticDBHelper(OneTimePBBLists.this);
						SQLiteDatabase staticDB = staticDBHelper.getReadableDatabase(); 
						
						arPlantList = new ArrayList<HelperPlantItem>();
						Cursor cursor = null;

						if(category[which].equals("Wild Flowers and Herbs")) {
							cursor = staticDB.rawQuery("SELECT _id, species_name, common_name, protocol_id FROM species WHERE protocol_id=" + HelperValues.WILD_FLOWERS + " ORDER BY common_name;",null);
							myTitleText.setText(" One Time Observation > Flowers");
						}
						else if(category[which].equals("Grass")) {
							cursor = staticDB.rawQuery("SELECT _id, species_name, common_name, protocol_id FROM species WHERE protocol_id=" + HelperValues.GRASSES + " ORDER BY common_name;",null);
							myTitleText.setText(" One Time Observation > Grass");
						}
						else if(category[which].equals("Deciduous Trees and Shrubs")) {
							cursor = staticDB.rawQuery("SELECT _id, species_name, common_name, protocol_id FROM species WHERE protocol_id=" + HelperValues.DECIDUOUS_TREES + " ORDER BY common_name;",null);
							myTitleText.setText(" One Time Observation > Deciduous");
						}
						else if(category[which].equals("Evergreen Trees and Shrubs")) {
							cursor = staticDB.rawQuery("SELECT _id, species_name, common_name, protocol_id FROM species WHERE protocol_id=" + HelperValues.EVERGREEN_TREES + " ORDER BY common_name;",null);
							myTitleText.setText(" One Time Observation > Evergreen");
						}
						else if(category[which].equals("Conifer")) {
							cursor = staticDB.rawQuery("SELECT _id, species_name, common_name, protocol_id FROM species WHERE protocol_id=" + HelperValues.CONIFERS + " ORDER BY common_name;",null);
							myTitleText.setText(" One Time Observation > Conifer");
						}
						else {
						}
						
						//header.setText(" " + category[which]);
						while(cursor.moveToNext()){
							Integer id = cursor.getInt(0);
							String species_name = cursor.getString(1);
							String common_name = cursor.getString(2);
							Integer protocol_id = cursor.getInt(3);
										
							HelperPlantItem pi;
							
							int resID = getResources().getIdentifier("edu.ucla.cens.budburstmobile:drawable/s"+id, null, null);
							
							//pi = aPicture, String aCommonName, String aSpeciesName, int aSpeciesID
							pi = new HelperPlantItem();
							pi.setPicture(resID);
							pi.setCommonName(common_name);
							pi.setSpeciesName(species_name);
							pi.setSpeciesID(id);
							pi.setProtocolID(protocol_id);
							arPlantList.add(pi);
						}
						
						// add plant at the last.
						HelperPlantItem pi = new HelperPlantItem();
						pi.setPicture(getResources().getIdentifier("edu.ucla.cens.budburstmobile:drawable/pbb_icon_main2", null, null));
						pi.setCommonName("Unknown/Other");
						pi.setSpeciesName("Unknown/Other");
						pi.setSpeciesID(999);
						arPlantList.add(pi);
						
						mylistapdater = new MyListAdapter(OneTimePBBLists.this, R.layout.plantlist_item2, arPlantList);
						MyList = getListView(); 
						MyList.setAdapter(mylistapdater);
						
						cursor.close();
						staticDB.close();
						staticDBHelper.close();
						
					}
				})
				.setNegativeButton("Back", null)
				.show();
				
			}
			
			else {
				myTitleText.setText(" " + getString(R.string.AddPlant_local));
				
				arPlantList = new ArrayList<HelperPlantItem>();
				
				OneTimeDBHelper otDBH = new OneTimeDBHelper(OneTimePBBLists.this);
				SQLiteDatabase otDB = otDBH.getReadableDatabase();
				Cursor cursor = staticDB.rawQuery("SELECT _id, species_name, common_name FROM species ORDER BY common_name;", null);
				
				while(cursor.moveToNext()) {
					String sName = cursor.getString(1);
					
					Cursor cursor2 = otDB.rawQuery("SELECT science_name FROM localPlantLists WHERE category=1 AND science_name=\"" + sName + "\"", null);
					if(cursor2.getCount() > 0) {
						int resID = getResources().getIdentifier("edu.ucla.cens.budburstmobile:drawable/s"+cursor.getInt(0), null, null);
						
						String species_name = cursor.getString(1);
						String common_name = cursor.getString(2);
						
						HelperPlantItem pi;
						//pi = aPicture, String aCommonName, String aSpeciesName, int aSpeciesID
						pi = new HelperPlantItem();
						pi.setPicture(resID);
						pi.setCommonName(common_name);
						pi.setSpeciesName(species_name);
						pi.setSpeciesID(cursor.getInt(0));
						arPlantList.add(pi);
					}
					
					cursor2.close();
				}
				
				otDBH.close();
				otDB.close();
								
				mylistapdater = new MyListAdapter(OneTimePBBLists.this, R.layout.plantlist_item2, arPlantList);
				MyList = getListView(); 
				MyList.setAdapter(mylistapdater);
				cursor.close();
			}
			
			staticDBHelper.close();
		}
	};
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){

		mCurrentPosition = position;
		
		/*
		 * If user chooses Unknown/Plant, show the popup dialog adding common name.
		 */
		if(arPlantList.get(position).getSpeciesID() == HelperValues.UNKNOWN_SPECIES) {
			dialog = new Dialog(OneTimePBBLists.this);
			
			dialog.setContentView(R.layout.species_name_custom_dialog);
			dialog.setTitle(getString(R.string.GetPhenophase_PBB_message));
			dialog.setCancelable(true);
			dialog.show();
			
			et1 = (EditText)dialog.findViewById(R.id.custom_common_name);
			Button doneBtn = (Button)dialog.findViewById(R.id.custom_done);
			doneBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mCommonName = et1.getText().toString();
					if(mCommonName.equals("")) {
						mCommonName = "Unknown/Other";
					}
					
					if(mPreviousActivity == HelperValues.FROM_QUICK_CAPTURE) {
						
						new AlertDialog.Builder(OneTimePBBLists.this)
						.setTitle("Select Category")
						.setIcon(android.R.drawable.ic_menu_more)
						.setItems(R.array.quick_capture_phenophase_category, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								String[] _category = getResources().getStringArray(R.array.quick_capture_phenophase_category);
								if(_category[which].equals("Trees and Shrubs")) {
									mProtocolID = 1;
								}
								else if(_category[which].equals("Wild Flowers")) {
									mProtocolID = 2;
								}
								else {
									mProtocolID = 3;
								}
								
								Intent intent = new Intent(OneTimePBBLists.this, OneTimePhenophase.class);
								
								pbbItem.setCommonName(mCommonName);
								pbbItem.setScienceName("Unknown/Other");
								pbbItem.setProtocolID(mProtocolID);
								pbbItem.setPhenophaseID(mPhenoID);
								pbbItem.setSpeciesID(HelperValues.UNKNOWN_SPECIES);
								
								intent.putExtra("pbbItem", pbbItem);
								intent.putExtra("from", HelperValues.FROM_QUICK_CAPTURE);
								startActivity(intent);
							}
						})
						.setNegativeButton("Back", null)
						.show();
					}
					else {
						Intent intent = new Intent(OneTimePBBLists.this, PBBAddNotes.class);

						pbbItem.setCommonName(mCommonName);
						pbbItem.setScienceName("Unknown/Other");
						pbbItem.setProtocolID(arPlantList.get(mCurrentPosition).getProtocolID());
						pbbItem.setPhenophaseID(mPhenoID);
						pbbItem.setSpeciesID(HelperValues.UNKNOWN_SPECIES);
						
						pbbItem.setCategory(HelperValues.TABLE_BUDBURSTS);
						
						intent.putExtra("pbbItem", pbbItem);
						intent.putExtra("from", HelperValues.FROM_LOCAL_PLANT_LISTS);
						startActivity(intent);
					}
					
					dialog.dismiss();					
				}
			});
		}
		/*
		 * If user chooses one of the official species..
		 */
		else {
			
			if(mPreviousActivity == HelperValues.FROM_QUICK_CAPTURE) {
				
				int getSpeciesID = arPlantList.get(position).getSpeciesID();
				int getProtocolID = 2;
				
				StaticDBHelper staticDBH = new StaticDBHelper(OneTimePBBLists.this);
				SQLiteDatabase staticDB = staticDBH.getReadableDatabase();
				
				/*
				 * We already know the Shared Plant category if we choose species from official budburst lists.
				 * - hardcoded protocolID
				 */
				
				Cursor cursor = staticDB.rawQuery("SELECT protocol_id FROM species WHERE _id=" + getSpeciesID + ";", null);
				while(cursor.moveToNext()) {
					getProtocolID = cursor.getInt(0);
				}
				
				switch(getProtocolID) {
				case HelperValues.WILD_FLOWERS:
					mProtocolID = HelperValues.QUICK_WILD_FLOWERS; 
					break;
				case HelperValues.DECIDUOUS_TREES:
					mProtocolID = HelperValues.QUICK_TREES_AND_SHRUBS;
					break;
				case HelperValues.EVERGREEN_TREES:
					mProtocolID = HelperValues.QUICK_TREES_AND_SHRUBS;
					break;
				case HelperValues.CONIFERS:
					mProtocolID = HelperValues.QUICK_TREES_AND_SHRUBS;
					break;
				case HelperValues.GRASSES:
					mProtocolID = HelperValues.QUICK_GRASSES;
					break;
				}
				
				cursor.close();
				staticDB.close();
				
				Intent intent = new Intent(OneTimePBBLists.this, OneTimePhenophase.class);
				
				pbbItem.setCommonName(arPlantList.get(mCurrentPosition).getCommonName());
				pbbItem.setScienceName(arPlantList.get(mCurrentPosition).getSpeciesName());
				pbbItem.setProtocolID(mProtocolID);
				pbbItem.setPhenophaseID(mPhenoID);
				pbbItem.setSpeciesID(arPlantList.get(mCurrentPosition).getSpeciesID());
				pbbItem.setCategory(HelperValues.LOCAL_BUDBURST_LIST);
				
				intent.putExtra("pbbItem", pbbItem);
				intent.putExtra("from", HelperValues.FROM_QUICK_CAPTURE);
				startActivity(intent);
				
			}
			else {
				Intent intent = new Intent(OneTimePBBLists.this, PBBAddNotes.class);
				
				pbbItem.setCommonName(arPlantList.get(position).getCommonName());
				pbbItem.setScienceName(arPlantList.get(position).getSpeciesName());
				pbbItem.setProtocolID(arPlantList.get(position).getProtocolID());
				pbbItem.setPhenophaseID(mPhenoID);
				pbbItem.setSpeciesID(arPlantList.get(position).getSpeciesID());
				pbbItem.setCategory(HelperValues.LOCAL_BUDBURST_LIST);
				
				intent.putExtra("pbbItem", pbbItem);
				intent.putExtra("from", HelperValues.FROM_LOCAL_PLANT_LISTS);
				startActivity(intent);
			}
		}
	}
}

