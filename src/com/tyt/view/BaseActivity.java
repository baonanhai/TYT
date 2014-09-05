package com.tyt.view;

import java.lang.ref.WeakReference;

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
import android.widget.Toast;

import com.dxj.tyt.R;
import com.tyt.background.TytService;
import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;
import com.tyt.common.TYTApplication;

public class BaseActivity extends Activity {
	public static final String ACTION_LOGIN_OTHER = "com.tyt.loginother"; 

	private TYTApplication mApplication;
	private LoginOtherReceiver mLoginOtherReceiver;

	protected Handler mHandler;

	private static class MyHandler extends Handler {  
		private WeakReference<BaseActivity> mActivity;

		public MyHandler(BaseActivity activity) {  
			mActivity = new WeakReference<BaseActivity>(activity);  
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			BaseActivity relActivity = mActivity.get();
			if (relActivity != null) {
				switch (msg.what) {
				case CommonDefine.ERR_LOCATION_INIT_END:
					relActivity.handleNomal(null);
					break;
				case CommonDefine.ERR_NONE:
					try {
						JSONObject msgJson = new JSONObject((String)msg.obj);
						int code = msgJson.getInt(JsonTag.CODE);
						if (code == CommonDefine.ERR_SERVER_NONE) {
							if (msgJson.has(JsonTag.DATA)) {
								relActivity.handleNomal(msgJson.getString(JsonTag.DATA));
							} else {
								relActivity.handleNomal(msgJson.getString(JsonTag.MSG));
							}
						} else if (code == CommonDefine.ERR_SERVER) {
							relActivity.handleServerErr(msgJson.getString(JsonTag.MSG));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case CommonDefine.ERR_NET:
					relActivity.handleNetErr(relActivity.getString(R.string.err_net));
					break;
				default:
					relActivity.handleOtherMsg(msg);
					break;
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new MyHandler(this);
		mApplication = (TYTApplication)getApplication();
		mLoginOtherReceiver = new LoginOtherReceiver();  
		IntentFilter filter = new IntentFilter();  
		filter.addAction(ACTION_LOGIN_OTHER);  
		registerReceiver(mLoginOtherReceiver, filter);  
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent serviceIntent = new Intent(getApplicationContext(), TytService.class);
		serviceIntent.putExtra(TytService.COMMAND, TytService.COMMAND_START_REFRESH);
		startService(serviceIntent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Intent serviceIntent = new Intent(getApplicationContext(), TytService.class);
		serviceIntent.putExtra(TytService.COMMAND, TytService.COMMAND_STOP_REFRESH);
		startService(serviceIntent);
	}

	public void doInThread(Runnable task) {
		mApplication.doInThread(task);
	}

	protected void handleNetErr(String err) {
		Toast.makeText(this, R.string.err_net, Toast.LENGTH_SHORT).show();
	}

	protected void handleServerErr(String err) {
		Toast.makeText(this, R.string.err_server, Toast.LENGTH_SHORT).show();
	}

	protected void handleNomal(String msg) {

	}

	protected void handleOtherMsg(Message msg) {

	}

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
