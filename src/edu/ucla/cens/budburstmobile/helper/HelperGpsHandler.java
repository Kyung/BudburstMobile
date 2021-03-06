package edu.ucla.cens.budburstmobile.helper;

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.maps.GeoPoint;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class HelperGpsHandler extends Service {

	private double mLatitude;
	private double mLongitude;
	private float mAccuracy;
	private Timer mTimer = new Timer();
	private Handler mHandler = new Handler();
	private final IBinder mBinder = new GpsBinder();
	private LocationManager mLocManager;
	private static GpsListener gpsListener;
	private Context mContext;
	
	public static final String GPSHANDLERFILTER = "projectbudburst";
	public static final long TIME_LIMIT = 60 * 1000;

	@Override
	public void onCreate() {
		gpsListener = new GpsListener();
		mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
		
		mTimer.schedule(new GetLastLocation(), TIME_LIMIT);
		
		mContext = this;
	}
	
	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("K", "HelperGpsHandler- Time Limit 60 sec");
			lowGpsSignal();
		}
	}
	
	@Override
	public void onDestroy() {
		Log.i("K", "Destory background service");
		if(mLocManager != null) {
			mLocManager.removeUpdates(gpsListener);
			mLocManager = null;
		}
		stopSelf();
		
		super.onDestroy();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	private void lowGpsSignal() {
		Intent intent = new Intent();
		intent.putExtra("signal", false);
		intent.putExtra("latitude", mLatitude);
		intent.putExtra("longitude", mLongitude);
		intent.putExtra("accuracy", mAccuracy);
		intent.setAction(GPSHANDLERFILTER); // define intent-filter
		sendBroadcast(intent);
	}
	
	private void broadCastGps() {
		
		mTimer.cancel();
		
		HelperSharedPreference hPref = new HelperSharedPreference(mContext);
		hPref.setPreferencesString("latitude", String.valueOf(mLatitude));
		hPref.setPreferencesString("longitude", String.valueOf(mLongitude));
		
		Intent intent = new Intent();
		intent.putExtra("signal", true);
		intent.putExtra("latitude", mLatitude);
		intent.putExtra("longitude", mLongitude);
		intent.putExtra("accuracy", mAccuracy);
		intent.setAction(GPSHANDLERFILTER); // define intent-filter
		sendBroadcast(intent);
	}
	
	public class GpsBinder extends Binder {
		public HelperGpsHandler getService() {
			return HelperGpsHandler.this;
		}
	}
	
	public class GpsListener implements LocationListener {
		
		@Override
		public void onLocationChanged(Location loc) {

			// TODO Auto-generated method stub
			if(loc != null) {
				mLatitude = loc.getLatitude();
				mLongitude = loc.getLongitude();
				mAccuracy = loc.getAccuracy();
				broadCastGps();
			} 
			else {
				Location lastLoc = mLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(lastLoc != null) {
					mLatitude = lastLoc.getLatitude();
					mLongitude = lastLoc.getLongitude();
					mAccuracy = lastLoc.getAccuracy();
					broadCastGps();
				}
			}
		}
		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			//Toast.makeText(AddSite.this, getString(R.string.AddSite_disabledGPS), Toast.LENGTH_SHORT).show();
			
		}
		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			//Toast.makeText(AddSite.this, getString(R.string.AddSite_enabledGPS), Toast.LENGTH_SHORT).show();
		}
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}	
	}
}
