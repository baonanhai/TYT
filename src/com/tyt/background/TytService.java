package com.tyt.background;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.tyt.common.CommonDefine;
import com.tyt.common.TYTApplication;
import com.tyt.data.LocationManager;
import com.tyt.data.OrderManager;
import com.tyt.net.HttpManager;

public class TytService extends Service {
	private boolean mIsRefresh = true;
	private OrderManager mOrderManager;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CommonDefine.ERR_NET:

				break;
			case CommonDefine.ERR_NONE:
				String orders = (String)msg.obj;
				mOrderManager.addOrderInfo(orders);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		//初始化地址数据
		LocationManager.getInstance(getApplicationContext()).initLocationInfo();

		mOrderManager = new OrderManager(getApplicationContext());

		TYTApplication application = (TYTApplication)getApplication();
		application.doInThread(new RefreshData());
	}

	class RefreshData implements Runnable {
		@Override
		public void run() {
			while (mIsRefresh) {
				HttpManager httpHandler = HttpManager.getInstance(mHandler);
				httpHandler.getAllInfo(0);
				try {
					Thread.sleep(CommonDefine.DELAY_FOR_GET_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
