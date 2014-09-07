package com.tyt.view;

import java.util.List;

import com.dxj.tyt.R;
import com.tyt.adpter.ReleaseOrderAdapter;
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

public class HistoryReleaseFragment extends Fragment implements OrderChangeObserver{
	private Handler mHandler;
	private LayoutInflater mInflater;
	private ListView mWeekOrdersList;

	public HistoryReleaseFragment(Handler handler) {
		mHandler = handler;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		View historyReleaseLayout = inflater.inflate(R.layout.history_release, container, false);
		mWeekOrdersList = (ListView)historyReleaseLayout.findViewById(R.id.seven_day_orders_list);
		refreshUI();
		return historyReleaseLayout;
	}
	
	private void refreshUI() {
		OrderManager orderManager = OrderManager.getInstance(getActivity().getApplicationContext());
		String tel = ((TYTApplication)getActivity().getApplication()).getPersonInfo().cellPhone;
		List<OrderInfo> mWeekOrders = orderManager.getAllReleaseOrderWeek(tel);
		mWeekOrdersList.setAdapter(new ReleaseOrderAdapter(mHandler, mWeekOrders, mInflater, ((TYTApplication)getActivity().getApplication())));
	}

	@Override
	public void onDataChange() {
		refreshUI();
	}
}
