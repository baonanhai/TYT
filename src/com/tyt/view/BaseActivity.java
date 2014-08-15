package com.tyt.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;

import com.dxj.tyt.R;
import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;
import com.tyt.common.TYTApplication;

public abstract class BaseActivity extends ActionBarActivity {
	private TYTApplication mApplication;
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
						handleNomal(msgJson.getString(JsonTag.DATA));
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
	}

	public void doInThread(Runnable task) {
		mApplication.doInThread(task);
	}

	public abstract void handleNetErr(String err);
	public abstract void handleServerErr(String err);
	public abstract void handleNomal(String msg);
	public abstract void handleOtherMsg(Message msg);
}
