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
import android.widget.Button;

public class SearchFragment extends Fragment implements OnClickListener {
	private Button mCar1;
	private Button mCar2;
	private Button mCar3;
	private Button mCar4;

	private Fragment mCar1Fragment;
	private Fragment mCar2Fragment;
	private Fragment mCar3Fragment;
	private Fragment mCar4Fragment;

	private int mState = R.id.car1;
	private FragmentManager mFragmentManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View searchLayout = inflater.inflate(R.layout.search, container, false); 
		mCar1 = (Button)searchLayout.findViewById(R.id.car1);
		mCar1.setOnClickListener(this);
		mCar2 = (Button)searchLayout.findViewById(R.id.car2);
		mCar2.setOnClickListener(this);
		mCar3 = (Button)searchLayout.findViewById(R.id.car3);
		mCar3.setOnClickListener(this);
		mCar4 = (Button)searchLayout.findViewById(R.id.car4);
		mCar4.setOnClickListener(this);

		mFragmentManager = getFragmentManager();

		refreshView();

		return searchLayout;
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
		if (mCar1Fragment != null) {
			transaction.hide(mCar1Fragment);
		}
		if (mCar2Fragment != null) {
			transaction.hide(mCar2Fragment);
		}
		if (mCar3Fragment != null) {
			transaction.hide(mCar3Fragment);
		}
		if (mCar4Fragment != null) {
			transaction.hide(mCar4Fragment);
		}
		switch (mState) {
		case R.id.car1:
			if (mCar1Fragment == null) {
				mCar1Fragment = new CarFragment();
				transaction.add(R.id.search_content, mCar1Fragment);
			} else {
				transaction.show(mCar1Fragment);
			}
			break;
		case R.id.car2:
			if (mCar2Fragment == null) {
				mCar2Fragment = new CarFragment();
				transaction.add(R.id.search_content, mCar2Fragment);
			} else {
				transaction.show(mCar2Fragment);
			}
			break;
		case R.id.car3:
			if (mCar3Fragment == null) {
				mCar3Fragment = new CarFragment();
				transaction.add(R.id.search_content, mCar3Fragment);
			} else {
				transaction.show(mCar3Fragment);
			}
			break;
		case R.id.car4:
			if (mCar4Fragment == null) {
				mCar4Fragment = new CarFragment();
				transaction.add(R.id.search_content, mCar4Fragment);
			} else {
				transaction.show(mCar4Fragment);
			}
			break;
		}
		transaction.commit();
	}
}
