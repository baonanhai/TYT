package com.tyt.data;

import com.dxj.tyt.R;

import android.content.Context;

public class SelectLocation {
	private LocationInfo mPro;
	private LocationInfo mCity;
	private LocationInfo mCounty;
	private int mRange;
	private Context mContext;

	public SelectLocation(Context context) {
		mContext = context;
	}
	public LocationInfo getPro() {
		return mPro;
	}
	public void setPro(LocationInfo pro) {
		mPro = pro;
	}

	public LocationInfo getCity() {
		return mCity;
	}

	public void setCity(LocationInfo city) {
		mCity = city;
	}

	public LocationInfo getCounty() {
		return mCounty;
	}

	public void setCounty(LocationInfo county) {
		mCounty = county;
	}

	public int getRange() {
		return mRange;
	}

	public void setRange(int range) {
		mRange = range;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(mPro.getName());
		sb.append(mCity.getName());
		if (mCounty != null) {
			sb.append(mCounty.getName());
		}
		sb.append(mRange);
		sb.append(mContext.getString(R.string.kilometre));
		return sb.toString();
	}
}
