package com.tyt.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dxj.tyt.R;
import com.tyt.data.LocationInfo;
import com.tyt.data.LocationManager;
import com.tyt.data.SelectLocation;

public class CarFragment extends Fragment implements OnItemSelectedListener, OnClickListener {
	private boolean mIsSearch = false;
	private Spinner mStartPro;
	private Spinner mStartCity;
	private Spinner mStartCounty;
	private Spinner mStartRange;
	private Spinner mEndPro;
	private Spinner mEndCity;
	private Spinner mEndCounty;
	private Spinner mEndRange;
	private Button mSearch;

	private TextView mConditionTip;
	private Button mRefresh;
	private Button mAutoRefresh;
	private Button mSearchChange;
	private Integer[] mRanges = {50, 60, 70, 80, 90, 100, 120, 150, 180, 200, 300, 500};
	private LocationManager mLocationManager;
	private List<LocationInfo> mAllLocationInfos;
	private List<LocationInfo> mStartCityInfo;
	private List<LocationInfo> mStartCountyInfo;
	private List<LocationInfo> mEndCityInfo;
	private List<LocationInfo> mEndCountyInfo;
	private SelectLocation mStart;
	private SelectLocation mEnd;
	private int mStartRangeValue;
	private int mEndRangeValue;
	private ViewGroup mSearchHeader;

	private View mSearchConditionView;
	private View mSearchInfoView;

