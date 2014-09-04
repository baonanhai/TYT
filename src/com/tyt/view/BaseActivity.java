package com.tyt.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.dxj.tyt.R;
import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;
import com.tyt.common.TYTApplication;

public abstract class BaseActivity extends Activity {
	public static final String ACTION_LOGIN_OTHER = "com.tyt.loginother"; 

	private TYTApplication mApplication;
	private LoginOtherReceiver mLoginOtherReceiver;
	protected Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CommonDefine.ERR_NONE:
				try {
					JSONObject msgJson = new JSONObject((String)msg.obj);
					int code = msgJson.getInt(JsonTag.CODE);
					if (code == CommonDefine.ERR_SERVER_NONE) {
						if (msgJson.has(JsonTag.DATA)) {
							handleNomal(msgJson.getString(JsonTag.DATA));
						} else {
							handleNomal(msgJson.getString(JsonTag.MSG));
						}
					} else if (code == CommonDefine.ERR_SERVER) {
						handleServerErr(msgJson.getString(JsonTag.MSG));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case CommonDefine.ERR_NET:
				handleNetErr(getString(R.string.err_net));
				break;
			default:
				handleOtherMsg(msg);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (TYTApplication)getApplication();
		mLoginOtherReceiver = new LoginOtherReceiver();  
		IntentFilter filter = new IntentFilter();  
		filter.addAction(ACTION_LOGIN_OTHER);  
		registerReceiver(mLoginOtherReceiver, filter);  
	}

	public void doInThread(Runnable task) {
		mApplication.doInThread(task);
	}

	public abstract void handleNetErr(String err);
	public abstract void handleServerErr(String err);
	public abstract void handleNomal(String msg);
	public abstract void handleOtherMsg(Message msg);

	class LoginOtherReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			loginOther();
		}
	}
	
	protected void setTitle(String title) {
		TextView titleView = (TextView)findViewById(R.id.title);
		titleView.setText(title);
	}


	protected void loginOther() {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mLoginOtherReceiver);
	}
}
