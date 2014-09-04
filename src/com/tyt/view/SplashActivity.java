package com.tyt.view;

import com.dxj.tyt.R;
import com.tyt.data.LocationManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
	private Handler mHander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent login = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(login);
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new Thread() {
			@Override
			public void run() {
				super.run();
				//初始化地址数据
				LocationManager.getInstance(getApplicationContext()).initLocationInfo();
				mHander.obtainMessage().sendToTarget();
			}
		}.start();
	}
}
