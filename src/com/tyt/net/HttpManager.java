package com.tyt.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;
import android.util.Log;

import com.tyt.common.CommonDefine;
import com.tyt.common.CommonUtil;
import com.tyt.common.JsonTag;
import com.tyt.common.UrlTag;

public class HttpManager {
	private static HttpManager mHttpHandler;
	private Handler mHandler;

	private HttpManager() {
	}

	public synchronized static HttpManager getInstance(Handler handler) {
		if (mHttpHandler == null) {
			mHttpHandler = new HttpManager();
		}
		mHttpHandler.setHandler(handler);
		return mHttpHandler;
	}

	private void setHandler(Handler handler) {
		mHandler = handler;
	}

	public void login(String account, String password) {
		List <NameValuePair> params = initRequest(account);
		params.add(new BasicNameValuePair(JsonTag.CELLPHONE, account)); 
		params.add(new BasicNameValuePair(JsonTag.PASSWORD, password)); 
		String response = HttpOperator.doPost(CommonDefine.URL_LOGIN, params);
		if (response == null) {
			mHandler.obtainMessage(CommonDefine.ERR_NET).sendToTarget();
		} else {
			mHandler.obtainMessage(CommonDefine.ERR_NONE, response).sendToTarget();
		}
	}
	
	public void register(String phone, String password, String qq, String name, String idcard) {
		List <NameValuePair> params = initRequest(phone);
		params.add(new BasicNameValuePair(JsonTag.CELLPHONE, phone)); 
		params.add(new BasicNameValuePair(JsonTag.PASSWORD, password)); 
		params.add(new BasicNameValuePair(JsonTag.USER_NAME, phone)); 
		params.add(new BasicNameValuePair(JsonTag.USERSIGN, CommonDefine.USERSIGN)); 
		params.add(new BasicNameValuePair(JsonTag.PCSIGN, CommonUtil.MD5(phone))); 
		params.add(new BasicNameValuePair(JsonTag.QQ, qq)); 
		params.add(new BasicNameValuePair(JsonTag.TRUE_NAME, name)); 
		params.add(new BasicNameValuePair(JsonTag.ID_CARD, idcard)); 
		String response = HttpOperator.doPost(CommonDefine.URL_REGISTER, params);
		Log.i("sssss", response);
		if (response == null) {
			mHandler.obtainMessage(CommonDefine.ERR_NET).sendToTarget();
		} else {
			mHandler.obtainMessage(CommonDefine.ERR_NONE, response).sendToTarget();
		}
	}
	
	public void getAllInfo(int maxId) {
		List <NameValuePair> params = initRequest("" + maxId);
		params.add(new BasicNameValuePair(JsonTag.MAX_ID, "" + maxId)); 
		params.add(new BasicNameValuePair(JsonTag.SIZE, "" + 1000)); 
		String response = HttpOperator.doPost(CommonDefine.URL_QUERY, params);
		if (response == null) {
			mHandler.obtainMessage(CommonDefine.ERR_NET).sendToTarget();
		} else {
			mHandler.obtainMessage(CommonDefine.ERR_NONE, response).sendToTarget();
		}
	}
	
	private List <NameValuePair> initRequest(String info) {
		List <NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair(JsonTag.VERSION, CommonDefine.VERSION)); 
		params.add(new BasicNameValuePair(JsonTag.PLAT_ID, CommonDefine.PLAT_ID)); 
		params.add(new BasicNameValuePair(JsonTag.TOKEN, initToken(info))); 
		return params;
	}

	private String initToken(String info) {
		return CommonUtil.MD5(info + CommonDefine.PRIVATEKEY);
	}
	
	public void getVerifyCode(String smsMob, int smsText) {
		StringBuilder urlSb = new StringBuilder();
		urlSb.append(CommonDefine.URL_FOR_VERIFYCODE);
		urlSb.append("?");
		urlSb.append(UrlTag.UID);
		urlSb.append("=");
		urlSb.append(CommonDefine.VERIFYCODE_UID);
		urlSb.append("&");
		urlSb.append(UrlTag.KEY);
		urlSb.append("=");
		urlSb.append(CommonDefine.VERIFYCODE_KEY);
		urlSb.append("&");
		urlSb.append(UrlTag.SMSMOB);
		urlSb.append("=");
		urlSb.append(smsMob);
		urlSb.append("&");
		urlSb.append(UrlTag.SMSTEXT);
		urlSb.append("=");
		urlSb.append(smsText);
		String response;
		response = HttpOperator.doGet(urlSb.toString());
		if (response == null) {
			mHandler.obtainMessage(CommonDefine.ERR_NET).sendToTarget();
		} else {
			if (Integer.parseInt(response) == 1) {
				mHandler.obtainMessage(CommonDefine.ERR_NONE_VERIFYCODE_OK, response).sendToTarget();
			} else {
				mHandler.obtainMessage(CommonDefine.ERR_VERIFYCODE, response).sendToTarget();
			}
		}
	}
}
