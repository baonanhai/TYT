package com.tyt.view;

import com.dxj.tyt.R;
import com.tyt.data.OrderInfo;
import com.tyt.data.OrderManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends Activity implements OnClickListener {
	public static final String ORDER_INFO = "OrderInfo";
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
		mOrderInfo = (OrderInfo) getIntent().getSerializableExtra(ORDER_INFO);

		mSearchCondition = (TextView)findViewById(R.id.condition);
		mSearchCondition.setText(getIntent().getStringExtra(SEARCH_CONDITION));
		mInfo = (TextView)findViewById(R.id.info);
		mInfo.setText(mOrderInfo.getTaskContent());
		mPhone = (TextView)findViewById(R.id.phone);
		mPhone.setText(mOrderInfo.getTel());
		mTime = (TextView)findViewById(R.id.time);
		mTime.setText(mOrderInfo.getPubTime());
		mQq = (TextView)findViewById(R.id.qq);
		mQq.setText(mOrderInfo.getPubQQ());
		mKeep = (Button)findViewById(R.id.keep);
		mKeep.setOnClickListener(this);
		mMap = (Button)findViewById(R.id.map);
		mMap.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.keep:
			mOrderInfo.setKeep(true);
			OrderManager.getInstance(getApplicationContext()).saveInfo();
			break;
		case R.id.map:

			break;
		default:
			break;
		}

	}

}
