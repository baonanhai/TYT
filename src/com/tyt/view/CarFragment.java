package com.tyt.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dxj.tyt.R;
import com.tyt.data.LocationInfo;
import com.tyt.data.LocationManager;
import com.tyt.data.OrderInfo;
import com.tyt.data.OrderManager;
import com.tyt.data.SearchObserver;
import com.tyt.data.SelectLocation;

public class CarFragment extends Fragment implements OnItemSelectedListener, OnClickListener,
SearchObserver, OnItemClickListener {
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
	private ListView mResultShow;
	private LayoutInflater mInflater;
	private SearchAdapter mSearchAdapter;
	private OrderManager mOrderManager;
	private String mCondition;
	private ArrayList<String> mSearchKey;
	private ArrayList<String> mSearchEndKey;
	private boolean mIsAutoRefresh = true;
	private DateFormat mDateFormat;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");  

		mInflater = inflater;
		View searchCarLayout = inflater.inflate(R.layout.search_content, container, false);
		mSearchHeader = (ViewGroup)searchCarLayout.findViewById(R.id.search_header);
		mSearchConditionView = inflater.inflate(R.layout.search_start_header, mSearchHeader, false);
		mSearchInfoView = inflater.inflate(R.layout.search_end_header, mSearchHeader, false);
		mSearchShow = searchCarLayout.findViewById(R.id.search_show);
		mResultShow = (ListView)searchCarLayout.findViewById(R.id.result_list);
		mResultShow.setOnItemClickListener(this);

		mOrderManager = OrderManager.getInstance(getActivity().getApplicationContext());
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
		mCondition = mStart.toString();
		if (mEnd != null) {
			mCondition = mCondition + mEnd.toString();
		}
		mConditionTip.setText(mCondition);

		mRefresh = (Button)mSearchInfoView.findViewById(R.id.refresh);
		mRefresh.setOnClickListener(this);
		mAutoRefresh = (Button)mSearchInfoView.findViewById(R.id.auto_refresh);
		mAutoRefresh.setOnClickListener(this);
		mSearchChange = (Button)mSearchInfoView.findViewById(R.id.search_change);
		mSearchChange.setOnClickListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.start_pro:
			if (position == 0) {
				mStart = null;
				setSpinnerData(null, mStartCity);
			} else {
				mStart = new SelectLocation(getActivity().getApplicationContext());
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
				mEnd = new SelectLocation(getActivity().getApplicationContext());
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
						mSearchKey = new ArrayList<String>();
						initSearchKey(mSearchKey, needSearchLocation);
						if (mEnd != null) {
							if (mEnd.getCity() == null) {
								Toast.makeText(getActivity(), R.string.search_no_city, Toast.LENGTH_LONG).show();
							} else {
								mEnd.setRange(mEndRangeValue);
								List<LocationInfo> needSearchEndLocation = mLocationManager.getAllLocationInRange(mEnd);
								mSearchEndKey = new ArrayList<String>();
								initSearchKey(mSearchEndKey, needSearchEndLocation);
								showResult(mOrderManager.search(mSearchKey, mSearchEndKey));
								mOrderManager.addSearchObserver(this);
							}
						} else {
							showResult(mOrderManager.search(mSearchKey, null));
							mOrderManager.addSearchObserver(this);
						}
					}
				}
			}
			break;
		case R.id.refresh:
			showResult(mOrderManager.search(mSearchKey, mSearchEndKey));
			break;
		case R.id.auto_refresh:
			if (mIsAutoRefresh) {
				mIsAutoRefresh = false;
				mAutoRefresh.setText(getString(R.string.auto_refresh_stop));
				mOrderManager.removeSearchObserver(this);
			} else {
				mIsAutoRefresh = true;
				mAutoRefresh.setText(getString(R.string.auto_refresh_open));
				mOrderManager.addSearchObserver(this);
			}
			break;
		case R.id.search_change:
			mIsSearch = false;
			mIsAutoRefresh = true;
			mOrderManager.removeSearchObserver(this);
			mSearchHeader.removeAllViews();
			initSearchConditionView();
			break;
		default:
			break;
		}
	}

	private void initSearchKey(ArrayList<String> result, List<LocationInfo> needSearchLocation) {
		for (LocationInfo locationInfo : needSearchLocation) {
			if (locationInfo.getLocationType() == LocationInfo.TYPE_CITY) {
				LocationManager.getAllUsefullLocation(result, locationInfo.getParent().getName(), locationInfo.getName(), null, null);
			} else if (locationInfo.getLocationType() == LocationInfo.TYPE_COUNTY) {
				LocationInfo county = locationInfo;
				LocationInfo city = locationInfo.getParent();
				LocationInfo pro = city.getParent();
				LocationManager.getAllUsefullLocation(result, pro.getName(), city.getName(), county.getName(), null);
			} else if (locationInfo.getLocationType() == LocationInfo.TYPE_TOWN) {
				LocationInfo town = locationInfo;
				LocationInfo county = locationInfo.getParent();
				LocationInfo city = county.getParent();
				LocationInfo pro = city.getParent();
				LocationManager.getAllUsefullLocation(result, pro.getName(), city.getName(), county.getName(), town.getName());
			}
		}
	}

	private void showResult(List<OrderInfo> info) {
		if(mSearchAdapter == null) {
			mSearchAdapter = new SearchAdapter(info);
		} else {
			mSearchAdapter.setSearchResult(info);
		}
		mResultShow.setAdapter(mSearchAdapter);
		mIsSearch = true;
		mSearchHeader.removeAllViews();
		initSearchInfoView();
	}

	class SearchAdapter extends BaseAdapter {
		private List<OrderInfo> mSearchResult;

		public void setSearchResult(List<OrderInfo> searchResult) {
			mSearchResult = searchResult;
			notifyDataSetChanged();
		}

		public List<OrderInfo> getSearchResult() {
			return mSearchResult;
		}

		public SearchAdapter(List<OrderInfo> searchResult) {
			mSearchResult = searchResult;
		}

		@Override
		public int getCount() {
			return mSearchResult.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemTag tag = null;
			if (convertView == null) {
				tag = new ItemTag();  
				convertView = mInflater.inflate(R.layout.search_item, null);
				tag.isHide = (TextView)convertView.findViewById(R.id.hide);
				tag.mContent = (TextView)convertView.findViewById(R.id.content);
				tag.mTime = (TextView)convertView.findViewById(R.id.time);
				convertView.setTag(tag);
			} else {
				tag = (ItemTag)convertView.getTag();
			}

			tag.isHide.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int position = (int) v.getTag();
					mOrderManager.addBlackOrder(mSearchResult.get(position));
				}
			});
			tag.isHide.setTag(position);
			tag.mContent.setText(mSearchResult.get(position).getTaskContent());
			long c = mSearchResult.get(position).getCtime();
			String cTime= mDateFormat.format(new Date(c));
			tag.mTime.setText(cTime);
			return convertView;
		}
	}

	class ItemTag {
		private TextView isHide;
		private TextView mContent;
		private TextView mTime;
	}

	@Override
	public void onDataChange() {
		showResult(mOrderManager.search(mSearchKey, mSearchEndKey));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), DetailActivity.class);
		intent.putExtra(DetailActivity.ORDER_ID, mSearchAdapter.getSearchResult().get(position).getId());
		intent.putExtra(DetailActivity.SEARCH_CONDITION, mCondition);
		startActivity(intent);
	}
}
