package com.tyt.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dxj.tyt.R;
import com.tyt.background.TytService;
import com.tyt.common.CommonDefine;
import com.tyt.data.LocationManager;
import com.tyt.net.HttpManager;

public class SplashActivity extends BaseActivity {
	private boolean mIsLoginSuc = false;
	private boolean mIsGetInfoSuc = false;
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		mSharedPreferences = getSharedPreferences(CommonDefine.SETTING, Context.MODE_PRIVATE);

		new Thread() {
			@Override
			public void run() {
				super.run();
				//初始化地址数据
				LocationManager.getInstance(getApplicationContext()).initLocationInfo();
				mHandler.obtainMessage(CommonDefine.ERR_LOCATION_INIT_END).sendToTarget();
			}
		}.start();
	}

	class LoginRunnable implements Runnable {
		@Override
		public void run() {
			String account = mSharedPreferences.getString(CommonDefine.ACCOUNT, null);
			String password = mSharedPreferences.getString(CommonDefine.PASSWORD, null);
			HttpManager httpHandler = HttpManager.getInstance(mHandler);
			httpHandler.login(account, password);
		}
	}

	class PersonDataInit implements Runnable {
		String account;
		String password;

		public PersonDataInit(String account, String password) {
			this.account = account;
			this.password = password;
		}

		@Override
		public void run() {
			HttpManager httpHandler = HttpManager.getInstance(mHandler);
			httpHandler.getPersonInfo(account, password);
		}
	}

	@Override
	public void handleNomal(String msg) {
		boolean isAutoLogin = mSharedPreferences.getBoolean(CommonDefine.IS_AUTO_LOGIN, true);
		if (isAutoLogin) {
			if (!mIsLoginSuc) {
				doInThread(new LoginRunnable());
				mIsLoginSuc = true;
			} else {
				if (!mIsGetInfoSuc) {
					String account = mSharedPreferences.getString(CommonDefine.ACCOUNT, null);
					String password = mSharedPreferences.getString(CommonDefine.PASSWORD, null);
					doInThread(new PersonDataInit(account, password));
					mIsGetInfoSuc = true;
				} else {
					Intent serviceIntent = new Intent(this, TytService.class);
					serviceIntent.putExtra(TytService.COMMAND, TytService.COMMAND_INIT);
					startService(serviceIntent);

					Intent allIntent = new Intent(this, AllInfoActivity.class);
					startActivity(allIntent);
				}
			}
		} else {
			Intent login = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(login);
			finish();
		}
	}
}
