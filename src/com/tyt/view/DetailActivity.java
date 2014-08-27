package com.tyt.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dxj.tyt.R;
import com.tyt.common.CommonDefine;
import com.tyt.data.OrderInfo;
import com.tyt.data.OrderManager;

public class DetailActivity extends Activity implements OnClickListener {
	public static final String ORDER_ID = "OrderId";
	public static final String SEARCH_CONDITION = "SearchCondition";

	private OrderInfo mOrderInfo;
	private TextView mSearchCondition;
	private TextView mInfo;
	private TextView mPhone;
	private TextView mTime;
	private TextView mQq;

	private Button mKeep;
	private Button mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		OrderManager orderManager = OrderManager.getInstance(getApplicationContext());

		mOrderInfo = orderManager.getOrder(getIntent().getIntExtra(ORDER_ID, 0));

		mSearchCondition = (TextView)findViewById(R.id.condition);
		mSearchCondition.setText(getIntent().getStringExtra(SEARCH_CONDITION));
		mInfo = (TextView)findViewById(R.id.info);
		mInfo.setText(mOrderInfo.getTaskContent());

		SharedPreferences sharedPreferences = getSharedPreferences(CommonDefine.SETTING, Context.MODE_PRIVATE);
		int serveDays = sharedPreferences.getInt(CommonDefine.SERVE_DAYS, 0);

		mPhone = (TextView)findViewById(R.id.phone);
		if (serveDays == 0) {
			mPhone.setText("***********");
		} else {
			mPhone.setText(mOrderInfo.getTel());
		}

		mTime = (TextView)findViewById(R.id.time);
		mTime.setText(mOrderInfo.getPubTime());

		mQq = (TextView)findViewById(R.id.qq);
		if (serveDays == 0) {
			mQq.setText("*********");
		} else {
			mQq.setText(mOrderInfo.getPubQQ());
		}

		mKeep = (Button)findViewById(R.id.keep);
		mKeep.setOnClickListener(this);
		if (serveDays == 0) {
			mKeep.setEnabled(false);
		}
		
		if (orderManager.isKeep(mOrderInfo.getId())) {
			mKeep.setText(getString(R.string.has_keep));
			mKeep.setEnabled(false);
		}

		mMap = (Button)findViewById(R.id.map);
		mMap.setOnClickListener(this);
		if (serveDays == 0) {
			mMap.setEnabled(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.keep:
			OrderManager.getInstance(getApplicationContext()).keep(mOrderInfo);
			mKeep.setText(getString(R.string.has_keep));
			mKeep.setEnabled(false);
			break;
		case R.id.map:
			Intent map = new Intent(this, MapActivity.class);
			map.putExtra(MapActivity.START, mOrderInfo.getStartPoint());
			map.putExtra(MapActivity.STOP, mOrderInfo.getDestPoint());
			startActivity(map);
			break;
		default:
			break;
		}

	}

}
