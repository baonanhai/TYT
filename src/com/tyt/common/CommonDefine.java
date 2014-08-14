package com.tyt.common;

import android.util.SparseIntArray;

import com.dxj.tyt.R;

public class CommonDefine {
	public static final boolean IS_DEBUG = true;
	
	private static final String URL_BASE_TEST = "http://182.92.186.31:8080/";
	private static final String URL_BASE_REAL = "http://www.teyuntong.cn/";
	
	private static final String URL_BASE;
	static {
		if (IS_DEBUG) {
			URL_BASE = URL_BASE_TEST;
		} else {
			URL_BASE = URL_BASE_REAL;
		}
	}
	
	public static final String URL_LOGIN = URL_BASE + "user/login";
	public static final String URL_QUERY = URL_BASE + "transport/query";
	
	public static final String VERSION = "2000";
	public static final String PRIVATEKEY = "1345~opo-4%";
	
	public static final SparseIntArray Login_err = new SparseIntArray();
	static {
		Login_err.put(-1, R.string.err_login_phone_not_exit);
		Login_err.put(-2, R.string.err_login_password_err);
		Login_err.put(-3, R.string.err_login_account_not_for_this_pc);
		Login_err.put(-4, R.string.err_login_account_out_date);
		Login_err.put(-5, R.string.err_login_soft_need_update);
		Login_err.put(-6, R.string.err_login_account_not_actived);
	}
	
	public static final int ERR_NONE = 0;
	public static final int ERR_NET = ERR_NONE + 1;
	
	public static final int ERR_SERVER_NONE = 200;
	public static final int ERR_SERVER = 500;
}
