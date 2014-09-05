package com.tyt.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.dxj.tyt.R;

public class MapActivity extends BaseActivity implements OnRouteSearchListener, OnGeocodeSearchListener{
	public static final String START = "Start";
	public static final String STOP = "Stop";

	private String mStrStart;
	private String mStrStop;
	private LatLonPoint mStartPoint;
	private LatLonPoint mStopPoint;
	private ProgressDialog mProgDialog = null;// 搜索时进度条

	private MapView mMapView;
	private AMap mAMap;
	private RouteSearch mRouteSearch;
	private DriveRouteResult mDriveRouteResult;// 驾车模式查询结果
	private GeocodeSearch mGeocoderSearch;

	private boolean isSearchStart = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		mStrStart = getIntent().getStringExtra(START);
		mStrStop = getIntent().getStringExtra(STOP);

		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);// 必须要写
		init();

		mGeocoderSearch = new GeocodeSearch(this); 
		mGeocoderSearch.setOnGeocodeSearchListener(this); 

		// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode 
		showProgressDialog();
		GeocodeQuery query = new GeocodeQuery(mStrStart, null); 
		mGeocoderSearch.getFromLocationNameAsyn(query); 
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();
		}
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (mProgDialog == null)
			mProgDialog = new ProgressDialog(this);
		mProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgDialog.setIndeterminate(false);
		mProgDialog.setCancelable(true);
		mProgDialog.setMessage("正在搜索");
		mProgDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (mProgDialog != null) {
			mProgDialog.dismiss();
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				mDriveRouteResult = result;
				DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
				mAMap.clear();// 清理地图上的所有覆盖物
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						this, mAMap, drivePath, mDriveRouteResult.getStartPos(),
						mDriveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
				dissmissProgressDialog();
			} else {
				dissmissProgressDialog();
				Toast.makeText(this, "无结果", Toast.LENGTH_SHORT).show();
			}
		} else { 
			dissmissProgressDialog();
			Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show(); 
		} 
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
	}

	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		if (rCode == 0) { 
			if (result != null && result.getGeocodeAddressList() != null  
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0); 
				if (isSearchStart) {
					mStartPoint = address.getLatLonPoint();
					GeocodeQuery query = new GeocodeQuery(mStrStop, null); 
					mGeocoderSearch.getFromLocationNameAsyn(query); 
					isSearchStart = false;
				} else {
					mStopPoint = address.getLatLonPoint();
					mRouteSearch = new RouteSearch(this);
					mRouteSearch.setRouteSearchListener(this);
					final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mStopPoint);
					DriveRouteQuery query = new DriveRouteQuery(fromAndTo, RouteSearch.DrivingSaveMoney,
							null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
					mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
				}
			} else { 
				Toast.makeText(this, "无结果", Toast.LENGTH_SHORT).show();
			} 
		} else { 
			Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show(); 
		} 
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
	}
}
