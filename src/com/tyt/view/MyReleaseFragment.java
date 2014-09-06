package com.tyt.view;

import com.dxj.tyt.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyReleaseFragment extends Fragment implements OnClickListener {
	private static final int STATE_TODAY = 0;
	private static final int STATE_HISTORY = STATE_TODAY + 1;

	private TextView mBtnToday;
	private TextView mBtnHistory;
	private int mState = STATE_TODAY;
	
	private Fragment mTodayFragment;
	private Fragment mHistoryFragment;
	
	private FragmentManager mFragmentManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View myReleaseLayout = inflater.inflate(R.layout.my_release, container, false);
		
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
				mTodayFragment = new TodayReleaseFragment();
				transaction.add(R.id.release_content, mTodayFragment);
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
				mHistoryFragment = new HistoryReleaseFragment();
				transaction.add(R.id.release_content, mHistoryFragment);
			} else {
				transaction.show(mHistoryFragment);
			}
			break;
		}
		transaction.commit();
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
