package com.tyt.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;

import com.tyt.common.CommonDefine;
import com.tyt.common.CommonUtil;
import com.tyt.common.JsonTag;

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
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initToken(String cell_phone) {
		mToken = CommonUtil.MD5(cell_phone + CommonDefine.PRIVATEKEY);
	}
}
