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

	public static List<String> getAllUsefullLocation(ArrayList<String> result, String pro, String city, String county) {
		StringBuilder sb = new StringBuilder();
		if (pro.endsWith("省")) {
			sb.append(pro.substring(0, pro.length() - 1));
		} else if (pro.endsWith("自治区")){
			sb.append(pro.substring(0, pro.length() - 3));
		}

		if (city.endsWith("市")) {
			String cityTemp = city.substring(0, city.length() - 1);
			if (!hasIn(result, cityTemp)) {
				result.add(cityTemp);
			}
			sb.append(cityTemp);
		}

		String countyTemp = null;
		if (county != null && (county.endsWith("县") || county.endsWith("区"))) {
			countyTemp = county.substring(0, county.length() - 1);
			if (!hasIn(result, countyTemp)) {
				result.add(countyTemp);
			}
		}
		if (!hasIn(result, sb.toString())) {
			result.add(sb.toString());
		}
		return result;
	}
	
	public static boolean hasIn(List<String> array, String content) {
		boolean hasIn = false;
		for (String info : array) {
			if (content.equals(info)) {
				hasIn = true;
				break;
			}
		}
		return hasIn;
	}
}
