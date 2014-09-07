package com.tyt.view;

import java.util.List;

import com.dxj.tyt.R;
import com.tyt.adpter.ReleaseOrderAdapter;
import com.tyt.common.CommonDefine;
import com.tyt.common.TYTApplication;
import com.tyt.data.OrderChangeObserver;
import com.tyt.data.OrderInfo;
import com.tyt.data.OrderManager;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TodayReleaseFragment extends Fragment implements OrderChangeObserver{
	private List<OrderInfo> mUsefullOrders;
	private List<OrderInfo> mCompleteOrders;
	private ListView mUsefullOrderList; 
	private ListView mCompleteOrderList; 
	private View mIsReleaseView;
	private View mIsCompleteView;
	private LayoutInflater mInflater;
	private Handler mHandler;

	public TodayReleaseFragment(Handler handler) {
		mHandler = handler;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		View todayReleaseLayout = inflater.inflate(R.layout.today_release, container, false);
		mUsefullOrderList = (ListView)todayReleaseLayout.findViewById(R.id.releasing_list);
		mIsReleaseView = todayReleaseLayout.findViewById(R.id.is_release_view);
		mIsCompleteView = todayReleaseLayout.findViewById(R.id.is_complete_view);
		mCompleteOrderList = (ListView)todayReleaseLayout.findViewById(R.id.is_complete_list);
		refreshUI();
		return todayReleaseLayout;
	}

	private void refreshUI() {
		OrderManager orderManager = OrderManager.getInstance(getActivity().getApplicationContext());
		String tel = ((TYTApplication)getActivity().getApplication()).getPersonInfo().cellPhone;
		mUsefullOrders = orderManager.getAllReleaseOrderToday(tel, CommonDefine.ORDER_STATE_USEFULL);
		if (mUsefullOrders.size() > 0) {
			mUsefullOrderList.setAdapter(new ReleaseOrderAdapter(mHandler, mUsefullOrders, mInflater, ((TYTApplication)getActivity().getApplication())));
			mIsReleaseView.setVisibility(View.VISIBLE);
		} else {
			mIsReleaseView.setVisibility(View.GONE);
		}
		mCompleteOrders = orderManager.getAllReleaseOrderToday(tel, CommonDefine.ORDER_STATE_COMPLETE);
		if (mCompleteOrders.size() > 0) {
			mCompleteOrderList.setAdapter(new ReleaseOrderAdapter(mHandler, mCompleteOrders, mInflater, ((TYTApplication)getActivity().getApplication())));
			mIsCompleteView.setVisibility(View.VISIBLE);
		} else {
			mIsCompleteView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDataChange() {
		refreshUI();
	}
}
