package edu.ucla.cens.budburstmobile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import edu.ucla.cens.budburstmobile.database.OneTimeDBHelper;
import edu.ucla.cens.budburstmobile.helper.HelperBackgroundService;
import edu.ucla.cens.budburstmobile.helper.HelperFunctionCalls;
import edu.ucla.cens.budburstmobile.helper.HelperJSONParser;
import edu.ucla.cens.budburstmobile.helper.HelperPlantItem;
import edu.ucla.cens.budburstmobile.helper.HelperSharedPreference;
import edu.ucla.cens.budburstmobile.helper.HelperValues;
import edu.ucla.cens.budburstmobile.onetime.OneTimeAddMyPlant;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Kyungsik Han
 */
public class PBBLogin extends Activity{
	
	private ProgressDialog mDialog = null;
	private EditText mTextUsername;
	private EditText mTextPassword;
	private int mPreviousActivity;
	private String mUsername;
	private String mPassword;
	private HelperSharedPreference mPref;
	private boolean mLoginValid = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		mTextUsername = (EditText)findViewById(R.id.username_text);
		mTextPassword = (EditText)findViewById(R.id.password_text);
		
		Intent previousIntent = getIntent();
		mPreviousActivity = previousIntent.getExtras().getInt("from");
		
		if(mPreviousActivity != HelperValues.FROM_SETTINGS) {
			// When user logout and come to login page. The app will not download species list.
			// Only user first login, the background service is working.
		}
		
		OneTimeDBHelper onetime = new OneTimeDBHelper(PBBLogin.this);
		
		//Define Preferences to store username and password
		mPref = new HelperSharedPreference(this);
		
		if(	!(mPref.getPreferenceString("Username", "").equals("")) 
				&& !(mPref.getPreferenceString("Password", "").equals(""))){
			Intent intent = new Intent(PBBLogin.this, PBBSync.class);
			intent.putExtra("from", 0);
			PBBLogin.this.startActivity(intent);
			finish();
		}

		//Login button
		Button buttonLogin = (Button)findViewById(R.id.login_button);
		buttonLogin.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				mUsername = mTextUsername.getText().toString().trim();
				mPassword = mTextPassword.getText().toString().trim();
				
				if(mUsername.equals("") || mPassword.equals("")){
					Toast.makeText(PBBLogin.this, getString(R.string.Alert_wrongUserPass),Toast.LENGTH_SHORT).show();	
				}else{
					new AsyncLogin().execute(getString(R.string.authentication) + "?username=" + mUsername + "&password=" + mPassword);
				}
			}
		}		
		);
		
		//Test login button
		Button buttonTestLogin = (Button)findViewById(R.id.test_login_button);
		buttonTestLogin.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				previewDialog();
			}
		}
		);
		
		//Sign up button
		Button buttonSignUp = (Button)findViewById(R.id.signup_button);
		buttonSignUp.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				//Make intent web browser for sign up
				Intent intent = new Intent (Intent.ACTION_VIEW);
				intent.setData(Uri.parse(getString(R.string.signupURL)));
				startActivity(intent);
			}
		}
		);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        stopService(new Intent(PBBLogin.this, HelperBackgroundService.class));
	        finish();
	        return true;
	    }
	    return false;
	}
	
	public void previewDialog() {
		new AlertDialog.Builder(PBBLogin.this)
		.setTitle(getString(R.string.Preview_Mode))
		.setMessage(getString(R.string.Preview_Mode_Description))
		.setPositiveButton(getString(R.string.Preview_Mode_Done), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				//Set id/pwd as test10/test10, which is 'preview'
				mPref.setPreferencesString("Username", HelperValues.PREVIEW_ID);
				mPref.setPreferencesString("Password", HelperValues.PREVIEW_PW);
				mPref.setPreferencesBoolean("Preview", true);
				
				Intent intent = new Intent(PBBLogin.this, PBBSync.class);
				intent.putExtra("from", 0);
				startActivity(intent);
				finish();
			}
		})
		.setNegativeButton(getString(R.string.Button_back), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// nothing to do
			}
		})
		.show();
	}	
	
	/**
	 * Async class for the login process
	 */
	class AsyncLogin extends AsyncTask<String, Integer, Void> {
		
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(PBBLogin.this, getString(R.string.Alert_loading), 
					getString(R.string.Alert_Logging_in), true);
		}
		@Override
		protected Void doInBackground(String... url) {
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url[0]);
			String result = "";
			
			try {
				HttpResponse response = httpClient.execute(httpPost);
				
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					
					StringBuilder result_str = new StringBuilder();
					for(;;){
						String line = br.readLine();
						if (line == null) 
							break;
						result_str.append(line+'\n');
					}
					result = result_str.toString();
					JSONObject jsonobj = new JSONObject(result);
					
					if(jsonobj.getBoolean("success") == false){
						mLoginValid = false;
					}
					else {
						mLoginValid = true;
						mPref.setPreferencesString("Username", mUsername.trim());
						mPref.setPreferencesString("Password", mPassword.trim());
			
						Intent intent = new Intent(PBBLogin.this, PBBSync.class);
						intent.putExtra("from", 0);
						PBBLogin.this.startActivity(intent);
						finish();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// TODO Auto-generated method stub
			return null;
		}
		
		protected void onPostExecute(Void unused) {
			if(!mLoginValid) {
				Toast.makeText(PBBLogin.this, getString(R.string.InValid_ID_PW), Toast.LENGTH_SHORT).show();
			}
			mDialog.dismiss();
		}
	}
}
