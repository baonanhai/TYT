package com.tyt.view;

import com.dxj.tyt.R;
import com.tyt.background.TytService;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AllInfoActivity extends BaseActivity implements OnClickListener {
	private Button mSearch;
	private Button mRelease;
	private Button mKeep;
	private Button mMyRelease;
	private int mState = R.id.search;

	private Fragment mSearchFragment;
	private Fragment mReleaseFragment;
	private Fragment mKeepFragment;
	private Fragment mMyReleaseFragment;

	private FragmentManager mFragmentManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_info);

		mSearch = (Button)findViewById(R.id.search);
		mSearch.setOnClickListener(this);
		mRelease = (Button)findViewById(R.id.release);
		mRelease.setOnClickListener(this);
		mKeep = (Button)findViewById(R.id.keep);
		mKeep.setOnClickListener(this);
		mMyRelease = (Button)findViewById(R.id.my_release);
		mMyRelease.setOnClickListener(this);

		mFragmentManager = getFragmentManager(); 
		
		refreshView();
	}
	
	protected void onResume() {
		super.onResume();
		Log.i("sssss", "AllInfoActivity COMMAND_START_REFRESH");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id != mState) {
			mState = id;
			refreshView();
		}  
	}

	private void refreshView() {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();  
		if (mSearchFragment != null) {
			transaction.hide(mSearchFragment);
		}
		if (mReleaseFragment != null) {
			transaction.hide(mReleaseFragment);
		}
		if (mKeepFragment != null) {
			transaction.hide(mKeepFragment);
		}
		if (mMyReleaseFragment != null) {
			transaction.hide(mMyReleaseFragment);
		}
		
		mSearch.setEnabled(true);
		mRelease.setEnabled(true);
		mKeep.setEnabled(true);
		mMyRelease.setEnabled(true);

		switch (mState) {
		case R.id.search:
			if (mSearchFragment == null) {  
				mSearchFragment = new SearchFragment();  
				transaction.add(R.id.content, mSearchFragment);  
			} else {  
				transaction.show(mSearchFragment);  
			}  
			mSearch.setEnabled(false);
			break;
		case R.id.release:
			if (mReleaseFragment == null) {  
				mReleaseFragment = new ReleaseFragment();  
				transaction.add(R.id.content, mReleaseFragment);  
			} else {  
				transaction.show(mReleaseFragment);  
			}  
			mRelease.setEnabled(false);
			break;
		case R.id.keep:
			if (mKeepFragment == null) {  
				mKeepFragment = new KeepFragment();  
				transaction.add(R.id.content, mKeepFragment);  
			} else {  
				transaction.show(mKeepFragment);  
			}  
			mKeep.setEnabled(false);
			break;
		case R.id.my_release:
			if (mMyReleaseFragment == null) {  
				mMyReleaseFragment = new MyReleaseFragment();  
				transaction.add(R.id.content, mMyReleaseFragment);  
			} else {  
				transaction.show(mMyReleaseFragment);  
			}  
			mMyRelease.setEnabled(false);
			break;
		}
		transaction.commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent serviceIntent = new Intent(getApplicationContext(), TytService.class);
		stopService(serviceIntent);
	}
}
