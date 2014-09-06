package com.tyt.adpter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dxj.tyt.R;
import com.tyt.data.OrderInfo;

public class ReleaseOrderAdapter extends BaseAdapter {
	private List<OrderInfo> mData;
	private LayoutInflater mInflater;
	private DateFormat mDateFormat;
	
	public ReleaseOrderAdapter(List<OrderInfo> data, LayoutInflater inflater) {
		mData = data;
		mInflater = inflater;
		mDateFormat = new SimpleDateFormat("HH:mm:ss"); 
	}

	@Override
	public int getCount() {
		return mData.size();
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
			convertView = mInflater.inflate(R.layout.release_list_item, null);
			tag.mContent = (TextView)convertView.findViewById(R.id.content);
			tag.mTime = (TextView)convertView.findViewById(R.id.time);
			tag.mOperate = (Button)convertView.findViewById(R.id.operate);
			convertView.setTag(tag);
		} else {
			tag = (ItemTag)convertView.getTag();
		}

		tag.mOperate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (int) v.getTag();
			}
		});
		tag.mOperate.setTag(position);
		tag.mContent.setText(mData.get(position).getTaskContent());
		long c = mData.get(position).getCtime();
		String cTime= mDateFormat.format(new Date(c));
		tag.mTime.setText(cTime);
		return convertView;
	}
	
	class ItemTag {
		private TextView mContent;
		private TextView mTime;
		private Button mOperate;
	}

}
