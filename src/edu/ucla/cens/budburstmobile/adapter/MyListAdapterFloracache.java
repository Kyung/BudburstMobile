package edu.ucla.cens.budburstmobile.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import edu.ucla.cens.budburstmobile.R;
import edu.ucla.cens.budburstmobile.database.OneTimeDBHelper;
import edu.ucla.cens.budburstmobile.floracaching.FloracacheDetail;
import edu.ucla.cens.budburstmobile.floracaching.FloracacheItem;
import edu.ucla.cens.budburstmobile.helper.HelperDrawableManager;
import edu.ucla.cens.budburstmobile.helper.HelperFunctionCalls;
import edu.ucla.cens.budburstmobile.helper.HelperListItem;
import edu.ucla.cens.budburstmobile.helper.HelperPlantItem;
import edu.ucla.cens.budburstmobile.helper.HelperValues;
import edu.ucla.cens.budburstmobile.lists.ListDetail;
import edu.ucla.cens.budburstmobile.mapview.CompassView;
import edu.ucla.cens.budburstmobile.myplants.DetailPlantInfo;
import edu.ucla.cens.budburstmobile.myplants.PBBObservationSummary;
import edu.ucla.cens.budburstmobile.utils.PBBItems;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyListAdapterFloracache extends BaseAdapter{

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<FloracacheItem> mArr;
	private int mLayout;

	public MyListAdapterFloracache(Context context, int alayout, ArrayList<FloracacheItem> arr){
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mArr = arr;
		mLayout = alayout;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArr.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
			convertView = mInflater.inflate(mLayout, parent, false);
		
		ImageView img = (ImageView)convertView.findViewById(R.id.icon);
		
		HelperFunctionCalls helper = new HelperFunctionCalls();
		helper.showSpeciesThumbNail(mContext
				, mArr.get(position).getUserSpeciesCategoryID()
				, mArr.get(position).getUserSpeciesID()
				, mArr.get(position).getScienceName()
				, img);

		TextView vTitle = (TextView) convertView.findViewById(R.id.text1);
		TextView vCredit = (TextView) convertView.findViewById(R.id.text2);
		TextView vDescription = (TextView) convertView.findViewById(R.id.text3);
		
		vTitle.setText(mArr.get(position).getCommonName());
		vCredit.setText("Taken by: " + mArr.get(position).getUserName() + " ");
		vDescription.setText(mArr.get(position).getFloracacheNotes() + " ");

		// call View from the xml and link the view to current position.
		View thumbnail = convertView.findViewById(R.id.wrap_icon);
		thumbnail.setTag(mArr.get(position));
		thumbnail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FloracacheItem pi = (FloracacheItem)v.getTag();
				
				// only if this species is from user defined lists.
				if(pi.getUserSpeciesCategoryID() >= 8) {
					final RelativeLayout linear = (RelativeLayout) View.inflate(mContext, R.layout.image_popup, null);
					
					AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
					ImageView imageView = (ImageView) linear.findViewById(R.id.main_image);
					ProgressBar spinner = (ProgressBar) linear.findViewById(R.id.spinner);
					spinner.setVisibility(View.VISIBLE);
					
					String getPhotoImageURL = mContext.getString(R.string.get_user_defined_tree_large_image) + pi.getUserSpeciesID() + ".jpg";
					
					HelperDrawableManager dm = new HelperDrawableManager(mContext, spinner, imageView);
					dm.fetchDrawableOnThread(getPhotoImageURL);
					
			        dialog.setView(linear);
			        dialog.show();
				}
			}
		});
	   
		/*
		 * final RelativeLayout linear = (RelativeLayout) View.inflate(mContext, R.layout.image_popup, null);
				
				
				// TODO Auto-generated method stub
				
		 */
		return convertView;

	}
}
