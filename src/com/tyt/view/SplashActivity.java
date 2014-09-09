package com.tyt.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Toast;

import com.dxj.tyt.R;
import com.tyt.background.TytService;
import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;
import com.tyt.common.TYTApplication;
import com.tyt.data.LocationManager;
import com.tyt.data.PersonInfo;
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
				mHandler.obtainMessage(CommonDefine.LOCATION_INIT_END).sendToTarget();
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
		String account = mSharedPreferences.getString(CommonDefine.ACCOUNT, null);
		String password = mSharedPreferences.getString(CommonDefine.PASSWORD, null);
		boolean isAutoLogin = mSharedPreferences.getBoolean(CommonDefine.IS_AUTO_LOGIN, true);
		if (isAutoLogin && account != null && password != null) {
			if (!mIsLoginSuc) {
				doInThread(new LoginRunnable());
				mIsLoginSuc = true;
			} else {
				if (!mIsGetInfoSuc) {
					Editor editor = mSharedPreferences.edit();
					try {
						JSONObject response = new JSONObject(msg);
						editor.putInt(CommonDefine.SERVE_DAYS, response.getInt(JsonTag.SERVE_DAYS));
						editor.putString(CommonDefine.TICKET, response.getString(JsonTag.TICKET));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					editor.commit();

					doInThread(new PersonDataInit(account, password));
					mIsGetInfoSuc = true;
				} else {
					((TYTApplication)getApplication()).setPersonInfo(new PersonInfo(msg));

					Intent serviceIntent = new Intent(this, TytService.class);
					serviceIntent.putExtra(TytService.COMMAND, TytService.COMMAND_INIT);
					startService(serviceIntent);

					Intent allIntent = new Intent(this, AllInfoActivity.class);
					startActivity(allIntent);
					finish();
				}
			}
		} else {
			Intent login = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(login);
			finish();
		}
	}

	@Override
	protected void handleNetErr(String err) {
		Intent login = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(login);
		finish();
		Toast.makeText(this, R.string.err_net, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void handleServerErr(String err) {
		Intent login = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(login);
		finish();
	}
}
