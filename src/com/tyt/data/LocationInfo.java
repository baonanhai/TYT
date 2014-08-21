package com.tyt.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class LocationInfo {
	private static final int TYPE_PRO = 0;
	public static final int TYPE_CITY = TYPE_PRO + 1;
	public static final int TYPE_COUNTY = TYPE_CITY + 1;
	private static final int TYPE_TOWN = TYPE_COUNTY + 1;

	public static final String TAG_PRO = "pro";
	public static final String TAG_RF = "RF";
	public static final String TAG_PX = "px";
	public static final String TAG_PY = "py";
	public static final String TAG_CITY = "city";
	public static final String TAG_COUNTY = "county";
	public static final String TAG_TOWN = "Town";

	private int mLocationType = TYPE_PRO;
	private String mName;
	private String mRF;
	private float mPx;
	private float mPy;
	private List<LocationInfo> mChildInfos;
	private LocationInfo mParent;

	public LocationInfo(JSONObject info, Context context, LocationInfo parent) {
		try {
			String tag = null;
			if (info.has(TAG_PRO)) {
				mLocationType = TYPE_PRO;
				mName = info.getString(TAG_PRO);
				tag = TAG_CITY;
			} else if (info.has(TAG_CITY)) {
				mLocationType = TYPE_CITY;
				mName = info.getString(TAG_CITY);
				tag = TAG_COUNTY;
			} else if (info.has(TAG_COUNTY)) {
				mLocationType = TYPE_COUNTY;
				mName = info.getString(TAG_COUNTY);
				tag = TAG_TOWN;
			} else if (info.has(TAG_TOWN)) {
				mLocationType = TYPE_TOWN;
				mName = info.getString(TAG_TOWN);
			}

			if (info.has(TAG_RF)) {
				mRF = info.getString(TAG_RF);
			}

			if (info.has(TAG_PX)) {
				String px = info.getString(TAG_PX);
				if (!px.equals("非数字")) {
					mPx = Float.parseFloat(px);
				}
			}

			if (info.has(TAG_PY)) {
				String py = info.getString(TAG_PY);
				if (!py.equals("非数字")) {
					mPy = Float.parseFloat(py);
				}
			}

			if (tag != null && info.has(tag)) {
				mChildInfos = new ArrayList<LocationInfo>();
				Object locations = info.get(tag);
				if (locations instanceof JSONObject) {
					mChildInfos.add(new LocationInfo((JSONObject)locations, context, this));
				} else {
					JSONArray locationArray = (JSONArray)locations;
					for (int i = 0; i < locationArray.length(); i++) {
						mChildInfos.add(new LocationInfo(locationArray.getJSONObject(i), context, this));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (mLocationType != TYPE_PRO) {
			LocationManager.getInstance(context).addLocationInfo(this);
		}
		
		mParent = parent;
	}

	public int getLocationType() {
		return mLocationType;
	}

	public String getName() {
		return mName;
	}

	public String getRF() {
		return mRF;
	}

	public float getPx() {
		return mPx;
	}

	public float getPy() {
		return mPy;
	}

	public LocationInfo getParent() {
		return mParent;
	}

	public List<LocationInfo> getChildInfos() {
		return mChildInfos;
	}
}
