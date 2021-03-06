package edu.ucla.cens.budburstmobile.floracaching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.ucla.cens.budburstmobile.PBBLogin;
import edu.ucla.cens.budburstmobile.PBBSync;
import edu.ucla.cens.budburstmobile.R;
import edu.ucla.cens.budburstmobile.adapter.MyListAdapter;
import edu.ucla.cens.budburstmobile.adapter.MyListAdapterMainPage;
import edu.ucla.cens.budburstmobile.database.OneTimeDBHelper;
import edu.ucla.cens.budburstmobile.database.StaticDBHelper;
import edu.ucla.cens.budburstmobile.helper.HelperListItem;
import edu.ucla.cens.budburstmobile.helper.HelperSettings;
import edu.ucla.cens.budburstmobile.helper.HelperSharedPreference;
import edu.ucla.cens.budburstmobile.helper.HelperValues;
import edu.ucla.cens.budburstmobile.lists.ListMain;
import edu.ucla.cens.budburstmobile.myplants.PBBAddPlant;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FloraCacheMain extends ListActivity {

	private ProgressDialog mDialog = null;
	private TextView myTitleText = null;
	private MyListAdapterMainPage mListapdater;
	//private HelperSharedPreference mPref;
	private ArrayList<HelperListItem> mListArr;
	private HelperListItem mItem;
	private int selectedPosition;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.floracachelist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.pbb_title);
		
		ViewGroup v = (ViewGroup) findViewById(R.id.title_bar).getParent().getParent();
		v = (ViewGroup)v.getChildAt(0);
		v.setPadding(0, 0, 0, 0);
		
		myTitleText = (TextView) findViewById(R.id.my_title);
		myTitleText.setText(" " + getString(R.string.Menu_Floracaching));
		
	    // TODO Auto-generated method stub
	}
	
	public void onResume() {
		super.onResume();
		
		getGroupLists();
	}
	
	private void getGroupLists() {
		
		mListArr = new ArrayList<HelperListItem>();
		
		OneTimeDBHelper oDBH = new OneTimeDBHelper(this);
		SQLiteDatabase oDB = oDBH.getReadableDatabase();
		
		Cursor getFloraGroup = oDB.rawQuery("SELECT id, name, date_created, latitude, longitude, radius, description, icon_url FROM floracacheGroups", null);
		while(getFloraGroup.moveToNext()) {
			
			String groupName = getFloraGroup.getString(1);
			String groupDist = getFloraGroup.getString(6);
			
			mItem = new HelperListItem();
			mItem.setHeaderText("none");
			mItem.setTitle(groupName);
			mItem.setImageURL("pbb_icon_main");
			mItem.setDescription(groupDist);
			mItem.setGroupID(getFloraGroup.getInt(0));
			
			mListArr.add(mItem);
		}
		
		getFloraGroup.close();
		oDB.close();
		
		if(getFloraGroup.getCount() == 0) {
			TextView instruction = (TextView)findViewById(R.id.instruction);
			instruction.setVisibility(View.VISIBLE);
		}
		
		mListapdater = new MyListAdapterMainPage(this, R.layout.onetime_list ,mListArr);
		ListView MyList = getListView();
		MyList.setAdapter(mListapdater);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		
		selectedPosition = position;
		
		new AlertDialog.Builder(FloraCacheMain.this)
		.setTitle(getString(R.string.Floracache_Level))
		.setItems(R.array.floracache_level, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String[] category = getResources().getStringArray(R.array.floracache_level);
				
				// easy
				if(category[which].equals("Map")) {
					Intent intent = new Intent(FloraCacheMain.this, FloraCacheEasyLevel.class);
					intent.putExtra("group_id", mListArr.get(selectedPosition).getGroupID());
					startActivity(intent);
				}
				// medium
				else if(category[which].equals("Direction")) {
					Intent intent = new Intent(FloraCacheMain.this, FloraCacheMedLevel.class);
					intent.putExtra("group_id", mListArr.get(selectedPosition).getGroupID());
					startActivity(intent);
					//Toast.makeText(FloraCacheMain.this, getString(R.string.Alert_comingSoon), Toast.LENGTH_SHORT).show();
				}
				// hard
				else {
					//Toast.makeText(FloraCacheMain.this, getString(R.string.Alert_comingSoon), Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(FloraCacheMain.this, FloraCacheHardLevel.class);
					intent.putExtra("group_id", mListArr.get(selectedPosition).getGroupID());
					startActivity(intent);
				}
			}
			
		})
		.show();
	}
	
	
	/*
	 * Menu option(non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, 1, 0, getString(R.string.Menu_help)).setIcon(android.R.drawable.ic_menu_help);
		menu.add(0, 2, 0, getString(R.string.Menu_settings)).setIcon(android.R.drawable.ic_menu_preferences);
		
		return true;
	}
	
	/*
	 * Menu option selection handling(non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch(item.getItemId()){
		case 1:
			Toast.makeText(FloraCacheMain.this, getString(R.string.Alert_comingSoon), Toast.LENGTH_SHORT).show();
			return true;
		case 2:			
			Intent intent = new Intent(FloraCacheMain.this, HelperSettings.class);
			intent.putExtra("from", HelperValues.FROM_PLANT_LIST);
			startActivity(intent);
			return true;
		}
		return false;
	}
}
