package com.tyt.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;

public class OrderManager {
	private List<SearchObserver> mSearchObservers;
	private Context mContext;
	private List<OrderInfo> mAllOrders;
	private List<OrderInfo> mAllKeepOrders;
	private Set<String> mBlackOrder;
	private static OrderManager mOrderManager;

	private OrderManager(Context context) {
		mContext = context;

		if (!getBackInfo()) {
			mAllOrders = new ArrayList<OrderInfo>();
		}

		if (!getKeepBackInfo()) {
			mAllKeepOrders = new ArrayList<OrderInfo>();
		}

		if (!getBlackInfo()) {
			mBlackOrder = new HashSet<String>();
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
		if (!mSearchObservers.contains(searchObserver)) {
			mSearchObservers.add(searchObserver);
		}
	}

	public void removeSearchObserver(SearchObserver searchObserver) {
		if (mSearchObservers != null && mSearchObservers.contains(searchObserver)) {
			mSearchObservers.remove(searchObserver);
		}
	}

	public int addOrderInfo(String response) {
		int maxId = 1;
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
					if (temp.getId() > maxId) {
						maxId = temp.getId();
					}
				}
			}

			if (isDataChange) {
				saveInfo(CommonDefine.ORDER_SAVE, mAllOrders);
				if (mSearchObservers != null) {
					for (SearchObserver searchObserver : mSearchObservers) {
						searchObserver.onDataChange();
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return maxId;
	}

	public List<OrderInfo> search(List<String> startSearchKey, List<String> endSearchKey) {
		Log.i("sssss", "startSearchKey:" + startSearchKey);
		List<OrderInfo> startResult = new ArrayList<OrderInfo>();
		for (int i = 0; i < mAllOrders.size(); i++) {
			for (int j = 0; j < startSearchKey.size(); j++) {
				if (startSearchKey.get(j).equals(mAllOrders.get(i).getStartPoint())) {
					startResult.add(mAllOrders.get(i));
				}
			}
		}

		if (endSearchKey != null) {
			List<OrderInfo> endResult = new ArrayList<OrderInfo>();
			for (int i = 0; i < startResult.size(); i++) {
				for (int j = 0; j < endSearchKey.size(); j++) {
					if (endSearchKey.get(j).equals(startResult.get(i).getDestPoint())) {
						endResult.add(startResult.get(i));
					}
				}
			}
			return checkBlack(endResult);
		} else {
			return checkBlack(startResult);
		}
	}

	private List<OrderInfo> checkBlack(List<OrderInfo> OrdersInfo) {
		List<OrderInfo> result = new ArrayList<OrderInfo>();
		for (OrderInfo info : OrdersInfo) {
			StringBuilder sb = new StringBuilder();
			String content = info.getTaskContent();
			if (content.startsWith("[") ) {
				sb.append(content.substring(content.indexOf(".") + 1));
			}
			sb.append(info.getPubQQ());
			sb.append(info.getTel());
			if (!mBlackOrder.contains(sb.toString())) {
				result.add(info);
			}
		}

		Collections.sort(result, new Comparator<OrderInfo>(){

			@Override
			public int compare(OrderInfo lhs, OrderInfo rhs) {
				return -(int)(lhs.getCtime() - rhs.getCtime());
			}
		});
		return result;
	}

	public List<OrderInfo> getAllOrder() {
		return mAllOrders;
	}

	public OrderInfo getOrder(int orderId) {
		OrderInfo result = null;
		for (OrderInfo info : mAllOrders) {
			if (info.getId() == orderId) {
				result = info;
				break;
			}
		}
		return result;
	}

	public boolean isKeep(int orderId) {
		for (OrderInfo info : mAllKeepOrders) {
			if (info.getId() == orderId) {
				return true;
			}
		}
		return false;
	}

	public void keep(OrderInfo info) {
		if (!mAllKeepOrders.contains(info)) {
			mAllKeepOrders.add(info);
			saveInfo(CommonDefine.KEEP_ORDER_SAVE, mAllKeepOrders);
		}
	}

	public List<OrderInfo> getAllKeepOrder() {
		return mAllKeepOrders;
	}

	public void addBlackOrder(OrderInfo info) {
		StringBuilder sb = new StringBuilder();
		String content = info.getTaskContent();
		if (content.startsWith("[") ) {
			sb.append(content.substring(content.indexOf(".") + 1));
		}
		sb.append(info.getPubQQ());
		sb.append(info.getTel());
		mBlackOrder.add(sb.toString());
		saveInfo(CommonDefine.BLACK_ORDER_SAVE, mBlackOrder);
		if (mSearchObservers != null) {
			for (SearchObserver searchObserver : mSearchObservers) {
				searchObserver.onDataChange();
			}
		}
	}

	public void saveInfo(String file, Object object) {
		FileOutputStream temp;
		try {
			temp = mContext.openFileOutput(file, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(temp); 
			os.writeObject(object);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private boolean getBackInfo() {
		FileInputStream temp;
		try {
			temp = mContext.openFileInput(CommonDefine.ORDER_SAVE);
			ObjectInputStream oi = new ObjectInputStream(temp);
			List<OrderInfo> tempOrders = (List<OrderInfo>)oi.readObject();
			int dayTime = 1000 * 60 * 60 * 24;
			for (OrderInfo info : tempOrders) {
				if ((System.currentTimeMillis() - info.getCtime()) < dayTime) {
					if (mAllOrders == null) {
						mAllOrders = new ArrayList<OrderInfo>();
					}
					mAllOrders.add(info);
				}
			}
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
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean getKeepBackInfo() {
		FileInputStream temp;
		try {
			temp = mContext.openFileInput(CommonDefine.KEEP_ORDER_SAVE);
			ObjectInputStream oi = new ObjectInputStream(temp);
			List<OrderInfo> tempOrders = (List<OrderInfo>)oi.readObject();
			int dayTime = 1000 * 60 * 60 * 24 * 7;
			for (OrderInfo info : tempOrders) {
				if ((System.currentTimeMillis() - info.getCtime()) < dayTime) {
					if (mAllKeepOrders == null) {
						mAllKeepOrders = new ArrayList<OrderInfo>();
					}
					mAllKeepOrders.add(info);
				}
			}
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
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean getBlackInfo() {
		FileInputStream temp;
		try {
			temp = mContext.openFileInput(CommonDefine.BLACK_ORDER_SAVE);
			ObjectInputStream oi = new ObjectInputStream(temp);
			mBlackOrder = (HashSet<String>)oi.readObject();
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
		return false;
	}
}
