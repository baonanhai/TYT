package com.tyt.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;

public class OrderManager {
	private List<SearchObserver> mSearchObservers;
	private Context mContext;
	private List<OrderInfo> mAllOrders;

	private static OrderManager mOrderManager;
	private SharedPreferences mSharedPreferences;

	private OrderManager(Context context) {
		mContext = context;
		mSharedPreferences = mContext.getSharedPreferences(CommonDefine.SETTING, Context.MODE_PRIVATE);
		
		if (!getBackInfo()) {
			mAllOrders = new ArrayList<OrderInfo>();
		}
	}

	public synchronized static OrderManager getInstance(Context context) {
		if (mOrderManager == null) {
			mOrderManager = new OrderManager(context);
		}
		return mOrderManager;
	}

	public void addSearchObserver(SearchObserver searchObserver) {
		if (mSearchObservers == null) {
			mSearchObservers = new ArrayList<SearchObserver>();
		}
		mSearchObservers.add(searchObserver);
	}

	public void removeSearchObserver(SearchObserver searchObserver) {
		if (mSearchObservers != null && mSearchObservers.contains(searchObserver)) {
			mSearchObservers.remove(searchObserver);
		}
	}

	public void addOrderInfo(String response) {
		JSONArray allInfo;
		try {
			boolean isDataChange = false;
			allInfo = new JSONObject(response).getJSONArray(JsonTag.DATA);
			for (int i = 0; i < allInfo.length(); i++) {
				OrderInfo temp = new OrderInfo(allInfo.getJSONObject(i));
				boolean hasIn = false;
				for (int j = 0; j < mAllOrders.size(); j++) {
					OrderInfo tempOrderInfo = mAllOrders.get(j);
					if (tempOrderInfo.getPubQQ().equals(temp.getPubQQ()) 
							&& tempOrderInfo.getTel().equals(temp.getTel()) 
							&& tempOrderInfo.getTaskContent().equals(temp.getTaskContent())) {
						hasIn = true;
						break;
					}
				}
				if (!hasIn) {
					isDataChange = true;
					mAllOrders.add(temp);
				}
			}

			if (isDataChange) {
				saveInfo();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (mSearchObservers != null) {
			for (SearchObserver searchObserver : mSearchObservers) {
				searchObserver.onDataChange();
			}
		}
	}

	public List<OrderInfo> search(List<String> startSearchKey, List<String> endSearchKey) {
		List<OrderInfo> startResult = new ArrayList<OrderInfo>();
		for (int i = 0; i < mAllOrders.size(); i++) {
			for (int j = 0; j < startSearchKey.size(); j++) {
				if (mAllOrders.get(i).getStartPoint().equals(startSearchKey.get(j))) {
					startResult.add(mAllOrders.get(i));
				}
			}
		}

		if (endSearchKey != null) {
			List<OrderInfo> endResult = new ArrayList<OrderInfo>();
			for (int i = 0; i < startResult.size(); i++) {
				for (int j = 0; j < endSearchKey.size(); j++) {
					if (startResult.get(i).getDestPoint().equals(endSearchKey.get(j))) {
						endResult.add(startResult.get(i));
					}
				}
			}
			return endResult;
		} else {
			return startResult;
		}
	}

	public void saveInfo() {
		FileOutputStream temp;
		try {
			temp = mContext.openFileOutput(CommonDefine.ORDER_SAVE, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(temp); 
			os.writeObject(mAllOrders);
			os.flush();
			os.close();
			Editor editor = mSharedPreferences.edit();
			editor.putLong(CommonDefine.SAVE_TIME, System.currentTimeMillis());
			editor.commit();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private boolean getBackInfo() {
		long saveTime = mSharedPreferences.getLong(CommonDefine.SAVE_TIME, 0);
		if (System.currentTimeMillis() - saveTime < 1000 * 60 * 60 * 24) {
			FileInputStream temp;
			try {
				temp = mContext.openFileInput(CommonDefine.ORDER_SAVE);
				ObjectInputStream oi = new ObjectInputStream(temp);
				mAllOrders = (List<OrderInfo>)oi.readObject();
				oi.close();
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
