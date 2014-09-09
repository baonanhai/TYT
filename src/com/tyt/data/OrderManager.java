package com.tyt.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;
import com.tyt.common.TytLog;

public class OrderManager {
	private List<OrderChangeObserver> mOrderChangeObservers;
	private static OrderManager mOrderManager;
	private RuntimeExceptionDao<OrderInfo, Integer> mOrdersDao;

	private OrderManager(Context context) {
		mOrdersDao = new DatabaseHelper(context).getOrderInfoDao();
	}

	public synchronized static OrderManager getInstance(Context context) {
		if (mOrderManager == null) {
			mOrderManager = new OrderManager(context);
		}
		return mOrderManager;
	}

	public void addOrderChangeObserver(OrderChangeObserver orderChangeObserver) {
		if (mOrderChangeObservers == null) {
			mOrderChangeObservers = new ArrayList<OrderChangeObserver>();
		}
		if (!mOrderChangeObservers.contains(orderChangeObserver)) {
			mOrderChangeObservers.add(orderChangeObserver);
		}
	}

	public void removeOrderChangeObserver(OrderChangeObserver orderChangeObserver) {
		if (mOrderChangeObservers != null && mOrderChangeObservers.contains(orderChangeObserver)) {
			mOrderChangeObservers.remove(orderChangeObserver);
		}
	}

	private void notifyObservers() {
		if (mOrderChangeObservers != null) {
			for (OrderChangeObserver searchObserver : mOrderChangeObservers) {
				searchObserver.onDataChange();
			}
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
				Map<String, Object> userMap=new HashMap<String, Object>();  
				userMap.put("pubQQ", temp.getPubQQ());  
				userMap.put("tel", temp.getTel());  
				userMap.put("taskContent", temp.getTaskContent());
				List<OrderInfo> infos = mOrdersDao.queryForFieldValues(userMap);
				if (infos.size() == 0) {
					isDataChange = true;
					mOrdersDao.create(temp);
				} else {
					TytLog.i(temp + "has in DB!");
				}
				if (temp.getId() > maxId) {
					maxId = temp.getId();
				}
			}
			if (isDataChange) {
				notifyObservers();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return maxId;
	}

	public void updateOrderStatus(int id, int status) {
		Log.i("sssss", "id" + id + " status:" + status);
		OrderInfo info = mOrdersDao.queryForId(id);
		info.setStatus(status);
		mOrdersDao.update(info);
		notifyObservers();
	}

	public long changedOrderInfo(String response) {
		long mtime = 1;
		try {
			boolean isDataChange = false;
			JSONArray allInfo = new JSONObject(response).getJSONArray(JsonTag.DATA);
			for (int i = 0; i < allInfo.length(); i++) {
				OrderInfo temp = new OrderInfo(allInfo.getJSONObject(i));
				OrderInfo info = mOrdersDao.queryForId(temp.getId());
				info.setStatus(temp.getStatus());
				mOrdersDao.update(info);
				mtime = info.getMtime();
				isDataChange = true;
			}

			if (isDataChange) {
				notifyObservers();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mtime;
	}

	@SuppressWarnings("unchecked")
	public List<OrderInfo> search(List<String> startSearchKey, List<String> endSearchKey) {
		long todayStart = getTodayMorning();
		List<OrderInfo> result = null;
		QueryBuilder<OrderInfo, Integer> qb = mOrdersDao.queryBuilder().orderBy("ctime", true);
		try {
			Where<OrderInfo, Integer> where = qb.where();
			if (endSearchKey == null) { 
				where.and(where.in("startPoint", startSearchKey), where.eq("status", CommonDefine.ORDER_STATE_USEFULL), 
						where.eq("mIsBlack", false), where.gt("ctime", todayStart));
			} else {
				where.and(where.in("startPoint", startSearchKey), where.in("destPoint", endSearchKey, 
						where.eq("status", CommonDefine.ORDER_STATE_USEFULL)), where.eq("mIsBlack", false), 
						where.gt("ctime", todayStart));
			}
			result = mOrdersDao.query(where.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public OrderInfo getOrder(int orderId) {
		OrderInfo result = mOrdersDao.queryForId(orderId);
		return result;
	}

	public boolean isKeep(int orderId) {
		OrderInfo result = mOrdersDao.queryForId(orderId);
		return result.isKeep();
	}

	public void keep(OrderInfo order) {
		order.setIsKeep(true);
		mOrdersDao.update(order);
	}

	@SuppressWarnings("unchecked")
	public List<OrderInfo> getAllKeepOrder() {
		long weekStart = getMondayMorning();
		List<OrderInfo> result = null;
		QueryBuilder<OrderInfo, Integer> qb = mOrdersDao.queryBuilder().orderBy("ctime", true);
		Where<OrderInfo, Integer> where = qb.where();
		try {
			where.and(where.eq("mIsKeep", true), where.gt("ctime", weekStart));
			result = mOrdersDao.query(where.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public void blackOrder(OrderInfo info) {
		info.setIsBlack(true);
		mOrdersDao.update(info);
		Map<String, Object> userMap=new HashMap<String, Object>();  
		userMap.put("pubQQ", info.getPubQQ());  
		userMap.put("tel", info.getTel());  
		List<OrderInfo> result = mOrdersDao.queryForFieldValues(userMap);
		String relTask = info.getTaskContent();
		if (relTask.startsWith("[") ) {
			relTask = relTask.substring(relTask.indexOf(".") + 1);
		}

		boolean isDataChange = false;

		for (OrderInfo order : result) {
			String content = order.getTaskContent();
			if (content.startsWith("[") ) {
				content = content.substring(content.indexOf(".") + 1);
			}
			if (relTask.equals(content)) {
				order.setIsBlack(true);
				mOrdersDao.update(order);
				isDataChange = true;
			}
		}

		if (isDataChange) {
			notifyObservers();
		}
	}

	public List<OrderInfo> getAllReleaseOrder (String tel) {
		return mOrdersDao.queryForEq("tel", tel);
	}

	public List<OrderInfo> getAllReleaseOrderToday (String tel, int orderState) {
		List<OrderInfo> result = getAllReleaseOrder(tel);
		List<OrderInfo> todayResult = new ArrayList<>();
		long todayStart = getTodayMorning();
		for (OrderInfo orderInfo : result) {
			if (orderInfo.getCtime() > todayStart && orderInfo.getStatus() == orderState) {
				todayResult.add(orderInfo);
			}
		}
		return todayResult;
	}

	public List<OrderInfo> getAllReleaseOrderWeek (String tel) {
		List<OrderInfo> result = getAllReleaseOrder(tel);
		List<OrderInfo> weekResult = new ArrayList<>();
		long weekStart = getMondayMorning();
		long todayStart = getTodayMorning();
		for (OrderInfo orderInfo : result) {
			if (orderInfo.getCtime() > weekStart && orderInfo.getCtime() < todayStart) {
				weekResult.add(orderInfo);
			}
		}
		return weekResult;
	}

	public static long getTodayMorning() { 
		Calendar cal = Calendar.getInstance(); 
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.set(Calendar.SECOND, 0); 
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.MILLISECOND, 0); 
		return cal.getTimeInMillis(); 
	} 

	public static long getMondayMorning(){ 
		Calendar cal = Calendar.getInstance(); 
		cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0,0); 
		return cal.getTimeInMillis(); 
	} 
}
