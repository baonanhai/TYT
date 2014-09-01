package com.tyt.background;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;
import com.tyt.common.TYTApplication;
import com.tyt.data.LocationManager;
import com.tyt.data.OrderManager;
import com.tyt.data.PersonInfo;
import com.tyt.net.HttpManager;
import com.tyt.view.BaseActivity;

public class TytService extends Service {
	public static final String COMMAND = "command";
	public static final int COMMAND_INIT = 0;
	public static final int COMMAND_START_REFRESH = COMMAND_INIT + 1;
	public static final int COMMAND_STOP_REFRESH = COMMAND_START_REFRESH + 1;

	public static final String FLAG = "Flag";
	public static final int FLAG_GET_ORDER = 0;
	public static final int FLAG_CHECK_TICKET = FLAG_GET_ORDER + 1;
	public static final int FLAG_GET_PERSON_INFO = FLAG_CHECK_TICKET + 1;
	public static final int FLAG_RELEASE = FLAG_GET_PERSON_INFO + 1;
	private boolean mIsRefresh = true;
	private OrderManager mOrderManager;
	private int mMaxId = 1;
	private TYTApplication mApplication ;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CommonDefine.ERR_NET:

				break;
			case CommonDefine.ERR_NONE:
				handleNoErrMsg(msg);
				break;
			default:
				break;
			}
		}
	};

	private void handleNoErrMsg(Message msg) {
		switch (msg.arg1) {
		case FLAG_GET_ORDER:
			String orders = (String)msg.obj;
			int maxId = mOrderManager.addOrderInfo(orders);
			if (maxId != 1) {
				mMaxId = maxId;
			}
			break;
		case FLAG_CHECK_TICKET:
			JSONObject msgJson;
			try {
				msgJson = new JSONObject((String)msg.obj);
				int code = msgJson.getInt(JsonTag.CODE);
				if (code == CommonDefine.ERR_SERVER) {
					Intent loginOtherIntent = new Intent(BaseActivity.ACTION_LOGIN_OTHER);
					sendBroadcast(loginOtherIntent);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case FLAG_GET_PERSON_INFO:
			mApplication.setPersonInfo(new PersonInfo((String)msg.obj));
			break;
		default:
			break;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		//初始化地址数据
		LocationManager.getInstance(getApplicationContext()).initLocationInfo();
		mOrderManager = OrderManager.getInstance(getApplicationContext());
		mApplication = (TYTApplication)getApplication();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			switch (intent.getIntExtra(COMMAND, COMMAND_INIT)) {
			case COMMAND_INIT:
				mApplication.doInThread(new RefreshData());
				String account = intent.getStringExtra(CommonDefine.ACCOUNT);
				String password = intent.getStringExtra(CommonDefine.PASSWORD);
				mApplication.doInThread(new PersonDataInit(account, password));
				break;
			case COMMAND_START_REFRESH:
				mIsRefresh = true;
				mApplication.doInThread(new RefreshData());
				break;
			case COMMAND_STOP_REFRESH:
				mIsRefresh = false;
				break;
			default:
				break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	class RefreshData implements Runnable {
		@Override
		public void run() {
			while (mIsRefresh) {
				HttpManager httpHandler = HttpManager.getInstance(mHandler);
				httpHandler.getAllInfo(mMaxId);
				try {
					Thread.sleep(CommonDefine.DELAY_FOR_GET_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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

	class CheckTicket implements Runnable {
		@Override
		public void run() {
			while (mIsRefresh) {
				try {
					Thread.sleep(CommonDefine.DELAY_FOR_CHECK_TICKET);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				HttpManager httpHandler = HttpManager.getInstance(mHandler);
				httpHandler.checkTicket(getApplicationContext());
			}
		}
	}
}
