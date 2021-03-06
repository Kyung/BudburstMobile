package edu.ucla.cens.budburstmobile;

import edu.ucla.cens.budburstmobile.helper.HelperBackgroundService;
import edu.ucla.cens.budburstmobile.helper.HelperSharedPreference;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class PBBSplash extends Activity {

	private HelperSharedPreference mPref;
	private boolean mSynced;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    mPref = new HelperSharedPreference(this);
	    /**
	     * mSync value is to check if the synchronization has been done.
	     * if not, the app will show the sync page
	     * if so, the app will show the main page.
	     */
		mSynced = mPref.getPreferenceBoolean("getSynced");
		
		//Check login
		if(!(mPref.getPreferenceString("Username", "").equals("")) 
				&& !(mPref.getPreferenceString("Password", "").equals(""))){
			if(mSynced){
				Intent intent = new Intent(PBBSplash.this, PBBMainPage.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent = new Intent(PBBSplash.this, PBBSync.class);
				intent.putExtra("from", 0);
				startActivity(intent);
				finish();
			}
		}
		else{
			Intent intent = new Intent(PBBSplash.this, PBBLogin.class);
			intent.putExtra("from", 0);
			startActivity(intent);
			finish();
		}
	    
	}
}