	private View mSearchShow;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View searchCarLayout = inflater.inflate(R.layout.search_content, container, false);
		mSearchHeader = (ViewGroup)searchCarLayout.findViewById(R.id.search_header);
		mSearchConditionView = inflater.inflate(R.layout.search_start_header, mSearchHeader, false);
		mSearchInfoView = inflater.inflate(R.layout.search_end_header, mSearchHeader, false);
		mSearchShow = searchCarLayout.findViewById(R.id.search_show);
		if (!mIsSearch) {
			initSearchConditionView();
		} else {
			initSearchInfoView();
		}
		return searchCarLayout;
	}

	private void initSearchConditionView() {
		mSearchShow.setVisibility(View.GONE);
		mSearchHeader.addView(mSearchConditionView);
		mStartPro = (Spinner)mSearchConditionView.findViewById(R.id.start_pro);
		mStartPro.setOnItemSelectedListener(this);
		mStartCity = (Spinner)mSearchConditionView.findViewById(R.id.start_city);
		mStartCity.setOnItemSelectedListener(this);
		mStartCounty = (Spinner)mSearchConditionView.findViewById(R.id.start_county);
		mStartCounty.setOnItemSelectedListener(this);
		mStartRange = (Spinner)mSearchConditionView.findViewById(R.id.start_range);
		mStartRange.setOnItemSelectedListener(this);
		mEndPro = (Spinner)mSearchConditionView.findViewById(R.id.end_pro);
		mEndPro.setOnItemSelectedListener(this);
		mEndCity = (Spinner)mSearchConditionView.findViewById(R.id.end_city);
		mEndCity.setOnItemSelectedListener(this);
		mEndCounty = (Spinner)mSearchConditionView.findViewById(R.id.end_county);
		mEndCounty.setOnItemSelectedListener(this);
		mEndRange = (Spinner)mSearchConditionView.findViewById(R.id.end_range);
		mEndRange.setOnItemSelectedListener(this);
		mSearch = (Button)mSearchConditionView.findViewById(R.id.search);
		mSearch.setOnClickListener(this);

		mLocationManager = LocationManager.getInstance(getActivity().getApplicationContext());
		mAllLocationInfos = mLocationManager.getAllLocationTree();
		setSpinnerData(mAllLocationInfos, mStartPro);
		setSpinnerData(mAllLocationInfos, mEndPro);
		mStartPro.setOnItemSelectedListener(this);
		mEndPro.setOnItemSelectedListener(this);

		ArrayAdapter<Integer> rangeAdapter = new ArrayAdapter<Integer>(getActivity().getApplicationContext(), R.layout.spinner_item, mRanges);  
		rangeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);  
		mStartRange.setAdapter(rangeAdapter);
		mStartRange.setOnItemSelectedListener(this);
		mEndRange.setAdapter(rangeAdapter);
		mEndRange.setOnItemSelectedListener(this);
	}

	private void initSearchInfoView() {
		mSearchShow.setVisibility(View.VISIBLE);
		mSearchHeader.addView(mSearchInfoView);
		mConditionTip = (TextView)mSearchInfoView.findViewById(R.id.search_condition_tip);
		mRefresh = (Button)mSearchInfoView.findViewById(R.id.refresh);
		mAutoRefresh = (Button)mSearchInfoView.findViewById(R.id.auto_refresh);
		mSearchChange = (Button)mSearchInfoView.findViewById(R.id.search_change);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.start_pro:
			if (position == 0) {
				mStart = null;
				setSpinnerData(null, mStartCity);
			} else {
				mStart = new SelectLocation();
				mStart.setPro(mAllLocationInfos.get(position - 1));

				mStartCityInfo = mAllLocationInfos.get(position - 1).getChildInfos();
				setSpinnerData(mStartCityInfo, mStartCity);
			}
			break;
		case R.id.start_city:
			if (position == 0) {
				if (mStart != null) {
					mStart.setCity(null);
					setSpinnerData(null, mStartCounty);
				}
			} else {
				mStart.setCity(mStartCityInfo.get(position - 1));
				mStartCountyInfo = mStartCityInfo.get(position - 1).getChildInfos();
				setSpinnerData(mStartCountyInfo, mStartCounty);
			}
			break;
		case R.id.start_county:
			if (position == 0) {
				if (mStart != null) {
					mStart.setCounty(null);
				}
			} else {
				mStart.setCounty(mStartCountyInfo.get(position - 1));
			}
			break;
		case R.id.end_pro:
			if (position == 0) {
				mEnd = null;
				setSpinnerData(null, mEndCity);
			} else {
				mEnd = new SelectLocation();
				mEnd.setPro(mAllLocationInfos.get(position - 1));
				mEndCityInfo = mAllLocationInfos.get(position - 1).getChildInfos();
				setSpinnerData(mEndCityInfo, mEndCity);
			}
			break;
		case R.id.end_city:
			if (position == 0) {
				if (mEnd != null) {
					mEnd.setCity(null);
					setSpinnerData(null, mEndCounty);
				}
			} else {
				mEnd.setCity(mEndCityInfo.get(position - 1));
				mEndCountyInfo = mEndCityInfo.get(position - 1).getChildInfos();
				setSpinnerData(mEndCountyInfo, mEndCounty);
			}
			break;
		case R.id.end_county:
			if (position == 0) {
				if (mEnd != null) {
					mEnd.setCounty(null);
				}
			} else {
				mEnd.setCounty(mEndCountyInfo.get(position - 1));
			}
			break;
		case R.id.start_range:
			mStartRangeValue = mRanges[position];
			break;
		case R.id.end_range:
			mEndRangeValue = mRanges[position];
			break;
		}
	}

	private void setSpinnerData(List<LocationInfo> info, Spinner spinner) {
		String[] allPro = null;
		if (info == null) {
			allPro = new String[1];
			allPro[0] = "";
		} else {
			allPro = new String[info.size() + 1];
			allPro[0] = "";
			for (int i = 1; i < info.size() + 1; i++) {
				allPro[i] = info.get(i - 1).getName();
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, allPro);  
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);  
		spinner.setAdapter(adapter);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search:
			if (!mIsSearch) {
				if (mStart != null) {
					if (mStart.getCity() == null) {
						Toast.makeText(getActivity(), R.string.search_no_city, Toast.LENGTH_LONG).show();
					} else {
						mStart.setRange(mStartRangeValue);
						List<LocationInfo> needSearchLocation = mLocationManager.getAllLocationInRange(mStart);
						List<String> searchKey = new ArrayList<String>();
						List<String> temp;
						for (LocationInfo locationInfo : needSearchLocation) {
							if (locationInfo.getLocationType() == LocationInfo.TYPE_CITY) {
								temp = LocationManager.getAllUsefullLocation(locationInfo.getParent().getName(), locationInfo.getName(), null);
								if (temp != null) {
									searchKey.addAll(temp);
								}
							} else if (locationInfo.getLocationType() == LocationInfo.TYPE_COUNTY) {
								LocationInfo county = locationInfo;
								LocationInfo city = locationInfo.getParent();
								LocationInfo pro = city.getParent();
								temp = LocationManager.getAllUsefullLocation(pro.getName(), city.getName(), county.getName());
								if (temp != null) {
									searchKey.addAll(temp);
								}
							}
						}
						mIsSearch = true;
						mSearchHeader.removeAllViews();
						initSearchInfoView();
					}
				}
			}
			break;
		default:
			break;
		}
	}
}
