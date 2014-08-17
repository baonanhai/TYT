package com.tyt.view;

import com.dxj.tyt.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Message;
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

		switch (mState) {
		case R.id.search:
			if (mSearchFragment == null) {  
				mSearchFragment = new SearchFragment();  
				transaction.add(R.id.content, mSearchFragment);  
			} else {  
				transaction.show(mSearchFragment);  
			}  
			break;
		case R.id.release:
			if (mReleaseFragment == null) {  
				mReleaseFragment = new ReleaseFragment();  
				transaction.add(R.id.content, mReleaseFragment);  
			} else {  
				transaction.show(mReleaseFragment);  
			}  
			break;
		case R.id.keep:
			if (mKeepFragment == null) {  
				mKeepFragment = new KeepFragment();  
				transaction.add(R.id.content, mKeepFragment);  
			} else {  
				transaction.show(mKeepFragment);  
			}  
			break;
		case R.id.my_release:
			if (mMyReleaseFragment == null) {  
				mMyReleaseFragment = new MyReleaseFragment();  
				transaction.add(R.id.content, mMyReleaseFragment);  
			} else {  
				transaction.show(mMyReleaseFragment);  
			}  
			break;
		}
		transaction.commit();
	}

	@Override
	public void handleNetErr(String err) {

	}

	@Override
	public void handleServerErr(String err) {

	}

	@Override
	public void handleNomal(String msg) {

	}

	@Override
	public void handleOtherMsg(Message msg) {

	}
}
