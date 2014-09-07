package com.tyt.view;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dxj.tyt.R;
import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;
import com.tyt.data.OrderManager;

public class MyReleaseFragment extends Fragment implements OnClickListener {
	private static final int STATE_TODAY = 0;
	private static final int STATE_HISTORY = STATE_TODAY + 1;

	private TextView mBtnToday;
	private TextView mBtnHistory;
	private int mState = STATE_TODAY;

	private Fragment mTodayFragment;
	private Fragment mHistoryFragment;
	private FragmentManager mFragmentManager;
	private Handler mHandler;
	private OrderManager mOrderManager;

	private static class MyHandler extends Handler {  
		private WeakReference<MyReleaseFragment> mReleaseFragment;

		public MyHandler(MyReleaseFragment releaseFragment) {  
			mReleaseFragment = new WeakReference<MyReleaseFragment>(releaseFragment);  
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MyReleaseFragment relReleaseFragment = mReleaseFragment.get();
			if (relReleaseFragment != null) {
				switch (msg.what) {
				case CommonDefine.ERR_NONE:
					try {
						JSONObject msgJson = new JSONObject((String)msg.obj);
						int code = msgJson.getInt(JsonTag.CODE);
						if (code == CommonDefine.ERR_SERVER_NONE) {
							int id = msg.arg1;
							int status = msg.arg2;
							relReleaseFragment.mOrderManager.updateOrderStatus(id, status);
						} else if (code == CommonDefine.ERR_SERVER) {
							relReleaseFragment.handleServerErr(msgJson.getString(JsonTag.MSG));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case CommonDefine.ERR_NET:
					relReleaseFragment.handleNetErr(relReleaseFragment.getString(R.string.err_net));
					break;
				}
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mOrderManager = OrderManager.getInstance(getActivity().getApplicationContext());

		View myReleaseLayout = inflater.inflate(R.layout.my_release, container, false);

		mHandler = new MyHandler(this);

		TextView title = (TextView)myReleaseLayout.findViewById(R.id.title);
		title.setText(R.string.my_release);

		mBtnToday = (TextView)myReleaseLayout.findViewById(R.id.today);
		mBtnToday.setOnClickListener(this);
		mBtnHistory = (TextView)myReleaseLayout.findViewById(R.id.history);
		mBtnHistory.setOnClickListener(this);

		mFragmentManager = getFragmentManager();

		refreshUi(mState);
		return myReleaseLayout;
	}

	private void refreshUi(int state) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();  
		if (mTodayFragment != null) {
			transaction.hide(mTodayFragment);
		}

		if (mHistoryFragment != null) {
			transaction.hide(mHistoryFragment);
		}

		switch (state) {
		case STATE_TODAY:
			mBtnToday.setBackgroundResource(R.drawable.left_select);
			mBtnToday.setTextColor(getResources().getColor(R.color.white));
			mBtnHistory.setBackgroundResource(R.drawable.right_normal);
			mBtnHistory.setTextColor(getResources().getColor(R.color.blue));
			if (mTodayFragment == null) {
				mTodayFragment = new TodayReleaseFragment(mHandler);
				transaction.add(R.id.release_content, mTodayFragment);
				mOrderManager.addOrderChangeObserver((TodayReleaseFragment)mTodayFragment);
			} else {
				transaction.show(mTodayFragment);
			}
			break;
		case STATE_HISTORY:
			mBtnToday.setBackgroundResource(R.drawable.left_normal);
			mBtnToday.setTextColor(getResources().getColor(R.color.blue));
			mBtnHistory.setBackgroundResource(R.drawable.right_select);
			mBtnHistory.setTextColor(getResources().getColor(R.color.white));
			if (mHistoryFragment == null) {
				mHistoryFragment = new HistoryReleaseFragment(mHandler);
				transaction.add(R.id.release_content, mHistoryFragment);
				mOrderManager.addOrderChangeObserver((HistoryReleaseFragment)mHistoryFragment);
			} else {
				transaction.show(mHistoryFragment);
			}
			break;
		}
		transaction.commit();
	}

	public void handleNetErr(String err) {
		Toast.makeText(this.getActivity(), R.string.err_net, Toast.LENGTH_LONG).show();
	}

	public void handleServerErr(String err) {
		Toast.makeText(this.getActivity(), R.string.operate_fail, Toast.LENGTH_LONG).show();
	}

	public void handleNomal(String msg, int id, int status) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.today:
			mState = STATE_TODAY;
			refreshUi(mState);
			break;
		case R.id.history:
			mState = STATE_HISTORY;
			refreshUi(mState);
			break;
		default:
			break;
		}
	}
}
