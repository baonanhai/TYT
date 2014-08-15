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

public class HttpHandler {
	private static HttpHandler mHttpHandler;
	private Handler mHandler;
	private String mToken;

	private HttpHandler() {
	}

	public synchronized static HttpHandler getInstance(Handler handler) {
		if (mHttpHandler == null) {
			mHttpHandler = new HttpHandler();
		}
		mHttpHandler.setHandler(handler);
		return mHttpHandler;
	}

	private void setHandler(Handler handler) {
		mHandler = handler;
	}

	public void login(final String account, final String password) {
		initToken(account);
		List <NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair(JsonTag.CELLPHONE, account)); 
		params.add(new BasicNameValuePair(JsonTag.PASSWORD, password)); 
		params.add(new BasicNameValuePair(JsonTag.VERSION, CommonDefine.VERSION)); 
		params.add(new BasicNameValuePair(JsonTag.TOKEN, mToken)); 
		String response = HttpOperator.doPost(CommonDefine.URL_LOGIN, params);
		if (response == null) {
			mHandler.obtainMessage(CommonDefine.ERR_NET).sendToTarget();
		} else {
			mHandler.obtainMessage(CommonDefine.ERR_NONE, response).sendToTarget();
		}
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
	
	public void register(String phone, String password, String qq, String name, String idcard) {
		initToken(phone);
		List <NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair(JsonTag.CELLPHONE, phone)); 
		params.add(new BasicNameValuePair(JsonTag.PASSWORD, password)); 
		params.add(new BasicNameValuePair(JsonTag.USER_NAME, phone)); 
		params.add(new BasicNameValuePair(JsonTag.USERSIGN, CommonDefine.USERSIGN)); 
		params.add(new BasicNameValuePair(JsonTag.PCSIGN, CommonDefine.PCSIGN)); 
		params.add(new BasicNameValuePair(JsonTag.QQ, qq)); 
		params.add(new BasicNameValuePair(JsonTag.TRUE_NAME, name)); 
		params.add(new BasicNameValuePair(JsonTag.ID_CARD, idcard)); 
		params.add(new BasicNameValuePair(JsonTag.VERSION, CommonDefine.VERSION)); 
		params.add(new BasicNameValuePair(JsonTag.TOKEN, mToken)); 
		String response = HttpOperator.doPost(CommonDefine.URL_REGISTER, params);
		Log.i("sssss", response);
		if (response == null) {
			mHandler.obtainMessage(CommonDefine.ERR_NET).sendToTarget();
		} else {
			mHandler.obtainMessage(CommonDefine.ERR_NONE, response).sendToTarget();
		}
	}

	private void initToken(String cell_phone) {
		mToken = CommonUtil.MD5(cell_phone + CommonDefine.PRIVATEKEY);
	}
}
