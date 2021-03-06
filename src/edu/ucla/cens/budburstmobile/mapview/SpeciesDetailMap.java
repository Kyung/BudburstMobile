package edu.ucla.cens.budburstmobile.mapview;

import java.io.File;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.ucla.cens.budburstmobile.R;
import edu.ucla.cens.budburstmobile.database.OneTimeDBHelper;
import edu.ucla.cens.budburstmobile.database.StaticDBHelper;
import edu.ucla.cens.budburstmobile.database.SyncDBHelper;
import edu.ucla.cens.budburstmobile.helper.HelperDrawableManager;
import edu.ucla.cens.budburstmobile.helper.HelperFunctionCalls;
import edu.ucla.cens.budburstmobile.helper.HelperValues;
import edu.ucla.cens.budburstmobile.lists.ListDetail;
import edu.ucla.cens.budburstmobile.myplants.DetailPlantInfo;
import edu.ucla.cens.budburstmobile.myplants.PBBObservationSummary;
import edu.ucla.cens.budburstmobile.myplants.UpdatePlantInfo;
import edu.ucla.cens.budburstmobile.onetime.OneTimePhenophase;
import edu.ucla.cens.budburstmobile.utils.PBBItems;
import edu.ucla.cens.budburstmobile.utils.QuickCapture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SpeciesDetailMap extends MapActivity {

	private OneTimeDBHelper otDBH = null;
	private String mCommonName = null;
	private String mScienceName = null;
	private String mDate = null;
	private String mNotes = null;
	private String mPhotoName = null;
	private String mImageID = null;
	private int mPhenoID = 0;
	private int mPhenoIcon = 0;
	private String mUserName = null;
	private String mPhenoName = null;
	private String mPhenoText = null;
	private int mSpeciesID = 0;
	private int mProtocolID = 0;
	private int mCategory;
	private double mLatitude = 0.0;
	private double mLongitude = 0.0;
	protected static final int GET_CHANGE_CODE = 1;
	
	private ImageView phoneImage = null;
	private TextView creditTxt = null;
	private TextView cnameTxt = null;
	private TextView snameTxt = null;
	private TextView dateTxt = null;
	private EditText notesTxt = null;
	private Button addBtn = null;
	
	private List<Overlay> mListOverlay = null;
	private SpeciesItemizedOverlay mOverlay = null;
	private HelperDrawableManager mDrawManager;
	private ProgressBar mSpinner;
	private HelperFunctionCalls mHelper;
	private PBBItems pbbItem;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleBar();
		getIntentValue();
		setUpLayout();
	    showMapView();
	    layoutControl();
	}
	
	private void layoutControl() {
		addBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(SpeciesDetailMap.this)
				.setTitle(getString(R.string.Menu_addQCPlant))
				.setMessage(getString(R.string.Start_Shared_Plant))
				.setPositiveButton(getString(R.string.Button_Photo), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						/*
						 * Move to QuickCapture
						 */
						Intent intent = new Intent(SpeciesDetailMap.this, QuickCapture.class);
						intent.putExtra("pbbItem", pbbItem);
						intent.putExtra("from", HelperValues.FROM_LOCAL_PLANT_LISTS);
						
						startActivity(intent);
					}
				})
				.setNeutralButton(getString(R.string.Button_NoPhoto), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						/*
						 * Move to Getphenophase without a photo.
						 */
						Intent intent = new Intent(SpeciesDetailMap.this, OneTimePhenophase.class);
						intent.putExtra("from", HelperValues.FROM_LOCAL_PLANT_LISTS);
						pbbItem.setLocalImageName("");
						intent.putExtra("pbbItem", pbbItem);
						
												
						startActivity(intent);

					}
				})
				.setNegativeButton(getString(R.string.Button_Cancel), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				})
				.show();

				
			}
		});
	    
	    mSpinner = (ProgressBar) findViewById(R.id.progressbar);
	    
	    // showing the species image differently based on the category
	    if(mCategory == HelperValues.LOCAL_FLICKR) {
	    	mDrawManager = new HelperDrawableManager(this, mSpinner, phoneImage);
		    if(mImageID != null)
		    	mDrawManager.fetchDrawableOnThread(mImageID);
		    
		    notesTxt.setEnabled(false);
		    notesTxt.setClickable(false);
		    
			if(mNotes.length() != 0) {
				notesTxt.setText(mNotes);
			}
			else {
				notesTxt.setText(getString(R.string.No_Notes));
			}
	    }
	    else {
	    	mSpinner.setVisibility(View.GONE);
	    	phoneImage.setVisibility(View.VISIBLE);
	    	mHelper = new HelperFunctionCalls();
	    	
	    	mHelper.showSpeciesThumbNail(SpeciesDetailMap.this, 
					mCategory, mSpeciesID, 
					mScienceName, phoneImage);
	    }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		phoneImage.setBackgroundResource(R.drawable.shapedrawable);
	    phoneImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final RelativeLayout rLayout = (RelativeLayout) View.inflate(SpeciesDetailMap.this, R.layout.image_popup, null);
				
				// TODO Auto-generated method stub
				AlertDialog.Builder dialog = new AlertDialog.Builder(SpeciesDetailMap.this);
				ImageView imageView = (ImageView) rLayout.findViewById(R.id.main_image);
				ProgressBar spinner = (ProgressBar) rLayout.findViewById(R.id.spinner);
				
				if(mCategory == HelperValues.LOCAL_FLICKR) {
					HelperDrawableManager manager = new HelperDrawableManager(SpeciesDetailMap.this, spinner, imageView);
					manager.fetchDrawableOnThread(mImageID);
				}
				else {
					mHelper.showSpeciesThumbNail(SpeciesDetailMap.this, 
							mCategory, mSpeciesID, 
							mScienceName, imageView);
				}
			    			    
			    // when press 'Back', close the dialog
				dialog.setPositiveButton(getString(R.string.Button_back), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});
		        dialog.setView(rLayout);
		        dialog.show();
			}
		});
	}
	
	public void setTitleBar() {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.species_detail_map);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.pbb_title);
		
		ViewGroup v = (ViewGroup) findViewById(R.id.title_bar).getParent().getParent();
		v = (ViewGroup)v.getChildAt(0);
		v.setPadding(0, 0, 0, 0);

		TextView myTitleText = (TextView) findViewById(R.id.my_title);
		myTitleText.setText(" " + getString(R.string.Observation_Summary));
	}
	
	public void getIntentValue() {
		Bundle bundle = getIntent().getExtras();
		pbbItem = bundle.getParcelable("pbbItem");
		
		// set parcelable object
	    mCommonName = pbbItem.getCommonName();
	    mScienceName = pbbItem.getScienceName();
	    mUserName = bundle.getString("username");
	    mDate = pbbItem.getDate();
	    mNotes = pbbItem.getNote();
	    mImageID = bundle.getString("imageID");
	    mSpeciesID = pbbItem.getSpeciesID();
	    mPhenoID = pbbItem.getPhenophaseID();
	    mProtocolID = pbbItem.getProtocolID();
	    mLatitude = pbbItem.getLatitude();
	    mLongitude = pbbItem.getLongitude();
	    mCategory = pbbItem.getCategory();
	    
	    StaticDBHelper sDBH = new StaticDBHelper(SpeciesDetailMap.this);
	    SQLiteDatabase sDB = sDBH.getReadableDatabase();
	    Cursor cursor = sDB.rawQuery("" +
	    		"SELECT Phenophase_Icon, Type, Description " +
	    		"FROM Onetime_Observation " +
	    		"WHERE _id=" + mPhenoID, null);
	    while(cursor.moveToNext()) {
	    	mPhenoIcon = cursor.getInt(0);
	    	mPhenoName = cursor.getString(2);
	    	mPhenoText = cursor.getString(1);
	    }

	    cursor.close();
	    sDB.close();
	}
	
	public void setUpLayout() {
	    // setting up layout
		phoneImage = (ImageView) findViewById(R.id.phone_image);
		creditTxt = (TextView) findViewById(R.id.pheno_title);
	    cnameTxt = (TextView) findViewById(R.id.common_name);
	    snameTxt = (TextView) findViewById(R.id.science_name);
	    dateTxt = (TextView) findViewById(R.id.timestamp_text);
	    notesTxt = (EditText) findViewById(R.id.mynotes);
	    addBtn = (Button) findViewById(R.id.edit);
	    phoneImage.setVisibility(View.VISIBLE);
	    
	    addBtn.setText(getString(R.string.PlantInfo_makeObs));

	    // put cname and sname in the textView
	    creditTxt.setText("Credit: " + mUserName);
	    dateTxt.setText(mDate + " ");
	    
	    cnameTxt.setText(mCommonName + " ");
	    snameTxt.setText(mScienceName + " ");
	}
	
	public void showMapView() {
		MapView myMap = (MapView) findViewById(R.id.simpleGM_map);
	    GeoPoint p = new GeoPoint((int)(mLatitude * 1000000), (int)(mLongitude * 1000000));
	    
	    myMap.setClickable(false);
	    MapController mc = myMap.getController();
	    mc.animateTo(p);
	    mc.setZoom(10);
	    
	    mListOverlay = myMap.getOverlays();
	    Drawable marker = getResources().getDrawable(R.drawable.marker);
	    mOverlay = new SpeciesItemizedOverlay(marker, this);
	    
	    OverlayItem overlayitem = new OverlayItem(p, "spot", "Species found");
	    
	    mOverlay.addOverlay(overlayitem);
	    mListOverlay.add(mOverlay);
		
		myMap.setSatellite(false);
		myMap.setBackgroundResource(R.drawable.shapedrawable);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}