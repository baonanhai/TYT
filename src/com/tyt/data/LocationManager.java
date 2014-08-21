package com.tyt.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class LocationManager {
	private static LocationManager mLocationHandler;
	private Context mContext;

	private static final String TAG_DATA = "data";

	private List<LocationInfo> mAllLocationTree;
	private List<LocationInfo> mAllLocationInfos;

	private LocationManager(Context context) {
		mContext = context;
	}

	public synchronized static LocationManager getInstance(Context context) {
		if (mLocationHandler == null) {
			mLocationHandler = new LocationManager(context);
		}
		return mLocationHandler;
	}

	public void initLocationInfo() {
		mAllLocationTree = new ArrayList<LocationInfo>();
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(mContext.getAssets().open("location.json")));
			String line = null;
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			JSONObject allLocation = new JSONObject(sb.toString());
			JSONObject data = allLocation.getJSONObject(TAG_DATA);
			JSONArray pros = data.getJSONArray(LocationInfo.TAG_PRO);
			for (int i = 0; i < pros.length(); i++) {
				mAllLocationTree.add(new LocationInfo(pros.getJSONObject(i), mContext, null));	
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public List<LocationInfo> getAllLocationInRange(SelectLocation selectLocation) {
		List<LocationInfo> result = null;
		for (LocationInfo locationInfo : mAllLocationInfos) {
			if (locationInfo.getPx() != 0 && locationInfo.getPy() != 0) {
				float xRange, yRange;
				if (selectLocation.getCounty() != null) {
					xRange = (selectLocation.getCounty().getPx() - locationInfo.getPx());
					yRange = (selectLocation.getCounty().getPy() - locationInfo.getPy());
				} else {
					xRange = (selectLocation.getCity().getPx() - locationInfo.getPx());
					yRange = (selectLocation.getCity().getPy() - locationInfo.getPy());
				}

				if (Math.sqrt(xRange * xRange + yRange * yRange) < selectLocation.getRange()) {
					if (result == null) {
						result = new ArrayList<LocationInfo>();
					}
					result.add(locationInfo);
				}
			}
		}
		return result;
	}

	public List<LocationInfo> getAllLocationTree() {
		return mAllLocationTree;
	}

	public void addLocationInfo(LocationInfo info) {
		if (mAllLocationInfos == null) {
			mAllLocationInfos = new ArrayList<LocationInfo>();
		}
		mAllLocationInfos.add(info);
	}
	
	public static List<String> getAllUsefullLocation(String pro, String city, String county) {
		Log.i("sssss", "pro:" + pro + " city:" + city + " county:" + county);
		return null;
	}
}
