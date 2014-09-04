package com.tyt.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dxj.tyt.R;
import com.tyt.data.OrderInfo;
import com.tyt.data.OrderManager;

public class KeepFragment extends Fragment implements OnItemClickListener {
	private LayoutInflater mInflater;
	private DateFormat mDateFormat;
	private List<OrderInfo> mKeepOrder;
	private OrderManager mOrderManager;
	private ListView mKeepList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		View keepLayout = inflater.inflate(R.layout.keep, container, false);
		mOrderManager = OrderManager.getInstance(getActivity().getApplicationContext());
		mKeepList = (ListView)keepLayout.findViewById(R.id.keep);
		mKeepList.setOnItemClickListener(this);
		mDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		return keepLayout;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mKeepOrder = mOrderManager.getAllKeepOrder();
		KeepAdapter keepAdapter = new KeepAdapter(mKeepOrder);
		mKeepList.setAdapter(keepAdapter);
	}

	class KeepAdapter extends BaseAdapter {
		private List<OrderInfo> mKeepOrder;
		
		public KeepAdapter(List<OrderInfo> keepOrder) {
			mKeepOrder = keepOrder;
		}

		@Override
		public int getCount() {
			return mKeepOrder.size();
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
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.keep_item, null);
				Tag tag = new Tag(); 
				tag.mContent = (TextView)convertView.findViewById(R.id.content);
				tag.mCreatTime = (TextView)convertView.findViewById(R.id.create_time);
				tag.mContactTime = (TextView)convertView.findViewById(R.id.contact_time);
				convertView.setTag(tag);
			}
			
			Tag tag = (Tag) convertView.getTag(); 
			tag.mContent.setText(mKeepOrder.get(position).getTaskContent());
			String cTime= mDateFormat.format(new Date(mKeepOrder.get(position).getCtime()));
			tag.mCreatTime.setText(cTime);
			String kTime= mDateFormat.format(new Date(mKeepOrder.get(position).getKeepTime()));
			tag.mContactTime.setText(kTime);
			return convertView;
		}
		
	}
	
	class Tag {
		public TextView mContent;
		public TextView mCreatTime;
		public TextView mContactTime;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}
}
