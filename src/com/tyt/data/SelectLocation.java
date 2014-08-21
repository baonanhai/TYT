package com.tyt.data;

public class SelectLocation {
	private LocationInfo mPro;
	private LocationInfo mCity;
	private LocationInfo mCounty;
	private int mRange;

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
}
