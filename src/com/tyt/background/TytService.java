package com.tyt.background;

import java.lang.ref.WeakReference;

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
import com.tyt.data.OrderManager;
import com.tyt.net.HttpManager;
import com.tyt.view.BaseActivity;

public class TytService extends Service {
	public static final String COMMAND = "command";
	public static final int COMMAND_INIT = 0;
	public static final int COMMAND_START_REFRESH = COMMAND_INIT + 1;
	public static final int COMMAND_STOP_REFRESH = COMMAND_START_REFRESH + 1;

	public static final String FLAG = "Flag";
	public static final int FLAG_GET_ORDER = 0;
	public static final int FLAG_GET_CHANGE_ORDER = FLAG_GET_ORDER + 1;
	public static final int FLAG_CHECK_TICKET = FLAG_GET_CHANGE_ORDER + 1;
	public static final int FLAG_GET_PERSON_INFO = FLAG_CHECK_TICKET + 1;
	public static final int FLAG_RELEASE = FLAG_GET_PERSON_INFO + 1;
	private boolean mIsRefresh = true;
	private boolean mIsCheckTicket = true;
	private boolean mIsCheckChange = true;
	private OrderManager mOrderManager;
	private int mMaxId = 1;
	private TYTApplication mApplication ;
	private long mTime;
	private boolean mIsRun = true;

	protected Handler mHandler;

	private static class ServiceHandler extends Handler {  
		private WeakReference<TytService> mService;

		public ServiceHandler(TytService service) {  
			mService = new WeakReference<TytService>(service);  
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			TytService relService = mService.get();
			if (relService != null) {
				switch (msg.what) {
				case CommonDefine.ERR_NET:

					break;
				case CommonDefine.ERR_NONE:
					relService.handleNoErrMsg(msg);
					break;
				default:
					break;
				}
			}
		}
	}

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
				String strMsg = msgJson.getString(JsonTag.MSG);
				if (!strMsg.trim().equals("ok")) {
					int msgCode = Integer.parseInt(strMsg);
					if (code == CommonDefine.ERR_SERVER && msgCode == 2) {
						Intent loginOtherIntent = new Intent(BaseActivity.ACTION_LOGIN_OTHER);
						sendBroadcast(loginOtherIntent);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case FLAG_GET_CHANGE_ORDER:
			String changeOrders = (String)msg.obj;
			long mtime = mOrderManager.changedOrderInfo(changeOrders);
			if (mtime != 1) {
				mTime = mtime;
			}
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
		mHandler = new ServiceHandler(this);

		mOrderManager = OrderManager.getInstance(getApplicationContext());
		mApplication = (TYTApplication)getApplication();

		mTime = System.currentTimeMillis();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			switch (intent.getIntExtra(COMMAND, COMMAND_INIT)) {
			case COMMAND_INIT:
				mApplication.doInThread(new RefreshData());
				mApplication.doInThread(new ChangeHandler());
				mApplication.doInThread(new CheckTicket());
				break;
			case COMMAND_START_REFRESH:
				mIsRefresh = true;
				mIsCheckTicket = true;
				mIsCheckChange = true;
				break;
			case COMMAND_STOP_REFRESH:
				mIsRefresh = false;
				mIsCheckTicket = false;
				mIsCheckChange = false;
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
			while (mIsRun) {
				if (mIsRefresh) {
					HttpManager httpHandler = HttpManager.getInstance(mHandler);
					httpHandler.getAllInfo(mMaxId);
				}
				try {
					Thread.sleep(CommonDefine.DELAY_FOR_GET_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class CheckTicket implements Runnable {
		@Override
		public void run() {
			while (mIsRun) {
				if (mIsCheckTicket) {
					HttpManager httpHandler = HttpManager.getInstance(mHandler);
					httpHandler.checkTicket(getApplicationContext());
				}
				try {
					Thread.sleep(CommonDefine.DELAY_FOR_CHECK_TICKET);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class ChangeHandler implements Runnable {
		@Override
		public void run() {
			while (mIsRun) {
				if (mIsCheckChange) {
					HttpManager httpHandler = HttpManager.getInstance(mHandler);
					httpHandler.getAllChangeInfo(mTime);
				}
				try {
					Thread.sleep(CommonDefine.DELAY_FOR_GET_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mIsRun = false;
	}
}
