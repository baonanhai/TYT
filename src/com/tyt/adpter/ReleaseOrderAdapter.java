package com.tyt.adpter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dxj.tyt.R;
import com.tyt.common.CommonDefine;
import com.tyt.common.TYTApplication;
import com.tyt.data.OrderInfo;
import com.tyt.net.HttpManager;

public class ReleaseOrderAdapter extends BaseAdapter implements OnClickListener {
	private List<OrderInfo> mData;
	private LayoutInflater mInflater;
	private DateFormat mDateFormat;
	private Handler mHandler;
	private TYTApplication mApplication;

	@SuppressLint("SimpleDateFormat")
	public ReleaseOrderAdapter(Handler handler, List<OrderInfo> data, LayoutInflater inflater, TYTApplication application) {
		mHandler = handler;
		mData = data;
		mInflater = inflater;
		mApplication = application;
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

	@SuppressLint("InflateParams")
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

		OrderInfo info = mData.get(position);

		tag.mContent.setText(info.getTaskContent());

		String cTime= mDateFormat.format(new Date(info.getCtime()));
		tag.mTime.setText(cTime);

		tag.mOperate.setOnClickListener(this);
		tag.mOperate.setTag(position);
		if (info.getStatus() == CommonDefine.ORDER_STATE_USEFULL) {
			tag.mOperate.setBackgroundResource(R.drawable.btn_set_complete);
			tag.mOperate.setText(R.string.set_complete);
		} else {
			tag.mOperate.setBackgroundResource(R.drawable.btn_reset_release);
			tag.mOperate.setText(R.string.re_release);
		}
		return convertView;
	}

	class ItemTag {
		private TextView mContent;
		private TextView mTime;
		private Button mOperate;
	}

	class UpdateRunable implements Runnable {
		private int mId;
		private int mStatus;

		public UpdateRunable(int id, int status) {
			mId = id;
			mStatus = status;
		}

		@Override
		public void run() {
			HttpManager httpHandler = HttpManager.getInstance(mHandler);
			httpHandler.updateOrder(mId, mStatus);
		}
	}

	@Override
	public void onClick(View v) {
		int position = (int) v.getTag();
		OrderInfo order = mData.get(position);
		int state = CommonDefine.ORDER_STATE_COMPLETE;
		if (order.getStatus() == CommonDefine.ORDER_STATE_COMPLETE) {
			state = CommonDefine.ORDER_STATE_NO_USEFULL;
		} 
		mApplication.doInThread(new UpdateRunable(order.getId(), state));
	}
}
